package q.app.qetaa.model.location;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class PublicRegion implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String nameAr;
	private double latitude;
	private double longitude;
	private int mapZoom;
	private int countryId;
	private List<PublicCity> cities;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getMapZoom() {
		return mapZoom;
	}
	public void setMapZoom(int mapZoom) {
		this.mapZoom = mapZoom;
	}
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public List<PublicCity> getCities() {
		return cities;
	}
	public void setCities(List<PublicCity> cities) {
		this.cities = cities;
	}
	
	
}
