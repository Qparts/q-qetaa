package q.app.qetaa.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.primefaces.PrimeFaces;
import q.app.qetaa.helpers.*;
import q.app.qetaa.model.cart.ThreeDConfirmRequest;
import q.app.qetaa.model.customer.PublicVehicle;
import q.app.qetaa.model.location.PublicCountry;
import q.app.qetaa.model.location.PublicRegion;
import q.app.qetaa.model.quotation.CreateQuotationItemRequest;
import q.app.qetaa.model.quotation.CreateQuotationRequest;
import q.app.qetaa.model.quotation.CreateQuotationResponse;
import q.app.qetaa.model.quotation.CardHolder;
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
    private char paymentMethod;
    private CardHolder cardHolder;
    private CreateQuotationResponse createQuotationResponse;
    private boolean paymentFailed;

	@Inject
	private NotLoggedRequester reqsNotLogged;
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


	public void init() {
	    this.vinImageUploaded = false;
		this.vinBase64 = "";
		this.quotationRequest = new CreateQuotationRequest();
		this.quotationRequest.setQuotationItems(new ArrayList<>());
		this.step = 0;
		addItem();
		this.selectedMake = null;
		this.selectedModel = null;
		this.selectedModelYear = null;
		this.selectedPublicVehicle = null;
		this.cardHolder = new CardHolder();
        this.createQuotationResponse = new CreateQuotationResponse();
        this.paymentMethod = 'N';

        this.selectedRegion = null;
        this.selectedRegionId = 0;
        PrimeFaces.current().executeScript("document.getElementById('form-1:vin-img').value = '';");
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
		case 6:
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
		addItem();
	}

	public void removeItem(CreateQuotationItemRequest item) {
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
		quotationRequest.setPaymentMethod(paymentMethod);
		quotationRequest.setCardHolder(cardHolder);
		System.out.println("payment method = " + paymentMethod);
		if(paymentMethod == 'W'){
			Response r = reqs.postSecuredRequest(AppConstants.POST_CREATE_QUOTATION_WIRE, quotationRequest);
			if(r.getStatus() == 200){
				createQuotationResponse =  r.readEntity(CreateQuotationResponse.class);
				successfulQuotationPayment();
			}
			else{
				Helper.addErrorMessage("An error occured");
			}
		}
		else if(paymentMethod == 'M' || paymentMethod == 'V'){
			Response r = reqs.postSecuredRequest(AppConstants.POST_CREATE_QUOTATION_CC, quotationRequest);
			System.out.println("response from server "+ r.getStatus());
			if (r.getStatus() == 200) {
				createQuotationResponse =  r.readEntity(CreateQuotationResponse.class);
				successfulQuotationPayment();
			}
			if(r.getStatus() == 202){
				createQuotationResponse =  r.readEntity(CreateQuotationResponse.class);
				lastOrderId = createQuotationResponse.getQuotationId();
				uploadVinImage(createQuotationResponse.getVehicleImageName());
				uploadItemsImages(createQuotationResponse);
				Helper.redirect(createQuotationResponse.getTransactionUrl());
			}
		}
	}

	private void successfulQuotationPayment(){
		/// UPLOAD // //
		lastOrderId = createQuotationResponse.getQuotationId();
		uploadVinImage(createQuotationResponse.getVehicleImageName());
		uploadItemsImages(createQuotationResponse);
		paymentFailed = false;
		Helper.redirect("quotation-created");
	}

	private void uploadVinImage(String imageName){
		if(this.vinImageUploaded) {
			quotationRequest.setImageString(vinBase64);
			AWSClient.uploadImage(quotationRequest.getImageString(), imageName, SysProps.getValue("quotationBucketName"));
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
					break;
				}
			}
		} else {
			this.quotationRequest.setCityId(0);
		}
	}

	public void verifyItems() {
		if (quotationRequest.getQuotationItems().size() == 0) {
			PrimeFaces.current().executeScript("resetActive(67, 'step-5');");
			PrimeFaces.current().ajax().update("form-1:item-msg");
			Helper.addErrorMessage(Bundler.getValue("addItemsReq"));
		} else {
			PrimeFaces.current().ajax().update("form-1:panel");
			PrimeFaces.current().executeScript("resetActive(83, 'step-6');");
			this.step = 5;
		}
	}

	public int getProgressPercentage() {
		switch (step) {
		case 0:
			return 0;
		case 1:
			return 17;
		case 2:
			return 33;
		case 3:
			return 50;
		case 4:
			return 67;
		case 5:
			return 83;
		case 6:
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
		case 6:
			return "step-7";
		case 7:
		    return "step-8";
		default:
			return "step-1";
		}
	}

	public void resetToStep(int i) {
		switch (i) {
		case 0:
			this.step = 0;
			this.selectedMake = null;
			this.selectedModel = null;
			this.selectedPublicVehicle = null;
			this.quotationRequest.setVehicleYearId(null);
			PrimeFaces.current().executeScript("resetActive(0, 'step-1');");
			break;
		case 1:
			this.step = 1;
			this.selectedModel = null;
			this.quotationRequest.setVehicleYearId(null);
			PrimeFaces.current().executeScript("resetActive(17, 'step-2');");
			break;
		case 2:
			this.step = 2;
			this.quotationRequest.setVehicleYearId(null);
			PrimeFaces.current().executeScript("resetActive(33, 'step-3');");
			break;
		case 3:
			this.step = 3;
			PrimeFaces.current().executeScript("resetActive(50, 'step-4');");
			break;
		case 4:
			this.step = 4;
			PrimeFaces.current().executeScript("resetActive(67, 'step-5');");
			break;
		case 5:
			this.step = 5;
			PrimeFaces.current().executeScript("resetActive(83, 'step-6');");
			break;
		case 6:
			this.step = 6;
			PrimeFaces.current().executeScript("resetActive(100, 'step-7');");
			break;
		case 7:
		    this.step = 7;
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
		this.selectedMake = selectedMake;
		this.step = 1;
	}

	public void chooseModel(PublicModel selectedType) {
		this.selectedModel = selectedType;
		this.step = 2;
	}

	public void chooseModelYear(PublicModelYear model) {
		this.yearNumber = model.getYear();
		this.selectedModelYear = model;
		this.quotationRequest.setVehicleYearId(model.getId());
		this.step = 3;
	}

	public void nextPayment(){
	    if(paymentMethod == 'N'){
            Helper.addErrorMessage("some error");
        }else {
            List<String> errors = cardHolder.verifyPayment(paymentMethod);
            if (errors.isEmpty()) {
                if (this.paymentMethod == 'V' || this.paymentMethod == 'M') {
                    //makeCreditCardRequest();
                } else if (this.paymentMethod == 'W') {
                    //makeWireTransferRequest();
                }
                resetToStep(6);
            } else {
                for (String error : errors) {
                    //resetToStep(6);
                    Helper.addErrorMessage(error);
                }
            }
        }
    }




    public void processPaymentResponse() {
        try {
            String paymentId = Helper.getParam("id");// some string
            String status = Helper.getParam("status");// paid , failed
            ThreeDConfirmRequest threeD = new ThreeDConfirmRequest();
            threeD.setQuotationId(createQuotationResponse.getQuotationId());
            threeD.setCustomerId(quotationRequest.getCustomerId());
            threeD.setType('Q');
            threeD.setId(paymentId);
            threeD.setStatus(status);
            Response r = reqs.putSecuredRequest(AppConstants.PUT_3D_SECURE_RESPONSE, threeD);
            if (r.getStatus() == 201) {
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("quotationId", createQuotationResponse.getQuotationId());
                map.put("paymentStatus", status);
                Response r2 = reqs.putSecuredRequest(AppConstants.PUT_QUOTATION_PAYMENT, map);
                if (status.equals("paid")) {
                    paymentFailed = false;
                    resetToStep(0);
                    Helper.redirect("quotation-created");
                    //update
                } else if (status.equals("failed")) {
                    paymentFailed = true;
                    resetToStep(5);
                    Helper.redirect("create-quotation");
                }

            } else if(r.getStatus() == 406){
                Helper.addErrorMessage("An error in updating! please contact customer service");
            }

        } catch (Exception ex) {

        }
    }



	public void verifyVin() {
		if(this.vinBase64.length() > 0) {
			this.vinImageUploaded = true;
		}
		if (!this.vinImageUploaded) {
			if (this.quotationRequest.getVin().length() == 17) {
				PrimeFaces.current().executeScript("resetActive(67, 'step-5');");
				this.step = 4;
				this.vinImageUploaded = false;
			} else {
				PrimeFaces.current().executeScript("resetActive(50, 'step-4');");
				Helper.addErrorMessage(Bundler.getValue("invalidVin"), "form-1:vin");
			}
		}else {
			quotationRequest.setVin("");
			PrimeFaces.current().executeScript("resetActive(67, 'step-5');");
			this.step = 4;
		}
	}

    public String getPaymentMethodString(){
	    switch (paymentMethod){
            case 'M':
                return "مدى";
            case 'V':
                return "بطاقة إئتمانية";
            case 'W':
                return "تحويل بنكي";
            default:
                return "";
        }
    }


    public void resetVinImage() {
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

    public char getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(char paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public CardHolder getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }

    public boolean isPaymentFailed() {
        return paymentFailed;
    }

    public void setPaymentFailed(boolean paymentFailed) {
        this.paymentFailed = paymentFailed;
    }
}
