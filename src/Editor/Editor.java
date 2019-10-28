package Editor;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;


public class Editor extends Application {

    public static Stage editorStage = new Stage();

    private ControllerEditorWindow controllerMainWindow;

    Image imageToEdit;

    ImageView editedImage;
    Canvas canvas;
    ColorPicker colorPicker;
    Slider opacitySlider;
    Label opacityLabel;

    Point clickedPosition = null;
    GraphicsContext gc;

    public Editor(Image imageToEdit) {
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

        editedImage = controllerMainWindow.getEditedImage();
        editedImage.setImage(imageToEdit);
        canvas = controllerMainWindow.getCanvas();
        canvas.setOnMousePressed(this::drawElement);
        canvas.setOnMouseDragged(this::resizeElement);
        canvas.setOnMouseReleased(this::clearPoint);

        colorPicker = controllerMainWindow.getColorPicker();
        colorPicker.setOnAction(this::colorPick);
        colorPicker.setValue(Color.RED);

        opacitySlider = controllerMainWindow.getOpacitySlider();
        opacityLabel = controllerMainWindow.getOpacityValue();
        opacityLabel.textProperty().bind(Bindings.format("%.2f", opacitySlider.valueProperty()));

        editorStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void colorPick(ActionEvent e) {
        System.out.println(colorPicker.getValue());

    }

    private void drawElement(MouseEvent e) {

        if (e.isPrimaryButtonDown()) {
            if (gc == null)
                gc = canvas.getGraphicsContext2D();

            int x = (int) e.getX();
            int y = (int) e.getY();

            if (clickedPosition == null) {
                clickedPosition = new Point(x, y);
            }

            gc.clearRect(0, 0, 705, 500);
            gc.setFill(colorPicker.getValue());
            gc.setGlobalAlpha(opacitySlider.getValue());
            gc.fillRoundRect(clickedPosition.x, clickedPosition.y, 1, 1, 5, 5);
        }
    }

    private void resizeElement(MouseEvent e) {

        if (e.isPrimaryButtonDown()) {
            int x = (int) e.getX();
            int y = (int) e.getY();

            x -= clickedPosition.x;
            y -= clickedPosition.y;

            int startX;
            int startY;
            int width;
            int height;

            if (x > 0) {
                startX = clickedPosition.x;
                width = x;
            } else {
                startX = clickedPosition.x + x;
                width = Math.abs(x);
            }

            if (y > 0) {
                startY = clickedPosition.y;
                height = y;
            } else {
                startY = clickedPosition.y + y;
                height = Math.abs(y);
            }

            gc.clearRect(0, 0, 705, 500);
            gc.fillRoundRect(startX, startY, width, height, 5, 5);
        }
    }

    private void clearPoint(MouseEvent e) {
        clickedPosition = null;
    }

}
