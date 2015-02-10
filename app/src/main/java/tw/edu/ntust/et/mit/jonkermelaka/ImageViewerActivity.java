package tw.edu.ntust.et.mit.jonkermelaka;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by 123 on 2015/1/28.
 */
public class ImageViewerActivity extends FragmentActivity {
    public static final String TAG = "ImageViewerActivity";

    public static final String ARG_PHOTO_URL_LIST =
            "ARG_PHOTO_URL_LIST";
    public static final String ARG_PHOTO_DESCRIPTION_LIST =
            "ARG_PHOTO_DESCRIPTION_LIST";
    public static final String ARG_FIRST_PHOTO_INDEX =
            "ARG_FIRST_PHOTO_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Bundle bundle = getIntent().getExtras();
        ImageViewerFragment fragment = new ImageViewerFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_image_viewer_layout, fragment, null)
                .commit();
    }
}
