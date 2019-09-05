package MainWindow;

import Common.Loader;
import Slide.Slide;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    private Loader loader = new Loader();
    private ControllerMainWindow controllerMainWindow;
    private Slide initSlide;
    private Slide currentSlide = new Slide();
    private List<Slide> slides = new ArrayList<Slide>();
    private FocusEvent focusEvent;
    private ListView slideList;
    private ObservableList<String> listViewSlides = FXCollections.observableArrayList();


    @Override
    public void start(Stage primaryStage) throws Exception {

        initSlide = new Slide(loader.imageLoad("C:/Users/pawel/IdeaProjects/AdministratorTI/src/images/dok.png"),
                loader.imageLoad("C:/Users/pawel/IdeaProjects/AdministratorTI/src/images/Cont_background.png"),
                "0", "NAGŁÓWEK", "Opis slajdu", "opis slajdu", 0);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        controllerMainWindow = fxmlLoader.getController();

        primaryStage.setTitle("Administrator treści Tablica Informacyjna");
        primaryStage.setScene(new Scene(root, 1100, 720));
        primaryStage.show();

        viewUpdate(initSlide);

        ImageView mainImage = controllerMainWindow.getMainImage();
        mainImage.setOnDragOver(this::dragDetect);
        mainImage.setOnDragDropped(this::dropDetect);

        slideList = controllerMainWindow.getSlideList();
        slideList.setCellFactory(param -> new CellsFactory(slideList, listViewSlides, controllerMainWindow, this));
        slideList.setOnMouseClicked(this::listClicked);

        focusEvent = new FocusEvent(controllerMainWindow, this);

    }


    public static void main(String[] args) {
        launch(args);
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

            String fileName = file.getAbsolutePath();
            fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
            fileName = fileName.substring(0,fileName.lastIndexOf("."));

            Slide tempSlide = new Slide();
            tempSlide.setImage(loader.imageLoad(file.getAbsolutePath()));
            tempSlide.setCategory(loader.imageLoad("C:/Users/pawel/IdeaProjects/AdministratorTI/src/images/dok.png"));
            tempSlide.setHeader(fileName);
            slides.add(tempSlide);

            viewUpdate(tempSlide);

            listViewSlides.add(tempSlide.getHeader());
            controllerMainWindow.addSlideToList(listViewSlides, listViewSlides.size() - 1);
            currentSlide = tempSlide;
        }
        e.consume();
    }

    private void viewUpdate(Slide slide) {
        controllerMainWindow.setMainImage(slide.getImage());
        controllerMainWindow.setCategoryImage(slide.getCategory());
        controllerMainWindow.setHeader(slide.getHeader());
        controllerMainWindow.setFirstLine(slide.getFirstDescription());
        controllerMainWindow.setSecondLineText(slide.getSecondDescription());
    }

    public void listClicked(MouseEvent e) {
        String test = slideList.getSelectionModel().getSelectedItem().toString();
        for (Slide slide : slides) {
            if (slide.getHeader().equals(test)) {
                currentSlide = slide;
                viewUpdate(currentSlide);
                break;
            }
        }
    }

    public void updateHeader(String text) {

        int slideID = listViewSlides.indexOf(currentSlide.getHeader());
        listViewSlides.remove(slideID);
        listViewSlides.add(slideID,text);
        currentSlide.setHeader(text);
        controllerMainWindow.addSlideToList(listViewSlides,slideID);
    }

    public void updateFirsLine(String text) {
        currentSlide.setFirstDescription(text);
    }

    public void updateSecondLine(String text) {
        currentSlide.setSecondDescription(text);
    }

    public void deleteSlide(){
        slides.remove(currentSlide);
        if(slides.size()>0)
            currentSlide=slides.get(0);
        else
            currentSlide=initSlide;

        controllerMainWindow.addSlideToList(listViewSlides,0);
        viewUpdate(currentSlide);
    }

}


