package Common;

import javafx.scene.control.Alert;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    public static void saveLog(String message, String filename) {

        //DATE FORMATS DECLARATION
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter dtf4 = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String logPath = "Logs/" + now.format(dtf1) + "/" + now.format(dtf2);
        String fileName = logPath + "/" + dtf3.format(now) + "_" + filename + ".txt";

        try {
            File logDirectory = new File(logPath);
            if (!logDirectory.exists())
                logDirectory.mkdirs();

            File logFile = new File(fileName);
            if (!logFile.exists())
                logFile.createNewFile();

            FileWriter fr = new FileWriter(logFile, true);

            BufferedWriter bw = new BufferedWriter(fr);
            bw.write(now.format(dtf4) + " - " + message);
            bw.newLine();
            bw.close();
            fr.close();

        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Logs_error").show();
        }
    }
}