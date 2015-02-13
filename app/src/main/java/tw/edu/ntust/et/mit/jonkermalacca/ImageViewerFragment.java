package tw.edu.ntust.et.mit.jonkermalacca;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tw.edu.ntust.et.mit.jonkermalacca.component.BaseActivity;
import tw.edu.ntust.et.mit.jonkermalacca.model.PhotoData;

/**
 * Created by 123 on 2015/1/27.
 */
public class ImageViewerFragment extends Fragment {
    public static final String TAG = "ImageViewerFragment";

    public static final String ARG_PHOTO_URL_LIST =
            "ARG_PHOTO_URL_LIST";
    public static final String ARG_PHOTO_DESCRIPTION_LIST =
            "ARG_PHOTO_DESCRIPTION_LIST";
    public static final String ARG_FIRST_PHOTO_INDEX =
            "ARG_FIRST_PHOTO_INDEX";

    private List<String> mPhotoUrls;
    private List<String> mPhotoDescriptions;
    private int mFirstPhotoIndex;

    private ViewPager mViewPager;
    private ImagePagerAdapter mImagePagerAdapter;

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity activity = (BaseActivity) getActivity();
        activity.initTracker(activity, TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPhotoUrls = bundle.getStringArrayList(
                    ARG_PHOTO_URL_LIST);
            mPhotoDescriptions = bundle.getStringArrayList(
                    ARG_PHOTO_DESCRIPTION_LIST);
            mFirstPhotoIndex = bundle.getInt(ARG_FIRST_PHOTO_INDEX);
        }

        View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.imgViewPager);
        mImagePagerAdapter = new ImagePagerAdapter(getActivity(), mPhotoUrls, mPhotoDescriptions);
        mViewPager.setAdapter(mImagePagerAdapter);
        mViewPager.setCurrentItem(mFirstPhotoIndex);

        return rootView;
    }

    public static ImageViewerFragment newInstance(List<PhotoData> photos) {
        return newInstance(photos, 0);
    }

    public static ImageViewerFragment newInstance(List<PhotoData> photos, int firstPhotoPosition) {
        String language = Locale.getDefault().getLanguage().toString();
        ArrayList<String> photoUrls = new ArrayList<String>();
        ArrayList<String> photoDescriptions = new ArrayList<String>();

        for (PhotoData photoData : photos) {
            photoUrls.add(photoData.getUrl());
            String description;
            if ("zh_TW".equals(language) || "zh_HK".equals(language)) {
                description = photoData.getDescriptionCht();
            } else if ("zh_CN".equals(language)) {
                description = photoData.getDescriptionChs();
            } else {
                description = photoData.getDescriptionEng();
            }
            photoDescriptions.add(description);
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ARG_PHOTO_URL_LIST, photoUrls);
        bundle.putStringArrayList(ARG_PHOTO_DESCRIPTION_LIST, photoDescriptions);
        bundle.putInt(ARG_FIRST_PHOTO_INDEX, firstPhotoPosition);

        ImageViewerFragment fragment = new ImageViewerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private final Context mContext;
        private final List<String> mPhotoUrls;
        private final List<String> mPhotoDescriptions;


        public ImagePagerAdapter(Context context, List<String> photoUrls,
                                 List<String> photoDescriptions) {
            mContext = context;
            mPhotoUrls = photoUrls;
            mPhotoDescriptions = photoDescriptions;
        }

        @Override
        public int getCount() {
            return mPhotoUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.image_viewer_page,  null);
            ((TextView) view.findViewById(R.id.image_viewer_description))
                    .setText(mPhotoDescriptions.get(position));
            ((TextView) view.findViewById(R.id.image_viewer_index))
                    .setText((position + 1) + "/" + getCount());
            ImageView iv = (ImageView)view.findViewById(R.id.image_viewer_photo);

            Picasso.with(mContext).load(mPhotoUrls.get(position)).into(iv);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
