package tw.edu.ntust.et.mit.jonkerstreetguide.component;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import tw.edu.ntust.et.mit.jonkerstreetguide.R;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.PhotoData;

/**
 * Created by 123 on 2015/1/23.
 */
public class ListAdapter extends ArrayAdapter<ListAdapter.Item> {
    private final LayoutInflater mInflater;
    private Location mLocation;

    public ListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item, null);
            setupGallery((FancyCoverFlow) view.findViewById(R.id.list_item_gallery));
        } else {
            view = convertView;
        }

        ListAdapter.Item item = getItem(i);
        InfoData infoData = item.getInfoData();
        ((TextView) ViewHolder.get(view, R.id.list_item_name)).setText(infoData.getName());
        ((TextView) ViewHolder.get(view,R.id.list_item_hour)).setText(infoData.getBusinessHour());
        ((TextView) ViewHolder.get(view,R.id.list_item_description)).setText(infoData.getDescription());

        ((ImageView) ViewHolder.get(view, R.id.list_item_expand_btn))
                .setImageResource(item.isViewExpand() ?
                        R.drawable.up_button : R.drawable.down_button);

        FancyCoverFlow gallery = ViewHolder.get(view,R.id.list_item_gallery);

        if(!item.equals(gallery.getTag()) && item.getPhotos() != null && item.isViewExpand()) {
            GalleryAdapter galleryAdapter = (GalleryAdapter) gallery.getAdapter();
            galleryAdapter.setItems(item.getPhotos());
            gallery.setTag(item);
        }

        ((TextView) ViewHolder.get(view,R.id.list_item_dist)).setText(
                Utility.calDistance(mLocation, infoData.getLocation()));

        ImageView iv = ((ImageView) view.findViewById(R.id.img_list_item_cover));
        Picasso.with(getContext()).load(infoData.getLogoUrl()).into(iv, new Callback.EmptyCallback() {
            @Override public void onSuccess() {}
        });
        return view;
    }

    private FancyCoverFlow setupGallery(FancyCoverFlow gallery) {
        gallery.setAdapter(new GalleryAdapter(getContext()));
        gallery.setUnselectedAlpha(0.75f);
        gallery.setUnselectedSaturation(0.0f);
        gallery.setUnselectedScale(0.5f);
        gallery.setMaxRotation(90);
        gallery.setScaleDownGravity(0.5f);
        gallery.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
        return gallery;
    }

    public static class Item {
        private InfoData mInfoData;
        private List<PhotoData> mPhotos;
        private boolean mViewExpand;

        public Item(InfoData infoData) {
            mInfoData = infoData;
        }

        public void setPhotos(List<PhotoData> photos) {
            mPhotos = photos;
        }

        public InfoData getInfoData() {
            return mInfoData;
        }

        public List<PhotoData> getPhotos() {
            return mPhotos;
        }

        public void setExpandViewState(boolean viewExpand) {
            mViewExpand = viewExpand;
        }

        public boolean isViewExpand() {
            return mViewExpand;
        }
    }
}
