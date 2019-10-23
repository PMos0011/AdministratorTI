package Common;

import Slide.Slide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONHandler {

    public static void createJSONFile(FileHandler fileHandler, List<Slide> slides) {
        Collection<JSONFile> jsonList = new ArrayList<>();
        for (Slide slide : slides) {
            jsonList.add(new JSONFile(slide.getCategoryPath(), slide.getHeader(),
                    slide.getFirstDescription(), slide.getSecondDescription()));
        }

        Gson gson = new Gson();
        String JSONString = gson.toJson(jsonList);

        JSONSave(fileHandler, JSONString);

        jsonList.clear();
    }

    public static void readJSONFile(FileHandler fileHandler) {

        FileFilter filter = pathname -> pathname.getName().endsWith("txt");

        File files = fileHandler.getTempDirectory();
        File[] jsonFile = files.listFiles(filter);

       if (jsonFile.length==1){
           try {
               BufferedReader reader = new BufferedReader(new FileReader(jsonFile[0]));
               StringBuilder jsonObject = new StringBuilder();
               String tempLine;
               while ((tempLine = reader.readLine()) !=null)
                   jsonObject.append(tempLine);

               Type targetClassType = new TypeToken<ArrayList<JSONFile>>(){}.getType();
               Collection<JSONFile> slideList = new Gson().fromJson(String.valueOf(jsonObject), targetClassType);

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

class JSONFile {
    private String categoryPath;
    private String header;
    private String firstLine;
    private String secondLine;

    public JSONFile(String categoryPath, String header, String firstLine, String secondLine) {
        this.categoryPath = categoryPath;
        this.header = header;
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }
}