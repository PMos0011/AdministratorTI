package JSONObjects;

public class JSONSlide {
    private String sa;      //file name (path)
    private String ti;      //category name
    private String th;      //header
    private String tt;      //description (both lines)

    public JSONSlide(String fileName, String category, String header, String description, int groupID) {
        sa = "SLIDES/" + groupID + "/" +fileName;
        ti = category;
        th = header;
        tt = description;
    }

    public String getFileName() {
        return sa;
    }

    public String getCategory() {
        return ti;
    }

    public String getHeader() {
        return th;
    }

    public String getDescription() {
        return tt;
    }
}
