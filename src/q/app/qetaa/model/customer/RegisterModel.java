package q.app.qetaa.model.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restfb.types.User;

import java.io.Serializable;

public class RegisterModel implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String contactEmail;//different email if not facebook email
    private String facebookId;//facebook id
    private Character type;//M = manual , F = facebook
    private Integer countryId;
    private String countryCode;
    private String mobile;
    private String password;
    private Integer createdBy;
    @JsonIgnore
    private String confirmPassword;

    public RegisterModel(){

    }

    public RegisterModel(User user, String mobile){
        this.type = 'F';
        this.email = user.getEmail();
        this.facebookId = user.getId();
        this.contactEmail = user.getEmail();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.mobile = mobile;
        this.createdBy = 0;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
