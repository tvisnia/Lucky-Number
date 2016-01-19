package com.tomek.luckynumber.services;

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

import com.tomek.luckynumber.R;
import com.tomek.luckynumber.model.LuckyNumber;
import com.tomek.luckynumber.model.utils.ConnectivityHelper;
import com.tomek.luckynumber.model.utils.PrefsUtils;
import com.tomek.luckynumber.NotificationReceiverActivity;

import java.io.IOException;

/**
 * Created by tomek on 10.12.15.
 */
public class GetLuckyNumberService extends IntentService {
    private static final String TAG = GetLuckyNumberService.class.getSimpleName();
    private int receivedNumber = 0;
    private boolean isOnline = false;

    public GetLuckyNumberService() {
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
                if (receivedNumber > 0 ) {
                    PrefsUtils.putIntInSharedPreferences(getApplicationContext(), PrefsUtils.CURRENT_NUMBER, receivedNumber);
                    if (receivedNumber == PrefsUtils.getIntFromSharedPreference(getApplicationContext(), PrefsUtils.MY_NUMBER_KEY)) {
                        PrefsUtils.putBoolInSharedPreferences(getApplicationContext(), PrefsUtils.ARE_YOU_LUCKY, true);
                    }
                    PrefsUtils.putBoolInSharedPreferences(getApplicationContext(), PrefsUtils.IS_NUMBER_UP_TO_DATE, true);
                }
            }
        }
    }
}
