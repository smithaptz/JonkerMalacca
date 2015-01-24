package tw.edu.ntust.et.mit.jonkerstreetguide;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends android.support.v4.app.Fragment {
    public static final String ARG_SUBSECTION_NUM = "ARG_SUBSECTION_NUM";
    public static final int FOOD_SEC_STARTING_NUM = 0;
    public static final int HOT_SPOT_SEC_STARTING_NUM = 2;
    public static final int CULTURE_SEC_STARTING_NUM = 5;
    public static final int MAP_SEC_STARTING_NUM = 6;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.listView);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public static ListFragment newInstance(int subsectionNum) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SUBSECTION_NUM, subsectionNum);
        fragment.setArguments(args);
        return fragment;
    }


}
