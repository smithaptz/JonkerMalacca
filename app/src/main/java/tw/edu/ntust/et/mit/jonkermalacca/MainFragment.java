package tw.edu.ntust.et.mit.jonkermalacca;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 123 on 2015/1/24.
 */
public class MainFragment extends Fragment {
    public static final String TAG = "MainFragment";

    private int mSectionNum;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static final int FOOD_CHINESE = 0;
    public static final int FOOD_NYONYA = 1;
    public static final int SPOT_TRADITION = 2;
    public static final int SPOT_WALL = 3;
    public static final int SPOT_ASSOCIATION = 4;
    public static final int SPOT_TEMPLE = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSectionNum = getArguments().getInt(MainActivity.ARG_SECTION_NUM);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.listViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        return rootView;
    }

    public void moveToPage(int position) {
        mViewPager.setCurrentItem(position, true);
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }
        @Override
        public long getItemId(int position) {
            return mSectionNum << 8 | getSubsectionNum(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            final long itemId = getItemId(position);

            Fragment fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));

            return fragment;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            mCurTransaction.detach((Fragment)object);
        }

        private String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            String title;
            String subtitle;
            int coverViewId = -1;
            int descriptionViewId = -1;
            int queryType = -1;
            int pagePositionType = ListFragment.PAGE_POSITION_SINGLE;

            switch(getSubsectionNum(position)) {
                case FOOD_CHINESE:
                    title = getString(R.string.title_food);
                    subtitle = getString(R.string.title_food_chinese);
                    coverViewId = R.drawable.cover_food_chinese;
                    descriptionViewId = R.layout.list_swipe_food_chinese;
                    queryType = FOOD_CHINESE;
                    pagePositionType = ListFragment.PAGE_POSITION_LEFT;
                 break;
                case FOOD_NYONYA:
                    title = getString(R.string.title_food);
                    subtitle = getString(R.string.title_food_nyonya);
                    coverViewId = R.drawable.cover_food_nyonya;
                    descriptionViewId = R.layout.list_swipe_food_nyonya;
                    queryType = FOOD_NYONYA;
                    pagePositionType = ListFragment.PAGE_POSITION_RIGHT;
                    break;
                case SPOT_TRADITION:
                    title = getString(R.string.title_spot);
                    subtitle = getString(R.string.title_spot_tradition);
                    coverViewId = R.drawable.cover_spot_tradition;
                    descriptionViewId = R.layout.list_swipe_spot_tradition;
                    queryType = SPOT_TRADITION;
                    pagePositionType = ListFragment.PAGE_POSITION_LEFT;
                    break;
                case SPOT_WALL:
                    title = getString(R.string.title_spot);
                    subtitle = getString(R.string.title_spot_wall);
                    coverViewId = R.drawable.cover_spot_association;
                    descriptionViewId = R.layout.list_swipe_spot_wall;
                    queryType = SPOT_WALL;
                    pagePositionType = ListFragment.PAGE_POSITION_MIDDLE;
                    break;
                case SPOT_ASSOCIATION:
                    title = getString(R.string.title_spot);
                    subtitle = getString(R.string.title_spot_association);
                    coverViewId = R.drawable.cover_spot_association;
                    descriptionViewId = R.layout.list_swipe_spot_association;
                    queryType = SPOT_ASSOCIATION;
                    pagePositionType = ListFragment.PAGE_POSITION_MIDDLE;
                    break;
                case SPOT_TEMPLE:
                    title = getString(R.string.title_spot);
                    subtitle = getString(R.string.title_spot_temple);
                    coverViewId = R.drawable.cover_spot_temple;
                    descriptionViewId = R.layout.list_swipe_spot_temple;
                    queryType = SPOT_TEMPLE;
                    pagePositionType = ListFragment.PAGE_POSITION_RIGHT;
                    break;
                default:
                    title = getString(R.string.title_other);
                    subtitle = getString(R.string.title_other);
            }

            return ListFragment.newInstance(title, subtitle, coverViewId, descriptionViewId, queryType, position, pagePositionType);
        }

        private int getSubsectionNum(int position) {
            switch(mSectionNum) {
                case MainActivity.SECTION_FOOD_NUM:
                    return position;
                case MainActivity.SECTION_HOT_SPOT_NUM:
                    return position + 2;
            }

            return 0;
        }


        @Override
        public int getCount() {
            switch(mSectionNum) {
                case MainActivity.SECTION_FOOD_NUM:
                    return 2;
                case MainActivity.SECTION_HOT_SPOT_NUM:
                    return 4;
            }

            return 0;
        }
    }
}
