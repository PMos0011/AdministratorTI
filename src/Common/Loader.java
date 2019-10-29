package Common;

import Slide.Slide;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Loader {

    public Image imageLoad(String path) {

        Image image = null;
        try {
            image = new Image(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            Logs.saveLog(e.toString(), "Loader");
        }

        return image;
    }

    public Image imageLoad(File file) {

        Image image = null;
        try {
            image = new Image(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Logs.saveLog(e.toString(), "Loader");
        }

        BufferedImage frame = SwingFXUtils.fromFXImage(image, null);
        frame = resizeImage(frame);
        image = SwingFXUtils.toFXImage(frame, null);
        frame.flush();

        return image;
    }

    public Slide loadInitialSlide() {

        return new Slide(imageLoad("src/images/dok.png"),
                imageLoad(new File("src/images/Cont_background.png")),
                Slide.generateName(16),
                "src/images/dok.png",
                "NAGŁÓWEK", "Opis slajdu", "opis slajdu");
    }

    public Image pdfLoad(File file) {
        Image image = null;
        int pageNumber = 1;

        try {
            PDDocument document = PDDocument.load(file);

            if (document.getNumberOfPages() > 1)
                pageNumber = getPage(document.getNumberOfPages(), file);

            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage pdfImage = renderer.renderImageWithDPI(pageNumber - 1, 300, ImageType.RGB);
            pdfImage = resizeImage(pdfImage);
            image = SwingFXUtils.toFXImage(pdfImage, null);
            document.close();
            pdfImage.flush();

        } catch (IOException e) {
            Logs.saveLog(e.toString(), "Loader");
        }

        return image;
    }

    public Image tiffLoad(File file) {

        Image image = null;
        int pageNumber = 1;

        try {
            ImageInputStream inputStream = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);

            ImageReader reader = readers.next();
            reader.setInput(inputStream);
            reader.getNumImages(true);

            if (reader.getNumImages(true) > 1)
                pageNumber = getPage(reader.getNumImages(true), file);

            BufferedImage tiffImage = reader.read(pageNumber - 1);
            tiffImage = resizeImage(tiffImage);
            image = SwingFXUtils.toFXImage(tiffImage, null);
            tiffImage.flush();

        } catch (IOException e) {
            Logs.saveLog(e.toString(), "Loader");
        }
        return image;
    }

    private int getPage(int pages, File file) {

        List<Integer> list = new ArrayList<>();

        for (int i = 1; i <= pages; i++)
            list.add(i);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, list);
        dialog.setTitle("Converter");
        dialog.setHeaderText("dokument: " + file.getName());
        dialog.setContentText("Wybierz stronę do przetworzenia: ");

        Optional<Integer> result = dialog.showAndWait();

        return result.orElse(1);
    }

    public BufferedImage resizeImage(BufferedImage image) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double proportion = imageWidth / imageHeight;

        double widthDifference = 0;
        double heightDifference = 0;

        if (proportion < 1.41) {
            imageWidth = imageHeight * 1.415;
            widthDifference = (imageWidth - image.getWidth()) / 2;
        } else if (proportion > 1.42) {
            imageHeight = imageWidth / 1.415;
            heightDifference = (imageHeight - image.getHeight()) / 2;
        }

        BufferedImage frame = new BufferedImage((int) imageWidth, (int) imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = frame.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, (int) imageWidth, (int) imageHeight);
        graphics2D.drawImage(image, null, (int) widthDifference, (int) heightDifference);
        graphics2D.dispose();

        return frame;
    }
}
