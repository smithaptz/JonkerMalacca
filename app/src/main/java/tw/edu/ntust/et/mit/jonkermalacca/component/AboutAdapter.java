package tw.edu.ntust.et.mit.jonkermalacca.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import tw.edu.ntust.et.mit.jonkermalacca.R;
import tw.edu.ntust.et.mit.jonkermalacca.model.PhotoData;

/**
 * Created by 123 on 2015/2/7.
 */
public class AboutAdapter extends ArrayAdapter<AboutAdapter.Item> implements
        FancyCoverFlow.OnItemClickListener, View.OnClickListener {
    private final LayoutInflater mInflater;

    private OnPhotoClickListener mOnPhotoClickListener;
    private OnUrlClickListener mOnUrlClickListener;

    public interface OnPhotoClickListener {
        void onPhotoClick(AdapterView<?> parent, View view,
                          AboutAdapter.Item item, int position);
    }

    public interface OnUrlClickListener {
        void onUrlClick(View view, AboutAdapter.Item item);
    }


    public AboutAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemGalleryClickListener(OnPhotoClickListener listener) {
        mOnPhotoClickListener = listener;
    }

    public void setOnUrlClickListener(OnUrlClickListener listener) {
        mOnUrlClickListener = listener;
    }

    public OnPhotoClickListener getOnItemGalleryClickListener() {
        return mOnPhotoClickListener;
    }

    public OnUrlClickListener getOnUrlClickListener() {
        return mOnUrlClickListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnPhotoClickListener != null) {
            mOnPhotoClickListener.onPhotoClick(parent, view,
                    (AboutAdapter.Item) parent.getTag(), position);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_item_website:
                if (mOnUrlClickListener != null) {
                    mOnUrlClickListener.onUrlClick(view, (AboutAdapter.Item) view.getTag());
                }
                break;
        }
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.about_item, null);
            ((TextView) ViewHolder.get(view, R.id.about_item_website)).setOnClickListener(this);
            setupGallery((FancyCoverFlow) view.findViewById(R.id.about_item_gallery));
        } else {
            view = convertView;
        }
        
        AboutAdapter.Item item = getItem(i);

        ((TextView) ViewHolder.get(view, R.id.about_item_name)).setText(item.getName());

        Picasso.with(getContext()).load(item.getCoverResourceId()).
                placeholder(R.drawable.loading).into(((ImageView) ViewHolder.get(view, R.id.about_item_cover)));


        ((ImageView) ViewHolder.get(view, R.id.about_item_expand_btn))
                .setImageResource(item.isViewExpand() ?
                        R.drawable.up_button : R.drawable.down_button);

        if (item.isViewExpand()) {
            String webUrl = item.getWebsiteUrl();
            TextView webUrlTxt = (TextView) ViewHolder.get(view, R.id.about_item_website);
            TextView webUrlConstTxt = (TextView) ViewHolder.get(view, R.id.about_item_website_const);

            webUrlTxt.setTag(item);

            if (webUrl != null && !webUrl.isEmpty()) {
                webUrlConstTxt.setVisibility(View.VISIBLE);
                webUrlTxt.setVisibility(View.VISIBLE);
                webUrlTxt.setText(item.getWebsiteUrl());
            } else {
                webUrlConstTxt.setVisibility(View.GONE);
                webUrlTxt.setVisibility(View.GONE);
            }

            //webUrlTxt.requestLayout();

            ((TextView) ViewHolder.get(view, R.id.about_item_description)).setText(item.getDescription());

            FancyCoverFlow gallery = ViewHolder.get(view, R.id.about_item_gallery);

            if (!item.equals(gallery.getTag()) && item.getPhotos() != null) {
                ListItemGalleryAdapter galleryAdapter = (ListItemGalleryAdapter) gallery.getAdapter();
                galleryAdapter.setItems(item.getPhotos());
                gallery.setTag(item);
            }
        }

        return view;
    }

    private FancyCoverFlow setupGallery(FancyCoverFlow gallery) {
        gallery.setAdapter(new ListItemGalleryAdapter(getContext()));
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
        private int id;
        private String name;
        private String description;
        private String websiteUrl;
        private String emailUrl;
        private int coverResourceId;
        private List<PhotoData> photos;
        private boolean viewExpand;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getWebsiteUrl() {
            return websiteUrl;
        }

        public void setWebsiteUrl(String websiteUrl) {
            this.websiteUrl = websiteUrl;
        }

        public String getEmailUrl() {
            return emailUrl;
        }

        public void setEmailUrl(String emailUrl) {
            this.emailUrl = emailUrl;
        }

        public int getCoverResourceId() {
            return coverResourceId;
        }

        public void setCoverResourceId(int coverResourceId) {
            this.coverResourceId = coverResourceId;
        }


        public List<PhotoData> getPhotos() {
            return photos;
        }

        public void setPhotos(List<PhotoData> photos) {
            this.photos = photos;
        }

        public void setExpandViewState(boolean viewExpand) {
            this.viewExpand = viewExpand;
        }

        public boolean isViewExpand() {
            return viewExpand;
        }
    }
}
