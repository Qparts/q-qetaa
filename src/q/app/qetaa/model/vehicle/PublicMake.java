package q.app.qetaa.model.vehicle;

import java.io.Serializable;
import java.util.List;

public class PublicMake implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String nameAr;
    private List<PublicModel> models;
    private String imageLarge;
    private String imageSmall;

    public String getImageLarge() {
        return imageLarge;
    }

    public void setImageLarge(String imageLarge) {
        this.imageLarge = imageLarge;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

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
