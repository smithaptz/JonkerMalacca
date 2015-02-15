package tw.edu.ntust.et.mit.jonkermalacca.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;
import tw.edu.ntust.et.mit.jonkermalacca.R;
import tw.edu.ntust.et.mit.jonkermalacca.model.PhotoData;

/**
 * Created by 123 on 2015/1/26.
 */
public class ListItemGalleryAdapter extends FancyCoverFlowAdapter {
    private static final int IMAGE_SIZE_X_DP = 150;
    private static final int IMAGE_SIZE_Y_DP = 150;

    private final BaseActivity mBaseActivity;
    private List<PhotoData> mPhotos;

    private final int IMAGE_SIZE_X_PIXEL;
    private final int IMAGE_SIZE_Y_PIXEL;

    public ListItemGalleryAdapter(BaseActivity context) {
        mBaseActivity = context;
        IMAGE_SIZE_X_PIXEL = (int) Utility.convertDpToPixel(IMAGE_SIZE_X_DP, mBaseActivity);
        IMAGE_SIZE_Y_PIXEL = (int) Utility.convertDpToPixel(IMAGE_SIZE_Y_DP, mBaseActivity);
    }

    public void setItems(List<PhotoData> photos) {
        mPhotos = photos;
        notifyDataSetChanged();
    }

    @Override
    public View getCoverFlowItem(final int position, View reusableView, ViewGroup parent) {
        ImageView iv;

        if (reusableView != null) {
            iv = (ImageView) reusableView;
        } else {
            iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new FancyCoverFlow.LayoutParams(IMAGE_SIZE_X_PIXEL, IMAGE_SIZE_Y_PIXEL));
        }

        if (!getItem(position).equals(iv.getTag())) {
            iv.setTag(getItem(position));

            mBaseActivity.getImageLoader()
                    .load(getItem(position).getUrl())
                    .resize(IMAGE_SIZE_X_PIXEL, IMAGE_SIZE_Y_PIXEL)
                    .centerCrop()
                    .placeholder(R.drawable.loading_photo)
                    .into(iv, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            notifyDataSetChanged();
                        }
                    });
        }

        return iv;
    }

    @Override
    public int getCount() {
        if (mPhotos == null) {
            return 0;
        }

        return mPhotos.size();
    }

    @Override
    public PhotoData getItem(int position) {
        if (mPhotos == null) {
            return null;
        }

        return mPhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
