package AppPropertiesWindow;

import Common.FileHandler;
import Common.Logs;
import TransferWindow.PHPConnections;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;


public class AppProperties extends Application {

    public final static String SERVER_ADDRESS_PROPERTY_NAME = "ServerAddress";
    private final static String DEFAULT_SERVER_ADDRESS = "192.70.100.100";

    public static Stage appPropertiesStage = new Stage();
    private FileHandler fileHandler;
    private String serverAddress;
    BasicCookieStore cookieStore;

    private TextField serverAddressTextField;
    private TextField userNameTextField;
    private PasswordField userPasswordPasswordField;
    private PasswordField newPasswordPasswordField;
    private PasswordField confirmPasswordPasswordField;

    public AppProperties(FileHandler fileHandler, String serverAddress, BasicCookieStore cookieStore) {
        this.fileHandler = fileHandler;
        this.serverAddress = serverAddress;
        this.cookieStore = cookieStore;
    }

    @Override
    public void start(Stage stage) {

        appPropertiesStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AppPropertiesWindow.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "AppProperties");
        }

        ControllerAppPropertiesWindow controllerAppPropertiesWindow = fxmlLoader.getController();

        appPropertiesStage.setTitle("Ustawienia");
        appPropertiesStage.setScene(new Scene(root, 410, 290));

        if (appPropertiesStage.getModality() != Modality.APPLICATION_MODAL) {
            appPropertiesStage.initModality(Modality.APPLICATION_MODAL);
            appPropertiesStage.initStyle(StageStyle.UTILITY);
        }

        serverAddressTextField = controllerAppPropertiesWindow.getServerAddressTextField();
        serverAddressTextField.setText(Objects.requireNonNullElse(serverAddress, DEFAULT_SERVER_ADDRESS));

        Button serverAddressSaveButton = controllerAppPropertiesWindow.getServerAddressSaveButton();
        serverAddressSaveButton.setOnAction(this::saveProperties);

        Button sCloseButton = controllerAppPropertiesWindow.getsCloseButton();
        sCloseButton.setOnAction(this::closeAppPropertiesWindow);

        userNameTextField = controllerAppPropertiesWindow.getUserNameTextField();
        userPasswordPasswordField = controllerAppPropertiesWindow.getUserPasswordPasswordField();
        newPasswordPasswordField = controllerAppPropertiesWindow.getNewPasswordPasswordField();
        confirmPasswordPasswordField = controllerAppPropertiesWindow.getConfirmPasswordPasswordField();

        Button changePasswordButton = controllerAppPropertiesWindow.getChangePasswordButton();
        changePasswordButton.setOnAction(this::changePassword);

        Button pCloseButton = controllerAppPropertiesWindow.getpCloseButton();
        pCloseButton.setOnAction(this::closeAppPropertiesWindow);


        appPropertiesStage.show();
    }

    private void closeAppPropertiesWindow(ActionEvent actionEvent) {
        appPropertiesStage.close();
    }

    private void changePassword(ActionEvent actionEvent) {

        if (newPasswordPasswordField.getText().equals(confirmPasswordPasswordField.getText())) {
            if (PHPConnections.changeUserPassword(cookieStore, serverAddress,
                    userNameTextField.getText(), userPasswordPasswordField.getText(), newPasswordPasswordField.getText()))
                new Alert(Alert.AlertType.INFORMATION, "Hasło zostało zmienione").showAndWait();
        } else
            new Alert(Alert.AlertType.INFORMATION, "Hasło w polach nowe i potwierdź nie jest jednakowe").showAndWait();
    }

    private void saveProperties(ActionEvent actionEvent) {
        Properties properties = new Properties();
        properties.setProperty(SERVER_ADDRESS_PROPERTY_NAME, serverAddressTextField.getText());
        fileHandler.saveAppPropertiesFile(properties);
        new Alert(Alert.AlertType.INFORMATION, "Adres został zmieniony").showAndWait();
    }
}
