package tw.edu.ntust.et.mit.jonkerstreetguide;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.daimajia.swipe.SwipeLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tw.edu.ntust.et.mit.jonkerstreetguide.component.FastBlur;
import tw.edu.ntust.et.mit.jonkerstreetguide.component.ListAdapter;
import tw.edu.ntust.et.mit.jonkerstreetguide.component.Utility;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.PhotoData;


public class ListFragment extends Fragment implements LocationListener,
        SlideExpandableListAdapter.OnItemExpandCollapseListener,
        ListAdapter.OnPhotoClickListener, ListAdapter.OnMapClickListener,
        ListView.OnTouchListener {
    public static final String TAG = "ListFragment";

    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_SUBTITLE = "ARG_SUBTITLE";
    public static final String ARG_QUERY_TYPE = "ARG_QUERY_TYPE";
    public static final String ARG_PAGE_POSITION_TYPE = "ARG_PAGE_POSITION_TYPE";

    public static final int PAGE_POSITION_SINGLE = 0;
    public static final int PAGE_POSITION_LEFT = 1;
    public static final int PAGE_POSITION_MIDDLE = 2;
    public static final int PAGE_POSITION_RIGHT = 3;

    private static final int PULL_DOWN_THRESHOLD_LENGTH = 125;

    private static final float BLUR_SCALE_DOWN_FACTOR = 12f;
    private static final int BLUR_SAMPLE_RADIUS = 4;

    private String mTitle;
    private String mSubtitle;
    private int mQueryType = -1;
    private int mPagePositionType;

    private int touchDownX;
    private int touchDownY;
    private boolean onPullDown;

    private TextView mTitleTxtView;
    private TextView mSubtitleTxtView;

    private LocationManager mLocationManager;
    private String mProvider;
    private Location mCurrentLocation;

    private ViewGroup mListLayout;
    private ListView mListView;
    private ListAdapter mAdapter;
    private SlideExpandableListAdapter mSlideExpandableAdapter;

    private SwipeLayout mSwipeView;
    private ViewGroup mSwipePullDownView;

    private Bitmap mBlurBackground;
    private Bitmap mBackground;
    private Bitmap mTransBackground;


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

        if (getArguments() != null) {
            Bundle args = getArguments();
            mTitle = args.getString(ARG_TITLE);
            mSubtitle = args.getString(ARG_SUBTITLE);
            mQueryType = args.getInt(ARG_QUERY_TYPE);
            mPagePositionType = args.getInt(ARG_PAGE_POSITION_TYPE);
        }

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListLayout = (ViewGroup) rootView.findViewById(R.id.list_items_layout);

        mTitleTxtView = (TextView) rootView.findViewById(R.id.list_section_title);
        mSubtitleTxtView = (TextView) rootView.findViewById(R.id.list_subsection_title);

        mTitleTxtView.setText(mTitle);
        mSubtitleTxtView.setText(mSubtitle);

        rootView.findViewById(R.id.list_right_button).setVisibility((
                mPagePositionType == PAGE_POSITION_LEFT ||
                mPagePositionType == PAGE_POSITION_MIDDLE) ?
                View.VISIBLE : View.GONE);

        rootView.findViewById(R.id.list_left_button).setVisibility((
                mPagePositionType == PAGE_POSITION_RIGHT ||
                        mPagePositionType == PAGE_POSITION_MIDDLE) ?
                View.VISIBLE : View.GONE);

        mListView = (ListView) rootView.findViewById(R.id.list_items);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOnTouchListener(this);

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

        mSwipeView =  (SwipeLayout) rootView.findViewById(R.id.list_swipe_layout);
        mSwipeView.setShowMode(SwipeLayout.ShowMode.PullOut);
        mSwipeView.setDragEdge(SwipeLayout.DragEdge.Top);
        mSwipeView.addSwipeListener(mSwipeListener);
        mSwipeView.setOnTouchListener(this);

        mSwipePullDownView = (ViewGroup) rootView.findViewById(R.id.list_swipe_down_layout);


        ((ViewGroup) rootView.findViewById(R.id.list_swipe_down_wrapper))
                .addView(inflater.inflate(R.layout.test, null, false));

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
        String language = Locale.getDefault().toString();
        ArrayList<String> photoUrls = new ArrayList<String>();
        ArrayList<String> photoDescriptions = new ArrayList<String>();

        for (PhotoData photoData : item.getPhotos()) {
            photoUrls.add(photoData.getUrl());
            String description;
            if ("zh_TW".equals(language) || "zh_HK".equals(language)) {
                description = photoData.getDescriptionCht();
            } else if ("zh_CN".equals(language) || "zh_SG".equals(language)) {
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
        String url = (mCurrentLocation == null) ?
                String.format("https://maps.google.com/maps?q=%f,%f",
                        location.getLatitude(), location.getLongitude()) :
                String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                        mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                        location.getLatitude(), location.getLongitude());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = false;

        if (v.equals(mSwipeView)) {
            SwipeLayout.Status openStatus = mSwipeView.getOpenStatus();
            if (SwipeLayout.Status.Close.equals(openStatus)) {
                mListView.dispatchTouchEvent(event);
                result = true;
            }
        } else if (v.equals(mListView)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchDownX = (int) event.getRawX();
                touchDownY = (int) event.getRawY();
            }

            int dist = (int) event.getRawY() - touchDownY;

            if (mListView.getFirstVisiblePosition() == 0 &&
                    mListView.getChildAt(0).getTop() >= 0) {
                onPullDown = true;

                if (event.getAction() == MotionEvent.ACTION_MOVE && dist > 0) {
                    result = true;
                } else if ((event.getAction() == MotionEvent.ACTION_CANCEL ||
                        event.getAction() == MotionEvent.ACTION_UP) &&
                        dist > Utility.convertDpToPixel(
                                PULL_DOWN_THRESHOLD_LENGTH, getActivity())) {
                    mSwipeView.open();
                }
            }

            if (onPullDown) {
                mSwipeView.onTouchEvent(event);
            }

            if (event.getAction() == MotionEvent.ACTION_CANCEL ||
                    event.getAction() == MotionEvent.ACTION_UP) {
                onPullDown = false;
            }

        }

        return result;
    }

    private class ParseInfoDataCallback extends FindCallback<ParseObject> {
        public void done(List<ParseObject> list, ParseException e) {
            if (e == null) {
                Log.d(TAG, "Updated info data successfully: size: " + list.size());
                mInfos = InfoData.adaptParseObjects(list);
                mAdapter.addAll(transToAdapterItems(mInfos));
                mSlideExpandableAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Updated info data failed");
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
            if (e == null) {
                Log.d(TAG, "Updated photo data successfully: size: " + list.size());
                mItem.setPhotos(PhotoData.adaptParseObjects(list));
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Updated photo data failed");
                e.printStackTrace();
            }
        }
    };

    private SwipeLayout.SwipeListener mSwipeListener =
            new SwipeLayout.SwipeListener() {
        private boolean mInitialized = false;
        private int mDefaultTitleTxtSize;
        private int mDefaultSubtitleTxtSize;

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
            float openRatio = (float) topOffset/ layout.getMeasuredHeight();
            setOpenRatio(openRatio);
        }

        @Override
        public void onStartOpen(SwipeLayout swipeLayout) {
            mListLayout.buildDrawingCache();
            mBackground = mListLayout.getDrawingCache();
            captureBlurBackground(mBackground, mSwipeView);

            if (mTransBackground == null || mTransBackground.isRecycled()) {
                mTransBackground = Bitmap.createBitmap(mBlurBackground.getWidth(),
                        mBlurBackground.getHeight(), Bitmap.Config.ARGB_8888);
                mSwipePullDownView.setBackground(new BitmapDrawable(getResources(), mTransBackground));
            }

            if (mInitialized) {
                return;
            }
            mDefaultTitleTxtSize = (int) mTitleTxtView.getTextSize();
            mDefaultSubtitleTxtSize = (int) mSubtitleTxtView.getTextSize();

            mInitialized = true;
        }

        @Override
        public void onOpen(SwipeLayout layout) {
        }

        @Override
        public void onStartClose(SwipeLayout swipeLayout) {
        }

        @Override
        public void onClose(SwipeLayout layout) {
        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            if (!mInitialized) {
                return;
            }

            SwipeLayout.Status openStatus = layout.getOpenStatus();

            if (SwipeLayout.Status.Open.equals(openStatus)) {
                setOpenRatio(1.0f);
            } else if (SwipeLayout.Status.Close.equals(openStatus)) {
                setOpenRatio(0);
            }
        }

        private void setOpenRatio(float ratio) {
            mTitleTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX ,
                    mDefaultTitleTxtSize * lerp(1.0f, 0, ratio));
            mSubtitleTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX ,
                    mDefaultSubtitleTxtSize * lerp(1.0f, 2.0f, ratio));
            mixTransBackground(mTransBackground, mBlurBackground, ratio);
        }

        private float lerp(float x, float y, float ratio) {
            return x * (1.0f - ratio) + y * ratio;
        }

    };


    private List<ListAdapter.Item> transToAdapterItems(List<InfoData> infos) {
        List<ListAdapter.Item> items = new ArrayList<ListAdapter.Item>();
        for (InfoData infoData : infos) {
            items.add(new ListAdapter.Item(infoData));
        }

        return items;
    }


    @Override
    public void onExpand(View itemView, int position) {
        ListAdapter.Item item = mAdapter.getItem(position);
        item.setExpandViewState(true);

        if (item.getPhotos() == null) {
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

    private void captureBlurBackground(Bitmap bitmap, View view) {
        mBlurBackground = Bitmap.createBitmap((int)
                        (view.getMeasuredWidth()/BLUR_SCALE_DOWN_FACTOR),
                (int) (view.getMeasuredHeight()/BLUR_SCALE_DOWN_FACTOR),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBlurBackground);
        canvas.translate(-view.getLeft()/BLUR_SCALE_DOWN_FACTOR,
                -view.getTop()/BLUR_SCALE_DOWN_FACTOR);
        canvas.scale(1 / BLUR_SCALE_DOWN_FACTOR, 1 /
                BLUR_SCALE_DOWN_FACTOR);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        mBlurBackground = FastBlur.doBlur(mBlurBackground,
                BLUR_SAMPLE_RADIUS, true);
    }

    private void mixTransBackground(Bitmap bitmap, Bitmap blurBitmap, float ratio) {
        int width = blurBitmap.getWidth();
        int height = blurBitmap.getHeight();

        System.out.println("w: " + width + ", h: " + height);
        int boundary = (int) (height * (1.0f - ratio));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (y < boundary) {
                    bitmap.setPixel(x, y, 0xffffffff);
                } else {
                    bitmap.setPixel(x, y, blurBitmap.getPixel(x, y - boundary));
                }
            }
        }
    }

}
