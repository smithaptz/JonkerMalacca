package tw.edu.ntust.et.mit.jonkerstreetguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 123 on 2015/1/24.
 */
public class MainFragment extends Fragment {
    private int mSectionNum;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        System.out.println("____________________arg: " + getArguments().getInt(MainActivity.ARG_SECTION_NUM));
        mSectionNum = getArguments().getInt(MainActivity.ARG_SECTION_NUM);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.listViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        return rootView;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private int getSubsectionNum(int position) {
            switch(mSectionNum) {
                case MainActivity.SECTION_FOOD_NUM:
                    return position;
                case MainActivity.SECTION_HOT_SPOT_NUM:
                    return position + 2;
                case MainActivity.SECTION_CULTURE_NUM:
                    return position + 3;
            }

            return 0;
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return ListFragment.newInstance(getSubsectionNum(position));
        }

        @Override
        public int getCount() {
            switch(mSectionNum) {
                case MainActivity.SECTION_FOOD_NUM:
                    return 2;
                case MainActivity.SECTION_HOT_SPOT_NUM:
                    return 3;
                case MainActivity.SECTION_CULTURE_NUM:
                    return 2;
            }

            return 0;
        }
    }
}
