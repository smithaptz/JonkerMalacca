package tw.edu.ntust.et.mit.jonkermalacca.model;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 2015/1/26.
 */
public class PhotoData {
    public static final String URL = "url";
    public static final String DESCRIPTION_ENG = "descriptionEng";
    public static final String DESCRIPTION_CHT = "descriptionCht";
    public static final String DESCRIPTION_CHS = "descriptionChs";

    protected ParseObject mParseObject;

    public PhotoData(ParseObject parseObject) {
        mParseObject = parseObject;
    }

    public String getId() {
        return mParseObject.getObjectId();
    }

    public String getUrl() {
        return mParseObject.getString(URL);
    }

    public String getDescriptionEng() {
        return mParseObject.getString(DESCRIPTION_ENG);
    }

    public String getDescriptionCht() {
        return mParseObject.getString(DESCRIPTION_CHT);
    }

    public String getDescriptionChs() {
        return mParseObject.getString(DESCRIPTION_CHS);
    }

    public static List<PhotoData> adaptParseObjects(List<ParseObject> parseObjects) {
        List<PhotoData> list = new ArrayList<PhotoData>();
        for (ParseObject parseObject : parseObjects) {
            list.add(new PhotoData(parseObject));
        }

        return list;
    }
}
