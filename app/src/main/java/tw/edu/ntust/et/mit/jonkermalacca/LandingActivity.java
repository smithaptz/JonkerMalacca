package tw.edu.ntust.et.mit.jonkermalacca;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import tw.edu.ntust.et.mit.jonkermalacca.component.BaseActivity;

/**
 * Created by 123 on 2015/2/13.
 */
public class LandingActivity extends BaseActivity {
    private static final int GOTO_MAIN_ACTIVITY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        options.inSampleSize = 2;
        ((ImageView) findViewById(R.id.landing_img)).setImageBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.landing_page, options));

        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 1000);
    }


    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(LandingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        };
    };
}
