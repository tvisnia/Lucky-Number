package com.tomek.luckynumber;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.tomek.luckynumber.model.LuckyNumber;
import com.tomek.luckynumber.model.utils.PrefsUtils;
import com.tomek.luckynumber.receivers.NotificationReceiverActivity;

import java.io.IOException;

/**
 * Created by tomek on 10.12.15.
 */
public class GetLuckyNumberService extends IntentService {
    private static final String LOG_TAG = GetLuckyNumberService.class.getSimpleName();
    private int receivedNumber = 0;

    public GetLuckyNumberService() {
        super(LOG_TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("onIntentReceived : isConnected : ", isOnline() + "");
        createNotification(receivedNumber);
        if (isOnline() && isConnectedOrConnecting(getApplicationContext())) {
            Log.d(LOG_TAG, getString(R.string.on_intent_log_tag));
            try {
                receivedNumber = LuckyNumber.getLucky();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String intentExtra = (intent.getStringExtra(PrefsUtils.AUTO_UPDATE_INTENT) == null) ?
                        getString(R.string.log_network_state_change) : (intent.getStringExtra(PrefsUtils.AUTO_UPDATE_INTENT));
                Log.d(LOG_TAG, intentExtra);
                Log.d(LOG_TAG, receivedNumber + "");
            }
        }
    }

    public void createNotification(int receivedNumber) {
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibPattern = {0, 200, 0};
        Notification notif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notif = new Notification.Builder(this)
                    .setContentTitle("New mail from " + "test@gmail.com")
                    .setContentText("Subject")
                    .setSmallIcon(R.drawable.bar)
                    .setContentIntent(pIntent)
                    .setSound(soundUri)
                    .setVibrate(vibPattern)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notif);
    }

    private boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isConnectedOrConnecting(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
