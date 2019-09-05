package Slide;

import javafx.scene.image.Image;

public class Slide {

    private Image category;
    private Image image;
    private String uID;
    private String header;
    private String firstDescription;
    private String secondDescription;
    private int viewPosition;

    public Slide() {
    }

    public Slide(Image category, Image image, String uID, String header, String firstDescription,String secondDescription, int viewPosition) {
        this.category = category;
        this.image = image;
        this.uID = uID;
        this.header = header;
        this.firstDescription = firstDescription;
        this.secondDescription=secondDescription;
        this.viewPosition = viewPosition;
    }

    public Image getCategory() {
        return category;
    }

    public void setCategory(Image category) {
        this.category = category;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFirstDescription() {
        return firstDescription;
    }

    public void setFirstDescription(String firstDescription) {
        this.firstDescription = firstDescription;
    }

    public String getSecondDescription() {
        return secondDescription;
    }

    public void setSecondDescription(String secondDescription) {
        this.secondDescription = secondDescription;
    }

    public int getViewPosition() {
        return viewPosition;
    }

    public void setViewPosition(int viewPosition) {
        this.viewPosition = viewPosition;
    }

}
