package JSONObjects;

import Common.FileHandler;
import Common.Loader;
import MainWindow.Main;
import Slide.Slide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONObject {
    private Collection<JSONSlide> slides = new ArrayList<>();

    public void addJSONSlide(JSONSlide slide) {
        slides.add(slide);
    }

    public List<Slide> getSlides(Main main, FileHandler fileHandler, Loader loader) {
        List<Slide> slidesList = new ArrayList<Slide>();

        for (JSONSlide slide : slides) {
            String fileName = slide.getFileName().substring(
                    slide.getFileName().lastIndexOf("/")+1);

            File file = new File(fileHandler.getFileTempDirectory(), fileName);
            Slide tempSlide = main.setImage(file);
            tempSlide.setHeader(slide.getHeader());
            tempSlide.setCategoryPath(Main.CATEGORY_PATH + slide.getCategory() + ".png");
            tempSlide.setCategory(loader.imageLoad(Main.CATEGORY_PATH + slide.getCategory() + ".png"));
            tempSlide.setFileName(fileName.substring(0,fileName.indexOf(".")));

            String[] description = slide.getDescription().split("<br>");
            tempSlide.setFirstDescription(description[0]);
            tempSlide.setSecondDescription(description[1]);

            slidesList.add(tempSlide);
        }

        return slidesList;
    }
}
