package Editor;

import Common.Loader;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Editor extends Application {

    public static Stage editorStage = new Stage();

    private final int X_MAX_DIMMENSION = 705;
    private final int Y_MAX_DIMMENSION = 500;

    private ControllerEditorWindow controllerEditorWindow;

    private ImageView editedImage;
    private Canvas canvas;
    private ColorPicker colorPicker;
    private Slider opacitySlider;
    private Label opacityLabel;
    private Button cropImageButton;
    private Button aspectCropImageButton;
    private Button clearSelectionButton;
    private Image imageToEdit;

    private int xDifference;
    private int yDifference;
    private Point clickedPosition;
    private Point aspectSize;
    private Point rectSize;
    private Point rectTopLeft;
    private Point imageTopLeft;
    private Point imageSize;
    private boolean isRectangle;

    private GraphicsContext gc;

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
        canvas.setOnMousePressed(this::canvasMousePressed);
        canvas.setOnMouseDragged(this::canvasMouseMove);
        canvas.setOnMouseReleased(this::releaseMouseButton);

        colorPicker = controllerEditorWindow.getColorPicker();
        colorPicker.setOnAction(this::colorPick);
        colorPicker.setValue(Color.RED);

        opacitySlider = controllerEditorWindow.getOpacitySlider();
        opacityLabel = controllerEditorWindow.getOpacityValue();
        opacityLabel.textProperty().bind(Bindings.format("%.2f", opacitySlider.valueProperty()));

        cropImageButton = controllerEditorWindow.getCropImage();
        cropImageButton.setOnAction(e -> {
            cropImage( false);
        });
        aspectCropImageButton = controllerEditorWindow.getCropAspectImage();
        aspectCropImageButton.setOnAction(e -> {
            cropImage( true);
        });
        clearSelectionButton = controllerEditorWindow.getClearSelection();
        clearSelectionButton.setOnAction(this::clearSelection);

        aspectSize = new Point(1, 1);
        rectSize = new Point(1, 1);
        clickedPosition = new Point();
        imageTopLeft = new Point();
        imageSize = new Point();
        isRectangle = false;

        editorStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void colorPick(ActionEvent e) {
        System.out.println(colorPicker.getValue());
    }

    private void canvasMousePressed(MouseEvent e) {

        clickedPosition.x = (int) e.getX();
        clickedPosition.y = (int) e.getY();

        if (e.isPrimaryButtonDown()) {
            if (gc == null)
                gc = canvas.getGraphicsContext2D();

            if (!isRectangle)
                rectTopLeft = (Point) clickedPosition.clone();
        }
        drawRects();
    }

    private void canvasMouseMove(MouseEvent e) {
        if (e.isPrimaryButtonDown()) {
            if (!isRectangle)
                drawRect(e);
            else
                moveRect(e);
        } else if (e.isSecondaryButtonDown())
            if (isRectangle)
                drawRect(e);

        calculateAspectRect();
        drawRects();
    }

    private void recapClickedPosition(MouseEvent e) {
        xDifference = (int) e.getX();
        yDifference = (int) e.getY();

        xDifference -= clickedPosition.x;
        yDifference -= clickedPosition.y;

        clickedPosition.x = (int) e.getX();
        clickedPosition.y = (int) e.getY();
    }

    private void drawRect(MouseEvent e) {
        recapClickedPosition(e);

        if (rectTopLeft.x + rectSize.x < X_MAX_DIMMENSION && rectTopLeft.x + rectSize.x > 0)
            rectSize.x += xDifference;
        if (rectTopLeft.y + rectSize.y < Y_MAX_DIMMENSION && rectTopLeft.y + rectSize.y > 0)
            rectSize.y += yDifference;

        if (rectSize.x == 0)
            rectSize.x = 1;
        if (rectSize.y == 0)
            rectSize.y = 1;
    }

    private void moveRect(MouseEvent e) {
        recapClickedPosition(e);

        if (rectSize.x < 0) {
            if (rectTopLeft.x + rectSize.x > 0)
                rectTopLeft.x += xDifference;
            else
                rectTopLeft.x = Math.max(rectTopLeft.x + xDifference, rectTopLeft.x);
        }
        if (rectSize.y < 0) {
            if (rectTopLeft.y + rectSize.y > 0)
                rectTopLeft.y += yDifference;
            else
                rectTopLeft.y = Math.max(rectTopLeft.y + yDifference, rectTopLeft.y);
        }
        if (rectSize.x > 0) {
            if (rectTopLeft.x >= 0 && rectTopLeft.x + rectSize.x < X_MAX_DIMMENSION)
                rectTopLeft.x += xDifference;
            else
                rectTopLeft.x = Math.min(rectTopLeft.x + xDifference, rectTopLeft.x);
        }
        if (rectSize.y > 0) {
            if (rectTopLeft.y >= 0 && rectTopLeft.y + rectSize.y < Y_MAX_DIMMENSION)
                rectTopLeft.y += yDifference;
            else
                rectTopLeft.y = Math.min(rectTopLeft.y + xDifference, rectTopLeft.y);
        }

        if (rectTopLeft.x < 0)
            rectTopLeft.x = 0;
        if (rectTopLeft.y < 0)
            rectTopLeft.y = 0;

        if (rectTopLeft.x > X_MAX_DIMMENSION)
            rectTopLeft.x = X_MAX_DIMMENSION;

        if (rectTopLeft.y > Y_MAX_DIMMENSION)
            rectTopLeft.y = Y_MAX_DIMMENSION;

        if (rectTopLeft.x + rectSize.x > X_MAX_DIMMENSION)
            rectSize.x = X_MAX_DIMMENSION - rectTopLeft.x;

        if (rectTopLeft.y + rectSize.y > Y_MAX_DIMMENSION)
            rectSize.y = Y_MAX_DIMMENSION - rectTopLeft.y;
    }

    private void calculateAspectRect() {

        aspectSize.x = Math.abs(rectSize.x);
        aspectSize.y = Math.abs(rectSize.y);

        int aspect = aspectSize.x / aspectSize.y;

        if (aspect < 1.415)
            aspectSize.x = (int) (aspectSize.y * 1.415);
        else
            aspectSize.y = (int) (aspectSize.x / 1.415);

        imageTopLeft = (Point) rectTopLeft.clone();

        if (rectSize.x < 0)
            imageTopLeft.x = rectTopLeft.x + rectSize.x;
        if (rectSize.y < 0)
            imageTopLeft.y = rectTopLeft.y + rectSize.y;

        if (imageTopLeft.x + aspectSize.x > X_MAX_DIMMENSION) {
            aspectSize.x = X_MAX_DIMMENSION - imageTopLeft.x;
            aspectSize.y = (int) (aspectSize.x / 1.415);
        }
        if (imageTopLeft.y + aspectSize.y > Y_MAX_DIMMENSION) {
            aspectSize.y = Y_MAX_DIMMENSION - imageTopLeft.y;
            aspectSize.x = (int) (aspectSize.y * 1.415);
        }
    }

    private void drawRects() {

        if (imageTopLeft.equals(rectTopLeft))
            imageSize = (Point) rectSize.clone();
        else {
            imageSize.x = Math.abs(rectSize.x);
            imageSize.y = Math.abs(rectSize.y);
        }

        gc.clearRect(0, 0, 705, 500);
        gc.setFill(colorPicker.getValue());
        gc.setStroke(colorPicker.getValue());
        gc.setGlobalAlpha(opacitySlider.getValue());
        gc.fillRoundRect(imageTopLeft.x, imageTopLeft.y, imageSize.x, imageSize.y, 2, 2);
        gc.setGlobalAlpha(1);
        gc.strokeRoundRect(imageTopLeft.x, imageTopLeft.y, aspectSize.x, aspectSize.y, 2, 2);
    }

    private void clearSelection(ActionEvent e) {
        isRectangle = false;
        aspectSize.x = aspectSize.y = 1;
        rectSize.x = rectSize.y = 1;
        gc.clearRect(0, 0, 705, 500);
    }

    private void releaseMouseButton(MouseEvent e) {

        if (!isRectangle) {
            isRectangle = true;
        }
    }

    private void cropImage(boolean isScaled) {
        Loader loader = new Loader();

        SnapshotParameters parameters = new SnapshotParameters();
        Rectangle2D rect;

        if (isScaled)
            rect = new Rectangle2D(imageTopLeft.x, imageTopLeft.y, aspectSize.x, aspectSize.y);
        else
            rect = new Rectangle2D(imageTopLeft.x, imageTopLeft.y, imageSize.x, imageSize.y);

        parameters.setFill(Color.WHITE);
        parameters.setViewport(rect);

        WritableImage image = new WritableImage((int) rect.getWidth(), (int) rect.getHeight());
        editedImage.snapshot(parameters, image);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image,null);
        image=SwingFXUtils.toFXImage(loader.resizeImage(bufferedImage),null);
        editedImage.setImage(image);

        clearSelection(null);
    }
}
