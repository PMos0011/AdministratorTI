package Editor;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ControllerEditorWindow {

    @FXML
    private ImageView editedImage;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider opacitySlider;

    @FXML
    private Label opacityValue;

    @FXML
    private StackPane imagePane;

    @FXML
    private Canvas canvas;
    @FXML
    private Button cropImage;
    @FXML
    private Button cropAspectImage;

    @FXML
    private Button clearSelection;

    public ImageView getEditedImage() {
        return editedImage;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public Slider getOpacitySlider() {
        return opacitySlider;
    }

    public Label getOpacityValue() {
        return opacityValue;
    }

    public StackPane getImagePane() {
        return imagePane;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Button getCropImage() {
        return cropImage;
    }

    public Button getCropAspectImage() {
        return cropAspectImage;
    }

    public Button getClearSelection() {
        return clearSelection;
    }
}
