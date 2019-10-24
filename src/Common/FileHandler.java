package Common;

import Slide.Slide;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class FileHandler {

    private final String tempDirectory;
    private final String saveDirectory;

    public FileHandler() {
        tempDirectory = System.getProperty("user.home") + "/TI/temp";
        saveDirectory = System.getProperty("user.home") + "/TI/save";
        createDirectory();
        clearTemp();
    }

    public File getSaveDirectory() {
        return new File(saveDirectory);
    }

    public File getTempDirectory(){
        return new File(tempDirectory);
    }

    public boolean saveSlide(Slide slide) {
        String fileName = tempDirectory + "/" + slide.getFileName() + ".png";
        fileName = fileName.replaceAll("\\s+", "");
        BufferedImage image = SwingFXUtils.fromFXImage(slide.getImage(), null);
        try {
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "fileHandler");
            return false;
        }
        image.flush();
        return true;
    }

    public boolean zipFiles(File fileName) {
        File[] files = getTempFiles();

        try {
            FileOutputStream zipFile = new FileOutputStream(fileName);
            ZipOutputStream zipOut = new ZipOutputStream(zipFile);

            for (File file : files) {
                FileInputStream inputFile = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputFile.read(bytes)) >= 0)
                    zipOut.write(bytes, 0, length);
                inputFile.close();
            }
            zipOut.close();
            zipFile.close();
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "fileHandler");
            clearTemp();
            return false;
        }
        clearTemp();
        return true;
    }

    public boolean unzipFiles(File fileName) {
        clearTemp();
        byte[] buffer = new byte[1024];

        try {
            ZipInputStream zipInput = new ZipInputStream(new FileInputStream(fileName));
            ZipEntry zipEntry = zipInput.getNextEntry();

            while (zipEntry != null) {
                File file = new File(tempDirectory, zipEntry.getName());
                FileOutputStream fileOutput = new FileOutputStream(file);
                int length;
                while ((length = zipInput.read(buffer)) > 0)
                    fileOutput.write(buffer, 0, length);
                fileOutput.close();
                zipEntry = zipInput.getNextEntry();
            }
            zipInput.closeEntry();
            zipInput.close();
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "fileHandler");
            return false;
        }
        return true;
    }

    private File[] getTempFiles() {
        File tempFiles = getTempDirectory();
        return tempFiles.listFiles();
    }


    private void createDirectory() {
        try {
            File newTempDirectory = getTempDirectory();
            File newSaveDirectory = getSaveDirectory();
            if (!newTempDirectory.exists())
                newTempDirectory.mkdirs();
            if (!newSaveDirectory.exists())
                newSaveDirectory.mkdirs();
        } catch (Exception e) {
            Logs.saveLog(e.toString(), "fileHandler");
        }
    }

    private void clearTemp() {
        File[] files = getTempFiles();
        for (File file : files) {
            file.delete();
        }
    }
}
