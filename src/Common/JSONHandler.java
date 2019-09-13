package Common;

import Slide.Slide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class JSONHandler {

    public static void createJSONFile(FileHandler fileHandler, List<Slide> slides) {
        List<JSONFile> jsonList = new ArrayList<>();
        for (Slide slide : slides) {
            String imagePath = "/TI/temp/" + slide.getHeader() + ".png";
            imagePath = imagePath.replaceAll("\\s+", "");
            jsonList.add(new JSONFile(imagePath, slide.getCategoryPath(), slide.getHeader(),
                    slide.getFirstDescription(), slide.getSecondDescription()));
        }

        Gson gson = new Gson();
        String JSONString = gson.toJson(jsonList);

        fileHandler.JSONSave("json.text",JSONString);

        jsonList.clear();
    }
}

class JSONFile {
    private String imagePath;
    private String categoryPath;
    private String header;
    private String firstLine;
    private String secondLine;

    public JSONFile(String imagePath, String categoryPath, String header, String firstLine, String secondLine) {
        this.imagePath = imagePath;
        this.categoryPath = categoryPath;
        this.header = header;
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }
}