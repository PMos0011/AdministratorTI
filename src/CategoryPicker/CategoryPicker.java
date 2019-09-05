package CategoryPicker;

import Common.Loader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class CategoryPicker extends Application {

    public static Stage pickerStage = new Stage();

    private Loader loader;

    private ImageView[][] images = new ImageView[3][6];
    private Tooltip[][] tooltips = new Tooltip[3][6];

    private List<String> categoryItems = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    public CategoryPicker(Loader loader) {
        this.loader = loader;
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

        for (int i = 0; i < 3; i++) {
            xPos = 10;
            for (int j = 0; j < 6; j++) {
                images[i][j] = new ImageView();
                images[i][j].setFitHeight(55);
                images[i][j].setFitWidth(55);
                images[i][j].setLayoutX(xPos);
                images[i][j].setLayoutY(yPos);
                image = loader.imageLoad(categoryItems.get(i * 3 + j));
                images[i][j].setImage(image);
                tooltips[i][j] = new Tooltip(categoryNames.get(i * 3 + j));
                images[i][j].setPickOnBounds(true);
                Tooltip.install(images[i][j], tooltips[i][j]);
                xPos += 60;
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
        categoryItems.add("src/images/bwazne.png");
        categoryItems.add("src/images/wazne.png");
        categoryItems.add("src/images/dok.png");
        categoryItems.add("src/images/foto.png");
        categoryItems.add("src/images/kultura.png");
        categoryItems.add("src/images/personalne.png");
        categoryItems.add("src/images/plan.png");
        categoryItems.add("src/images/pogoda.png");
        categoryItems.add("src/images/praca.png");
        categoryItems.add("src/images/prawo.png");
        categoryItems.add("src/images/sport.png");
        categoryItems.add("src/images/szkolenia.png");
        categoryItems.add("src/images/swieta.png");
        categoryItems.add("src/images/swietapan.png");
        categoryItems.add("src/images/wczasy.png");
        categoryItems.add("src/images/wiadomosci.png");
        categoryItems.add("src/images/wycieczki.png");
        categoryItems.add("src/images/zdrowie.png");
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
}
