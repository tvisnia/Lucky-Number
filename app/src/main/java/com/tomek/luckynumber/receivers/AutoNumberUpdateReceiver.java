package com.tomek.luckynumber.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tomek.luckynumber.GetLuckyNumberService;
import com.tomek.luckynumber.model.utils.PrefsUtils;

import java.util.Calendar;

/**
 * Created by tomek on 25.12.15.
 */
public class AutoNumberUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onAlarmManagerIntentReceived. isWeekday " , isWeekday() + "");
        if (isWeekday()) {
            Intent checkForLuckyNumberIntent = new Intent(context, GetLuckyNumberService.class);
            checkForLuckyNumberIntent.putExtra(PrefsUtils.AUTO_UPDATE_INTENT, PrefsUtils.AUTO_UPDATE_INTENT);
            context.startService(checkForLuckyNumberIntent);
        }
    }

    private boolean isWeekday() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return ((dayOfWeek >= Calendar.MONDAY) && (dayOfWeek <= Calendar.FRIDAY));
    }
}
