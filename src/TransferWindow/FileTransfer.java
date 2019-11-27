package TransferWindow;

import Common.FileHandler;
import Common.Logs;
import MainWindow.Main;
import Slide.Slide;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileTransfer extends Application {

    static Stage fileTransferStage = new Stage();

    private Main main;
    private FileTransferHandler fileTransferHandler;

    FileTransfer(boolean isDownloading, String fileName, Main main) {

        this.main = main;

        if (isDownloading)
            fileTransferStage.setTitle("Pobieram");
        else
            fileTransferStage.setTitle("Wysyłam");

        fileTransferHandler = new FileTransferHandler(isDownloading, fileName, this, main);
    }

    @Override
    public void start(Stage stage) {

        fileTransferStage = stage;

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefHeight(20);
        progressBar.setPrefWidth(200);
        progressBar.setLayoutX(25);
        progressBar.setLayoutY(40);
        progressBar.setProgress(0);
        progressBar.progressProperty().bind(fileTransferHandler.progressProperty());

        Pane root = new Pane();
        root.getChildren().add(progressBar);

        fileTransferStage.setScene(new Scene(root, 250, 100));
        if (fileTransferStage.getModality() != Modality.APPLICATION_MODAL) {
            fileTransferStage.initModality(Modality.APPLICATION_MODAL);
            fileTransferStage.initStyle(StageStyle.UTILITY);
        }

        fileTransferStage.show();
        new Thread(fileTransferHandler).start();

    }

    void fileTransferProcessEnd() {
        fileTransferStage.close();
    }

    void fileTransferProcessEnd(String fileName, FileHandler filehandler) {

        File file = new File(filehandler.getTransferDirectory(), fileName);
        main.getPresentationFromFile(file);
        fileTransferStage.close();
    }
}

class FileTransferHandler extends Task {

    private final static int DATA_BUFFER_SIZE = 1024;

    private FileHandler fileHandler;
    private FileTransfer fileTransfer;
    private Main main;

    private boolean isDownloading;
    private String fileName;

    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    FileTransferHandler(boolean isDownloading, String fileName, FileTransfer fileTransfer, Main main) {
        this.isDownloading = isDownloading;
        this.fileName = fileName;
        this.fileTransfer = fileTransfer;
        this.main = main;

        fileHandler = new FileHandler();
    }

    @Override
    protected Object call() throws Exception {

        setTCPConnection();

        if (isDownloading)
            downloadFile();
        else
            uploadFile();

        closeTCPConnection();
        Platform.runLater(closeStage);
        return null;
    }

    private void setTCPConnection() {
        try {
            clientSocket = new Socket(fileHandler.getServerIPAddress(), 55000);
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            inputStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "File_transfer");
        }
    }

    private void downloadFile() {

        fileHandler.clearDirectory(fileHandler.getFileTransferDirectory());
        String requestName = "0@0@" + fileName;
        byte[] dataBuffer = requestName.getBytes(StandardCharsets.UTF_8);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileHandler.getTransferDirectory() + fileName, true);

            outputStream.write(dataBuffer, 0, requestName.length());
            int receivedDataLength = inputStream.read(dataBuffer);
            long fileSize = Long.parseLong(new String(dataBuffer, 0, receivedDataLength));
            long counter = 0;

            dataBuffer = new byte[DATA_BUFFER_SIZE];
            do {
                outputStream.write(6);
                receivedDataLength = inputStream.read(dataBuffer);
                fileOutputStream.write(dataBuffer, 0, receivedDataLength);
                counter += receivedDataLength;
                this.updateProgress(counter, fileSize);
            } while (counter < fileSize);

            fileOutputStream.close();

        } catch (IOException e) {
            Logs.saveLog(e.toString(), "File_transfer");
        }
    }

    private void uploadFile() {

        fileHandler.clearDirectory(fileHandler.getFileTransferDirectory());
        fileHandler.clearDirectory(fileHandler.getFileTempDirectory());

        String name = generateName();
        File file = new File(fileHandler.getTransferDirectory(),name);

        main.zipFileFromTempToDirectory(file,name,Integer.parseInt(fileName));
        long fileSize = file.length();
        long counter = 0;

        String packageName = "1@"+fileSize+"@"+name;
        byte[] dataBuffer = packageName.getBytes(StandardCharsets.UTF_8);

        try {
            outputStream.write(dataBuffer);
            dataBuffer = new byte[DATA_BUFFER_SIZE];
            int sendDataLength;
            FileInputStream fileInputStream = new FileInputStream(file);

            while((sendDataLength=fileInputStream.read(dataBuffer,0,DATA_BUFFER_SIZE))>0){
                inputStream.read();
                outputStream.write(dataBuffer);
                counter+=sendDataLength;
                this.updateProgress(counter, fileSize);
            }
            fileInputStream.close();

        } catch (IOException e) {
            Logs.saveLog(e.toString(), "File_transfer");
        }
    }

    private void closeTCPConnection() {
        try {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "File_transfer");
        }
    }

    private String generateName(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(new Date());

        return fileName+"."+date+"."+ Slide.generateName(4);
    }

    private Runnable closeStage = new Runnable() {
        @Override
        public void run() {
            if (isDownloading)
                fileTransfer.fileTransferProcessEnd(fileName, fileHandler);
            else
                fileTransfer.fileTransferProcessEnd();
        }
    };
}




