package com.tomek.luckynumber.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.tomek.luckynumber.MainActivity;
import com.tomek.luckynumber.NotificationReceiverActivity;
import com.tomek.luckynumber.R;
import com.tomek.luckynumber.model.LuckyNumber;
import com.tomek.luckynumber.model.utils.ConnectivityHelper;
import com.tomek.luckynumber.model.utils.PrefsUtils;
import com.tomek.luckynumber.model.utils.SoundHelper;

import java.io.IOException;

public class GetNumberOnUpdateService extends IntentService {

    private static final String TAG = GetNumberOnUpdateService.class.getSimpleName();
    private int receivedNumber = 0;
    private boolean isOnline = false;

    public GetNumberOnUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getString(R.string.intent_received_log), ConnectivityHelper.isOnline() + "");
        if (ConnectivityHelper.isOnline() && ConnectivityHelper.isConnectedOrConnecting(getApplicationContext())) {
            Log.d(TAG, getString(R.string.on_intent_log_tag));
            isOnline = true;
            try {
                receivedNumber = LuckyNumber.getLucky();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String intentExtra = (intent.getStringExtra(PrefsUtils.AUTO_UPDATE_INTENT) == null) ?
                        getString(R.string.log_network_state_change) : (intent.getStringExtra(PrefsUtils.AUTO_UPDATE_INTENT));
                Log.d(TAG, intentExtra);
                Log.d(TAG, receivedNumber + "");
                if (receivedNumber > 0) {
                    PrefsUtils.putIntInSharedPreferences(getApplicationContext(), PrefsUtils.CURRENT_NUMBER, receivedNumber);
                    if (receivedNumber == PrefsUtils.getIntFromSharedPreference(getApplicationContext(), PrefsUtils.MY_NUMBER_KEY)) {
                        PrefsUtils.putBoolInSharedPreferences(getApplicationContext(), PrefsUtils.ARE_YOU_LUCKY, true);
                    }
                    PrefsUtils.putBoolInSharedPreferences(getApplicationContext(), PrefsUtils.IS_NUMBER_UP_TO_DATE, true);
                }
            }
        }
        createNotification(receivedNumber, isOnline);
    }


    public void createNotification(int receivedNumber, boolean isOnline) {
        String contentTitle = "";
        String contentText = "";
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibPattern = {0, 500, 0};
        Notification notif = null;

        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
        activityIntent.setAction(Long.toString(System.currentTimeMillis()));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (!isOnline && !PrefsUtils.getBoolFromSharedPreference(getApplicationContext(), PrefsUtils.IS_NUMBER_UP_TO_DATE)) {
            contentTitle = getString(R.string.new_number_av);
            contentText = getString(R.string.turn_on_to_check);
            //ikonka w ikonkawifi.jpg
        } else if (receivedNumber < 1) {
            contentTitle = getString(R.string.error);
            //ikonka = ikonkablad.jpg
        } else {

            if (receivedNumber == PrefsUtils.getIntFromSharedPreference(getApplicationContext(), PrefsUtils.MY_NUMBER_KEY)) {
                SoundHelper.play(getApplicationContext(), R.raw.tada);
                contentTitle = getString(R.string.number_notif) + receivedNumber;
                contentText = getString(R.string.my_number_notif);
            }
            else {
                contentTitle = getString(R.string.success);
                contentText = getString(R.string.tommorow_number) + receivedNumber;
            }
        }
        Resources resources = getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            android.support.v4.app.NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_sync_black_24dp, "Sprawdź", pendingIntent).build();

            notif = new NotificationCompat.Builder(this)
                    .setContentTitle(contentTitle)
                    .addAction(action)
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.appr)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.appr))
                    .setContentIntent(pIntent)
                    .setSound(soundUri)
                    .setVibrate(vibPattern)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            notif.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(0, notif);
        }


    }
}
