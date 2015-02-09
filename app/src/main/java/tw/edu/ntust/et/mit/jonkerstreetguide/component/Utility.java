package tw.edu.ntust.et.mit.jonkerstreetguide.component;


import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.DisplayMetrics;


import tw.edu.ntust.et.mit.jonkerstreetguide.R;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;


public class Utility {
    public static String calDistance(Context context, Location currentLocation, InfoData infoData) {
       return calDistance(context, currentLocation, infoData.getLocation());
    }

    public static String calDistance(Context context, Location currentLocation, Location location) {
        if ( currentLocation == null ){
            return context.getString(R.string.unknown_distance);
        }
        float distance = currentLocation.distanceTo(location);
        return (distance < 1000f) ? String.format("%.1fm", distance) : String.format("%.1fkm", distance / 1000);
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}
