package JSONObjects;

import Common.FileHandler;
import Common.Loader;
import Common.Logs;
import MainWindow.Main;
import Slide.Slide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class JSONHandler {

    public static void createJSONFile(FileHandler fileHandler, List<Slide> slides, List<String> fileNames) {

        JSONObject JSONobject = new JSONObject();
        int counter = 1;

        for (String fileName : fileNames) {
            for (Slide slide : slides) {
                if (fileName.equals(slide.getFileName())) {

                    String description = "";
                    if (slide.getFirstDescription() != null)
                        description += slide.getFirstDescription() + "<br>";
                    else
                        description += "<br>";
                    if (slide.getSecondDescription() != null)
                        description += slide.getSecondDescription() + "<br>";
                    else
                        description += "<br>";
                    description += counter + "/" + slides.size();
                    counter++;

                    JSONSlide JSONslide = new JSONSlide(slide.getFileName() + ".png", slide.getCategoryPath(),
                            slide.getHeader(), description);
                    JSONobject.addJSONSlide(JSONslide);
                }
            }
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String JSONString = gson.toJson(JSONobject);

        JSONSave(fileHandler, JSONString);

    }

    public static List<Slide> readJSONFile(Main main, FileHandler fileHandler, Loader loader) {

        FileFilter filter = pathname -> pathname.getName().endsWith("json");

        File files = fileHandler.getTempDirectory();
        File[] jsonFile = files.listFiles(filter);

        if (jsonFile.length == 1) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(jsonFile[0]));
                StringBuilder jsonObject = new StringBuilder();
                String tempLine;
                while ((tempLine = reader.readLine()) != null)
                    jsonObject.append(tempLine);

                JSONObject JSONobject;
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                JSONobject = gson.fromJson(jsonObject.toString(), JSONObject.class);

                return JSONobject.getSlides(main, fileHandler, loader);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void JSONSave(FileHandler fileHandler, String json) {

        File fileName = new File(fileHandler.getTempDirectory(), "json.json");
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            Logs.saveLog(e.toString(), "fileHandler");
        }
    }
}