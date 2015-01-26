package tw.edu.ntust.et.mit.jonkerstreetguide.component;


import android.location.Location;


import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;


public class Utility {
    public static String calDistance(Location currentLocation, InfoData infoData) {
       return calDistance(currentLocation, infoData.getLocation());
    }

    public static String calDistance(Location currentLocation, Location location) {
        if ( currentLocation == null ){
            return "無法得知距離";
        }
        float distance = currentLocation.distanceTo(location);
        return (distance < 1000f) ? String.format("%.1fm", distance) : String.format("%.1fkm", distance / 1000);
    }

}
