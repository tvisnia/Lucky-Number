package com.tomek.luckynumber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by tomek on 10.12.15.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "onInternetAvailabilityChecked";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onIntentReceived", isConnectedorConnecting(context) + " " + isOnline() + " ");
        if (isOnline() && isConnectedorConnecting(context)) {
            Intent checkForLuckyNumberIntent = new Intent(context, GetLuckyNumberService.class);
            context.startService(checkForLuckyNumberIntent);
        }
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private boolean isConnectedorConnecting(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e) {
            return false;
        }
    }
}
