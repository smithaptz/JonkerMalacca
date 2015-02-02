package tw.edu.ntust.et.mit.jonkerstreetguide;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tw.edu.ntust.et.mit.jonkerstreetguide.component.ListAdapter;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.PhotoData;


public class ListFragment extends Fragment implements LocationListener,
        SlideExpandableListAdapter.OnItemExpandCollapseListener,
        ListAdapter.OnPhotoClickListener, ListAdapter.OnMapClickListener {
    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_SUBTITLE = "ARG_SUBTITLE";
    public static final String ARG_QUERY_TYPE = "ARG_QUERY_TYPE";
    public static final String ARG_PAGE_POSITION_TYPE = "ARG_PAGE_POSITION_TYPE";

    public static final int PAGE_POSITION_SINGLE = 0;
    public static final int PAGE_POSITION_LEFT = 1;
    public static final int PAGE_POSITION_MIDDLE = 2;
    public static final int PAGE_POSITION_RIGHT = 3;

    private String mTitle;
    private String mSubtitle;
    private int mQueryType = -1;
    private int mPagePositionType;


    private LocationManager mLocationManager;
    private String mProvider;
    private Location mCurrentLocation;

    private ListView mListView;
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

        if(getArguments() != null) {
            Bundle args = getArguments();
            mTitle = args.getString(ARG_TITLE);
            mSubtitle = args.getString(ARG_SUBTITLE);
            mQueryType = args.getInt(ARG_QUERY_TYPE);
            mPagePositionType = args.getInt(ARG_PAGE_POSITION_TYPE);
        }

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ((TextView) rootView.findViewById(R.id.list_section_title)).setText(mTitle);
        ((TextView) rootView.findViewById(R.id.list_subsection_title)).setText(mSubtitle);

        rootView.findViewById(R.id.list_right_button).setVisibility((
                mPagePositionType == PAGE_POSITION_LEFT ||
                mPagePositionType == PAGE_POSITION_MIDDLE) ?
                View.VISIBLE : View.GONE);

        rootView.findViewById(R.id.list_left_button).setVisibility((
                mPagePositionType == PAGE_POSITION_RIGHT ||
                        mPagePositionType == PAGE_POSITION_MIDDLE) ?
                View.VISIBLE : View.GONE);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setVerticalScrollBarEnabled(false); // disable scroll bar
        //mListView.addFooterView(inflater.inflate(R.layout.dummy_view, null, false));
        mAdapter = new ListAdapter(getActivity());
        mAdapter.setLocation(mCurrentLocation);
        mAdapter.setOnItemGalleryClickListener(this);
        mAdapter.setOnMapClickListener(this);

        mSlideExpandableAdapter = new SlideExpandableListAdapter(
                mListView,
                mAdapter,
                R.id.list_item_expand_btn,
                R.id.list_item_expand_layout);
        mSlideExpandableAdapter.setAnimationDuration(500);
        mSlideExpandableAdapter.setItemExpandCollapseListener(this);
        mListView.setAdapter(mSlideExpandableAdapter);

        updateData();

        return rootView;
    }

    public static ListFragment newInstance(String title, String subtitle, int queryType, int pagePositionType) {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SUBTITLE, subtitle);
        args.putInt(ARG_QUERY_TYPE, queryType);
        args.putInt(ARG_PAGE_POSITION_TYPE, pagePositionType);
        fragment.setArguments(args);

        return fragment;
    }

    private void updateData() {
        System.out.println("--------------------------UpdateData");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("InfoEng");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("type", mQueryType);
        query.whereEqualTo("public", true);
        query.findInBackground(new ParseInfoDataCallback());
    }

    private void updatePhoto(ListAdapter.Item item) {
        String referenceId = item.getInfoData().getReferenceId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("referenceId", referenceId);
        query.whereEqualTo("public", true);
        query.findInBackground(new ParsePhotoDataCallback(item));
    }

    @Override
    public void onPhotoClick(AdapterView<?> parent, View view, ListAdapter.Item item, int position) {
        String language = Locale.getDefault().getLanguage().toString();
        ArrayList<String> photoUrls = new ArrayList<String>();
        ArrayList<String> photoDescriptions = new ArrayList<String>();

        for(PhotoData photoData : item.getPhotos()) {
            photoUrls.add(photoData.getUrl());
            String description;
            if("zh_TW".equals(language) || "zh_HK".equals(language)) {
                description = photoData.getDescriptionCht();
            } else if("zh_CN".equals(language)) {
                description = photoData.getDescriptionChs();
            } else {
                description = photoData.getDescriptionEng();
            }
            photoDescriptions.add(description);
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ImageViewerActivity.ARG_PHOTO_URL_LIST, photoUrls);
        bundle.putStringArrayList(ImageViewerActivity.ARG_PHOTO_DESCRIPTION_LIST, photoDescriptions);
        bundle.putInt(ImageViewerActivity.ARG_FIRST_PHOTO_INDEX, position);

        Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    @Override
    public void onMapClick(InfoData infoData) {
        final Location location = infoData.getLocation();
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Warning")
                .setMessage("Open google map")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = (mCurrentLocation == null) ?
                                String.format("https://maps.google.com/maps?q=%f,%f",
                                location.getLatitude(), location.getLongitude()) :
                                String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                                        mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                                        location.getLatitude(), location.getLongitude());
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                }).create();
        dialog.show();
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

        System.out.println("----------mListView == null: " + (mListView == null) + ", " + position);
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
}
