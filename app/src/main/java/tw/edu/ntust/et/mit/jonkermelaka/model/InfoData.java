package tw.edu.ntust.et.mit.jonkermelaka.model;

import android.location.Location;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 2015/1/26.
 */

public class InfoData {
    public static final String REFERENCE_ID = "referenceId";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String BRIEF_DESCRIPTION = "briefDescription";
    public static final String LOGO_URL = "logoUrl";
    public static final String BUSINESS_HOUR = "businessHour";
    public static final String LOCATION = "location";
    public static final String ADDRESS = "address";
    public static final String TYPE = "type";

    protected ParseObject mParseObject;

    public enum Type {
        FOOD_CHINESE, FOOD_NYONYA, SPOT_TRADITION,
        SPOT_ASSOCIATION, SPOT_TEMPLE, OTHER
    }

    public InfoData(ParseObject parseObject) {
        mParseObject = parseObject;
    }

    public String getId() {
        return mParseObject.getObjectId();
    }

    public String getReferenceId() {
        return mParseObject.getString(REFERENCE_ID);
    }

    public String getName() {
        return mParseObject.getString(NAME);
    }

    public String getBriefDescription() {
        return mParseObject.getString(BRIEF_DESCRIPTION);
    }

    public String getDescription() {
        return mParseObject.getString(DESCRIPTION);
    }

    public String getBusinessHour() {
        return mParseObject.getString(BUSINESS_HOUR);
    }

    public String getLogoUrl() {
        return mParseObject.getString(LOGO_URL);
    }

    public Location getLocation() {
        ParseGeoPoint geoPoint =  mParseObject.getParseGeoPoint(LOCATION);
        Location location = new Location(getName());

        if (geoPoint != null) {
            location.setLatitude(geoPoint.getLatitude());
            location.setLongitude(geoPoint.getLongitude());
        }

        return location;
    }

    public String getAddress() {
        return mParseObject.getString(ADDRESS);
    }

    public Type getType() {
        switch(mParseObject.getInt(TYPE)) {
            case 0:
                return Type.FOOD_CHINESE;
            case 1:
                return Type.FOOD_NYONYA;
            case 2:
                return Type.SPOT_TRADITION;
            case 3:
                return Type.SPOT_ASSOCIATION;
            case 4:
                return Type.SPOT_TEMPLE;
            default:
                return Type.OTHER;
        }
    }

    public static List<InfoData> adaptParseObjects(List<ParseObject> parseObjects) {
        List<InfoData> list = new ArrayList<InfoData>();
        for (ParseObject parseObject : parseObjects) {
            list.add(new InfoData(parseObject));
        }

        return list;
    }
}

