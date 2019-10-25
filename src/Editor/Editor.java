package Editor;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Editor extends Application {

    public static Stage editorStage = new Stage();

    private ControllerEditorWindow controllerMainWindow;

    Image imageToEdit;

    ImageView editedImage;
    ColorPicker colorPicker;
    Slider opacitySlider;
    Label opacityLabel;

    public Editor(Image imageToEdit){
        this.imageToEdit = imageToEdit;
    }
    @Override
    public void start(Stage stage) throws Exception {

        editorStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditorWindow.fxml"));
        Parent root = fxmlLoader.load();

        editorStage.setTitle("Edycja slajdu");
        editorStage.setScene(new Scene(root, 900, 600));

        if (editorStage.getModality() != Modality.APPLICATION_MODAL) {
            editorStage.initModality(Modality.APPLICATION_MODAL);
            editorStage.initStyle(StageStyle.UTILITY);
        }

        controllerMainWindow = fxmlLoader.getController();

        editorStage.show();

        editedImage = controllerMainWindow.getEditedImage();
        editedImage.setImage(imageToEdit);

        colorPicker = controllerMainWindow.getColorPicker();
        colorPicker.setOnAction(this::colorPick);
        colorPicker.setValue(Color.RED);

        opacitySlider = controllerMainWindow.getOpacitySlider();
        opacityLabel = controllerMainWindow.getOpacityValue();
        opacityLabel.textProperty().bind(Bindings.format("%.2f",opacitySlider.valueProperty()));


    }

    public static void main(String[] args) {
        launch(args);
    }

    private void colorPick(ActionEvent e) {
        System.out.println(colorPicker.getValue());

    }

}
