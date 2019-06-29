package q.app.qetaa.model.customer;

import q.app.qetaa.model.vehicle.PublicModelYearContained;

import java.io.Serializable;


public class PublicVehicle implements Serializable {
	private long id;
	private Integer vehicleYearId;
	private long customerId;
	private boolean defaultVehicle;
	private String vin;
	private Boolean imageAttached;
	private PublicModelYearContained vehicle;


	public PublicVehicle() {
		
	}

	public Boolean getImageAttached() {
		return imageAttached;
	}

	public void setImageAttached(Boolean imageAttached) {
		this.imageAttached = imageAttached;
	}

	public PublicModelYearContained getVehicle() {
		return vehicle;
	}

	public void setVehicle(PublicModelYearContained vehicle) {
		this.vehicle = vehicle;
	}

	public boolean isDefaultVehicle() {
		return defaultVehicle;
	}

	public void setDefaultVehicle(boolean defaultVehicle) {
		this.defaultVehicle = defaultVehicle;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getVehicleYearId() {
		return vehicleYearId;
	}
	public void setVehicleYearId(Integer vehicleId) {
		this.vehicleYearId = vehicleId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	
	
}
