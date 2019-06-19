package q.app.qetaa.model.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.app.qetaa.model.customer.PublicAddress;

import java.io.Serializable;
import java.util.List;

public class CartRequest implements Serializable {

    private long customerId;
    private long addressId;
    @JsonIgnore
    private PublicAddress address;
    private double deliveryCharges;
    private Long discountId;
    @JsonIgnore
    private Discount discount;
    private Integer preferredCuorier;
    private List<CartItemRequest> cartItems;

    private Integer ccMonth;
    private Integer ccYear;
    private String ccName;
    private String ccNumber;
    private String ccCvc;



    @JsonIgnore
    public double getTotalProducts(){
        double total = 0;
        for(CartItemRequest cartItem : cartItems){
            total += cartItem.getSalesPrice() * cartItem.getQuantity();
        }
        return total;
    }

    @JsonIgnore
    public double getTotalProductsAfterDiscount(){
        double total = 0;
        for(CartItemRequest cartItem : cartItems){
            total += cartItem.getSalesPriceAfterDiscount() * cartItem.getQuantity();
        }
        return total;
    }

    @JsonIgnore
    public double getTotalProductsDiscountValue(){
        double total = 0;
        for(CartItemRequest cartItem : cartItems){
            total += cartItem.getDiscountValue() * cartItem.getQuantity();
        }
        return total;
    }

    @JsonIgnore
    public double getTotalDiscount(){
        double total = 0;
        for(CartItemRequest cartItem : cartItems){
            total += cartItem.getDiscountValue() * cartItem.getQuantity();
        }
        if(discount != null){
            total += deliveryCharges;
        }
        return total;
    }

    /**
     *
     * @return a total before discount and vat
     */
    @JsonIgnore
    public double getSubTotal(){
        return getTotalProducts() + deliveryCharges;
    }

    /**
     *
     * @return a total before vat
     */
    @JsonIgnore
    public double getSubTotalAfterDiscount(){
        return getTotalProducts() + deliveryCharges - getTotalDiscount();
    }

    @JsonIgnore
    public double getTotalVat(){
        return  getSubTotal() * 0.05;
    }

    @JsonIgnore
    public double getTotalVatAfterDiscount(){
        return  getSubTotalAfterDiscount() * 0.05;
    }

    @JsonIgnore
    public double getGrandTotal(){
        return getSubTotal() + getTotalVat();
    }

    @JsonIgnore
    public double getGrandTotalAfterDiscount(){
        return getSubTotalAfterDiscount() + getTotalVatAfterDiscount();
    }

    public String getCcCvc() {
        return ccCvc;
    }

    public void setCcCvc(String ccCvc) {
        this.ccCvc = ccCvc;
    }

    public Integer getCcMonth() {
        return ccMonth;
    }

    public void setCcMonth(Integer ccMonth) {
        this.ccMonth = ccMonth;
    }

    public Integer getCcYear() {
        return ccYear;
    }

    public void setCcYear(Integer ccYear) {
        this.ccYear = ccYear;
    }

    public String getCcName() {
        return ccName;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public List<CartItemRequest> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemRequest> cartItems) {
        this.cartItems = cartItems;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public Integer getPreferredCuorier() {
        return preferredCuorier;
    }

    public void setPreferredCuorier(Integer preferredCuorier) {
        this.preferredCuorier = preferredCuorier;
    }

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public PublicAddress getAddress() {
        return address;
    }

    public void setAddress(PublicAddress address) {
        this.address = address;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
}
