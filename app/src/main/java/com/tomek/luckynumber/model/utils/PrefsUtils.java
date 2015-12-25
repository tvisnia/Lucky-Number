package com.tomek.luckynumber.model.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tomek on 09.12.15.
 */
public class PrefsUtils {

    public static final String SHARED_PREFERENCES_KEY = "com.tomek.luckynumber";
    public static final String VERSION_CODE = "com.tomek.luckynumber.versioncode";
    public static final String MY_NUMBER_KEY = "com.tomek.luckynumber.mynumber";
    public static final String CURRENT_NUMBER_KEY = "com.tomek.luckynumber.currentnumber";
    public static final String DEFAULT_HOUR = "com.tomek.luckynumber.defaulthour";
    public static final String DEFAULT_MINUTE = "com.tomek.luckynumber.defaultminute";

    public static final String AUTO_UPDATE_INTENT = "com.tomek.luckynumber.autoupdate";

    public static final int PREFS_DEFAULT_INT_VALUE = -1;

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
