package q.app.qetaa.model.cart;

import java.io.Serializable;
import java.util.Date;

public class Discount implements Serializable {

    private long id;
    private char discountType;//D= delivery value , P = discount percentage.
    private char status;//A=active, U=used, E=expired, D=deactivated
    private Date created;
    private Date expire;
    private int createdBy;
    private String code;
    private String name;//-name associated to the promocode (by default q.parts promocode)
    private String nameAr;//name associated to the promocode in arabic (by default q.parts promocode)
    private boolean reusable;//cannot be reused after first usage
    private boolean customerSpecific;//-- is only for one customer
    private Long customerId;//only if it is for one customer
    private Double percentage;//--only if type is p (percentage on purchased products)
    private int appCode;


    public int getAppCode() {
        return appCode;
    }

    public void setAppCode(int appCode) {
        this.appCode = appCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public char getDiscountType() {
        return discountType;
    }

    public void setDiscountType(char discountType) {
        this.discountType = discountType;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    public boolean isCustomerSpecific() {
        return customerSpecific;
    }

    public void setCustomerSpecific(boolean customerSpecific) {
        this.customerSpecific = customerSpecific;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
