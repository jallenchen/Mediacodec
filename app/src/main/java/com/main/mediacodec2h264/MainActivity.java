package com.main.mediacodec2h264;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author jalle
 * @package:com.main.mediacodec2h264
 * @fileName:MainActivity
 * @date: 2019-03-14
 * @desc:TODO
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void onSaveH264(View v) {
        Intent intent = new Intent(this, RecordH264Activity.class);
        startActivity(intent);

    }

    public void onPlayH264(View v) {
        Intent intent = new Intent(this, PlayH264Activity.class);
        startActivity(intent);
    }
}
