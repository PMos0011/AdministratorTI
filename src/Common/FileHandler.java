package Common;

import Slide.Slide;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileHandler {

    private final String tempDirectory;

    public FileHandler() {
        tempDirectory = System.getProperty("user.home") + "/TI/temp";
        createTempDirectory();
        clearTemp();
    }

public void JSONSave(String fileName, String json){

        fileName = tempDirectory+"/"+fileName;
    try {
        FileWriter writer = new FileWriter(fileName);
        writer.write(json);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public boolean saveSlide(Slide slide) {
        String fileName = tempDirectory + "/" + slide.getHeader() + ".png";
        fileName=fileName.replaceAll("\\s+","");
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


    private void createTempDirectory() {
        try {
            File logDirectory = new File(tempDirectory);
            if (!logDirectory.exists())
                logDirectory.mkdirs();
        } catch (Exception e) {
            Logs.saveLog(e.toString(), "fileHandler");
        }
    }

    private void clearTemp() {
        File tempFile = new File(tempDirectory + "/");
        File[] files = tempFile.listFiles();
        for (File file : files) {
            file.delete();
        }
    }
}
