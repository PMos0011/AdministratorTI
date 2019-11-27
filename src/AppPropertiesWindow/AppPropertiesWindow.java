package AppPropertiesWindow;

import Common.FileHandler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.Properties;


public class AppPropertiesWindow extends Application {

    public final static String SERVER_ADDRESS_PROPERTY_NAME = "ServerAddress";

    private final String DEFAULT_SERVER_ADDRESS = "192.70.100.100";

    public static Stage appPropertiesStage = new Stage();
    private FileHandler fileHandler;

    private String serverAddress;
    private TextField textField;

    public AppPropertiesWindow(FileHandler fileHandler, String serverAddress) {
        this.fileHandler = fileHandler;
        this.serverAddress = serverAddress;
    }

    @Override
    public void start(Stage stage) {

        appPropertiesStage = stage;

        Label label = new Label();
        label.setLayoutX(10);
        label.setLayoutY(10);
        label.setText("adres serwera:");

        textField = new TextField();
        textField.setLayoutX(10);
        textField.setLayoutY(40);
        textField.setPrefWidth(190);

        textField.setText(Objects.requireNonNullElse(serverAddress, DEFAULT_SERVER_ADDRESS));

        Button button = new Button();
        button.setPrefSize(70, 20);
        button.setLayoutX(130);
        button.setLayoutY(70);
        button.setText("Zapisz");
        button.setOnAction(this::saveProperties);

        Pane root = new Pane();
        root.getChildren().add(label);
        root.getChildren().add(textField);
        root.getChildren().add(button);

        appPropertiesStage.setScene(new Scene(root, 210, 100));
        if (appPropertiesStage.getModality() != Modality.APPLICATION_MODAL) {
            appPropertiesStage.initModality(Modality.APPLICATION_MODAL);
            appPropertiesStage.initStyle(StageStyle.UTILITY);
        }

        appPropertiesStage.show();
    }

    private void saveProperties(ActionEvent actionEvent) {
        Properties properties = new Properties();
        properties.setProperty(SERVER_ADDRESS_PROPERTY_NAME, textField.getText());
        fileHandler.saveAppPropertiesFile(properties);
        appPropertiesStage.close();
    }
}
