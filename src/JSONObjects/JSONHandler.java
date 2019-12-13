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

    public static void createJSONFile(FileHandler fileHandler, List<Slide> slides, List<String> fileNames, String name, int groupID) {

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

                    String categoryName = slide.getCategoryPath().substring(
                            slide.getCategoryPath().lastIndexOf("/")+1,
                            slide.getCategoryPath().indexOf(".")
                    );

                    JSONSlide JSONslide = new JSONSlide(slide.getFileName() + ".png", categoryName,
                            slide.getHeader(), description, groupID);
                    JSONobject.addJSONSlide(JSONslide);
                }
            }
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(fileHandler.getFileTempDirectory() + File.separator + name + ".json"),"UTF-8")) {
            gson.toJson(JSONobject,writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Slide> readJSONFile(Main main, FileHandler fileHandler, Loader loader) {

        FileFilter filter = pathname -> pathname.getName().endsWith("json");

        File files = fileHandler.getFileTempDirectory();
        File[] jsonFile = files.listFiles(filter);

        if (jsonFile.length == 1) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(jsonFile[0].getAbsolutePath()), "UTF8"));
                StringBuilder jsonObject = new StringBuilder();
                String tempLine;
                while ((tempLine = reader.readLine()) != null)
                    jsonObject.append(tempLine);

                JSONObject JSONobject;
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                JSONobject = gson.fromJson(jsonObject.toString(), JSONObject.class);
                reader.close();
                return JSONobject.getSlides(main, fileHandler, loader);

            } catch (IOException e) {
                Logs.saveLog(e.toString(), "JSONHandler");
            }
        }
        return null;
    }
}