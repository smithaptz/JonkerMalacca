package tw.edu.ntust.et.mit.jonkerstreetguide;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;



public class MainActivity extends FragmentActivity {
    public static final String ARG_SECTION_NUM = "ARG_SECTION_NUM";
    public static final int SECTION_FOOD_NUM = 0;
    public static final int SECTION_HOT_SPOT_NUM = 1;
    public static final int SECTION_CULTURE_NUM = 2;
    public static final int SECTION_MAP_NUM = 3;

    public static final String SECTION_FOOD_TAG = "SECTION_FOOD";
    public static final String SECTION_HOT_SPOT_TAG = "SECTION_HOT_SPOT";
    public static final String SECTION_CULTURE_TAG = "SECTION_CULTURE";
    public static final String SECTION_MAP_TAG = "SECTION_MAP";

    private FragmentTabHost mTabHost;


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

        addTab(mTabHost.newTabSpec(SECTION_FOOD_TAG).setIndicator("Food", null),
                SECTION_FOOD_NUM);
        addTab(mTabHost.newTabSpec(SECTION_HOT_SPOT_TAG).setIndicator("Hot Spot", null),
                SECTION_HOT_SPOT_NUM);
        addTab(mTabHost.newTabSpec(SECTION_CULTURE_TAG).setIndicator("Culture", null),
                SECTION_CULTURE_NUM);
        addTab(mTabHost.newTabSpec(SECTION_MAP_TAG).setIndicator("Map", null),
                SECTION_MAP_NUM);
    }

    private void addTab(TabHost.TabSpec tabSpec, int sectionNum) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUM, sectionNum);
        mTabHost.addTab(tabSpec, MainFragment.class, bundle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
