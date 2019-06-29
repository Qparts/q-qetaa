package q.app.qetaa.beans;

import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.helpers.PojoRequester;
import q.app.qetaa.model.location.PublicCity;
import q.app.qetaa.model.location.PublicCountry;
import q.app.qetaa.model.location.PublicRegion;
import q.app.qetaa.reqs.NotLoggedRequester;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class CountryBean implements Serializable {

    private List<PublicCountry> countries;
    private List<PublicCity> cities;
    private List<PublicRegion> regions;

    @Inject
    private NotLoggedRequester reqs;

    @Inject
    private LoginBean loginBean;


    @PostConstruct
    private void init(){
        initCountries();
        initRegions();
        initCities();
    }

    private void initCountries(){
        String header = reqs.getSecurityHeader();
        Response r = PojoRequester.getSecuredRequest(AppConstants.GET_ACTIVE_COUNTRIES, header);
        System.out.println(r.getStatus());
        if (r.getStatus() == 200) {
            countries = r.readEntity(new GenericType<List<PublicCountry>>() {
            });
        } else {
            countries = new ArrayList<>();
        }
    }

    private void initRegions(){
        regions = new ArrayList<>();
        for(var country : countries){
            regions.addAll(country.getRegions());
        }
    }


    private void initCities(){
        cities  = new ArrayList<>();
        for(var region : regions){
            cities.addAll(region.getCities());
        }
    }




    public PublicCountry getCountryFromId(int id){
        for(PublicCountry country : countries){
            if(id == country.getId()){
                return country;
            }
        }
        return null;
    }


    public PublicCity getCityFromId(int id){
        for(var city : cities){
            if(id == city.getId()){
                return city;
            }
        }
        return null;
    }



    public PublicRegion getRegionFromId(int id){
        for(var region : regions){
            if(id == region.getId()){
                return region;
            }
        }
        return null;
    }




    public List<PublicCountry> getCountries() {
        return countries;
    }

    public List<PublicCity> getCities() {
        return cities;
    }


    public List<PublicRegion> getRegions() {
        return regions;
    }

}
