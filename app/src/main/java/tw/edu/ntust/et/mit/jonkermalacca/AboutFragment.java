package tw.edu.ntust.et.mit.jonkermalacca;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tw.edu.ntust.et.mit.jonkermalacca.component.AboutAdapter;
import tw.edu.ntust.et.mit.jonkermalacca.component.BaseActivity;
import tw.edu.ntust.et.mit.jonkermalacca.component.Utility;
import tw.edu.ntust.et.mit.jonkermalacca.model.PhotoData;

/**
 * Created by 123 on 2015/2/7.
 */
public class AboutFragment extends Fragment implements
        MainActivity.OnBackPressedListener,
        SlideExpandableListAdapter.OnItemExpandCollapseListener,
        AboutAdapter.OnPhotoClickListener, AboutAdapter.OnUrlClickListener,
        ListView.OnTouchListener {
    public static final String TAG = "AboutFragment";

    private static final int PULL_DOWN_THRESHOLD_LENGTH = 125;

    private static final float TITLE_TEXT_ZOOM_SCALE = 0;
    private static final float SUBTITLE_TEXT_ZOOM_SCALE = 1.7f;

    private int touchDownX;
    private int touchDownY;
    private boolean onPullDown;

    private TextView mTitleTxtView;
    private TextView mSubtitleTxtView;
    private ViewGroup mListLayout;
    private ListView mListView;
    private AboutAdapter mAdapter;
    private SlideExpandableListAdapter mSlideExpandableAdapter;

    private SwipeLayout mSwipeView;
    private ViewGroup mSwipePullDownView;
    private ImageView mSwipeIndicator;


    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity activity = (BaseActivity) getActivity();
        activity.initTracker(activity, TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListLayout = (ViewGroup) rootView.findViewById(R.id.list_items_layout);

        mTitleTxtView = (TextView) rootView.findViewById(R.id.list_section_title);
        mSubtitleTxtView = (TextView) rootView.findViewById(R.id.list_subsection_title);

        mTitleTxtView.setText(R.string.title_about);
        mSubtitleTxtView.setText(R.string.title_about_orign);

        ((BaseActivity) getActivity()).getImageLoader()
                .load(R.drawable.cover_about)
                .config(Bitmap.Config.RGB_565)
                .resize(1080, 432)
                .into((ImageView) rootView.findViewById(R.id.list_subcategory));


        rootView.findViewById(R.id.list_cover_layout).setOnTouchListener(this);
        rootView.findViewById(R.id.list_right_button).setVisibility(View.GONE);
        rootView.findViewById(R.id.list_left_button).setVisibility(View.GONE);

        swipeViewInit(rootView);
        listViewInit(rootView);

        return rootView;
    }

    protected void swipeViewInit(View rootView) {
        mSwipeView = (SwipeLayout) rootView.findViewById(R.id.list_swipe_layout);
        mSwipeView.setShowMode(SwipeLayout.ShowMode.PullOut);
        mSwipeView.setDragEdge(SwipeLayout.DragEdge.Top);
        mSwipeView.addSwipeListener(mSwipeListener);
        mSwipeView.setOnTouchListener(this);

        mSwipePullDownView = (ViewGroup) rootView.findViewById(R.id.list_swipe_down_layout);
        mSwipeIndicator = (ImageView) rootView.findViewById(R.id.list_swipe_indicator);

        ((ViewGroup) rootView.findViewById(R.id.list_swipe_down_wrapper))
                .addView(LayoutInflater.from(getActivity()).inflate(R.layout.list_swipe_about, null, false));
    }


    protected void listViewInit(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_items);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOnTouchListener(this);

        mAdapter = new AboutAdapter((BaseActivity) getActivity());
        mAdapter.setOnItemGalleryClickListener(this);
        mAdapter.setOnUrlClickListener(this);

        mSlideExpandableAdapter = new SlideExpandableListAdapter(
                mListView,
                mAdapter,
                R.id.about_item_cover_layout,
                R.id.about_item_expand_layout);
        mSlideExpandableAdapter.setAnimationDuration(500);
        mSlideExpandableAdapter.setItemExpandCollapseListener(this);
        mListView.setAdapter(mSlideExpandableAdapter);

        dataInit();
    }

    private void dataInit() {
        List<AboutAdapter.Item> items = new ArrayList<AboutAdapter.Item>();
        items.add(instanceItem(1000, getString(R.string.about_ntust_name),
                getString(R.string.about_ntust_description),
                getString(R.string.about_ntust_website),
                null, R.drawable.ntust_cover));
        items.add(instanceItem(1001, getString(R.string.about_pfms_name),
                getString(R.string.about_pfms_description),
                getString(R.string.about_pfms_website),
                null, R.drawable.pfms_cover));
        items.add(instanceItem(1002, getString(R.string.about_whc_name),
                getString(R.string.about_whc_description),
                getString(R.string.about_whc_website), null, R.drawable.whc_cover));

        mAdapter.addAll(items);
    }

    private AboutAdapter.Item instanceItem(int id, String name,
            String description, String webUrl, List<PhotoData> photos,
            int coverResouceId) {

        AboutAdapter.Item item = new AboutAdapter.Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setWebsiteUrl(webUrl);
        item.setPhotos(photos);
        item.setCoverResourceId(coverResouceId);

        return item;
    }

    @Override
    public boolean onBackPressed() {
        return mSlideExpandableAdapter.collapseLastOpen();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = false;
        int dist;

        switch (v.getId()) {
            case R.id.list_cover_layout:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDownX = (int) event.getRawX();
                    touchDownY = (int) event.getRawY();
                }

                dist = (int) event.getRawY() - touchDownY;

                if (SwipeLayout.Status.Open.equals(mSwipeView.getOpenStatus())) {
                    mSwipeView.close();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE && dist > Utility.convertDpToPixel(
                        30, getActivity())) {
                    mSwipeView.open();
                }
                result = true;

                break;
            case R.id.list_swipe_layout:
                if (SwipeLayout.Status.Close.equals(
                        mSwipeView.getOpenStatus())) {
                    mListView.dispatchTouchEvent(event);
                    result = true;
                }
                break;
            case R.id.list_items:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDownX = (int) event.getRawX();
                    touchDownY = (int) event.getRawY();
                }

                dist = (int) event.getRawY() - touchDownY;

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
                break;
        }

        return result;
    }

    private SwipeLayout.SwipeListener mSwipeListener =
            new SwipeLayout.SwipeListener() {
                private boolean mInitialized = false;
                private int mDefaultTitleTxtSize;
                private int mDefaultSubtitleTxtSize;
                private boolean iconSetup = false;

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    float openRatio = (float) topOffset / layout.getMeasuredHeight();
                    setOpenRatio(openRatio);

                    if (!iconSetup && openRatio > 0.95f) {
                        mSwipeIndicator.setImageResource(R.drawable.swipe_up);
                        iconSetup = true;
                        mSwipeView.open();
                    }
                }

                @Override
                public void onStartOpen(SwipeLayout swipeLayout) {
                    mSwipeIndicator.setImageResource(R.drawable.swipe_down);
                    iconSetup = false;

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
                    mTitleTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            mDefaultTitleTxtSize * lerp(1.0f, TITLE_TEXT_ZOOM_SCALE, ratio));
                    mSubtitleTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            mDefaultSubtitleTxtSize * lerp(1.0f, SUBTITLE_TEXT_ZOOM_SCALE, ratio));

                }

                private float lerp(float x, float y, float ratio) {
                    return x * (1.0f - ratio) + y * ratio;
                }

            };

    @Override
    public void onExpand(View itemView, int position) {
        AboutAdapter.Item item = mAdapter.getItem(position);
        item.setExpandViewState(true);

        if (item.getPhotos() == null) {
            updatePhoto(item);
        }
    }

    @Override
    public void onCollapse(View itemView, int position) {
        AboutAdapter.Item item = mAdapter.getItem(position);
        item.setExpandViewState(false);

    }

    private void updatePhoto(AboutAdapter.Item item) {
        int id = item.getId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("type", id);
        query.whereEqualTo("public", true);
        query.findInBackground(new ParsePhotoDataCallback(item));
    }

    private class ParsePhotoDataCallback extends FindCallback<ParseObject> {
        private AboutAdapter.Item mItem;

        public ParsePhotoDataCallback(AboutAdapter.Item item) {
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

    @Override
    public void onUrlClick(View view, AboutAdapter.Item item) {
        Tracker t = ((BaseActivity) getActivity()).getTracker();
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.category_user_behavior))
                .setAction(getString(R.string.action_open_website))
                .setLabel(item.getName())
                .build());
    }

    @Override
    public void onPhotoClick(AdapterView<?> parent, View view, AboutAdapter.Item item, int position) {
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
}
