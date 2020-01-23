package AppPropertiesWindow;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Objects;


public class ControllerAppPropertiesWindow {
    @FXML
    private TextField serverAddressTextField;
    @FXML
    private Button serverAddressSaveButton;
    @FXML
    private Button sCloseButton;
    @FXML
    private TextField userNameTextField;
    @FXML
    private PasswordField userPasswordPasswordField;
    @FXML
    private PasswordField newPasswordPasswordField;
    @FXML
    private PasswordField confirmPasswordPasswordField;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button pCloseButton;

    public TextField getServerAddressTextField() {
        return serverAddressTextField;
    }

    public Button getServerAddressSaveButton() {
        return serverAddressSaveButton;
    }

    public Button getsCloseButton() {
        return sCloseButton;
    }

    public TextField getUserNameTextField() {
        return userNameTextField;
    }

    public PasswordField getUserPasswordPasswordField() {
        return userPasswordPasswordField;
    }

    public PasswordField getNewPasswordPasswordField() {
        return newPasswordPasswordField;
    }

    public PasswordField getConfirmPasswordPasswordField() {
        return confirmPasswordPasswordField;
    }

    public Button getChangePasswordButton() {
        return changePasswordButton;
    }

    public Button getpCloseButton() {
        return pCloseButton;
    }

}
