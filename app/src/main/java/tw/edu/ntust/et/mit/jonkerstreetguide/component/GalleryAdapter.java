package tw.edu.ntust.et.mit.jonkerstreetguide.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;
import tw.edu.ntust.et.mit.jonkerstreetguide.R;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.PhotoData;

/**
 * Created by 123 on 2015/1/26.
 */
public class GalleryAdapter extends FancyCoverFlowAdapter {
    private static final int IMAGE_SIZE_X_DP = 150;
    private static final int IMAGE_SIZE_Y_DP = 150;

    private final Context mContext;
    private List<PhotoData> mPhotos;

    private final int IMAGE_SIZE_X_PIXEL;
    private final int IMAGE_SIZE_Y_PIXEL;

    public GalleryAdapter(Context context) {
        mContext = context;
        IMAGE_SIZE_X_PIXEL = (int) Utility.convertDpToPixel(IMAGE_SIZE_X_DP, mContext);
        IMAGE_SIZE_Y_PIXEL = (int) Utility.convertDpToPixel(IMAGE_SIZE_Y_DP, mContext);
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

            Picasso.with(mContext).load(getItem(position).getUrl())
                    .resize(IMAGE_SIZE_X_PIXEL, IMAGE_SIZE_Y_PIXEL)
                    .centerCrop()
                    .error(R.drawable.food_2)
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
