package com.tomek.luckynumber;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

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
    private boolean isOnline = false;

    public GetLuckyNumberService() {
        super(LOG_TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getString(R.string.intent_received_log), isOnline() + "");
        if (isOnline() && isConnectedOrConnecting(getApplicationContext())) {
            Log.d(LOG_TAG, getString(R.string.on_intent_log_tag));
            isOnline = true;
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

        if (!isOnline) {
            contentTitle = getString(R.string.new_number_av);
            contentText = getString(R.string.turn_on_to_check);
            //ikonka w ikonkawifi.jpg
        }
        else if (receivedNumber == 0) {
            contentTitle = getString(R.string.error);
            //ikonka = ikonkablad.jpg
        }
        else {
            contentTitle = getString(R.string.success);
            contentText = getString(R.string.tommorow_number) + receivedNumber;
        }
        Resources resources = getResources();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notif = new Notification.Builder(this)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.appr)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.appr))
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
