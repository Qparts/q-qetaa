package q.app.qetaa.beans;

import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.helpers.Bundler;
import q.app.qetaa.helpers.Helper;
import q.app.qetaa.model.cart.*;
import q.app.qetaa.model.location.PublicCountry;
import q.app.qetaa.model.quotation.PaymentRequest;
import q.app.qetaa.model.quotation.PublicQuotation;
import q.app.qetaa.model.quotation.PublicQuotationItem;
import q.app.qetaa.reqs.Requester;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Named
@SessionScoped
public class CartBean implements Serializable {

    private CartRequest cartRequest;
    private char paymentMethod;
    private CartRequestResponse cartRequestResponse;
    private boolean paymentFailed;
    private int stage;
    private String promoCodeQuery;
    private boolean promoVerified;
    private boolean havePromo;

    @Inject
    private LoginBean loginBean;

    @Inject
    private Requester reqs;

    @Inject
    private CountryBean countryBean;

    @PostConstruct
    public void init() {
        stage = 1;
        cartRequest = new CartRequest();
        cartRequest.setCartItems(new ArrayList<>());
        cartRequest.setDeliveryCharges(35);
        cartRequest.setCustomerId(loginBean.getLoginObject().getCustomer().getId());
        paymentMethod = 'N';
        havePromo = false;
        promoVerified =false;
        promoCodeQuery = "";
        initLiveWallet();
    }

    public void checkStage() {
        if (stage == 1) {
            Helper.redirect("quotations");
        } else if (stage == 2) {
            Helper.redirect("select-address");
        }
    }

    public void initLiveWallet(){
        Response r = reqs.getSecuredRequest(AppConstants.getLiveWallet(loginBean.getLoginObject().getCustomer().getId()));
        if(r.getStatus() == 200){
            Map<String,Object> map = r.readEntity(Map.class);
            this.cartRequest.setWalletAmount(((Number)map.get("amount")).doubleValue());
        }
    }

    //
    public void verifyPromoCode() {
        Response r = reqs.getSecuredRequest(AppConstants.getPromotionCodeFromCode(this.promoCodeQuery));
        if (r.getStatus() == 200) {
            promoVerified = true;
            Discount discount = r.readEntity(Discount.class);
            if(discount.getDiscountType() == 'D'){
                this.cartRequest.setDiscountId(discount.getId());
                this.cartRequest.setDiscount(discount);
            }
            else{
                for(var cartItem : cartRequest.getCartItems()){
                    if(cartItem.getDiscountId() == null){
                        cartItem.setDiscount(discount);
                        cartItem.setDiscountId(discount.getId());
                    }
                }
            }
        } else if (r.getStatus() == 498) {
            Helper.addErrorMessage(Bundler.getValue("promCodeQuestion"), "form-3:ms");
        } else if (r.getStatus() == 410) {
            Helper.addErrorMessage(Bundler.getValue("promCodeUsed"), "form-3:ms");
        } else if (r.getStatus() == 404) {
            Helper.addErrorMessage(Bundler.getValue("promCodeNotFound"), "form-3:ms");
        } else {
            Helper.addErrorMessage(Bundler.getValue("errorOccured"));
        }
    }

    public void addQuotation(PublicQuotation quotation) {
        for (PublicQuotationItem quotationItem : quotation.getQuotationItems()) {
            CartItemRequest cartItem = new CartItemRequest();
            cartItem.setItemName(quotationItem.getName());
            cartItem.setQuantity(quotationItem.getQuantity());
            cartItem.setPublicProduct(quotationItem.getProducts());
            cartItem.setProductId(quotationItem.getProducts().getId());
            cartItem.setSalesPrice(quotationItem.getProducts().getSalesPrice());
            cartRequest.getCartItems().add(cartItem);
        }
    }

    public void checkout() {
        List<String> errors = verifyPayment();
        if (errors.isEmpty()) {
            if (this.paymentMethod == 'V') {
                makeCreditCardRequest();
            } else if (this.paymentMethod == 'W') {
                makeWireTransferRequest();
            }
        } else {
            for (String error : errors) {
                Helper.addErrorMessage(error);
            }
        }
    }

    private void preparePaymentRequest(PaymentRequest paymentRequest){
        paymentRequest.setSalesType('C');
        paymentRequest.setPaymentMethod('C');
        paymentRequest.setCustomerId(loginBean.getLoginObject().getCustomer().getId());
        paymentRequest.setBaseAmount(this.cartRequest.getSubTotal());
        paymentRequest.setPromoDiscount(this.cartRequest.getTotalDiscount());
        paymentRequest.setVatPercentage(.05);
        paymentRequest.setCreated(new Date());
        paymentRequest.setCountryId(loginBean.getLoginObject().getCustomer().getCountryId());
        paymentRequest.setCurrency("SAR");
        paymentRequest.setThreeDSecure(true);
        //card info
        paymentRequest.setNumber(Long.valueOf(this.cartRequest.getCcNumber()));
        paymentRequest.setExpMonth(this.cartRequest.getCcMonth());
        paymentRequest.setExpYear(this.cartRequest.getCcYear() - 2000);
        paymentRequest.setCvc(Integer.valueOf(this.cartRequest.getCcCvc()));
        paymentRequest.setNameOnCard(this.cartRequest.getCcName());
        PublicCountry country = countryBean.getCountryFromId(loginBean.getLoginObject().getCustomer().getCountryId());
        paymentRequest.setCountry(country.getName());
        //customer or vendor user
        paymentRequest.setFirstName(loginBean.getLoginObject().getCustomer().getFirstName());
        paymentRequest.setLastName(loginBean.getLoginObject().getCustomer().getLastName());
        paymentRequest.setEmail(loginBean.getLoginObject().getCustomer().getEmail());
        paymentRequest.setCountryCode(country.getCountryCode());
        paymentRequest.setMobile(loginBean.getLoginObject().getCustomer().getMobile());
        paymentRequest.setClientIp(getClientIp());
    }

    private String getClientIp() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }


    private void makeCreditCardRequest() {
        PaymentRequest paymentRequest = new PaymentRequest();
        preparePaymentRequest(paymentRequest);
        cartRequest.setPaymentRequest(paymentRequest);
        Response r = reqs.postSecuredRequest(AppConstants.POST_CART_CREDIT_CARD, cartRequest);
        if (r.getStatus() == 202) {
            cartRequestResponse = r.readEntity(CartRequestResponse.class);
            Helper.redirect(cartRequestResponse.getTransactionUrl());
        } else if (r.getStatus() == 200) {
            //successful transaction
            cartRequestResponse = r.readEntity(CartRequestResponse.class);
            paymentFailed = false;
            stage = 5;
            Helper.redirect("checkout");
        } else {
            Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
        }
    }


    private void makeWireTransferRequest() {
        Response r = reqs.postSecuredRequest(AppConstants.POST_CART_WIRE_TRANSFER, cartRequest);
        if (r.getStatus() == 200) {
            cartRequestResponse = r.readEntity(CartRequestResponse.class);
            stage = 4;
            Helper.redirect("checkout");
        } else {
            Helper.addErrorMessage("Something went wrong please try again later");
        }
    }


    private List<String> verifyPayment() {
        List<String> errors = new ArrayList<>();
        if (paymentMethod == 'V') {
            cartRequest.setCcNumber(cartRequest.getCcNumber().trim());
            Pattern pattern = Pattern.compile(regexCardNumber());
            Matcher matcher = pattern.matcher(cartRequest.getCcNumber());
            // check card number
            if (!matcher.matches()) {
                errors.add("*" + Bundler.getValue("wrongCartNumber"));
            }
            // check card name
            cartRequest.setCcName(cartRequest.getCcName().trim());
            if (cartRequest.getCcName().length() == 0 || !cartRequest.getCcName().contains(" ")) {
                errors.add("*" + Bundler.getValue("wrongName"));
            }
            // check cvc

            cartRequest.setCcCvc(cartRequest.getCcCvc().trim());
            Pattern pattern2 = Pattern.compile(regexCVV());
            Matcher matcher2 = pattern2.matcher(cartRequest.getCcCvc());
            // check card number
            if (!matcher2.matches()) {
                errors.add("*" + Bundler.getValue("wrongCVC"));
            }

            // check card expiry date
            Calendar c = Calendar.getInstance();
            int currentYear = c.get(Calendar.YEAR);
            int currentMonth = c.get(Calendar.MONTH) + 1;

            if (cartRequest.getCcYear() < currentYear || (cartRequest.getCcYear() == currentYear && cartRequest.getCcMonth() <= currentMonth)) {
                errors.add("*" + Bundler.getValue("cardExpired"));
            }
        }
        if (this.paymentMethod == 'N') {
            errors.add("*" + Bundler.getValue("selectPayment"));
        }

        if (cartRequest.getAddress() == null || cartRequest.getAddressId() == 0) {
            errors.add("*" + Bundler.getValue("enterAddress"));
        }
        return errors;
    }


    private String regexCardNumber() {
        return "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|(?<mastercard>5[1-5][0-9]{14}))$";
    }

    private String regexCVV() {
        return "^[0-9]{3}$";
    }


    public int[] getExpiryDates() {
        int[] years = new int[10];
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < years.length; i++) {
            years[i] = year;
            year++;
        }
        return years;
    }

    public int[] getExpiryMonths() {
        int[] months = new int[12];
        for (int i = 0; i < months.length; i++) {
            months[i] = i + 1;
        }
        return months;
    }


    public void processPaymentResponse() {
        try {

            String tapChargeId = Helper.getParam("tap_id");
            Map<String, String> map = new HashMap<>();
            map.put("chargeId", tapChargeId);
            Response r = reqs.putSecuredRequest(AppConstants.PUT_PAYMENT_REQUEST, map);
            if (r.getStatus() == 200) {
                //update cart status and fund wallet
                paymentFailed = false;
                stage = 5;
                updateCartStatus("paid");
                Helper.redirect("checkout");
            } else if(r.getStatus() == 409){
                //this is duplicate
                paymentFailed = false;
                Helper.redirect("checkout");
            }
            else {
                paymentFailed = true;
                Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
                updateCartStatus("failed");
                Helper.redirect("checkout");
            }

        } catch (Exception ex) {

        }
    }

    private void updateCartStatus(String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("cartId", this.cartRequestResponse.getCartId());
        map.put("paymentStatus", status);
        Response r = reqs.putSecuredRequest(AppConstants.PUT_CART_PAYMENT, map);
    }



    public CartRequest getCartRequest() {
        return cartRequest;
    }

    public void setCartRequest(CartRequest cartRequest) {
        this.cartRequest = cartRequest;
    }

    public char getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(char paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isPaymentFailed() {
        return paymentFailed;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public CartRequestResponse getCartRequestResponse() {
        return cartRequestResponse;
    }

    public String getPromoCodeQuery() {
        return promoCodeQuery;
    }

    public void setPromoCodeQuery(String promoCodeQuery) {
        this.promoCodeQuery = promoCodeQuery;
    }

    public boolean isPromoVerified() {
        return promoVerified;
    }

    public void setPromoVerified(boolean promoVerified) {
        this.promoVerified = promoVerified;
    }

    public boolean isHavePromo() {
        return havePromo;
    }

    public void setHavePromo(boolean havePromo) {
        this.havePromo = havePromo;
    }
}
