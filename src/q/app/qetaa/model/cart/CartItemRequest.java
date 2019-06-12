package q.app.qetaa.model.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.app.qetaa.model.product.PublicProduct;

public class CartItemRequest {
    private long productId;
    private int quantity;
    private double salesPrice;
    private Long discountId;

    @JsonIgnore
    private Discount discount;
    @JsonIgnore
    private PublicProduct publicProduct;
    @JsonIgnore
    private String itemName;


    @JsonIgnore
    public double getDiscountValue(){
        if(discount != null){
            return salesPrice * (discount.getPercentage()/100);
        }
        return 0;
    }


    public PublicProduct getPublicProduct() {
        return publicProduct;
    }

    public void setPublicProduct(PublicProduct publicProduct) {
        this.publicProduct = publicProduct;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
}
