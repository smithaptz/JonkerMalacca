package tw.edu.ntust.et.mit.jonkerstreetguide;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tw.edu.ntust.et.mit.jonkerstreetguide.component.ListAdapter;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.PhotoData;


public class ListFragment extends Fragment implements LocationListener,
        SlideExpandableListAdapter.OnItemExpandCollapseListener {
    public static final String ARG_SUBSECTION_NUM = "ARG_SUBSECTION_NUM";
    public static final int FOOD_SEC_STARTING_NUM = 0;
    public static final int HOT_SPOT_SEC_STARTING_NUM = 2;
    public static final int CULTURE_SEC_STARTING_NUM = 5;
    public static final int MAP_SEC_STARTING_NUM = 6;

    private LocationManager mLocationManager;
    private String mProvider;
    private Location mCurrentLocation;

    private ListAdapter mAdapter;
    private SlideExpandableListAdapter mSlideExpandableAdapter;

    private List<InfoData> mInfos;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(mProvider, 10000, 0, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, false);
        mCurrentLocation = mLocationManager.getLastKnownLocation(mProvider);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("--------------------------onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.listView);

        mAdapter = new ListAdapter(getActivity());
        mAdapter.setLocation(mCurrentLocation);

        mSlideExpandableAdapter = new SlideExpandableListAdapter(
                mAdapter,
                R.id.list_item_expand_btn,
                R.id.list_item_expand_layout);
        mSlideExpandableAdapter.setItemExpandCollapseListener(this);
        lv.setAdapter(mSlideExpandableAdapter);


        updateData();

        return rootView;
    }

    public static ListFragment newInstance(int subsectionNum) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SUBSECTION_NUM, subsectionNum);
        fragment.setArguments(args);
        return fragment;
    }

    private void updateData() {
        System.out.println("--------------------------UpdateData");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("InfoEng");
//        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
//        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("public", true);
        query.findInBackground(new ParseInfoDataCallback());
    }

    private void updatePhoto(ListAdapter.Item item) {
        String referenceId = item.getInfoData().getReferenceId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
//        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
//        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("referenceId", referenceId);
        query.whereEqualTo("public", true);
        query.findInBackground(new ParsePhotoDataCallback(item));
    }

    private class ParseInfoDataCallback extends FindCallback<ParseObject> {
        public void done(List<ParseObject> list, ParseException e) {
            System.out.println("--------------------------parseInfoDataCallback");
            if (e == null) {
                System.out.println("--------------updateSuccessfully ");
                mInfos = InfoData.adaptParseObjects(list);
                mAdapter.addAll(transToAdapterItems(mInfos));
                mSlideExpandableAdapter.notifyDataSetChanged();
            } else {
                System.out.println("--------------fail to update ");
                e.printStackTrace();
            }
        }
    };

    private class ParsePhotoDataCallback extends FindCallback<ParseObject> {
        private ListAdapter.Item mItem;

        public ParsePhotoDataCallback(ListAdapter.Item item) {
            mItem = item;
        }

        public void done(List<ParseObject> list, ParseException e) {
            System.out.println("--------------------------parsePhotoCallback");
            if (e == null) {
                System.out.println("--------------updateSuccessfully: size: " + list.size());
                mItem.setPhotos(PhotoData.adaptParseObjects(list));
                mAdapter.notifyDataSetChanged();
            } else {
                System.out.println("--------------fail to update ");
                e.printStackTrace();
            }
        }
    };



    private List<ListAdapter.Item> transToAdapterItems(List<InfoData> infos) {
        List<ListAdapter.Item> items = new ArrayList<ListAdapter.Item>();

        for(InfoData infoData : infos) {
            items.add(new ListAdapter.Item(infoData));
        }

        return items;
    }

    @Override
    public void onExpand(View itemView, int position) {
        ListAdapter.Item item = mAdapter.getItem(position);
        item.setExpandViewState(true);

        if(item.getPhotos() == null) {
            updatePhoto(item);
        }
    }

    @Override
    public void onCollapse(View itemView, int position) {
        ListAdapter.Item item = mAdapter.getItem(position);
        item.setExpandViewState(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mAdapter.setLocation(mCurrentLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

//    @Override
//    public void onClick(View itemView, View clickedView, int position) {
//        if(clickedView.getTag() == null) {
//            clickedView.setTag(ExpandState.COLLAPSE);
//        }
//
//        if(ExpandState.COLLAPSE.equals(clickedView.getTag())) {
//            clickedView.setTag(ExpandState.EXPAND);
//            ((ImageView) clickedView).setImageResource(R.drawable.up_button);
//        } else {
//            clickedView.setTag(ExpandState.COLLAPSE);
//            ((ImageView) clickedView).setImageResource(R.drawable.up_button);
//        }
//
//    }
//
//    enum ExpandState {
//        COLLAPSE, EXPAND
//    }
}
