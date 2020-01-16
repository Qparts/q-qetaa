package q.app.qetaa.model.product;

import java.io.Serializable;
import java.util.List;

public class PublicProduct implements Serializable {

    private long id;
    private String productNumber;
    private String desc;
    private String descAr;
    private String details;
    private String detailsAr;
    private PublicBrand brand;
    private List<PublicSpec> specs;
    private double salesPrice;
    private String image;
    private List<PublicReview> reviews;
    private List<PublicProduct> variants;

    public List<PublicProduct> getVariants() {
        return variants;
    }

    public void setVariants(List<PublicProduct> variants) {
        this.variants = variants;
    }

    public String getDescAr() {
        return descAr;
    }

    public void setDescAr(String descAr) {
        this.descAr = descAr;
    }

    public String getDetailsAr() {
        return detailsAr;
    }

    public void setDetailsAr(String detailsAr) {
        this.detailsAr = detailsAr;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public PublicBrand getBrand() {
        return brand;
    }

    public void setBrand(PublicBrand brand) {
        this.brand = brand;
    }

    public List<PublicSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<PublicSpec> specs) {
        this.specs = specs;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }


    public List<PublicReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<PublicReview> reviews) {
        this.reviews = reviews;
    }
}
