package com.tomek.luckynumber.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tomek.luckynumber.GetLuckyNumberService;

import java.util.Calendar;

/**
 * Created by tomek on 25.12.15.
 */
public class AutoNumberUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean isWeekday = ((dayOfWeek >= Calendar.MONDAY) && (dayOfWeek <= Calendar.FRIDAY));
        Log.d("CO TU SIE DZIEJE" , dayOfWeek + " " + isWeekday);
        if (isWeekday) {
            Intent checkForLuckyNumberIntent = new Intent(context, GetLuckyNumberService.class);
            context.startService(checkForLuckyNumberIntent);
        }
    }
}
