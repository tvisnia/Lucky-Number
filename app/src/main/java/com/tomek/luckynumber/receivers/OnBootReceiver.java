package com.tomek.luckynumber.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tomek.luckynumber.services.InitAlarmService;
import com.tomek.luckynumber.R;


/**
 * Created by tomek on 16.01.16.
 */
public class OnBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(context.getString(R.string.log_on_boot) , "onReceived");
        context.startService(new Intent(context, InitAlarmService.class));
        // reinit alarm after rebooting phone.
    }
}
