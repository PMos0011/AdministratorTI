package Common;

import Slide.Slide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JSONHandler {

    public static void createJSONFile(FileHandler fileHandler, List<Slide> slides, List<String> fileNames) {

        JSONObject JSONobject = new JSONObject();

        for (String fileName : fileNames) {
            for (Slide slide : slides) {
                if (fileName.equals(slide.getFileName())) {
                    String description = "";
                    if(slide.getFirstDescription()!=null)
                        description+=slide.getFirstDescription();
                    if(slide.getSecondDescription()!=null)
                        description+=slide.getSecondDescription();
                    JSONSlide JSONslide = new JSONSlide(slide.getFileName(), slide.getCategoryPath(),
                            slide.getHeader(), description);
                    JSONobject.addJSONSlide(JSONslide);
                }
            }
        }


        Gson gson = new Gson();
        String JSONString = gson.toJson(JSONobject);

        JSONSave(fileHandler, JSONString);

    }

    public static void readJSONFile(FileHandler fileHandler) {

        FileFilter filter = pathname -> pathname.getName().endsWith("txt");

        File files = fileHandler.getTempDirectory();
        File[] jsonFile = files.listFiles(filter);

        if (jsonFile.length == 1) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(jsonFile[0]));
                StringBuilder jsonObject = new StringBuilder();
                String tempLine;
                while ((tempLine = reader.readLine()) != null)
                    jsonObject.append(tempLine);

                Type targetClassType = new TypeToken<ArrayList<JSONSlide>>() {
                }.getType();
                Collection<JSONSlide> slideList = new Gson().fromJson(String.valueOf(jsonObject), targetClassType);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void JSONSave(FileHandler fileHandler, String json) {

        File fileName = new File(fileHandler.getTempDirectory(), "json.txt");
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "fileHandler");
        }
    }
}

class JSONSlide {
    private String sa;      //file name (path)
    private String ti;      //category name
    private String th;      //header
    private String tt;      //description (both lines)

    public JSONSlide(String fileName, String category, String header, String description) {
        sa = fileName;
        ti = category;
        th = header;
        tt = description;
    }
}

class JSONObject {

    private Collection<JSONSlide> slides = new ArrayList<>();

    public void addJSONSlide(JSONSlide slide) {
        slides.add(slide);
    }


}