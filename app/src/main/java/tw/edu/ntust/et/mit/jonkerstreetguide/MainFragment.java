package tw.edu.ntust.et.mit.jonkerstreetguide;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
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
        mSectionNum = getArguments().getInt(MainActivity.ARG_SECTION_NUM);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.listViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        return rootView;
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
            return ListFragment.newInstance(getSubsectionNum(position));
        }

        private int getSubsectionNum(int position) {
            switch(mSectionNum) {
                case MainActivity.SECTION_FOOD_NUM:
                    return position;
                case MainActivity.SECTION_HOT_SPOT_NUM:
                    return position + 2;
                case MainActivity.SECTION_CULTURE_NUM:
                    return position + 5;
            }

            return 0;
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
