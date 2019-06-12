package q.app.qetaa.vendor;

import java.io.Serializable;

public class VendorJoinRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String companyName;
	private String workActivity;
	private String carModel;
	private String partsType;// A = Genuine, B = Non-Genuine, C = All
	private String mainBranchLocation;
	private String numberOfBranch;
	private boolean deliveryService;
	private String nameOfManger;
	private String phoneNumber;
	private String email;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getWorkActivity() {
		return workActivity;
	}

	public void setWorkActivity(String workActivity) {
		this.workActivity = workActivity;
	}

	public String getCarModel() {
		return carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getMainBranchLocation() {
		return mainBranchLocation;
	}

	public void setMainBranchLocation(String mainBranchLocation) {
		this.mainBranchLocation = mainBranchLocation;
	}

	public String getNumberOfBranch() {
		return numberOfBranch;
	}

	public void setNumberOfBranch(String numberOfBranch) {
		this.numberOfBranch = numberOfBranch;
	}

	public boolean isDeliveryService() {
		return deliveryService;
	}

	public void setDeliveryService(boolean deliveryService) {
		this.deliveryService = deliveryService;
	}

	public String getNameOfManger() {
		return nameOfManger;
	}

	public void setNameOfManger(String nameOfManger) {
		this.nameOfManger = nameOfManger;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPartsType() {
		return partsType;
	}

	public void setPartsType(String partsType) {
		this.partsType = partsType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

}
