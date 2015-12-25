package com.tomek.luckynumber;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.tomek.luckynumber.model.LuckyNumber;
import com.tomek.luckynumber.model.utils.PrefsUtils;

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
        Log.d("onIntentReceived", isConnectedOrConnecting(getApplicationContext()) + " " + isOnline() + " ");
        if (isOnline() && isConnectedOrConnecting(getApplicationContext())) {
            Log.d(LOG_TAG, getString(R.string.on_intent_log_tag));
            try {
                receivedNumber = LuckyNumber.getLucky();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.d(LOG_TAG, intent.getStringExtra(PrefsUtils.AUTO_UPDATE_INTENT));
                Log.d(LOG_TAG, receivedNumber + "");
            }
        }
    }

    private boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private boolean isConnectedOrConnecting(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e) {
            return false;
        }
    }
}
