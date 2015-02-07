package tw.edu.ntust.et.mit.jonkerstreetguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

import tw.edu.ntust.et.mit.jonkerstreetguide.component.AboutAdapter;
import tw.edu.ntust.et.mit.jonkerstreetguide.component.FastBlur;
import tw.edu.ntust.et.mit.jonkerstreetguide.component.Utility;

/**
 * Created by 123 on 2015/2/7.
 */
public class AboutFragment extends Fragment implements
        SlideExpandableListAdapter.OnItemExpandCollapseListener,
        AboutAdapter.OnPhotoClickListener, ListView.OnTouchListener {
    public static final String TAG = "AboutFragment";

    private static final int PULL_DOWN_THRESHOLD_LENGTH = 125;

    private static final float BLUR_SCALE_DOWN_FACTOR = 12f;
    private static final int BLUR_SAMPLE_RADIUS = 4;

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

    private Bitmap mBlurBackground;
    private Bitmap mBackground;
    private Bitmap mTransBackground;


    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListLayout = (ViewGroup) rootView.findViewById(R.id.list_items_layout);

        mTitleTxtView = (TextView) rootView.findViewById(R.id.list_section_title);
        mSubtitleTxtView = (TextView) rootView.findViewById(R.id.list_subsection_title);

        mTitleTxtView.setText("關於");
        mSubtitleTxtView.setText("緣起");

        rootView.findViewById(R.id.list_right_button).setVisibility(View.GONE);
        rootView.findViewById(R.id.list_left_button).setVisibility(View.GONE);

        swipeViewInit(rootView);
        listViewInit(rootView);

        return rootView;
    }

    protected void swipeViewInit(View rootView) {
        mSwipeView =  (SwipeLayout) rootView.findViewById(R.id.list_swipe_layout);
        mSwipeView.setShowMode(SwipeLayout.ShowMode.PullOut);
        mSwipeView.setDragEdge(SwipeLayout.DragEdge.Top);
        mSwipeView.addSwipeListener(mSwipeListener);
        mSwipeView.setOnTouchListener(this);

        mSwipePullDownView = (ViewGroup) rootView.findViewById(R.id.list_swipe_down_layout);

        ((ViewGroup) rootView.findViewById(R.id.list_swipe_down_wrapper))
                .addView(LayoutInflater.from(getActivity()).inflate(R.layout.test, null, false));
    }


    protected void listViewInit(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_items);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOnTouchListener(this);

        mAdapter = new AboutAdapter(getActivity());
        mAdapter.setOnItemGalleryClickListener(this);

        mSlideExpandableAdapter = new SlideExpandableListAdapter(
                mListView,
                mAdapter,
                R.id.about_item_expand_btn,
                R.id.about_item_expand_layout);
        mSlideExpandableAdapter.setAnimationDuration(500);
        mSlideExpandableAdapter.setItemExpandCollapseListener(this);
        mListView.setAdapter(mSlideExpandableAdapter);

        dataInit();
    }

    private void dataInit() {
        List<AboutAdapter.Item> items = new ArrayList<AboutAdapter.Item>();
        items.add(instanceItem("國立臺灣科技大學", null, null, null, null, -1));
        items.add(instanceItem("培風中學", null, null, null, null, -1));

        mAdapter.addAll(items);
    }

    private AboutAdapter.Item instanceItem(
            String name, String description, String webUrl,
            String email, List<String> photoUrls, int coverResouceId) {
        AboutAdapter.Item item = new AboutAdapter.Item();
        item.setName(name);
        item.setDescription(description);
        item.setWebsiteUrl(webUrl);
        item.setEmailUrl(email);
        item.setPhotos(photoUrls);
        item.setCoverResourceId(coverResouceId);

        return item;
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
                            mDefaultTitleTxtSize * lerp(1.0f, TITLE_TEXT_ZOOM_SCALE, ratio));
                    mSubtitleTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX ,
                            mDefaultSubtitleTxtSize * lerp(1.0f, SUBTITLE_TEXT_ZOOM_SCALE, ratio));
                    mixTransBackground(mTransBackground, mBlurBackground, ratio);
                }

                private float lerp(float x, float y, float ratio) {
                    return x * (1.0f - ratio) + y * ratio;
                }

            };

    @Override
    public void onExpand(View itemView, int position) {
        AboutAdapter.Item item = mAdapter.getItem(position);
        item.setExpandViewState(true);
    }

    @Override
    public void onCollapse(View itemView, int position) {
        AboutAdapter.Item item = mAdapter.getItem(position);
        item.setExpandViewState(false);

    }

    private void captureBlurBackground(Bitmap bitmap, View view) {
        mBlurBackground = Bitmap.createBitmap((int)
                        (view.getMeasuredWidth()/BLUR_SCALE_DOWN_FACTOR),
                (int) (view.getMeasuredHeight()/BLUR_SCALE_DOWN_FACTOR),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBlurBackground);
        canvas.translate(-view.getLeft() / BLUR_SCALE_DOWN_FACTOR,
                -view.getTop() / BLUR_SCALE_DOWN_FACTOR);
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

    @Override
    public void onPhotoClick(AdapterView<?> parent, View view, AboutAdapter.Item item, int position) {
        ArrayList<String> photoUrls = new ArrayList<String>(item.getPhotos());

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ImageViewerActivity.ARG_PHOTO_URL_LIST, photoUrls);
        bundle.putInt(ImageViewerActivity.ARG_FIRST_PHOTO_INDEX, position);

        Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
}
