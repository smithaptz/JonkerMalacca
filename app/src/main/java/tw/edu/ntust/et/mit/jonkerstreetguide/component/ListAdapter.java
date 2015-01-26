package tw.edu.ntust.et.mit.jonkerstreetguide.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import tw.edu.ntust.et.mit.jonkerstreetguide.R;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;

/**
 * Created by 123 on 2015/1/23.
 */
public class ListAdapter extends ArrayAdapter<InfoData> {
    private final LayoutInflater mInflater;

    public ListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item, null);
        } else {
            view = convertView;
        }

        InfoData infoData = getItem(i);
        ((TextView) ViewHolder.get(view, R.id.list_item_name)).setText(infoData.getName());
        ((TextView) ViewHolder.get(view,R.id.list_item_hour)).setText(infoData.getBusinessHour());
        ((TextView) ViewHolder.get(view,R.id.list_item_description)).setText(infoData.getDescription());
        //((TextView) ViewHolder.get(view,R.id.list_item_dist)).setText(Utility.calDistance(mCurrentLocation, infoData.getLocation()));

        ImageView iv = ((ImageView) view.findViewById(R.id.img_list_item_cover));
        Picasso.with(getContext()).load(infoData.getLogoUrl()).into(iv, new Callback.EmptyCallback() {
            @Override public void onSuccess() {}
        });
        return view;
    }
}
