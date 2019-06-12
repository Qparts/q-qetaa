package q.app.qetaa.vendor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import q.app.qetaa.beans.CountryBean;
import q.app.qetaa.beans.LoginBean;
import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.model.location.PublicCity;
import q.app.qetaa.model.location.PublicCountry;
import q.app.qetaa.model.location.PublicRegion;
import q.app.qetaa.reqs.NotLoggedRequester;

@Named
@ViewScoped
public class JoinUsBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private VendorJoinRequest joinUs;
	private int selectedCountryId;
	private int selectedRegionId;
	private boolean submitted;

	@Inject
	private LoginBean loginBean;

	@Inject
	private CountryBean countryBean;

	@Inject
	private NotLoggedRequester reqs;
	
	@PostConstruct
	private void init() {
		submitted = false;
		reset();
				
	}
	
	private void reset() {
		joinUs = new VendorJoinRequest();
		selectedCountryId = 0;
		selectedRegionId = 0;
	}
	
	public void addNewVendor() {
		Response r = reqs.postSecuredRequest(AppConstants.POST_JOIN_US, this.joinUs);
		reset();
		submitted = true;
		
	}

	public VendorJoinRequest getJoinUs() {
		return joinUs;
	}
	
	public List<PublicRegion> getSelectedCountryRegions(){
		for(PublicCountry c : countryBean.getCountries()) {
			if(this.selectedCountryId == c.getId())
				return c.getRegions();
		}
		return new ArrayList<>();
	}
	
	public List<PublicCity> getSelectedRegionCities(){
		for(PublicCountry c : countryBean.getCountries()) {
			if(this.selectedCountryId == c.getId()) {
				for(PublicRegion r : c.getRegions()) {
					if(r.getId() == this.selectedRegionId) {
						return r.getCities();
					}
				}
			}
				
		}
		return new ArrayList<>();
	}

	public void setJoinUs(VendorJoinRequest joinUs) {
		this.joinUs = joinUs;
	}

	public int getSelectedCountryId() {
		return selectedCountryId;
	}

	public void setSelectedCountryId(int selectedCountryId) {
		this.selectedCountryId = selectedCountryId;
	}

	public int getSelectedRegionId() {
		return selectedRegionId;
	}

	public void setSelectedRegionId(int selectedRegionId) {
		this.selectedRegionId = selectedRegionId;
	}

	public boolean isSubmitted() {
		return submitted;
	}

	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}

	
	

	
	
}
