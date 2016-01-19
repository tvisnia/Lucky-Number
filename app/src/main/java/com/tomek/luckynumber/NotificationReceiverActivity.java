package com.tomek.luckynumber;

/**
 * Created by tomek on 08.01.16.
 */
import android.app.Activity;
import android.os.Bundle;

import com.tomek.luckynumber.R;

public class NotificationReceiverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
    }
}
