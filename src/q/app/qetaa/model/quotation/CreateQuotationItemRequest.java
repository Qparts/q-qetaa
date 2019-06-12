package q.app.qetaa.model.quotation;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CreateQuotationItemRequest {

    private boolean hasImage;
    private int quantity;
    private String itemName;
    private int tempId;
    @JsonIgnore
    private String imageString;
    @JsonIgnore
    private int[] quantityArray;


    @JsonIgnore
    public CreateQuotationItemRequest() {
        quantityArray = new int[20];
        for (int i = 0; i < quantityArray.length; i++) {
            quantityArray[i] = i + 1;
        }
    }

    @JsonIgnore
    public int[] getQuantityArray() {
        return quantityArray;
    }

    @JsonIgnore
    public void setQuantityArray(int[] quantityArray) {
        this.quantityArray = quantityArray;
    }



    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
