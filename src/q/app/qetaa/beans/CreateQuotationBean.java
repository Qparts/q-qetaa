package q.app.qetaa.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.primefaces.PrimeFaces;
import q.app.qetaa.helpers.*;
import q.app.qetaa.model.customer.PublicVehicle;
import q.app.qetaa.model.location.PublicCountry;
import q.app.qetaa.model.location.PublicRegion;
import q.app.qetaa.model.quotation.CreateQuotationItemRequest;
import q.app.qetaa.model.quotation.CreateQuotationRequest;
import q.app.qetaa.model.quotation.CreateQuotationResponse;
import q.app.qetaa.model.vehicle.PublicMake;
import q.app.qetaa.model.vehicle.PublicModel;
import q.app.qetaa.model.vehicle.PublicModelYear;
import q.app.qetaa.reqs.NotLoggedRequester;
import q.app.qetaa.reqs.Requester;

@Named
@SessionScoped
public class CreateQuotationBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private CreateQuotationRequest quotationRequest;
	private List<PublicMake> makes;
	private int step;
	private PublicVehicle selectedPublicVehicle;
	private long selectedPublicVehicleId;
	private PublicMake selectedMake;
	private PublicModel selectedModel;
	private PublicModelYear selectedModelYear;
	private int yearNumber;
	private Long lastOrderId;
	private boolean lastNoVin;
	private boolean vinImageUploaded;
	private boolean lastVinImage;
	private PublicRegion selectedRegion;
	private boolean agree;
	private int selectedRegionId;
	private String vinBase64;

	@Inject
	private NotLoggedRequester reqsNotLogged;
	@Inject
	private ActivityMonitorBean monitorBean;
	@Inject
	private Requester reqs;
	@Inject
	private CountryBean countryBean;
	@Inject
	private LoginBean loginBean;

	@PostConstruct
	private void beanInit() {
		vinImageUploaded = false;
		vinBase64 = "";
		initMakes();
		init();
	}


	private void init() {
		vinImageUploaded = false;
		vinBase64 = "";
		quotationRequest = new CreateQuotationRequest();
		quotationRequest.setQuotationItems(new ArrayList<>());
		step = 0;
		addItem();
		this.selectedMake = null;
		this.selectedModel = null;
	}

	public String getVin(){
		if(this.selectedPublicVehicle != null){
			return selectedPublicVehicle.getVin();
		}
		else{
			return this.quotationRequest.getVin();
		}
	}

	public String getFullVehicleName() {
		String s = "";
		switch (step) {
		case 1:
			s += this.selectedMake.getNameAr();
			break;
		case 2:
			s += this.selectedMake.getNameAr() + " - " + this.selectedModel.getNameAr();
			break;
		case 3:
		case 4:
		case 5:
			if(this.selectedPublicVehicle != null){
				s += selectedPublicVehicle.getVehicle().getFullName();
			}
			else{
				s += this.selectedMake.getNameAr() + " - " + this.selectedModel.getNameAr() + " - " + this.yearNumber;
			}
			break;

		}
		return s;
	}

	public void addItem() {
		CreateQuotationItemRequest item = new CreateQuotationItemRequest();
		quotationRequest.getQuotationItems().add(item);
	}

	public void addItem(String desc) {
		monitorBean.addToActivity(desc);
		addItem();
	}

	public void removeItem(CreateQuotationItemRequest item) {
		monitorBean.addToActivity("removed item: " + item.getItemName() + ", quantity: " + item.getQuantity());
		this.quotationRequest.getQuotationItems().remove(item);
	}



	private void initMakes() {
		makes = new ArrayList<>();
		Response r = reqsNotLogged.getSecuredRequest(AppConstants.GET_ACTIVE_MAKES);
		if (r.getStatus() == 200) {
			makes = r.readEntity(new GenericType<List<PublicMake>>() {
			});
		}
	}

	public void submit() {
		monitorBean.addToActivity("chose city: " + quotationRequest.getCityId());
		monitorBean.addToActivity("submitting order");
		quotationRequest.setCustomerId(this.loginBean.getLoginObject().getCustomer().getId());
		if(selectedPublicVehicle != null){
			quotationRequest.setCustomerVehicleId(selectedPublicVehicle.getId());
			quotationRequest.setMakeId(this.selectedPublicVehicle.getVehicle().getMake().getId());
			quotationRequest.setVin("");
		}
		else{
			quotationRequest.setMakeId(this.selectedMake.getId());
		}
		if(this.vinImageUploaded) {
			quotationRequest.setImageAttached(true);
		}
		int index = 0;
		for (CreateQuotationItemRequest cqir : quotationRequest.getQuotationItems()) {
			cqir.setTempId(index);
			cqir.setItemName(cqir.getItemName().trim());
			index++;
		}
		Response r = reqs.postSecuredRequest(AppConstants.POST_CREATE_QUOTATION, quotationRequest);
		if (r.getStatus() == 200) {
			/// UPLOAD // //
			CreateQuotationResponse qResponse =  r.readEntity(CreateQuotationResponse.class);
			lastOrderId = qResponse.getQuotationId();
			uploadVinImage(qResponse.getVehicleImageName());
			uploadItemsImages(qResponse);
			PrimeFaces.current().executeScript("showCompleteDialog()");
			monitorBean.addToActivity("successful order submitted: " + lastOrderId);
			init();
			PrimeFaces.current().executeScript("document.getElementById('form-1:vin-img').value = '';"); 		
			
		} else if (r.getStatus() == 429) {
			// log too many requests
		} else {
			// log exception
		}
	}

	private void uploadVinImage(String imageName){
		if(this.vinImageUploaded) {
			String fileName = imageName + ".png";
			quotationRequest.setImageString(vinBase64);
			AWSClient.uploadImage(quotationRequest.getImageString(), fileName, SysProps.getValue("quotationBucketName"));
			this.lastVinImage = true;
		}
	}

	private void uploadItemsImages(CreateQuotationResponse res){
		//there is no items image upload.
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
					monitorBean.addToActivity(
							"chose region " + selectedRegion.getCountryId() + " - " + selectedRegion.getName());
					break;
				}
			}
		} else {
			this.quotationRequest.setCityId(0);
		}
	}

	public void verifyItems() {
		if (quotationRequest.getQuotationItems().size() == 0) {
			monitorBean.addToActivity("clicked next but no items added");
			PrimeFaces.current().executeScript("resetActive(80, 'step-5');");
			PrimeFaces.current().ajax().update("form-1:item-msg");
			Helper.addErrorMessage(Bundler.getValue("addItemsReq"));
		} else {
			for (CreateQuotationItemRequest citem : quotationRequest.getQuotationItems()) {
				monitorBean.addToActivity("added item: " + citem.getItemName() + ", quantity : " + citem.getQuantity());
			}
			PrimeFaces.current().ajax().update("form-1:panel");
			PrimeFaces.current().executeScript("resetActive(100, 'step-6');");
			this.step = 5;
		}
	}

	public int getProgressPercentage() {
		switch (step) {
		case 0:
			return 0;
		case 1:
			return 20;
		case 2:
			return 40;
		case 3:
			return 60;
		case 4:
			return 80;
		case 5:
			return 100;
		default:
			return 0;
		}
	}

	public String getProgressStepName() {
		switch (step) {
		case 0:
			return "step-1";
		case 1:
			return "step-2";
		case 2:
			return "step-3";
		case 3:
			return "step-4";
		case 4:
			return "step-5";
		case 5:
			return "step-6";
		default:
			return "step-1";
		}
	}

	public void clickOrderButton(String button, int i) {
		monitorBean.addToActivity("clicked " + button);
		resetToStep(i);
	}

	public void resetToStep(int i) {
		switch (i) {
		case 0:
			monitorBean.addToActivity("clicked back from models tab");
			this.step = 0;
			this.selectedMake = null;
			this.selectedModel = null;
			this.selectedPublicVehicle = null;
			this.quotationRequest.setVehicleYearId(null);
			PrimeFaces.current().executeScript("resetActive(0, 'step-1');");
			break;
		case 1:
			monitorBean.addToActivity("clicked back from model years tab");
			this.step = 1;
			this.selectedModel = null;
			this.quotationRequest.setVehicleYearId(null);
			PrimeFaces.current().executeScript("resetActive(20, 'step-2');");
			break;
		case 2:
			monitorBean.addToActivity("clicked back from vin tab");
			this.step = 2;
			this.quotationRequest.setVehicleYearId(null);
			PrimeFaces.current().executeScript("resetActive(40, 'step-3');");
			break;
		case 3:
			monitorBean.addToActivity("clicked back from add items tab");
			this.step = 3;
			PrimeFaces.current().executeScript("resetActive(60, 'step-4');");
			break;
		case 4:
			monitorBean.addToActivity("clicked back from submit tab");
			this.step = 4;
			PrimeFaces.current().executeScript("resetActive(80, 'step-5');");
			break;
		case 5:
			this.step = 5;
			PrimeFaces.current().executeScript("resetActive(100, 'step-6');");
			break;
		}
	}

	public void choosePublicVehicle(PublicVehicle publicVehicle){
		this.selectedPublicVehicle = publicVehicle;
		this.selectedMake = publicVehicle.getVehicle().getMake();
		this.selectedModel = publicVehicle.getVehicle().getModel();
		PublicModelYear pmy = new PublicModelYear();
		pmy.setId(publicVehicle.getVehicle().getId());
		pmy.setMakeId(publicVehicle.getVehicle().getMake().getId());
		pmy.setModelId(publicVehicle.getVehicle().getModel().getId());
		pmy.setYear(publicVehicle.getVehicle().getYear());
		this.selectedModelYear = pmy;
		this.yearNumber = pmy.getYear();
		this.quotationRequest.setVehicleYearId(publicVehicle.getVehicle().getId());
		this.quotationRequest.setVin(publicVehicle.getVin());
		this.step = 4;
	}

	public void chooseMake(PublicMake selectedMake) {
		monitorBean.addToActivity("chose make: " + selectedMake.getName());
		this.selectedMake = selectedMake;
		this.step = 1;
	}

	public void chooseModel(PublicModel selectedType) {
		monitorBean.addToActivity("chose model: " + selectedType.getName());
		this.selectedModel = selectedType;
		this.step = 2;
	}

	public void chooseModelYear(PublicModelYear model) {
		monitorBean.addToActivity("chose model year: " + model.getYear());
		this.yearNumber = model.getYear();
		this.selectedModelYear = model;
		this.quotationRequest.setVehicleYearId(model.getId());
		this.step = 3;
	}

	public void verifyVin() {
		if(this.vinBase64.length() > 0) {
			this.vinImageUploaded = true;
		}
		if (!this.vinImageUploaded) {
			if (this.quotationRequest.getVin().length() == 17) {
				monitorBean.addToActivity("entered correct vin: " + quotationRequest.getVin());
				PrimeFaces.current().executeScript("resetActive(80, 'step-5');");
				this.step = 4;
				this.vinImageUploaded = false;
			} else {
				monitorBean.addToActivity("entered wrong vin: " + quotationRequest.getVin());
				PrimeFaces.current().executeScript("resetActive(60, 'step-4');");
				Helper.addErrorMessage(Bundler.getValue("invalidVin"), "form-1:vin");
			}
		}else {
			quotationRequest.setVin("");
			monitorBean.addToActivity("selected no vin ");
			PrimeFaces.current().executeScript("resetActive(80, 'step-5');");
			this.step = 4;
		}
	}
	
	public void resetVinImage() {
		monitorBean.addToActivity("deleted vin image");
		this.vinBase64 = "";
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public PublicMake getSelectedMake() {
		return selectedMake;
	}

	public void setSelectedMake(PublicMake selectedMake) {
		this.selectedMake = selectedMake;
	}

	public PublicModel getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(PublicModel selectedType) {
		this.selectedModel = selectedType;
	}

	public CreateQuotationRequest getQuotationRequest() {
		return quotationRequest;
	}

	public void setQuotationRequest(CreateQuotationRequest quotationRequest) {
		this.quotationRequest = quotationRequest;
	}

	public List<PublicMake> getMakes() {
		return makes;
	}

	public Long getLastOrderId() {
		return lastOrderId;
	}

	public boolean isAgree() {
		return agree;
	}

	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	public PublicRegion getSelectedRegion() {
		return selectedRegion;
	}

	public void setSelectedRegion(PublicRegion selectedRegion) {
		this.selectedRegion = selectedRegion;
	}

	public int getSelectedRegionId() {
		return selectedRegionId;
	}

	public void setSelectedRegionId(int selectedRegionId) {
		this.selectedRegionId = selectedRegionId;
	}

	public PublicModelYear getSelectedModelYear() {
		return selectedModelYear;
	}

	public void setSelectedModelYear(PublicModelYear selectedModelYear) {
		this.selectedModelYear = selectedModelYear;
	}

	public boolean isLastNoVin() {
		return lastNoVin;
	}

	public void setLastNoVin(boolean lastNoVin) {
		this.lastNoVin = lastNoVin;
	}

	public String getVinBase64() {
		return vinBase64;
	}

	public void setVinBase64(String vinBase64) {
		this.vinBase64 = vinBase64;
	}

	public boolean isLastVinImage() {
		return lastVinImage;
	}


	public PublicVehicle getSelectedPublicVehicle() {
		return selectedPublicVehicle;
	}

	public void setSelectedPublicVehicle(PublicVehicle selectedPublicVehicle) {
		this.selectedPublicVehicle = selectedPublicVehicle;
	}

	public long getSelectedPublicVehicleId() {
		return selectedPublicVehicleId;
	}

	public void setSelectedPublicVehicleId(long selectedPublicVehicleId) {
		this.selectedPublicVehicleId = selectedPublicVehicleId;
	}
}
