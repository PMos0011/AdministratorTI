package Common;

import MainWindow.ControllerMainWindow;
import MainWindow.Main;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CellsFactory extends ListCell<String> {

    public CellsFactory(ListView slideList, ObservableList<String> listViewSlides, ControllerMainWindow controllerMainWindow, Main main) {
        ListCell thisCell = this;

        setOnDragDetected(event -> {
            if (getItem() == null) {
                return;
            }

            Text text = new Text();
            text.setFont(Font.font(20));
            text.setText(slideList.getSelectionModel().getSelectedItem().toString());

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(getItem());
            dragboard.setDragView(text.snapshot(null, null));
            dragboard.setContent(content);

            event.consume();
        });

        setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);

            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                setOpacity(1);
            }
        });

        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            int draggedIdx = listViewSlides.indexOf(db.getString());
            int thisIdx = listViewSlides.indexOf(getItem());
            String name = listViewSlides.get(draggedIdx);
            listViewSlides.remove(draggedIdx);

            if (getItem() == null)
                listViewSlides.add(name);
            else if (db.hasString())
                listViewSlides.add(thisIdx, name);

            controllerMainWindow.addSlideToList(listViewSlides,thisIdx);
            event.setDropCompleted(true);

            event.consume();
        });

        setOnDragDone(event -> {
            Dragboard db = event.getDragboard();
            if (event.getTransferMode() != TransferMode.MOVE) {
                listViewSlides.remove(db.getString());
                main.deleteSlide();
            }
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setStyle("-fx-background-color: white");
        } else {
            setText(item);
            setStyle("");
        }

    }
}
