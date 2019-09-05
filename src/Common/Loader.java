package Common;

import Slide.Slide;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

    public Slide loadInitialSlide(){
        Slide slide = new Slide(imageLoad("src/images/dok.png"),
                imageLoad("src/images/Cont_background.png"),
                "0", "NAGŁÓWEK", "Opis slajdu", "opis slajdu", 0);

        return slide;
    }
}
