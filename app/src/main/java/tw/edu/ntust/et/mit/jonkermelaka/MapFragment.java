package tw.edu.ntust.et.mit.jonkermelaka;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import tw.edu.ntust.et.mit.jonkermelaka.model.InfoData;

/**
 * Created by 123 on 2015/1/27.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback
        , GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    public static final String TAG = "MapFragment";

    private static final double CENTRAL_LAT = 2.196835;
    private static final double CENTRAL_LNG = 102.247305;
    private static final float DEFAULT_ZOOM_SIZE = 17f;

    private SupportMapFragment mMapFragment;
    private Marker mPositionMarker;
    private GoogleMap mMap;
    private List<InfoData> mInfos;
    private List<Marker> mFoodMarkers = new ArrayList<>();
    private List<Marker> mHotSpotMarkers = new ArrayList<>();
    private RadioGroup mRadioGroup;

    private Map<Marker, InfoData> mInfoMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group_list_selector);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .zoomControlsEnabled(false);

        mMapFragment = SupportMapFragment.newInstance(options);
        getChildFragmentManager().beginTransaction()
                .add(R.id.map_frame, mMapFragment, null)
                .commit();

        mMapFragment.getMapAsync(this);

        mInfoMap = new HashMap<>();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        moveCamera(CENTRAL_LAT, CENTRAL_LNG, DEFAULT_ZOOM_SIZE);

        markerInit();
        radioGroupInit();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mPositionMarker = marker;

        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // InfoData data = mInfoMap.get(marker);
    }

    private void markerInit() {
        updateData();
    }

    private void setMarkers() {
        mMap.clear();

        for (InfoData data : mInfos) {
            Location loc = data.getLocation();
            List<Marker> markers = mHotSpotMarkers;
            int pinIconResourceId = R.drawable.hotspot_pin;


            switch(data.getType()) {
                case FOOD_CHINESE:
                case FOOD_NYONYA:
                    markers = mFoodMarkers;
                    pinIconResourceId = R.drawable.food_pin;
                    break;
                case SPOT_ASSOCIATION:
                case SPOT_TEMPLE:
                case SPOT_TRADITION:
                    markers = mHotSpotMarkers;
                    pinIconResourceId = R.drawable.hotspot_pin;
                    break;
            }

            if (markers != null) {
                int scaleFactor = 4;
                Bitmap pinIcon = BitmapFactory.decodeResource(getResources(), pinIconResourceId);
                pinIcon = Bitmap.createScaledBitmap(pinIcon, pinIcon.getWidth() / scaleFactor,
                        pinIcon.getHeight() / scaleFactor, false);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(pinIcon))
                        .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                        .title(data.getName())
                        .snippet(data.getBriefDescription())
                        .visible(false));
                markers.add(marker);
                mInfoMap.put(marker, data);
            }
        }

        mRadioGroup.check(R.id.map_all_btn);

        setMarkerVisibility(mFoodMarkers, true);
        setMarkerVisibility(mHotSpotMarkers, true);
    }

    private void radioGroupInit() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                moveCamera(CENTRAL_LAT, CENTRAL_LNG, DEFAULT_ZOOM_SIZE);

                boolean foodMarkerVisible = false;
                boolean spotMarkerVisible = false;

                switch (checkedId) {
                    case R.id.map_food_btn:
                        foodMarkerVisible = true;
                        break;
                    case R.id.map_hotspot_btn:
                        spotMarkerVisible = true;
                        break;
                    case R.id.map_all_btn:
                        foodMarkerVisible = true;
                        spotMarkerVisible = true;
                        break;
                }

                setMarkerVisibility(mFoodMarkers, foodMarkerVisible);
                setMarkerVisibility(mHotSpotMarkers, spotMarkerVisible);
            }
        });
    }

    private void setMarkerVisibility(List<Marker> markers, boolean visible) {
        for (Marker m : markers) {
            m.setVisible(visible);
        }
    }

    private void moveCamera(double lat, double lng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng), zoom));
    }

    private void updateData() {
        String language = Locale.getDefault().toString();
        String queryTable = "InfoEng";
        if ("zh_TW".equals(language) || "zh_HK".equals(language)) {
            queryTable = "InfoCht";
        } else if ("zh_CN".equals(language) || "zh_SG".equals(language)) {
            queryTable = "InfoChs";
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(queryTable);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("public", true);
        query.findInBackground(mParseInfoDataCallback);
    }

    private FindCallback<ParseObject> mParseInfoDataCallback = new FindCallback<ParseObject>() {
        public void done(List<ParseObject> list, ParseException e) {
            if (e == null) {
                Log.d(TAG, "Updated info data successfully: size: " + list.size());
                mInfos = InfoData.adaptParseObjects(list);
                setMarkers();
            } else {
                Log.d(TAG, "Updated info data failed");
                e.printStackTrace();
            }
        }
    };
}
