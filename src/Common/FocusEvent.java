package Common;

import MainWindow.ControllerMainWindow;
import MainWindow.Main;
import javafx.scene.control.TextField;

import java.awt.event.FocusListener;

public class FocusEvent implements FocusListener {
    private Main main;

    private TextField headerField;
    private TextField firsField;
    private TextField secondField;

    @Override
    public void focusGained(java.awt.event.FocusEvent e) {

    }

    @Override
    public void focusLost(java.awt.event.FocusEvent e) {

    }

    public FocusEvent(ControllerMainWindow controllerMainWindow, Main main) {

        this.main = main;

        headerField = controllerMainWindow.getHeaderText();
        firsField = controllerMainWindow.getFirstLineText();
        secondField = controllerMainWindow.getSecondLineText();

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
