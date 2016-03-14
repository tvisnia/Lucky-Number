package com.tomek.luckynumber.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tomek.luckynumber.R;
import com.tomek.luckynumber.receivers.AutoNumberUpdateReceiver;

import java.util.Calendar;

/**
 * Created by tomek on 16.01.16.
 */
public class InitAlarmService extends IntentService {

    private static final String CONSTRUCTOR_TAG = InitAlarmService.class.getSimpleName();
    private static final int DEFAULT_ALARM_HOUR = 15;
    private static final int DEFAULT_ALARM_MINUTE = 05;
    private static final int DEFAULT_FLAG = 0;
    private static final int INTENT_ID = 15;
    private static final String INTENT_ACTION_ID = "com.tomek.luckynumber.action.alarm";

    private AlarmManager alarmMgr;
    private Intent notifyAutoUpdateReceiver;
    private PendingIntent alarmIntent;

    public InitAlarmService() {
        super(CONSTRUCTOR_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initAlarm();
    }

    private void initAlarm() {
        if (!isAlarmSet()) {
            setAlarm();
            Log.d("onAlarmReinited", isAlarmSet() + "");
        }
    }

    private void setAlarm() {
        long timeInMillis;
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(System.currentTimeMillis());
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notifyAutoUpdateReceiver = new Intent(getApplicationContext(), AutoNumberUpdateReceiver.class);
        notifyAutoUpdateReceiver.setAction(INTENT_ACTION_ID);
        notifyAutoUpdateReceiver.setData(Uri.parse("custom://" + INTENT_ACTION_ID));
        alarmIntent = PendingIntent
                .getBroadcast(getApplicationContext(), INTENT_ID, notifyAutoUpdateReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_ALARM_HOUR);
        calendar.set(Calendar.MINUTE, DEFAULT_ALARM_MINUTE);
        if (currentDate.after(calendar)) {
            Log.d(getString(R.string.alarm_set_log), getString(R.string.set_after_current));
//            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
            timeInMillis = calendar.getTimeInMillis() + 1000 * 60 * 60 * 24;
            //24 h delay if alarm is set after 15:05
        } else {
            Log.d(getString(R.string.alarm_set_log), getString(R.string.set_before_current));
        }
        timeInMillis = calendar.getTimeInMillis();
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeInMillis,
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private void cancelAlarm() {
        Intent notifyAutoUpdateReceiver = new Intent(getApplicationContext(), AutoNumberUpdateReceiver.class);
        notifyAutoUpdateReceiver.setAction(INTENT_ACTION_ID);
        notifyAutoUpdateReceiver.setData(Uri.parse("custom://" + INTENT_ACTION_ID));
        PendingIntent alarmIntent = PendingIntent
                .getBroadcast(getApplicationContext(), INTENT_ID, notifyAutoUpdateReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);
        this.alarmIntent.cancel();
    }

    private boolean isAlarmSet() {
        Intent intent = new Intent(getApplicationContext(), AutoNumberUpdateReceiver.class);
        intent.setAction(INTENT_ACTION_ID);
        intent.setData(Uri.parse("custom://" + INTENT_ACTION_ID));
        return (PendingIntent.getBroadcast(getApplicationContext(), INTENT_ID, intent, PendingIntent.FLAG_NO_CREATE) != null);
    }
}
