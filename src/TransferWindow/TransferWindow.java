package TransferWindow;

import Common.Logs;
import MainWindow.Main;
import Slide.Slide;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class TransferWindow extends Application {

    public static Stage transferStage = new Stage();
    private Main main;
    private BasicCookieStore cookieStore;

    private ListView listView = new ListView();
    private Button button = new Button();

    private List<Group> groups = new ArrayList<>();
    private List<Save> saves = new ArrayList<>();
    private String serverAddress;
    private boolean importAction;

    public TransferWindow(boolean importAction, Main main, String serverAddress, BasicCookieStore cookieStore) {
        this.importAction = importAction;
        this.main = main;
        this.serverAddress = serverAddress;
        this.cookieStore = cookieStore;
    }

    @Override
    public void start(Stage stage) {

        try {
            if (!PHPConnections.checkPHPSessionIfValidReturnTrue(cookieStore, serverAddress)) {

                LoginWindow transferWindow = new LoginWindow(this, serverAddress, cookieStore);
                try {
                    transferWindow.start(LoginWindow.loginWindow);
                } catch (Exception e) {
                    Logs.saveLog(e.toString(),"TransferWindow");
                    new Alert(Alert.AlertType.INFORMATION, "Błąd tworzenia okna").showAndWait();
                }
            } else {
                transferStage = stage;
                Pane root = new Pane();

                listView.setMaxWidth(200);
                listView.setMaxHeight(180);
                listView.setLayoutX(5);
                listView.setLayoutY(5);

                button.setOnAction(this::buttonOnBasicAction);
                button.setText("OK");
                button.setMinWidth(100);
                button.setLayoutX(225);
                button.setLayoutY(160);

                groupListWrite(PHPConnections.returnStringFromHttpResponse(cookieStore,serverAddress,null));

                root.getChildren().add(listView);
                root.getChildren().add(button);

                transferStage.setTitle("Import/Eksport");
                transferStage.setScene(new Scene(root, 355, 195));
                if (transferStage.getModality() != Modality.APPLICATION_MODAL) {
                    transferStage.initModality(Modality.APPLICATION_MODAL);
                    transferStage.initStyle(StageStyle.UTILITY);
                }
                transferStage.show();
            }
        } catch (IOException e) {
            Logs.saveLog(e.toString(),"TransferWindow");
            new Alert(Alert.AlertType.INFORMATION, "Błąd http").showAndWait();
        }
    }

    private void groupListWrite(String response) {
        groups.clear();
        Type groupListType = new TypeToken<ArrayList<Group>>() {
        }.getType();
        groups = new Gson().fromJson(response, groupListType);

        listView.getItems().clear();
        for (Group group : groups) {
            listView.getItems().add(group.getName());
        }
    }

    private void savesListWrite(String response) {
        saves.clear();
        Type groupListType = new TypeToken<ArrayList<Save>>() {
        }.getType();
        saves = new Gson().fromJson(response, groupListType);

        listView.getItems().clear();
        for (Save save : saves) {
            listView.getItems().add(save.getName().substring(2, save.getName().length() - 5));
        }
    }

    private void buttonOnBasicAction(ActionEvent e) {

        int selectedItem = listView.getSelectionModel().getSelectedIndex();
        if (selectedItem >= 0) {
            if (importAction) {
                button.setOnAction(this::buttonOnImportAction);
                try {
                    savesListWrite(PHPConnections.returnStringFromHttpResponse(cookieStore,serverAddress,groups.get(selectedItem).getParam()));
                } catch (IOException ex) {
                    Logs.saveLog(ex.toString(),"TransferWindow");
                    new Alert(Alert.AlertType.INFORMATION, "Błąd pobierania listy grup").showAndWait();
                }
            } else {
                //String fileName = Slide.generateName(groups.get(selectedItem).getParam());
                FileTransfer fileTransfer = new FileTransfer(false, groups.get(selectedItem).getParam(), main);
                fileTransfer.start(FileTransfer.fileTransferStage);
                //PHPConnections.addPublicationLogs(cookieStore,serverAddress);
                transferStage.close();
            }

        } else
            new Alert(Alert.AlertType.INFORMATION, "Wybierz grupę").showAndWait();
    }

    private void buttonOnImportAction(ActionEvent e) {
        int selectedItem = listView.getSelectionModel().getSelectedIndex();
        if (selectedItem >= 0) {
            transferStage.close();
            FileTransfer fileTransfer = new FileTransfer(true, saves.get(selectedItem).getName(), main);
            fileTransfer.start(FileTransfer.fileTransferStage);
        } else
            new Alert(Alert.AlertType.INFORMATION, "Wybierz zapis").showAndWait();
    }

}

class Group {

    @SerializedName("GRP_NAME")
    private String name;
    @SerializedName("GRP_ID")
    private String param;

    public Group(String name, String param) {
        this.name = name;
        this.param = param;
    }

    String getName() {
        return name;
    }

    String getParam() {
        return param;
    }
}

class Save {
    @SerializedName("NAME")
    private String name;
    @SerializedName("DATE")
    private String param;

    public Save(String name, String param) {
        this.name = name;
        this.param = param;
    }

    String getName() {
        return name;
    }

    String getParam() {
        return param;
    }
}

