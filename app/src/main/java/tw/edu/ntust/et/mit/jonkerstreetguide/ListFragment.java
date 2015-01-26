package tw.edu.ntust.et.mit.jonkerstreetguide;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import tw.edu.ntust.et.mit.jonkerstreetguide.component.ListAdapter;
import tw.edu.ntust.et.mit.jonkerstreetguide.model.InfoData;


public class ListFragment extends Fragment {
    public static final String ARG_SUBSECTION_NUM = "ARG_SUBSECTION_NUM";
    public static final int FOOD_SEC_STARTING_NUM = 0;
    public static final int HOT_SPOT_SEC_STARTING_NUM = 2;
    public static final int CULTURE_SEC_STARTING_NUM = 5;
    public static final int MAP_SEC_STARTING_NUM = 6;

    private ListAdapter mAdapter;

    private List<InfoData> mInfos;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("--------------------------onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.listView);

        mAdapter = new ListAdapter(getActivity());
        lv.setAdapter(new SlideExpandableListAdapter(
                mAdapter,
                R.id.list_item_expand_btn,
                R.id.list_item_expand_layout));
        updateData();

        return rootView;
    }

    public static ListFragment newInstance(int subsectionNum) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SUBSECTION_NUM, subsectionNum);
        fragment.setArguments(args);
        return fragment;
    }

    private void updateData() {
        System.out.println("--------------------------UpdateData");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("InfoEng");
//        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
//        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("public", true);
        query.findInBackground(parseQueryCallback);
    }

    private FindCallback<ParseObject> parseQueryCallback = new FindCallback<ParseObject>() {
        public void done(List<ParseObject> list, ParseException e) {
            System.out.println("--------------------------parseQueryCallback");
            if (e == null) {
                System.out.println("--------------updateSuccessfully ");
                mInfos = InfoData.adaptParseObjects(list);
                mAdapter.addAll(mInfos);
                mAdapter.notifyDataSetChanged();
            } else {
                System.out.println("--------------fail to update ");
                e.printStackTrace();
            }
        }
    };
}
