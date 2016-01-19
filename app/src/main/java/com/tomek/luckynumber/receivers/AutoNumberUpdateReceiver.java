package com.tomek.luckynumber.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tomek.luckynumber.services.GetLuckyNumberService;
import com.tomek.luckynumber.model.utils.PrefsUtils;
import com.tomek.luckynumber.services.GetNumberOnUpdateService;

import java.util.Calendar;

/**
 * Created by tomek on 25.12.15.
 */
public class AutoNumberUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onAlarmManagerIntentReceived. isWeekday " , isWeekday() + "");
        PrefsUtils.putBoolInSharedPreferences(context, PrefsUtils.IS_NUMBER_UP_TO_DATE, false);
        if (isWeekday()) {
            Intent checkForLuckyNumberIntent = new Intent(context, GetNumberOnUpdateService.class);
            checkForLuckyNumberIntent.putExtra(PrefsUtils.AUTO_UPDATE_INTENT, PrefsUtils.AUTO_UPDATE_INTENT);
            context.startService(checkForLuckyNumberIntent);
        }
        PrefsUtils.putBoolInSharedPreferences(context, PrefsUtils.IS_WEEKDAY, isWeekday());
        PrefsUtils.putBoolInSharedPreferences(context, PrefsUtils.ARE_YOU_LUCKY, false);
    }

    private boolean isWeekday() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return ((dayOfWeek >= Calendar.SUNDAY) && (dayOfWeek <= Calendar.THURSDAY));
    }
}
