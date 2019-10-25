package Editor;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.ImageView;

public class ControllerEditorWindow {

    @FXML
    private ImageView editedImage;

    @FXML
    private ColorPicker colorPicker;

    public ImageView getEditedImage() {
        return editedImage;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }
}
