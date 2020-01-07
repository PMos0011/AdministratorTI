package CategoryPicker;

import Common.Loader;
import MainWindow.Main;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CategoryPicker extends Application {

    public static Stage pickerStage = new Stage();

    private Loader loader;
    private Main main;

    private ImageView[][] images = new ImageView[3][6];
    private Tooltip[][] tooltips = new Tooltip[3][6];

    private List<String> categoryItems = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    public CategoryPicker(Loader loader, Main main) {
        this.loader = loader;
        this.main = main;
        setCategoryPaths();
        setCategoryNames();
    }

    @Override
    public void start(Stage stage) {

        pickerStage = stage;

        Image image;

        Pane root = new Pane();
        int xPos;
        int yPos = 10;
        int categoryID=0;

        for (int i = 0; i < 3; i++) {
            xPos = 10;
            for (int j = 0; j < 6; j++) {
                images[i][j] = new ImageView();
                images[i][j].setCursor(Cursor.HAND);
                images[i][j].setFitHeight(55);
                images[i][j].setFitWidth(55);
                images[i][j].setLayoutX(xPos);
                images[i][j].setLayoutY(yPos);
                images[i][j].setId(String.valueOf(categoryID));
                image = loader.imageLoad(Main.CATEGORY_PATH + categoryItems.get(categoryID));
                images[i][j].setImage(image);
                tooltips[i][j] = new Tooltip(categoryNames.get(categoryID));
                images[i][j].setPickOnBounds(true);
                images[i][j].addEventHandler(MouseEvent.MOUSE_CLICKED, new sendCategoryPath());
                Tooltip.install(images[i][j], tooltips[i][j]);
                xPos += 60;
                categoryID++;
                root.getChildren().add(images[i][j]);
            }
            yPos += 60;
        }

        pickerStage.setTitle("Kategorie");
        pickerStage.setScene(new Scene(root, 375, 195));
        if (pickerStage.getModality() != Modality.APPLICATION_MODAL) {
            pickerStage.initModality(Modality.APPLICATION_MODAL);
            pickerStage.initStyle(StageStyle.UTILITY);
        }
        pickerStage.show();
    }

    private void setCategoryPaths() {
        categoryItems.add("bwazne.png");
        categoryItems.add("wazne.png");
        categoryItems.add("dok.png");
        categoryItems.add("foto.png");
        categoryItems.add("kultura.png");
        categoryItems.add("personalne.png");
        categoryItems.add("plan.png");
        categoryItems.add("pogoda.png");
        categoryItems.add("praca.png");
        categoryItems.add("prawo.png");
        categoryItems.add("sport.png");
        categoryItems.add("szkolenia.png");
        categoryItems.add("swieta.png");
        categoryItems.add("swietapan.png");
        categoryItems.add("wczasy.png");
        categoryItems.add("wiadomosci.png");
        categoryItems.add("wycieczki.png");
        categoryItems.add("zdrowie.png");
    }

    private void setCategoryNames() {
        categoryNames.add("bardzo ważne");
        categoryNames.add("ważne");
        categoryNames.add("dokumenty");
        categoryNames.add("fotogalerai");
        categoryNames.add("kultura");
        categoryNames.add("personalne");
        categoryNames.add("plan");
        categoryNames.add("pogoda");
        categoryNames.add("praca");
        categoryNames.add("prawo");
        categoryNames.add("sport");
        categoryNames.add("szkolenia");
        categoryNames.add("święta");
        categoryNames.add("święta państwowe");
        categoryNames.add("wczasy");
        categoryNames.add("wiadomości");
        categoryNames.add("wycieczki");
        categoryNames.add("zdrowie");
    }

    private class sendCategoryPath implements EventHandler<Event> {

        @Override
        public void handle(Event event) {
            int categoryID = Integer.parseInt(((ImageView) event.getSource()).getId());
            String path = Main.CATEGORY_PATH + categoryItems.get(categoryID);
            main.setCategory(path);
            pickerStage.close();
        }
    }
}