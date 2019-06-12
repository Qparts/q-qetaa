package q.app.qetaa.model.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


public class PublicModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int makeId;
    private String name;
    private String nameAr;
    private List<PublicModelYear> modelYears;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
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

    public List<PublicModelYear> getModelYears() {
        return modelYears;
    }

    public void setModelYears(List<PublicModelYear> modelYears) {
        this.modelYears = modelYears;
    }
}
