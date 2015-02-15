package tw.edu.ntust.et.mit.jonkermalacca.component;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import tw.edu.ntust.et.mit.jonkermalacca.R;

/**
 * Created by 123 on 2015/2/12.
 */
public class BaseActivity extends FragmentActivity {
    private static Picasso mPicasso;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPicasso == null) {
            Runtime rt = Runtime.getRuntime();
            int memoryCache = (int) (rt.maxMemory() * 0.75);
            mPicasso = new Picasso.Builder(this).memoryCache(new LruCache(memoryCache)).build();
        }
    }

    public Picasso getImageLoader() {
        return mPicasso;
    }

    public synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            mTracker = analytics.newTracker(R.xml.ga_global_tracker);
        }
        mTracker.enableAdvertisingIdCollection(true);

        return mTracker;
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void initTracker(Activity activity) {
        Tracker t = getTracker();
        t.setScreenName(activity.getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void initTracker(Activity activity, String fragmentName) {
        Tracker t = getTracker();
        t.setScreenName(activity.getClass().getSimpleName() + " - " + fragmentName);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void initTracker(Activity activity, String fragmentName, String label) {
        Tracker t = getTracker();
        t.setScreenName(activity.getClass().getSimpleName() + " - " + fragmentName + " - " + label);
        t.send(new HitBuilders.AppViewBuilder().build());
    }
}
