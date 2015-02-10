package tw.edu.ntust.et.mit.jonkermelaka.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

/**
 * Created by 123 on 2015/2/7.
 */
public class AboutItemGalleryAdapter extends FancyCoverFlowAdapter {
    private static final int IMAGE_SIZE_X_DP = 150;
    private static final int IMAGE_SIZE_Y_DP = 150;

    private final Context mContext;
    private List<String> mPhotos;

    private final int IMAGE_SIZE_X_PIXEL;
    private final int IMAGE_SIZE_Y_PIXEL;

    public AboutItemGalleryAdapter(Context context) {
        mContext = context;
        IMAGE_SIZE_X_PIXEL = (int) Utility.convertDpToPixel(IMAGE_SIZE_X_DP, mContext);
        IMAGE_SIZE_Y_PIXEL = (int) Utility.convertDpToPixel(IMAGE_SIZE_Y_DP, mContext);
    }

    public void setItems(List<String> photos) {
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

            Picasso.with(mContext).load(getItem(position))
                    .resize(IMAGE_SIZE_X_PIXEL, IMAGE_SIZE_Y_PIXEL)
                    .centerCrop()
                    .into(iv, new Callback.EmptyCallback() {
                        @Override public void onSuccess() {
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
    public String getItem(int position) {
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
