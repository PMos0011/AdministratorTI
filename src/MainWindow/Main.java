package MainWindow;

import CategoryPicker.CategoryPicker;
import Common.*;
import Slide.Slide;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static String DEFAULT_CATEGORY_PATH = "src/images/dok.png";

    private Stage primaryStage;
    private Loader loader = new Loader();
    private CategoryPicker picker = new CategoryPicker(loader, this);
    private ControllerMainWindow controllerMainWindow;
    private Slide initSlide;
    private ListView slideList;
    private TextField headerField;
    private TextField firsField;
    private TextField secondField;
    private Slide currentSlide = new Slide();
    private List<Slide> slides = new ArrayList<Slide>();
    private ObservableList<String> listFileNames = FXCollections.observableArrayList();
    private List<String> slideHeaders = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) throws Exception {
        initSlide = loader.loadInitialSlide();

        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        controllerMainWindow = fxmlLoader.getController();

        primaryStage.setTitle("Administrator treści Tablica Informacyjna");
        primaryStage.setScene(new Scene(root, 1050, 720));
        primaryStage.show();

        viewUpdate(initSlide);

        ImageView mainImage = controllerMainWindow.getMainImage();
        mainImage.setOnDragOver(this::dragDetect);
        mainImage.setOnDragDropped(this::dropDetect);

        ImageView categoryImage = controllerMainWindow.getCategoryImage();
        categoryImage.setOnMouseClicked(this::showCategoryWindow);

        slideList = controllerMainWindow.getSlideList();
        slideList.setCellFactory(param -> new CellsFactory(slideList, listFileNames, slideHeaders, controllerMainWindow, this));
        slideList.setOnMousePressed(this::listClicked);

        headerField = controllerMainWindow.getHeaderText();
        firsField = controllerMainWindow.getFirstLineText();
        secondField = controllerMainWindow.getSecondLineText();

        headerField.setOnKeyReleased(this::updateHeader);
        firsField.setOnKeyReleased(this::updateFirsLine);
        secondField.setOnKeyReleased(this::updateSecondLine);

        MenuItem saveAction = controllerMainWindow.getSaveMenuItem();
        saveAction.setOnAction(this::saveFiles);
        MenuItem openAction = controllerMainWindow.getOpenMenuItem();
        openAction.setOnAction(this::openFile);

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void updateHeader(KeyEvent e) {
        int slideID = slideList.getSelectionModel().getSelectedIndex();
        int caretPosition = headerField.getCaretPosition();
        String slideName = headerField.getText().toUpperCase();

        slideHeaders.remove(slideID);
        currentSlide.setHeader(slideName);
        headerField.setText(slideName);
        headerField.positionCaret(caretPosition);

        slideHeaders.add(slideID, currentSlide.getHeader());
        controllerMainWindow.addSlideToList(slideHeaders, slideID);
    }

    private void updateFirsLine(KeyEvent e) {
        currentSlide.setFirstDescription(firsField.getText());
    }

    private void updateSecondLine(KeyEvent e) {
        currentSlide.setSecondDescription(secondField.getText());
    }

    public void deleteSlide() {
        slides.remove(currentSlide);
        if (slides.size() > 0)
            currentSlide = slides.get(0);
        else
            currentSlide = initSlide;

        controllerMainWindow.addSlideToList(slideHeaders, 0);
        viewUpdate(currentSlide);
    }

    public void setCategory(String categoryPath) {
        currentSlide.setCategory(loader.imageLoad(categoryPath));
        currentSlide.setCategoryPath(categoryPath);
        viewUpdate(currentSlide);
    }

    private void dragDetect(DragEvent e) {

        e.acceptTransferModes(TransferMode.ANY);
        e.consume();
    }

    private void dropDetect(DragEvent e) {

        Dragboard dragboard = e.getDragboard();

        List<File> files = new ArrayList<>();

        if (dragboard.hasFiles()) {
            files = dragboard.getFiles();
        }

        for (File file : files) {
            if (fileFilter(file)) {
                String header = crateSlideName(file);
                Slide tempSlide = setImage(file);
                tempSlide.setCategory(loader.imageLoad(DEFAULT_CATEGORY_PATH));
                tempSlide.setCategoryPath(DEFAULT_CATEGORY_PATH);
                tempSlide.setHeader(header);
                tempSlide.setFileName(Slide.generateName(16));
                slides.add(tempSlide);

                viewUpdate(tempSlide);

                listFileNames.add(tempSlide.getFileName());
                slideHeaders.add(tempSlide.getHeader());
                controllerMainWindow.addSlideToList(slideHeaders, listFileNames.size() - 1);
                currentSlide = tempSlide;
            } else {
                new Alert(Alert.AlertType.WARNING, "Nieobsługiwany plik: " + file.getName()).show();
            }
        }
        e.consume();
    }

    private Slide setImage(File file) {
        Slide slide = new Slide();

        if (file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".jpeg") ||
                file.getName().toLowerCase().endsWith(".jpg"))
            slide.setImage(loader.imageLoad(file));
        else if (file.getName().toLowerCase().endsWith(".pdf"))
            slide.setImage(loader.pdfLoad(file));
        else if (file.getName().toLowerCase().endsWith(".tif"))
            slide.setImage(loader.tiffLoad(file));
        return slide;
    }

    private boolean fileFilter(File file) {
        return file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".jpeg") ||
                file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".pdf") ||
                file.getName().toLowerCase().endsWith(".tif");
    }

    private void viewUpdate(Slide slide) {
        controllerMainWindow.setMainImage(slide.getImage());
        controllerMainWindow.setCategoryImage(slide.getCategory());
        controllerMainWindow.setHeader(slide.getHeader());
        controllerMainWindow.setFirstLine(slide.getFirstDescription());
        controllerMainWindow.setSecondLineText(slide.getSecondDescription());
    }

    private void listClicked(MouseEvent e) {
        int slideID = slideList.getSelectionModel().getSelectedIndex();
        String test2 = listFileNames.get(slideID);
        for (Slide slide : slides) {
            if (slide.getFileName().equals(test2)) {
                currentSlide = slide;
                viewUpdate(currentSlide);
                break;
            }
        }
    }

    private String crateSlideName(File file) {

        String fileName = file.getAbsolutePath();
        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        fileName = fileName.toUpperCase();
        return fileName;
    }

    private void showCategoryWindow(MouseEvent e) {
        picker.start(CategoryPicker.pickerStage);
    }

    private void saveFiles(ActionEvent event) {
        FileHandler fileHandler = new FileHandler();

        for (Slide slide : slides) {
            if (!fileHandler.saveSlide(slide)) {
                new Alert(Alert.AlertType.ERROR, "Błąd zapisu : " + slide.getHeader()).showAndWait();
                break;
            }
        }

        JSONHandler.createJSONFile(fileHandler, slides, listFileNames);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz prezentację");
        fileChooser.setInitialDirectory(fileHandler.getSaveDirectory());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("archiwum zip", "*.zip"));
        File saveFile = fileChooser.showSaveDialog(primaryStage);

        if (saveFile != null) {
            if (fileHandler.zipFiles(saveFile))
                new Alert(Alert.AlertType.INFORMATION, "Zapisano").showAndWait();
            else
                new Alert(Alert.AlertType.ERROR, "Błąd zapisu").showAndWait();
        }
    }

    private void openFile(ActionEvent event) {
        FileHandler fileHandler = new FileHandler();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz prezentację");
        fileChooser.setInitialDirectory(fileHandler.getSaveDirectory());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("archiwum zip", "*.zip"));
        File openFile = fileChooser.showOpenDialog(primaryStage);

        if (openFile != null)
            if (fileHandler.unzipFiles(openFile)) {
                JSONHandler.readJSONFile(fileHandler);
            } else
                new Alert(Alert.AlertType.ERROR, "Ups, coś poszło nie tak").showAndWait();

    }
}


