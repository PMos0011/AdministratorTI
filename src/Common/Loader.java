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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Loader {

    public Image imageLoad(String path) {

        Image image = null;
        try {
            image = new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return image;
    }

    public Image imageLoad(File file) {

        Image image = null;
        try {
            image = new Image(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return image;
    }

    public Slide loadInitialSlide() {
        Slide slide = new Slide(imageLoad("src/images/dok.png"),
                imageLoad("src/images/Cont_background.png"),
                "0", "NAGŁÓWEK", "Opis slajdu", "opis slajdu", 0);

        return slide;
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
            image = SwingFXUtils.toFXImage(pdfImage, null);
            document.close();
            pdfImage.flush();

        } catch (IOException e) {
            e.printStackTrace();
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
            image = SwingFXUtils.toFXImage(tiffImage, null);
            tiffImage.flush();

        } catch (IOException e) {
            e.printStackTrace();
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

        if (result.isPresent())
            return result.get();
        else

            return 1;
    }

    public void resizeImage(Image image) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double proportion = imageWidth / imageHeight;

        double widthDifference;
        double heightDifference;

        if (proportion < 1.41) {
            imageWidth = imageHeight * 1.415;
            widthDifference = (imageWidth - image.getWidth()) / 2;
        } else if (proportion > 1.42) {
            imageHeight = imageWidth / 1.415;
            heightDifference = (imageHeight - image.getHeight()) / 2;
        }

    }
}
