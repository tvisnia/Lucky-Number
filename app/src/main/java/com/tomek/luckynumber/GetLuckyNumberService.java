package com.tomek.luckynumber;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.tomek.luckynumber.model.LuckyNumber;

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
        Log.d(LOG_TAG, getString(R.string.on_intent_log_tag));
        try {
            receivedNumber = LuckyNumber.getLucky();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Log.d(LOG_TAG, receivedNumber +"");
        }

    }
}
