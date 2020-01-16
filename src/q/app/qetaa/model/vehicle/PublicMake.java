package q.app.qetaa.model.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicMake implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String nameAr;
    private List<PublicModel> models;

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

    public List<PublicModel> getModels() {
        return models;
    }

    public void setModels(List<PublicModel> models) {
        this.models = models;
    }
}
