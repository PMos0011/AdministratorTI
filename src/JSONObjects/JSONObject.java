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
            File file = new File(fileHandler.getTempDirectory(), slide.getFileName());
            Slide tempSlide = main.setImage(file);
            tempSlide.setHeader(slide.getHeader());
            tempSlide.setCategoryPath(slide.getCategory());
            tempSlide.setCategory(loader.imageLoad(slide.getCategory()));
            tempSlide.setFileName(slide.getFileName());

            String[] description = slide.getDescription().split("<br>");
            tempSlide.setFirstDescription(description[0]);
            tempSlide.setSecondDescription(description[1]);

            slidesList.add(tempSlide);
        }

        return slidesList;
    }
}
