package Slide;

import javafx.scene.image.Image;

import java.util.Random;

public class Slide {

    private Image category;
    private Image image;
    private String fileName;
    private String header;
    private String firstDescription;
    private String secondDescription;
    private String categoryPath;

    public Slide() {
    }

    public Slide(Image category, Image image, String fileName, String categoryPath, String header, String firstDescription, String secondDescription) {
        this.category = category;
        this.image = image;
        this.fileName = fileName;
        this.header = header;
        this.categoryPath = categoryPath;
        this.firstDescription = firstDescription;
        this.secondDescription = secondDescription;
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

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public static String generateName(int charCount) {
        Random random = new Random();

        StringBuilder name = new StringBuilder();

        for (int i = 0; i < charCount; i++) {
            char asciCode = (char) (random.nextInt(25) + 97);
            name.append(asciCode);
        }
        return name.toString();
    }
}
