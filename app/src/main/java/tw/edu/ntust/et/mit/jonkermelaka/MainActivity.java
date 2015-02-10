package tw.edu.ntust.et.mit.jonkermelaka;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.parse.Parse;
import com.parse.ParseInstallation;

import tw.edu.ntust.et.mit.jonkermelaka.model.InfoData;


public class MainActivity extends FragmentActivity {
    public static final String TAG = "MainActivity";

    public static final String ARG_SECTION_NUM = "ARG_SECTION_NUM";
    public static final int SECTION_FOOD_NUM = 0;
    public static final int SECTION_HOT_SPOT_NUM = 1;
    public static final int SECTION_ABOUT_NUM = 2;
    public static final int SECTION_MAP_NUM = 3;

    public static final String SECTION_FOOD_TAG = "SECTION_FOOD";
    public static final String SECTION_HOT_SPOT_TAG = "SECTION_HOT_SPOT";
    public static final String SECTION_ABOUT_TAG = "SECTION_ABOUT";
    public static final String SECTION_MAP_TAG = "SECTION_MAP";

    private FragmentTabHost mTabHost;
    private TabWidget mTabWidget;


    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(R.id.tabHost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        addTab(mTabHost.newTabSpec(SECTION_FOOD_TAG).setIndicator(null,
                        getResources().getDrawable(R.drawable.food_icon_selector)),
                SECTION_FOOD_NUM);
        addTab(mTabHost.newTabSpec(SECTION_HOT_SPOT_TAG).setIndicator(null,
                        getResources().getDrawable(R.drawable.hotspot_icon_selector)),
                SECTION_HOT_SPOT_NUM);
        mTabHost.addTab(mTabHost.newTabSpec(SECTION_ABOUT_TAG).setIndicator(null,
                getResources().getDrawable(R.drawable.about_icon_selector)), AboutFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(SECTION_MAP_TAG).setIndicator(null,
                getResources().getDrawable(R.drawable.map_icon_selector)), MapFragment.class, null);
        mTabWidget = mTabHost.getTabWidget();
        setTabWidgetStyle();
        initializeParse();
    }

    private void addTab(TabHost.TabSpec tabSpec, int sectionNum) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUM, sectionNum);
        mTabHost.addTab(tabSpec, MainFragment.class, bundle);
    }

    private void setTabWidgetStyle() {
        for (int i = 0; i < mTabWidget.getChildCount(); i++) {
            View v = mTabWidget.getChildAt(i);
            v.setBackgroundResource(R.drawable.tab_indicator);
        }
    }
    protected void initializeParse() {
        Parse.initialize(this, getString(R.string.parse_app_id),
                getString(R.string.parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
