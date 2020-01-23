package MainWindow;

import AppPropertiesWindow.AppProperties;
import CategoryPicker.CategoryPicker;
import Common.*;
import Editor.Editor;
import JSONObjects.JSONHandler;
import Slide.Slide;
import TransferWindow.TransferWindow;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static final String CATEGORY_PATH = "src/images/";
    private static final String DEFAULT_CATEGORY_ICON = CATEGORY_PATH + "dok.png";

    private BasicCookieStore sessionCookieStore;

    private Stage primaryStage;
    private Loader loader;
    private FileHandler fileHandler;
    private CategoryPicker picker;
    private ControllerMainWindow controllerMainWindow;
    private Slide initSlide;
    private ListView slideList;
    private TextField headerField;
    private TextField firsField;
    private TextField secondField;
    private Slide currentSlide;
    private List<Slide> slides;
    private ObservableList<String> listFileNames;
    private List<String> slideHeaders = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) throws Exception {

        sessionCookieStore = new BasicCookieStore();

        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        controllerMainWindow = fxmlLoader.getController();

        primaryStage.setTitle("Administrator treści Tablica Informacyjna");
        primaryStage.setScene(new Scene(root, 1050, 720));
        primaryStage.show();

        loader = new Loader();
        picker = new CategoryPicker(loader, this);
        fileHandler = new FileHandler();

        currentSlide = new Slide();
        slides = new ArrayList<Slide>();
        listFileNames = FXCollections.observableArrayList();

        initSlide = loader.loadInitialSlide();
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
        MenuItem clearAction = controllerMainWindow.getClearMenuItem();
        clearAction.setOnAction(this::deleteSlides);
        MenuItem editAction = controllerMainWindow.getEditMenuItem();
        editAction.setOnAction(this::editSlides);
        MenuItem importActionMenuItem = controllerMainWindow.getImportMenuItem();
        importActionMenuItem.setOnAction(this::onImportAction);
        MenuItem exportActionMenuItem = controllerMainWindow.getExportMenuItem();
        exportActionMenuItem.setOnAction(this::onExportAction);
        MenuItem settingsActionMenuItem = controllerMainWindow.getSettingsMenuItem();
        settingsActionMenuItem.setOnAction(this::onSettingsAction);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void createSlidesList(List<File> files) {
        for (File file : files) {
            if (fileFilter(file)) {
                Slide tempSlide = setImage(file);
                String header = crateSlideName(file);
                tempSlide.setCategory(loader.imageLoad(DEFAULT_CATEGORY_ICON));
                tempSlide.setCategoryPath(DEFAULT_CATEGORY_ICON);

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

        if (dragboard.hasFiles()) {
            List<File> files = dragboard.getFiles();
            createSlidesList(files);
        }
        e.consume();
    }

    public Slide setImage(File file) {
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
        String slides = listFileNames.get(slideID);
        for (Slide slide : this.slides) {
            if (slide.getFileName().equals(slides)) {
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

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz prezentację");
        fileChooser.setInitialDirectory(fileHandler.getFileSaveDirectory());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("archiwum zip", "*.zip"));
        File saveFile = fileChooser.showSaveDialog(primaryStage);

        boolean success = false;
        if (saveFile != null)
            success = zipFileFromTempToDirectory(saveFile, "save", 0);

        if (success)
            new Alert(Alert.AlertType.INFORMATION, "Zapisano").showAndWait();
        else
            new Alert(Alert.AlertType.ERROR, "Błąd zapisu").showAndWait();
    }

    public boolean zipFileFromTempToDirectory(File fileName, String JSOName, int groupID) {

        for (Slide slide : slides) {
            if (!fileHandler.saveSlide(slide)) {
                new Alert(Alert.AlertType.ERROR, "Błąd zapisu : " + slide.getHeader()).showAndWait();
                break;
            }
        }

        JSONHandler.createJSONFile(fileHandler, slides, listFileNames, JSOName, groupID);

        return (fileHandler.zipFiles(fileName));
    }


    private void openFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz prezentację");
        fileChooser.setInitialDirectory(fileHandler.getFileSaveDirectory());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("archiwum zip", "*.zip"));
        File openFile = fileChooser.showOpenDialog(primaryStage);

        if (openFile != null)
            getPresentationFromFile(openFile);
    }

    public void getPresentationFromFile(File file) {
        if (fileHandler.unzipFiles(file)) {
            slides.clear();
            slides = JSONHandler.readJSONFile(this, fileHandler, loader);

            listFileNames.clear();
            slideHeaders.clear();

            for (Slide slide : slides) {
                listFileNames.add(slide.getFileName());
                slideHeaders.add(slide.getHeader());
            }

            controllerMainWindow.addSlideToList(slideHeaders, 0);
            currentSlide = slides.get(0);
            viewUpdate(currentSlide);

        } else
            new Alert(Alert.AlertType.ERROR, "Ups, coś poszło nie tak").showAndWait();
    }

    private void deleteSlides(ActionEvent event) {
        slides.clear();
        listFileNames.clear();
        slideHeaders.clear();
        controllerMainWindow.addSlideToList(slideHeaders, 0);
        viewUpdate(initSlide);
    }

    private void editSlides(ActionEvent event) {
        Editor editor = new Editor(currentSlide.getImage(), this);
        try {
            editor.start(Editor.editorStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void currentSlideImageUpdate(Image image) {
        currentSlide.setImage(image);
        viewUpdate(currentSlide);
    }

    private void onImportAction(ActionEvent event) {
        TransferWindow transferWindow = new TransferWindow(true, this, fileHandler.getServerIPAddress(), sessionCookieStore);
        try {
            transferWindow.start(TransferWindow.transferStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onExportAction(ActionEvent event) {
        TransferWindow transferWindow = new TransferWindow(false, this, fileHandler.getServerIPAddress(), sessionCookieStore);
        try {
            transferWindow.start(TransferWindow.transferStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSettingsAction(ActionEvent actionEvent) {
        AppProperties appProperties = new AppProperties(fileHandler, fileHandler.getServerIPAddress());
        appProperties.start(AppProperties.appPropertiesStage);
    }
}