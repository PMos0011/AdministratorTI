package MainWindow;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;


public class ControllerMainWindow {

    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField headerText;
    @FXML
    private TextField firstLineText;
    @FXML
    private TextField secondLineText;
    @FXML
    private ImageView mainImage;
    @FXML
    private ImageView categoryImage;
    @FXML
    private ListView slideList;
    @FXML
    private MenuItem saveMenuItem;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public ImageView getMainImage() {
        return mainImage;
    }

    public ListView getSlideList() {
        return slideList;
    }

    public TextField getHeaderText() {
        return headerText;
    }

    public TextField getFirstLineText() {
        return firstLineText;
    }

    public TextField getSecondLineText() {
        return secondLineText;
    }

    public ImageView getCategoryImage() {
        return categoryImage;
    }

    public MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    public void setMainImage(Image image) {
        mainImage.setImage(image);
    }

    public void setCategoryImage(Image image) {
        categoryImage.setImage(image);
    }

    public void setHeader(String text) {
        headerText.setText(text);
    }

    public void setFirstLine(String text) {
        firstLineText.setText(text);
    }

    public void setSecondLineText(String text) {
        secondLineText.setText(text);
    }

    public void addSlideToList(ObservableList<String> list, int selectedItem) {
        slideList.getItems().clear();
        slideList.getItems().addAll(list);
        slideList.getSelectionModel().select(selectedItem);
    }

}
