package q.app.qetaa.model.location;


import java.io.Serializable;
import java.util.List;

public class PublicCountry implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String nameAr;
	private double latitude;
	private double longitude;
	private int mapZoom;
	private String countryCode;
	private String mobileRegex;
	private List<PublicRegion> regions;
	
	
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
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getMobileRegex() {
		return mobileRegex;
	}
	public void setMobileRegex(String mobileRegex) {
		this.mobileRegex = mobileRegex;
	}
	public List<PublicRegion> getRegions() {
		return regions;
	}
	public void setRegions(List<PublicRegion> regions) {
		this.regions = regions;
	}
	public String getNameAr() {
		return nameAr;
	}
	public void setNameAr(String nameAr) {
		this.nameAr = nameAr;
	}

}
