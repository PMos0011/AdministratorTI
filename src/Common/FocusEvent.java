package Common;

import MainWindow.ControllerMainWindow;
import MainWindow.Main;
import javafx.scene.control.TextField;

import java.awt.event.FocusListener;

public class FocusEvent implements FocusListener {

    @Override
    public void focusGained(java.awt.event.FocusEvent e) {

    }

    @Override
    public void focusLost(java.awt.event.FocusEvent e) {

    }

    public static void setFocusEvent(ControllerMainWindow controllerMainWindow, Main main) {

        TextField headerField = controllerMainWindow.getHeaderText();
        TextField firsField = controllerMainWindow.getFirstLineText();
        TextField secondField = controllerMainWindow.getSecondLineText();

        headerField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV)
                main.updateHeader(headerField.getText());
        });

       firsField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV)
                main.updateFirsLine(firsField.getText());
        });

        secondField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV)
                main.updateSecondLine(secondField.getText());
        });
    }
}
