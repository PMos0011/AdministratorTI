package Editor;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

public class ControllerEditorWindow {

    @FXML
    private ImageView editedImage;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider opacitySlider;

    @FXML
    private Label opacityValue;

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
}
