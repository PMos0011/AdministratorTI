package Editor;

import Common.Loader;
import MainWindow.Main;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Editor extends Application {

    public static Stage editorStage = new Stage();

    private final int X_MAX_DIMENSION = 705;
    private final int Y_MAX_DIMENSION = 500;

    private ControllerEditorWindow controllerEditorWindow;
    private Main main;

    private BorderPane editorWindow;
    private ImageView editedImage;
    private Canvas canvas;
    private ColorPicker colorPicker;
    private ColorPicker alphaColorPicker;
    private Slider opacitySlider;
    private Slider alphaToleranceSlider;
    private Label opacityLabel;
    private Label alphaToleranceLabel;
    private Button cropImageButton;
    private Button aspectCropImageButton;
    private Button clearSelectionButton;
    private Button saveImageButton;
    private Button alphaColorCutButton;
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

    public Editor(Image imageToEdit, Main main) {
        this.imageToEdit = imageToEdit;
        this.main = main;
    }

    @Override
    public void start(Stage stage) throws Exception {

        editorStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditorWindow.fxml"));
        Parent root = fxmlLoader.load();
        root.setOnKeyPressed(this::editorStageKeyPressedListener);
        root.setOnKeyReleased(this::editorStageKeyReleaseListener);

        editorStage.setTitle("Edycja slajdu");
        editorStage.setScene(new Scene(root, 1085, 600));

        if (editorStage.getModality() != Modality.APPLICATION_MODAL) {
            editorStage.initModality(Modality.APPLICATION_MODAL);
            editorStage.initStyle(StageStyle.UTILITY);
        }

        controllerEditorWindow = fxmlLoader.getController();

        editorWindow = controllerEditorWindow.getEditorWindow();
        editedImage = controllerEditorWindow.getEditedImage();
        editedImage.setImage(imageToEdit);
        canvas = controllerEditorWindow.getCanvas();
        basicCanvasMouseEventHandlers();

        colorPicker = controllerEditorWindow.getColorPicker();
        colorPicker.setOnAction(this::colorPick);
        colorPicker.setValue(Color.RED);

        alphaColorPicker = controllerEditorWindow.getAlphaColorPicker();

        opacitySlider = controllerEditorWindow.getOpacitySlider();
        opacityLabel = controllerEditorWindow.getOpacityValue();
        opacityLabel.textProperty().bind(Bindings.format("%.2f", opacitySlider.valueProperty()));

        alphaToleranceSlider = controllerEditorWindow.getAlphaToleranceSlider();
        alphaToleranceLabel = controllerEditorWindow.getAlphaToleranceValue();
        alphaToleranceLabel.textProperty().bind(Bindings.format("%.2f", alphaToleranceSlider.valueProperty()));

        cropImageButton = controllerEditorWindow.getCropImage();
        cropImageButton.setOnAction(e -> {
            cropImage(false);
        });
        aspectCropImageButton = controllerEditorWindow.getCropAspectImage();
        aspectCropImageButton.setOnAction(e -> {
            cropImage(true);
        });
        clearSelectionButton = controllerEditorWindow.getClearSelection();
        clearSelectionButton.setOnAction(this::clearSelection);

        saveImageButton = controllerEditorWindow.getSaveImage();
        saveImageButton.setOnAction(this::saveImage);

        alphaColorCutButton = controllerEditorWindow.getAlphaCutButton();
        alphaColorCutButton.setOnAction(this::cutAlphaColor);

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

        if (rectTopLeft.x + rectSize.x < X_MAX_DIMENSION && rectTopLeft.x + rectSize.x > 0)
            rectSize.x += xDifference;
        if (rectTopLeft.y + rectSize.y < Y_MAX_DIMENSION && rectTopLeft.y + rectSize.y > 0)
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
            if (rectTopLeft.x >= 0 && rectTopLeft.x + rectSize.x < X_MAX_DIMENSION)
                rectTopLeft.x += xDifference;
            else
                rectTopLeft.x = Math.min(rectTopLeft.x + xDifference, rectTopLeft.x);
        }
        if (rectSize.y > 0) {
            if (rectTopLeft.y >= 0 && rectTopLeft.y + rectSize.y < Y_MAX_DIMENSION)
                rectTopLeft.y += yDifference;
            else
                rectTopLeft.y = Math.min(rectTopLeft.y + xDifference, rectTopLeft.y);
        }

        if (rectTopLeft.x < 0)
            rectTopLeft.x = 0;
        if (rectTopLeft.y < 0)
            rectTopLeft.y = 0;

        if (rectTopLeft.x > X_MAX_DIMENSION)
            rectTopLeft.x = X_MAX_DIMENSION;

        if (rectTopLeft.y > Y_MAX_DIMENSION)
            rectTopLeft.y = Y_MAX_DIMENSION;

        if (rectTopLeft.x + rectSize.x > X_MAX_DIMENSION)
            rectSize.x = X_MAX_DIMENSION - rectTopLeft.x;

        if (rectTopLeft.y + rectSize.y > Y_MAX_DIMENSION)
            rectSize.y = Y_MAX_DIMENSION - rectTopLeft.y;
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

        if (imageTopLeft.x + aspectSize.x > X_MAX_DIMENSION) {
            aspectSize.x = X_MAX_DIMENSION - imageTopLeft.x;
            aspectSize.y = (int) (aspectSize.x / 1.415);
        }
        if (imageTopLeft.y + aspectSize.y > Y_MAX_DIMENSION) {
            aspectSize.y = Y_MAX_DIMENSION - imageTopLeft.y;
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
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        image = SwingFXUtils.toFXImage(loader.resizeImage(bufferedImage), null);
        editedImage.setImage(image);

        clearSelection(null);
    }

    private void saveImage(ActionEvent e) {
        main.currentSlideImageUpdate(editedImage.getImage());
        editorStage.close();
    }

    private void editorStageKeyPressedListener(KeyEvent e) {

        if (e.getCode() == KeyCode.SHIFT && editorWindow.getCursor() != Cursor.CROSSHAIR) {
            editorWindow.setCursor(Cursor.CROSSHAIR);
            alternativeCanvasMouseEventHandlers();
        }
    }

    private void editorStageKeyReleaseListener(KeyEvent e) {
        if (editorWindow.getCursor() == Cursor.CROSSHAIR) {
            editorWindow.setCursor(Cursor.DEFAULT);
            basicCanvasMouseEventHandlers();
        }
    }

    private void basicCanvasMouseEventHandlers() {
        canvas.setOnMousePressed(this::canvasMousePressed);
        canvas.setOnMouseDragged(this::canvasMouseMove);
        canvas.setOnMouseReleased(this::releaseMouseButton);
    }

    private void alternativeCanvasMouseEventHandlers() {
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
        canvas.setOnMouseReleased(this::canvasGetColour);
    }

    private void canvasGetColour(MouseEvent e) {
        Image image = editedImage.getImage();
        PixelReader pixelReader = image.getPixelReader();

        double pixelX = e.getX() * (image.getWidth() / X_MAX_DIMENSION);
        double pixelY = e.getY() * (image.getHeight() / Y_MAX_DIMENSION);

        alphaColorPicker.setValue(pixelReader.getColor((int) pixelX, (int) pixelY));
    }

    private void cutAlphaColor(ActionEvent e) {
        Image originalImage = editedImage.getImage();
        PixelReader pixelReader = originalImage.getPixelReader();
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int leftBorder = 0;
        int rightBorder = width;
        int topBorder = 0;
        int bottomBorder = height;

        if (isRectangle) {
            double imageSizeCorrectionX = (double) width / X_MAX_DIMENSION;
            double imageSizeCorrectionY = (double) height / Y_MAX_DIMENSION;

            leftBorder = (int) (imageTopLeft.x * imageSizeCorrectionX);
            topBorder = (int) (imageTopLeft.y * imageSizeCorrectionY);
            rightBorder = (int) (leftBorder + imageSize.x * imageSizeCorrectionX);
            bottomBorder = (int) (topBorder + imageSize.y * imageSizeCorrectionY);

        }

        double tolerance = alphaToleranceSlider.getValue();
        Color alphaColor = alphaColorPicker.getValue();
        for (int w = 0; w < width; w++)
            for (int h = 0; h < height; h++) {
                Color currentPixelColor = pixelReader.getColor(w, h);
                if (colorTolerance(currentPixelColor, alphaColor, tolerance)
                        && (w > leftBorder && w < rightBorder)
                        && (h > topBorder && h < bottomBorder))
                    pixelWriter.setColor(w, h, Color.WHITE);
                else
                    pixelWriter.setColor(w, h, currentPixelColor);
            }
        editedImage.setImage(writableImage);
    }

    private boolean colorTolerance(Color a, Color b, double tolerance) {
        return (colorTolerance(a.getRed(), b.getRed(), tolerance)
                && colorTolerance(a.getGreen(), b.getGreen(), tolerance)
                && colorTolerance(a.getBlue(), b.getBlue(), tolerance));
    }

    private boolean colorTolerance(double a, double b, double tolerance) {
        return Math.abs(a - b) < tolerance;
    }
}
