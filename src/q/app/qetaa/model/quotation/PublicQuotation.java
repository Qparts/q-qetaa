package q.app.qetaa.model.quotation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

public class PublicQuotation {


    private long id;
    private long customerId;
    private Date created;
    private Long customerVehicleId;
    private int cityId;
    private boolean read;
    private List<PublicQuotationItem> quotationItems;
    private List<PublicComment> comments;

    @JsonIgnore
    public void initNewQuantities(){
        for(var pqi : quotationItems){
            pqi.initNewQuantity();
        }
    }

    @JsonIgnore
    public double getTotalProducts(){
        try{
            double total = 0;
            for(PublicQuotationItem qi : quotationItems){
                total += qi.getQuantity() * qi.getProducts().getSalesPrice();
            }
            return total;
        }catch (NullPointerException ex){
            return 0;
        }
    }

    @JsonIgnore
    public double getDeliveryFees(){
        return 35;
    }

    @JsonIgnore
    public double getTotalVat(){
        return getSubtotal() * 0.05;
    }

    @JsonIgnore
    public double getSubtotal(){
        return getTotalProducts() + getDeliveryFees();
    }
    @JsonIgnore
    public double getGrandTotal(){
        return getSubtotal() + getTotalVat();
    }





    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCustomerVehicleId() {
        return customerVehicleId;
    }

    public void setCustomerVehicleId(Long customerVehicleId) {
        this.customerVehicleId = customerVehicleId;
    }

    public List<PublicQuotationItem> getQuotationItems() {
        return quotationItems;
    }

    public void setQuotationItems(List<PublicQuotationItem> quotationItems) {
        this.quotationItems = quotationItems;
    }

    public List<PublicComment> getComments() {
        return comments;
    }

    public void setComments(List<PublicComment> comments) {
        this.comments = comments;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
