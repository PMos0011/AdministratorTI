package Editor;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class ControllerEditorWindow {

    @FXML
    private BorderPane editorWindow;
    @FXML
    private ImageView editedImage;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ColorPicker alphaColorPicker;
    @FXML
    private Slider opacitySlider;
    @FXML
    private Slider alphaToleranceSlider;
    @FXML
    private Label opacityValue;
    @FXML
    private Label alphaToleranceValue;
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
    @FXML
    private Button saveImage;
    @FXML
    private Button alphaCutButton;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private RadioButton cutAlphaRadioButton;

    public BorderPane getEditorWindow() {
        return editorWindow;
    }

    public ImageView getEditedImage() {
        return editedImage;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public ColorPicker getAlphaColorPicker() {
        return alphaColorPicker;
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

    public Button getSaveImage() {
        return saveImage;
    }

    public Button getAlphaCutButton() {
        return alphaCutButton;
    }

    public Slider getAlphaToleranceSlider() {
        return alphaToleranceSlider;
    }

    public Label getAlphaToleranceValue() {
        return alphaToleranceValue;
    }

    public MenuItem getUndoMenuItem() {
        return undoMenuItem;
    }

    public MenuItem getRedoMenuItem() {
        return redoMenuItem;
    }

    public boolean isReplaceCutColorOptionOn(){
        return !cutAlphaRadioButton.isSelected();
    }
}
