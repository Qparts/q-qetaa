package q.app.qetaa.model.product;


import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PublicReview implements Serializable {

    private static final long serialVersionUID = 1L;
    private long id;
    private Long customerId;
    private String customerName;
    private Integer rating;
    private String text;
    private long productId;
    private Date created;


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicReview that = (PublicReview) o;
        return id == that.id &&
                productId == that.productId &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(rating, that.rating) &&
                Objects.equals(text, that.text) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, rating, text, productId, created);
    }
}
