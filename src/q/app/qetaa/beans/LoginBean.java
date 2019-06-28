package q.app.qetaa.beans;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
import org.primefaces.PrimeFaces;
import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.helpers.Bundler;
import q.app.qetaa.helpers.Helper;
import q.app.qetaa.model.customer.LoginObject;
import q.app.qetaa.model.customer.RegisterModel;
import q.app.qetaa.model.location.PublicCountry;
import q.app.qetaa.model.quotation.PublicQuotation;
import q.app.qetaa.reqs.Requester;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private char loginMechanism = 'N';
    private char loginStatus = 'N';
    private LoginObject loginObject;
    private String username;
    private String password;
    private RegisterModel registerModel;
    private boolean remember;
    private boolean fromCookie;
    private String smsCode;
    private String smsCodeUser;

    @Inject
    private Requester reqs;

    @Inject
    private CreateQuotationBean createQuotationBean;

    @Inject
    private ActivityMonitorBean monitorBean;

    @Inject
    private CountryBean countryBean;


    @PostConstruct
    private void init() {
        remember = false;
        fromCookie = false;
        registerModel = new RegisterModel();
        registerModel.setCountryId(1);
        registerModel.setMobile("");
        // scheckCookieMap();
    }


    private void scheckCookieMap() {
        String email = null;
        String password = null;
        Map<String, Object> requestCookieMap = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestCookieMap();
        for (Map.Entry<String, Object> entry : requestCookieMap.entrySet()) {
            Cookie c = (Cookie) entry.getValue();
            if (c.getName().equals("email")) {
                email = c.getValue();
            } else if (c.getName().equals("password")) {
                password = c.getValue();
            }
        }
        if (email != null && password != null) {
            this.registerModel.setPassword(password);
            this.registerModel.setEmail(email);
            this.fromCookie = true;
            doEmailLogin();
            // do login from cookies
        }
    }

    private void saveCookie() {
        if (remember) {
            // create cookies
            Cookie emailCk = new Cookie("email", this.registerModel.getEmail());
            Cookie passCk = new Cookie("password", this.registerModel.getPassword());
            // set expiry date
            emailCk.setMaxAge(60 * 60 * 24);
            passCk.setMaxAge(60 * 60 * 24);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                    .getResponse();
            response.addCookie(emailCk);
            response.addCookie(passCk);
        }
    }

    public void chooseFacebookLogin() {
        loginMechanism = 'F';
        monitorBean.addToActivity("requested login/register from facebook");
        Helper.redirect(AppConstants.FB_DIALOG_URL);
    }

    public void checkLogin() {
        if (loginMechanism == 'F') {
            doFacebookLogin();
        } else if (loginMechanism == 'E') {
            Helper.redirect("/index");
        } else if (loginMechanism == 'N') {
            Helper.redirect("/index");
        }
    }

    public void checkCodeLogin() {
        try{
            monitorBean.addToActivity("login from code");
            String code = Helper.getParam("c");
            String email = Helper.getParam("e");
            String quotationId = Helper.getParam("q");
            Map<String,Object> map = new HashMap<>();
            map.put("code", code);
            map.put("email", email);
            Response r = reqs.postSecuredRequest(AppConstants.POST_CODE_LOGIN, map, null, 0);
            System.out.println(r.getStatus() + " " + AppConstants.POST_CODE_LOGIN);
            if (r.getStatus() == 200) {
                monitorBean.addToActivity("successful code");
                this.loginObject = r.readEntity(LoginObject.class);
                this.loginStatus = 'A';
                //saveCookie();
                doLogin();
                //check q.app.qetaa.cart status
                String anchor = "#c" +quotationId;
                Helper.redirect("/quotations" + anchor);
            } else {
                monitorBean.addToActivity("invalid code login");
                throw new Exception();
            }
        }catch (Exception ex){
            Helper.redirect("/index");
        }
    }



    public void chooseEmailRegistration() {
        monitorBean.addToActivity("chose email registration");
        this.loginMechanism = 'E';// registration
        this.loginStatus = 'R';
    }

    private Map<String,String> initSMSMap(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", registerModel.getMobile());
        map.put("email", registerModel.getEmail());
        if (this.registerModel.getCountryId() == 1) {
            //monitorBean.addToActivity("sending sms");
            map.put("countryCode", "966");
            registerModel.setCountryCode("966");
        } else {
            PublicCountry pc = countryBean.getCountryFromId(registerModel.getCountryId());
            map.put("countryCode", pc.getCountryCode());
        }
        return map;
    }



    public void requestSMS() {
        System.out.println("requesting");
        if (registerModel.getPassword().equals(registerModel.getConfirmPassword())) {
            Map<String,String> map = initSMSMap();
            Response r = reqs.postSecuredRequest(AppConstants.POST_SIGNUP_SMS, map, null, 0);
            if (r.getStatus() == 200) {
                this.smsCode = r.readEntity(String.class);
                monitorBean.addToActivity("sms sent to customer: " + smsCode);
            } else if (r.getStatus() == 409) {
                monitorBean.addToActivity("customer already registered, failed registration");
                Helper.addErrorMessage(Bundler.getValue("customerRegistered"));
            } else {
                Helper.addErrorMessage(Bundler.getValue("errorOccured"));
            }
        } else {
            monitorBean.addToActivity("entered password did not match");
            Helper.addErrorMessage(Bundler.getValue("passwordNotMatched"));
        }
    }

    public void requestSMSFacebook() {
        /*
        monitorBean.addToActivity("request facebook register");
        Map<String, String> map = new HashMap<String, String>();
        monitorBean.addToActivity("facebook registration mobile: " + facebookAccess.getMobile());
        monitorBean.addToActivity("facebook registration email: "+ facebookAccess.getEmail());
        monitorBean.addToActivity("facebook registration name: " + facebookAccess.getFirstName() + " " + facebookAccess.getLastName());
        map.put("mobile", facebookAccess.getMobile());
        map.put("email", facebookAccess.getEmail());
        String link = "";
        if (this.facebookAccess.getCountryId() == 1) {
            monitorBean.addToActivity("sending sms");
            map.put("countryCode", "966");
            facebookAccess.setCountryCode("966");
            link = AppConstants.POST_REGISTER_SMS;
        }

        else {
            for (PublicCountry c : countryBean.getCountries()) {
                if (c.getId() == facebookAccess.getCountryId()) {
                    monitorBean.addToActivity("chose country: " + c.getName());
                    map.put("countryCode", c.getCountryCode());
                    facebookAccess.setCountryCode(c.getCountryCode());
                    break;
                }
            }
            link = AppConstants.POST_REGISTER_EMAIL;
        }

        Response r = reqs.postSecuredRequest(link, map);
        if (r.getStatus() == 200) {
            this.smsCode = r.readEntity(String.class);
            monitorBean.addToActivity("sms sent to q.app.qetaa.customer: " + smsCode);
        } else if (r.getStatus() == 409) {
            monitorBean.addToActivity("q.app.qetaa.customer already registered, failed registration");
            Helper.addErrorMessage(Bundler.getValue("customerRegistered"));
        } else {
            Helper.addErrorMessage(Bundler.getValue("errorOccured"));
        }
        */
    }

    public void signup() {
        monitorBean.addToActivity("verifying sms");
        if (this.smsCode.equals(this.smsCodeUser)) {
            registerModel.setCreatedBy(0);
            registerModel.setType('M');
            if(this.loginMechanism == 'F') {
                registerModel.setType('F');
            }
            Response r = reqs.postSecuredRequest(AppConstants.POST_SIGNUP, registerModel, null, 0);
            if(r.getStatus() == 200) {
                monitorBean.addToActivity("successful registration mobile: " + registerModel.getMobile());
                this.loginObject = r.readEntity(LoginObject.class);
                this.loginStatus = 'A';
                this.registerModel = new RegisterModel();
                doLogin();
            } else {
                monitorBean.addToActivity("failed registration");
            }
        } else {
            monitorBean.addToActivity("entered invalid sms code");
            Helper.addErrorMessage(Bundler.getValue("invalidSms"));
        }
    }

    public void activateAndRegisterSMSFaceook() {
        /*
        monitorBean.addToActivity("verifying sms");
        if (this.smsCode.equals(this.smsCodeUser)) {
            Response r = reqs.postSecuredRequest(AppConstants.POST_FACEBOOK_REGISTER, facebookAccess);
            if (r.getStatus() == 200) {
                monitorBean.addToActivity("successful registration mobile: " + facebookAccess.getMobile());
                this.loginObject = r.readEntity(LoginObject.class);
                this.loginStatus = 'A';
                this.facebookAccess = new FacebookAccess();
                doLogin();
            }
        } else {
            monitorBean.addToActivity("entered invalid sms");
            Helper.addErrorMessage(Bundler.getValue("invalidSms"));
        }
        */
    }

    public void doEmailLogin() {
        monitorBean.addToActivity("tried to login as " + username);
        Map<String,String> map = new HashMap<>();
        map.put("email", username);
        map.put("password", password);
        Response r = reqs.postSecuredRequest(AppConstants.POST_EMAIL_LOGIN, map, null, 0);
        if (r.getStatus() == 200) {
            this.loginObject = r.readEntity(LoginObject.class);
            this.loginStatus = 'A';
            doLogin();
            //saveCookie();
        } else if (r.getStatus() == 404) {
            monitorBean.addToActivity("wrong password in login");
            Helper.addErrorMessage(Bundler.getValue("passwordNotMatched"));
        } else if (r.getStatus() == 500) {
            Helper.addErrorMessage(Bundler.getValue("passwordNotMatched"));
        }
    }

    private void deleteCookie() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("maxAge", 0);
        /*
         * the cookie I want to overwrite (expire)
         */
        FacesContext.getCurrentInstance().getExternalContext().addResponseCookie("email", "", props);
        /*
         * the cookie I want to overwrite (expire)
         */
        FacesContext.getCurrentInstance().getExternalContext().addResponseCookie("password", "", props); // t
    }

    private void doFacebookLogin() {
        String code = Helper.getParam("code");
        if (code != null) {
            monitorBean.addToActivity("received login code from facebook");
            FacebookClient client = new DefaultFacebookClient(Version.VERSION_2_8);
            FacebookClient.AccessToken token = client.obtainUserAccessToken(AppConstants.FB_APP_ID, AppConstants.FB_APP_SECRET, AppConstants.PAGE_SUCCESSFUL, code);
            client = new DefaultFacebookClient(token.getAccessToken(), Version.VERSION_2_8);
            User user = client.fetchObject("me", User.class, Parameter.with("fields", "id,first_name,last_name,email"));
            registerModel = new RegisterModel(user, "");
            registerModel.setCountryId(1);
            Response r = reqs.postSecuredRequest(AppConstants.POST_FACEBOOK_LOGIN, registerModel, null, 0);
            if (r.getStatus() == 404) {
                monitorBean.addToActivity("requested login from customer but not registered, redirected to register dialog");
                this.loginStatus = 'R';// needs registration
                Helper.redirect("/index");

            } else if (r.getStatus() == 200) {
                monitorBean.addToActivity("successful login from facebook");
                this.loginStatus = 'A';
                this.loginObject = r.readEntity(LoginObject.class);
                Helper.redirect("/index");
                // login
                doLogin();
            }
        } else {
            monitorBean.addToActivity("error in facebook code");
        }
    }

    public String getSecurityHeader() {
        return "Bearer " + this.getLoginObject().getToken() + " && " + this.getLoginObject().getCustomer().getId() + " && "
                + AppConstants.APP_SECRET + " && C";// from user
    }

    public void updateLogin(LoginObject loginObject) {
        this.loginObject = loginObject;
        doLogin();
    }

    public void doLogin() {
        monitorBean.addToActivity("successful login");
        monitorBean.initCustomer();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("customer.loginObject", loginObject);
        if (createQuotationBean.getStep() == 5) {
            PrimeFaces.current().executeScript("resetActive(" + createQuotationBean.getProgressPercentage() + ", '"
                    + createQuotationBean.getProgressStepName() + "');");
            PrimeFaces.current().executeScript("showCartDialog()");
        }
    }

    public void doLogout() {
        monitorBean.addToActivity("logged out");
        deleteCookie();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("customer.loginObject");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        Helper.redirect("/index");
    }

    public boolean isLogged() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("customer.loginObject") != null;
    }




    public String getCountryRegex() {
        String regex = "";
        try {
            PublicCountry country = countryBean.getCountryFromId(registerModel.getCountryId());
            regex = country.getMobileRegex();
        }catch (Exception ex){

        }
        return regex;
    }

    public char getLoginMechanism() {
        return loginMechanism;
    }

    public char getLoginStatus() {
        return loginStatus;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getSmsCodeUser() {
        return smsCodeUser;
    }

    public void setSmsCodeUser(String smsCodeUser) {
        this.smsCodeUser = smsCodeUser;
    }

    public void setLoginStatus(char loginStatus) {
        this.loginStatus = loginStatus;
    }

    public void setLoginObject(LoginObject loginObject) {
        this.loginObject = loginObject;
    }

    public LoginObject getLoginObject() {
        return loginObject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RegisterModel getRegisterModel() {
        return registerModel;
    }

    public void setRegisterModel(RegisterModel registerModel) {
        this.registerModel = registerModel;
    }
}
