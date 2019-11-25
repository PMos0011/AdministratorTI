package Common;

import Slide.Slide;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class FileHandler {

    private final String tempDirectory;
    private final String saveDirectory;
    private final String transferDirectory;

    public FileHandler() {
        tempDirectory = System.getProperty("user.home") + "/TI/temp";
        saveDirectory = System.getProperty("user.home") + "/TI/save";
        transferDirectory = System.getProperty("user.home") + "/TI/transfer";
        createDirectory();
        clearDirectory(getFileTempDirectory());
    }

    public String getTempDirectory() {
        return tempDirectory + File.separator;
    }

    public String getSaveDirectory() {
        return saveDirectory + File.separator;
    }

    public String getTransferDirectory() {
        return transferDirectory+ File.separator;
    }

    public File getFileSaveDirectory() {
        return new File(saveDirectory);
    }

    public File getFileTempDirectory() {
        return new File(tempDirectory);
    }

    public File getFileTransferDirectory() {
        return new File(transferDirectory);
    }

    public boolean saveSlide(Slide slide) {
        String fileName = tempDirectory + File.separator + slide.getFileName() + ".png";
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
        File[] files = getFileTempDirectory().listFiles();

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

            return false;
        } finally {
            clearDirectory(getFileTempDirectory());
        }
        return true;
    }

    public boolean unzipFiles(File fileName) {
        clearDirectory(getFileTempDirectory());
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

    private void createDirectory() {
        try {
            List<File> directories = new ArrayList<>();
            directories.add(getFileTempDirectory());
            directories.add(getFileSaveDirectory());
            directories.add(getFileTransferDirectory());

            for (File directory : directories) {
                if (!directory.exists())
                    directory.mkdirs();
            }
        } catch (Exception e) {
            Logs.saveLog(e.toString(), "fileHandler");
        }
    }

    public void clearDirectory(File directory) {
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
          file.delete();
        }
    }
}