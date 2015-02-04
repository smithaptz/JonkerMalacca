package tw.edu.ntust.et.mit.jonkerstreetguide.component;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class ListAdapter extends ArrayAdapter<ListAdapter.Item> implements
        FancyCoverFlow.OnItemClickListener, View.OnClickListener {
    private final LayoutInflater mInflater;
    private Location mLocation;

    private OnPhotoClickListener mOnPhotoClickListener;
    private OnMapClickListener mOnMapClickListener;


    public interface OnPhotoClickListener {
        void onPhotoClick(AdapterView<?> parent, View view,
                          ListAdapter.Item item, int position);
    }

    public interface OnMapClickListener {
        void onMapClick(InfoData infoData);
    }

    public ListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemGalleryClickListener(OnPhotoClickListener listener) {
        mOnPhotoClickListener = listener;
    }

    public void setOnMapClickListener(OnMapClickListener listener) {
        mOnMapClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mOnPhotoClickListener != null) {
            mOnPhotoClickListener.onPhotoClick(parent, view,
                    (ListAdapter.Item) parent.getTag(), position);
        }
    }

    @Override
    public void onClick(View v) {
        if(mOnMapClickListener != null && v.getTag() != null) {
            mOnMapClickListener.onMapClick(((ListAdapter.Item)
                    v.getTag()).getInfoData());
        }
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item, null);
            view.findViewById(R.id.list_item_navigation).setOnClickListener(this);
            setupGallery((FancyCoverFlow) view.findViewById(R.id.list_item_gallery));
        } else {
            view = convertView;
        }

        ListAdapter.Item item = getItem(i);
        InfoData infoData = item.getInfoData();

        ((TextView) ViewHolder.get(view, R.id.list_item_name)).setText(infoData.getName());
        ((TextView) ViewHolder.get(view, R.id.list_item_dist)).setText(
                Utility.calDistance(mLocation, infoData.getLocation()));

        ImageView iv = ((ImageView) view.findViewById(R.id.list_item_cover));
        Picasso.with(getContext()).load(infoData.getLogoUrl()).into(iv, new Callback.EmptyCallback() {
            @Override public void onSuccess() {}
        });

        ((ImageView) ViewHolder.get(view, R.id.list_item_expand_btn))
                .setImageResource(item.isViewExpand() ?
        R.drawable.up_button : R.drawable.down_button);

        if(item.isViewExpand()) {
            ((TextView) ViewHolder.get(view, R.id.list_item_address)).setText(infoData.getAddress());
            ((TextView) ViewHolder.get(view, R.id.list_item_hour)).setText(infoData.getBusinessHour());
            ((TextView) ViewHolder.get(view, R.id.list_item_description)).setText(infoData.getDescription());
            ((TextView) ViewHolder.get(view, R.id.list_item_address_2)).setText(infoData.getAddress());
            ViewHolder.get(view, R.id.list_item_navigation).setTag(item);

            FancyCoverFlow gallery = ViewHolder.get(view, R.id.list_item_gallery);

            if(!item.equals(gallery.getTag()) && item.getPhotos() != null) {
                GalleryAdapter galleryAdapter = (GalleryAdapter) gallery.getAdapter();
                galleryAdapter.setItems(item.getPhotos());
                gallery.setTag(item);
            }

            Location loc = infoData.getLocation();
            ImageView map = ViewHolder.get(view, R.id.list_item_map);
            Picasso.with(getContext()).load(getGoogleMapPicUrl(loc.getLatitude(), loc.getLongitude(), 520, 180, 17))
                    .into(map, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                        }
                    });
        }

        return view;
    }

    private String getGoogleMapPicUrl(double latitude, double longitude, int sizeX, int sizeY, int zoom) {
       return  String.format("http://maps.googleapis.com/maps/api/staticmap?" +
                        "center=%f,%f&markers=%f,%f&zoom=%d&size=%dx%d&" +
                       "scale=2&sensor=true&format=jpg",
                latitude, longitude, latitude, longitude, zoom, sizeX, sizeY);
    }

    private FancyCoverFlow setupGallery(FancyCoverFlow gallery) {
        gallery.setAdapter(new GalleryAdapter(getContext()));
        gallery.setUnselectedAlpha(0.75f);
        gallery.setUnselectedSaturation(0.0f);
        gallery.setUnselectedScale(0.5f);
        gallery.setMaxRotation(90);
        gallery.setScaleDownGravity(0.5f);
        gallery.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
        gallery.setOnItemClickListener(this);

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
