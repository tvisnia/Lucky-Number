package com.tomek.luckynumber.model.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tomek on 09.12.15.
 */
public class SharedPreferencesUtils {

    public static final String SHARED_PREFERENCES_KEY = "com.tomek.luckynumber";
    public static final String MY_NUMBER = "com.tomek.luckynumber.mynumber";


    public static final int PREFS_DEFAULT_INT_VALUE = 0;

    public static void putIntInSharedPreferences(Context context, String key, int value) {
        SharedPreferences prefs =
                context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        prefs
                .edit()
                .putInt(key, value)
                .commit();
    }

    public static int getIntFromSharedPreference(Context context, String key) {
        SharedPreferences prefs =
                context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        return prefs.getInt(key, PREFS_DEFAULT_INT_VALUE);
    }
}
