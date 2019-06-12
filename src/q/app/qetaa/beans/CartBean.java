package q.app.qetaa.beans;

import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.helpers.Bundler;
import q.app.qetaa.helpers.Helper;
import q.app.qetaa.model.cart.*;
import q.app.qetaa.model.quotation.PublicQuotation;
import q.app.qetaa.model.quotation.PublicQuotationItem;
import q.app.qetaa.reqs.Requester;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
    private ActivityMonitorBean monitorBean;

    @Inject
    private LoginBean loginBean;

    @Inject
    private Requester reqs;

    @PostConstruct
    public void init() {
        stage = 1;
        cartRequest = new CartRequest();
        cartRequest.setCartItems(new ArrayList<>());
        cartRequest.setDeliveryCharges(35);
        cartRequest.setCustomerId(loginBean.getLoginObject().getCustomer().getId());
        paymentMethod = 'N';
    }

    public void checkStage() {
        if (stage == 1) {
            Helper.redirect("quotations");
        } else if (stage == 2) {
            Helper.redirect("select-address");
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
                    if(cartItem.getDiscountId() != null){
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
        monitorBean.addToActivity("verifying q.app.qetaa.payment");
        List<String> errors = verifyPayment();
        if (errors.isEmpty()) {
            if (this.paymentMethod == 'V') {
                monitorBean.addToActivity("making credit card payment request");
                makeCreditCardRequest();
            } else if (this.paymentMethod == 'W') {
                monitorBean.addToActivity("making wire transfer request");
                makeWireTransferRequest();
            }
        } else {
            for (String error : errors) {
                monitorBean.addToActivity("q.app.qetaa.payment error: " + error);
                Helper.addErrorMessage(error);
            }
        }
    }

    private void makeCreditCardRequest() {
        Response r = reqs.postSecuredRequest(AppConstants.POST_CART_CREDIT_CARD, cartRequest);
        System.out.println(r.getStatus());
        if (r.getStatus() == 202) {
            monitorBean.addToActivity("redirecting to payment page");
            cartRequestResponse = r.readEntity(CartRequestResponse.class);
            Helper.redirect(cartRequestResponse.getTransactionUrl());
        } else if (r.getStatus() == 200) {
            monitorBean.addToActivity("successfully paid without 3d secure");
            //successful transaction
            cartRequestResponse = r.readEntity(CartRequestResponse.class);
            paymentFailed = false;
            stage = 5;
            Helper.redirect("checkout");
        } else {
            monitorBean.addToActivity("error in payment from our server");
            Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
        }
    }


    private void makeWireTransferRequest() {
        Response r = reqs.postSecuredRequest(AppConstants.POST_CART_WIRE_TRANSFER, cartRequest);
        System.out.println(r.getStatus());
        if (r.getStatus() == 200) {
            monitorBean.addToActivity("successful wire transfer request");
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
            monitorBean.addToActivity("received response from credit card payment");
            String paymentId = Helper.getParam("id");// some string
            String status = Helper.getParam("status");// paid , failed
            ThreeDConfirmRequest threeD = new ThreeDConfirmRequest();
            threeD.setCartId(cartRequestResponse.getCartId());
            threeD.setCustomerId(cartRequest.getCustomerId());
            threeD.setId(paymentId);
            threeD.setStatus(status);
            Response r = reqs.putSecuredRequest(AppConstants.PUT_3D_SECURE_RESPONSE, threeD);
            if (r.getStatus() == 201) {
                if (status.equals("paid")) {
                    monitorBean.addToActivity("successful payment at credit card, payment id: " + paymentId);
                    paymentFailed = false;
                    stage = 5;
                    Helper.redirect("checkout");
                } else if (status.equals("failed")) {
                    monitorBean.addToActivity("failed credit card payment");
                    paymentFailed = true;
                    Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
                    Helper.redirect("checkout");
                    // do not proceed.
                }
            } else {
                Helper.addErrorMessage("An error in updating! please contact customer service");
            }

        } catch (Exception ex) {

        }
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
