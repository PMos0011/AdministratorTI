package Editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Editor extends Application {

    public static Stage editorStage = new Stage();

    @Override
    public void start(Stage stage) throws Exception {

        editorStage=stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditorWindow.fxml"));
        Parent root = fxmlLoader.load();

        editorStage.setTitle("Edycja slajdu");
        editorStage.setScene(new Scene(root, 900, 600));

        if (editorStage.getModality() != Modality.APPLICATION_MODAL) {
            editorStage.initModality(Modality.APPLICATION_MODAL);
            editorStage.initStyle(StageStyle.UTILITY);
        }

        editorStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
