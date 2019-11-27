package TransferWindow;

import Common.Logs;
import MainWindow.Main;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TransferWindow extends Application {

    public static Stage transferStage = new Stage();
    private Main main;

    private ListView listView = new ListView();
    private Button button = new Button();

    private List<Group> groups = new ArrayList<>();
    private List<Save> saves = new ArrayList<>();
    private String serverAddress;
    private boolean importAction;

    public TransferWindow(boolean importAction, Main main, String serverAddress) {
        this.importAction = importAction;
        this.main = main;
        this.serverAddress = serverAddress;
    }

    @Override
    public void start(Stage stage) {
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

        String response = getUrlResponse();
        groupListWrite(response);

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

    private String getUrlResponse() {

        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("http://" + serverAddress + "/TI/PHP/getGRP.php");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String temp;
            while ((temp = in.readLine()) != null)
                response.append(temp);
            in.close();
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "Transfer_window");
        }
        return response.toString();
    }

    private String getUrlResponse(String groupID) {

        String request = "GRP=" + groupID;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL("http://" + serverAddress + "/TI/PHP/getSaves.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(request);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;
            while ((temp = in.readLine()) != null)
                response.append(temp);
            out.flush();
            out.close();
            in.close();
            connection.disconnect();

        } catch (IOException e) {
            Logs.saveLog(e.toString(), "File_transfer");
        }
        return response.toString();
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
                String response = getUrlResponse(groups.get(selectedItem).getParam());
                savesListWrite(response);
            } else {
                FileTransfer fileTransfer = new FileTransfer(false, groups.get(selectedItem).getParam(), main);
                fileTransfer.start(FileTransfer.fileTransferStage);
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

