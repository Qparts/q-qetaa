package q.app.qetaa.beans;

import jdk.jfr.Name;
import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.helpers.Helper;
import q.app.qetaa.model.customer.PublicAddress;
import q.app.qetaa.model.location.PublicCountry;
import q.app.qetaa.model.location.PublicRegion;
import q.app.qetaa.reqs.Requester;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.Serializable;

@Named
@ViewScoped
public class SelectAddressBean implements Serializable {


    private int selectedRegionId;
    private PublicRegion selectedRegion;
    private PublicAddress address;
    private boolean newAddress;


    @Inject
    private Requester reqs;

    @Inject
    private LoginBean loginBean;

    @Inject
    private CountryBean countryBean;

    @Inject
    private CartBean cartBean;

    @PostConstruct
    private void init(){
        address = new PublicAddress();
    }

    public void selectAddress(PublicAddress address){
        cartBean.getCartRequest().setAddress(address);
        cartBean.getCartRequest().setAddressId(address.getId());
        cartBean.setStage(3);
        Helper.redirect("checkout");
    }


    public void createAddress(){
        address.setCustomerId(loginBean.getLoginObject().getCustomer().getId());
        address.setMobile(loginBean.getLoginObject().getCustomer().getMobile());
        address.setTitle(address.getLine1());
        Response r= reqs.postSecuredRequest(AppConstants.POST_ADDRESS, address);
        if(r.getStatus() == 200){
            PublicAddress publicAddress = r.readEntity(PublicAddress.class);
            loginBean.getLoginObject().getCustomer().getAddresses().add(publicAddress);
            this.newAddress = false;
            Helper.redirect("select-address");
        }
    }


    public PublicCountry getCustomerCountry() {
        int countryId = loginBean.getLoginObject().getCustomer().getCountryId();
        for (PublicCountry c : countryBean.getCountries()) {
            if (c.getId() == countryId) {
                return c;
            }
        }
        PublicCountry c = new PublicCountry();
        c.setId(1);
        return c;
    }

    public void chooseRegion() {
        PublicCountry country = getCustomerCountry();
        if (this.selectedRegionId > 0) {
            for (PublicRegion region : country.getRegions()) {
                if (selectedRegionId == region.getId()) {
                    this.selectedRegion = region;
                    break;
                }
            }
        } else {
           // this.quotationRequest.setCityId(0);
        }
    }

    public int getSelectedRegionId() {
        return selectedRegionId;
    }

    public void setSelectedRegionId(int selectedRegionId) {
        this.selectedRegionId = selectedRegionId;
    }

    public PublicRegion getSelectedRegion() {
        return selectedRegion;
    }

    public void setSelectedRegion(PublicRegion selectedRegion) {
        this.selectedRegion = selectedRegion;
    }

    public PublicAddress getAddress() {
        return address;
    }

    public void setAddress(PublicAddress address) {
        this.address = address;
    }

    public boolean isNewAddress() {
        return newAddress;
    }

    public void setNewAddress(boolean newAddress) {
        this.newAddress = newAddress;
    }
}
