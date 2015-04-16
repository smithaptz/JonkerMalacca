package tw.edu.ntust.et.mit.jonkermalacca;

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
import android.widget.TextView;

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

import tw.edu.ntust.et.mit.jonkermalacca.component.BaseActivity;
import tw.edu.ntust.et.mit.jonkermalacca.model.InfoData;

/**
 * Created by 123 on 2015/1/27.
 */
public class MapFragment extends Fragment implements View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {
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

    private View mFoodBtn;
    private View mHotSpotBtn;
    private View mAllBtn;

    private TextView mFoodBtnTxt;
    private TextView mHotSpotBtnTxt;
    private TextView mAllBtnTxt;

    private Map<Marker, InfoData> mInfoMap;

    private int currentSelectBtn;

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity activity = (BaseActivity) getActivity();
        activity.initTracker(activity, TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mFoodBtn = rootView.findViewById(R.id.map_food_btn);
        mHotSpotBtn = rootView.findViewById(R.id.map_hotspot_btn);
        mAllBtn = rootView.findViewById(R.id.map_all_btn);

        mFoodBtnTxt = (TextView) rootView.findViewById(R.id.map_food_btn_txt);
        mHotSpotBtnTxt = (TextView) rootView.findViewById(R.id.map_hotspot_btn_txt);
        mAllBtnTxt = (TextView) rootView.findViewById(R.id.map_all_btn_txt);

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
        buttonInit();
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
                case SPOT_WALL:
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

        mAllBtn.callOnClick();

        setMarkerVisibility(mFoodMarkers, true);
        setMarkerVisibility(mHotSpotMarkers, true);
    }


    private void buttonInit() {
        mFoodBtn.setOnClickListener(this);
        mHotSpotBtn.setOnClickListener(this);
        mAllBtn.setOnClickListener(this);
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
        String queryTable = "InfoEng"; // temp
        if ("zh_TW".equals(language) || "zh_HK".equals(language)) {
            queryTable = "InfoCht";
        } else if ("zh_CN".equals(language) || "zh_SG".equals(language)) {
            queryTable = "InfoChs";
        } else if("ms".equals(language) || "ms_MY".equals(language)) {
            queryTable = "InfoMY";
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_food_btn:
                setButtonSelect(R.id.map_food_btn);
                break;
            case R.id.map_hotspot_btn:
                setButtonSelect(R.id.map_hotspot_btn);
                break;
            case R.id.map_all_btn:
                setButtonSelect(R.id.map_all_btn);
                break;
        }
    }

    private void setButtonSelect(int selectBtnId) {
        moveCamera(CENTRAL_LAT, CENTRAL_LNG, DEFAULT_ZOOM_SIZE);

        boolean foodMarkerVisible = false;
        boolean spotMarkerVisible = false;


        int foodBtnBackground = R.drawable.map_button_selector;
        int hotSpotBackground = R.drawable.map_button_selector;
        int allBtnBackground = R.drawable.map_button_selector;
        int foodBtnTxtColor = 0xff9b9b9b;
        int hotSpotBtnTxtColor = 0xff9b9b9b;
        int allBtnTxtColor = 0xff9b9b9b;

        switch (selectBtnId) {
            case R.id.map_food_btn:
                currentSelectBtn = R.id.map_food_btn;
                foodMarkerVisible = true;
                foodBtnBackground = R.drawable.map_button_clicked;
                foodBtnTxtColor = 0xffffffff;
                break;
            case R.id.map_hotspot_btn:
                currentSelectBtn = R.id.map_hotspot_btn;
                spotMarkerVisible = true;
                hotSpotBackground = R.drawable.map_button_clicked;
                hotSpotBtnTxtColor = 0xffffffff;
                break;
            case R.id.map_all_btn:
                currentSelectBtn = R.id.map_all_btn;
                foodMarkerVisible = true;
                spotMarkerVisible = true;
                allBtnBackground = R.drawable.map_button_clicked;
                allBtnTxtColor = 0xffffffff;
                break;
        }

        mFoodBtn.setBackgroundResource(foodBtnBackground);
        mHotSpotBtn.setBackgroundResource(hotSpotBackground);
        mAllBtn.setBackgroundResource(allBtnBackground);

        mFoodBtnTxt.setTextColor(foodBtnTxtColor);
        mHotSpotBtnTxt.setTextColor(hotSpotBtnTxtColor);
        mAllBtnTxt.setTextColor(allBtnTxtColor);

         setMarkerVisibility(mFoodMarkers, foodMarkerVisible);
         setMarkerVisibility(mHotSpotMarkers, spotMarkerVisible);
    }
}


