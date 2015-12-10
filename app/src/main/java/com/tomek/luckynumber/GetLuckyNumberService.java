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
    int receivedNumber = 0;

    public GetLuckyNumberService() {
        super("GetLuckyNumberService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("GetLuckyNumberService", "onHandleIntent");
        try {
            receivedNumber = LuckyNumber.getLucky();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
