package Editor;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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

    private final int X_DIM = 705;
    private final int Y_DIM = 500;

    private ControllerEditorWindow controllerEditorWindow;

    Image imageToEdit;

    ImageView editedImage;
    Canvas canvas;
    ColorPicker colorPicker;
    Slider opacitySlider;
    Label opacityLabel;
    Button clearSelectionButton;

    Point clickedPosition;
    Point aspectSize;
    Point rectSize;
    Point rectTopLeft;
    boolean isRectangle;
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

        controllerEditorWindow = fxmlLoader.getController();

        editedImage = controllerEditorWindow.getEditedImage();
        editedImage.setImage(imageToEdit);
        canvas = controllerEditorWindow.getCanvas();
        canvas.setOnMousePressed(this::drawElement);
        canvas.setOnMouseDragged(this::resizeElement);
        canvas.setOnMouseReleased(this::releseMouseButton);

        colorPicker = controllerEditorWindow.getColorPicker();
        colorPicker.setOnAction(this::colorPick);
        colorPicker.setValue(Color.RED);

        opacitySlider = controllerEditorWindow.getOpacitySlider();
        opacityLabel = controllerEditorWindow.getOpacityValue();
        opacityLabel.textProperty().bind(Bindings.format("%.2f", opacitySlider.valueProperty()));

        clearSelectionButton = controllerEditorWindow.getClearSelection();
        clearSelectionButton.setOnAction(this::clearSelection);

        aspectSize = new Point(1, 1);
        rectSize = new Point(1, 1);
        clickedPosition = new Point();
        isRectangle = false;

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

            clickedPosition.x = (int) e.getX();
            clickedPosition.y = (int) e.getY();

            if (!isRectangle)
                rectTopLeft = (Point) clickedPosition.clone();


        }
        drawRects();
    }

    private void drawRects() {

        int x, y, a, b;

        if (rectSize.x < 0) {
            x = rectTopLeft.x + rectSize.x;
            a = Math.abs(rectSize.x);
        } else {
            x = rectTopLeft.x;
            a = rectSize.x;
        }

        if (rectSize.y < 0) {
            y = rectTopLeft.y + rectSize.y;
            b = Math.abs(rectSize.y);
        } else {
            y = rectTopLeft.y;
            b = rectSize.y;
        }

        gc.clearRect(0, 0, 705, 500);
        gc.setFill(colorPicker.getValue());
        gc.setStroke(colorPicker.getValue());
        gc.setGlobalAlpha(opacitySlider.getValue());
        gc.fillRoundRect(x, y, a, b, 2, 2);
        gc.setGlobalAlpha(1);
        // gc.strokeRoundRect(rectTopLeft.x, rectTopLeft.y, aspectSize.x, aspectSize.y, 2, 2);
    }

    private void resizeElement(MouseEvent e) {
        int xDifference = (int) e.getX();
        int yDifference = (int) e.getY();

        if (e.isPrimaryButtonDown()) {

            xDifference -= clickedPosition.x;
            yDifference -= clickedPosition.y;

            clickedPosition.x = (int) e.getX();
            clickedPosition.y = (int) e.getY();

            if (!isRectangle) {
                if (rectTopLeft.x + rectSize.x < X_DIM && rectTopLeft.x + rectSize.x > 0)
                    rectSize.x += xDifference;
                if (rectTopLeft.y + rectSize.y < Y_DIM && rectTopLeft.y + rectSize.y > 0)
                    rectSize.y += yDifference;
            } else {
                if (rectTopLeft.x >= 0 && rectTopLeft.x + rectSize.x <= X_DIM)
                    rectTopLeft.x += xDifference;
                if (rectTopLeft.y >= 0 && rectTopLeft.y + rectSize.y <= Y_DIM)
                    rectTopLeft.y += yDifference;
            }

            if(rectTopLeft.x<0)
                rectTopLeft.x=0;
            if(rectTopLeft.y<0)
                rectTopLeft.y=0;

            if(rectTopLeft.x>X_DIM)
                rectTopLeft.x=X_DIM;

            if(rectTopLeft.y>Y_DIM)
                rectTopLeft.y=Y_DIM;










//            int startX;
//            int startY;
//            int width;
//            int height;
//
//
//            if (xDifference > 0) {
//                startX = clickedPosition.x;
//                width = xDifference;
//            } else {
//                if (clickedPosition.x + xDifference < 0)
//                    xDifference = -clickedPosition.x + 1;
//                startX = clickedPosition.x + xDifference;
//                width = Math.abs(xDifference);
//            }
//
//            if (yDifference > 0) {
//                startY = clickedPosition.y;
//                height = yDifference;
//            } else {
//                if (clickedPosition.y + yDifference < 0)
//                    yDifference = -clickedPosition.y;
//                startY = clickedPosition.y + yDifference;
//                height = Math.abs(yDifference);
//            }
//
//
//            if (width + startX > 705)
//                width = 705;
//            if (height + startY > 500)
//                height = 500;
//
//            if (width <= 0)
//                width = 1;
//
//            if (height <= 0)
//                height = 1;
//
//
//            double aspect = width / height;
//            int aspectWidth = width;
//            int aspectHeight = height;
//
//            if (aspect < 1.415)
//                aspectWidth = (int) (height * 1.415);
//            else
//                aspectHeight = (int) (width / 1.415);
//
//            if (aspectWidth + startX <= 705 && aspectHeight + startY < 500) {
//                aspectSize.x = aspectWidth;
//                aspectSize.y = aspectHeight;
//            }

            drawRects();
        }
    }

    private void clearSelection(ActionEvent e) {
        isRectangle = false;
        aspectSize.x = aspectSize.y = 1;
        rectSize.x = rectSize.y = 1;
        gc.clearRect(0, 0, 705, 500);
    }

    private void releseMouseButton(MouseEvent e) {

        if (!isRectangle) {
            isRectangle = true;
        }

    }

}
