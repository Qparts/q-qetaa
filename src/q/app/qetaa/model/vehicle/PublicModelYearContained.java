package q.app.qetaa.model.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class PublicModelYearContained implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private PublicModel model;
    private PublicMake make;
    private int year;

    @JsonIgnore
    public String getFullName(){
        return make.getNameAr() + " - " + model.getNameAr() + " - " + year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PublicModel getModel() {
        return model;
    }

    public void setModel(PublicModel model) {
        this.model = model;
    }

    public PublicMake getMake() {
        return make;
    }

    public void setMake(PublicMake make) {
        this.make = make;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
