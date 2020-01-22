package TransferWindow;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.impl.client.BasicCookieStore;


public class LoginWindow extends Application {

    public static Stage loginWindow = new Stage();
    private TransferWindow transferWindow;
    private String serverAddress;
    private BasicCookieStore cookieStore;

    public LoginWindow(TransferWindow transferWindow, String serverAddress, BasicCookieStore cookieStore) {
        this.transferWindow = transferWindow;
        this.serverAddress = serverAddress;
        this.cookieStore = cookieStore;
    }

    private Text loginText;
    private Text passwordText;
    private TextField loginTextField;
    private PasswordField passwordField;
    private Button logInButton;
    private Button cancelButton;

    @Override
    public void start(Stage stage) {

        loginWindow = stage;
        Pane root = new Pane();
        root.setOnKeyPressed(this::KeyPressedListener);

        loginText = new Text();
        loginText.setText("Login");
        loginText.setLayoutX(10);
        loginText.setLayoutY(20);

        loginTextField = new TextField();
        loginTextField.setLayoutX(10);
        loginTextField.setLayoutY(30);
        loginTextField.setPrefWidth(200);

        passwordText = new Text();
        passwordText.setText("Hasło");
        passwordText.setLayoutX(10);
        passwordText.setLayoutY(70);

        passwordField = new PasswordField();
        passwordField.setLayoutX(10);
        passwordField.setLayoutY(80);
        passwordField.setPrefWidth(200);

        logInButton = new Button();
        logInButton.setText("Zaloguj");
        logInButton.setLayoutX(10);
        logInButton.setLayoutY(120);
        logInButton.setPrefWidth(60);
        logInButton.setOnAction(this::authorizeUser);

        cancelButton = new Button();
        cancelButton.setText("Anuluj");
        cancelButton.setLayoutX(80);
        cancelButton.setLayoutY(120);
        cancelButton.setPrefWidth(60);
        cancelButton.setOnAction(this::closeWindow);

        root.getChildren().addAll(loginText, loginTextField, passwordText, passwordField, logInButton, cancelButton);


        loginWindow.setTitle("Login");
        loginWindow.setScene(new Scene(root, 220, 170));
        if (loginWindow.getModality() != Modality.APPLICATION_MODAL) {
            loginWindow.initModality(Modality.APPLICATION_MODAL);
            loginWindow.initStyle(StageStyle.UTILITY);
        }
        loginWindow.show();

    }

    private void KeyPressedListener(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER)
            authorizeUser(null);
        if(keyEvent.getCode() == KeyCode.ESCAPE)
            loginWindow.close();
    }

    private void closeWindow(ActionEvent actionEvent) {
        loginWindow.close();
    }

    private void authorizeUser(ActionEvent actionEvent) {

        if(PHPConnections.authorizeUserPHPSession(cookieStore,serverAddress,loginTextField.getText(),passwordField.getText())){
            transferWindow.start(TransferWindow.transferStage);
            loginWindow.close();
        }

    }
}
