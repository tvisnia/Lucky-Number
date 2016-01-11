package com.tomek.luckynumber;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tomek.luckynumber.model.LuckyNumber;
import com.tomek.luckynumber.model.utils.PrefsUtils;
import com.tomek.luckynumber.model.utils.Utils;
import com.tomek.luckynumber.receivers.AutoNumberUpdateReceiver;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_ALARM_HOUR = 15;
    private static final int DEFAULT_ALARM_MINUTE = 5;
    private static final int MAX_CHARACTERS = 2;
    private static final int EDIT_TEXT_VIEW_PADDING = 15;
    private static final int DEFAULT_FLAG = 0;
    private static final int INTENT_ID = 15;
    private static final String INTENT_ACTION_ID = "com.tomek.luckynumber.action.alarm";
    private int myNumber;
    private int luckyNumber;
    private MaterialDialog mDialog;
    private TextView luckyText;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private MaterialEditText mInputText;
    private AlarmManager alarmMgr;
    private Intent notifyAutoUpdateReceiver;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAlarm();
        luckyText = ((TextView) findViewById(R.id.lucky_text));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckNumber().execute();
                Log.d("onClickpoExeceute", "number : " + luckyNumber + " text : " + luckyText);

            }
        });
        checkFirstRun();
    }

    class CheckNumber extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                luckyNumber = LuckyNumber.getLucky();
                Log.d("doInBackground", "number : " + luckyNumber + " text : " + luckyText);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            luckyText.setText(String.valueOf(luckyNumber));
            Log.d("onPostExecute", "number : " + luckyNumber + " text : " + luckyText);
            String message = "";
            if (luckyNumber == 0) {
                message = "Błąd !";
            } else message = "Pomyslnie pobrano numerek !";
            Snackbar.make(fab, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkFirstRun() {

        final String PREFS_KEY = PrefsUtils.SHARED_PREFERENCES_KEY;
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), PackageManager.PERMISSION_GRANTED)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            if (PrefsUtils.
                    getIntFromSharedPreference(MainActivity.this, PrefsUtils.MY_NUMBER_KEY) == 0) {
                initDialog();

            }
            Log.d("LaunchChecker", "onNormalRun");
            return;
        } else if (savedVersionCode == DOESNT_EXIST) {
            initDialog();
            Log.d("LaunchChecker", "onFirstRun");

        } else if (currentVersionCode > savedVersionCode) {
            if (PrefsUtils.
                    getIntFromSharedPreference(MainActivity.this, PrefsUtils.MY_NUMBER_KEY) == 0) {
                initDialog();
                Log.d("LaunchChecker", "onUpdateRun");

            }
        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();
    }

    private void initDialog() {
        initInputEditTexT();
        mDialog = new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.enter_number_title)
                .customView((View) mInputText, true)
                .positiveText(R.string.ok)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String mInput = String.valueOf(mInputText.getText());
                        if (!TextUtils.isEmpty(mInput)) {
                            myNumber = Integer.valueOf(mInput);
                        } else
                            Utils.makeShortToast(MainActivity.this, getString(R.string.invalid_number));
                        if (myNumber < 1 || myNumber > 36
                                || TextUtils.isEmpty(String.valueOf(mInputText.getText()))) {
                            Utils.makeShortToast(MainActivity.this, getString(R.string.invalid_number));
                        } else {
                            PrefsUtils
                                    .putIntInSharedPreferences
                                            (MainActivity.this, PrefsUtils.MY_NUMBER_KEY, myNumber);
                            dialog.dismiss();
                        }
                        super.onPositive(dialog);
                    }
                })
                .autoDismiss(false)
                .cancelable(false)
                .show();
    }

    private void initInputEditTexT() {
        mInputText = new MaterialEditText(this);
        mInputText.setPaddings(EDIT_TEXT_VIEW_PADDING, EDIT_TEXT_VIEW_PADDING, EDIT_TEXT_VIEW_PADDING,
                EDIT_TEXT_VIEW_PADDING);
        mInputText.setMaxCharacters(MAX_CHARACTERS);
        mInputText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mInputText.setHint(R.string.input_your_number_hint);
    }

    private void initAlarm() {
        if (!isAlarmSet()) {
            setAlarm();
            Log.d("onAlarmReinited", isAlarmSet() + "");
        }
    }

    private void setAlarm() {
        long timeInMillis;
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(System.currentTimeMillis());
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notifyAutoUpdateReceiver = new Intent(getApplicationContext(), AutoNumberUpdateReceiver.class);
        notifyAutoUpdateReceiver.setAction(INTENT_ACTION_ID);
        notifyAutoUpdateReceiver.setData(Uri.parse("custom://" + INTENT_ACTION_ID));
        alarmIntent = PendingIntent
                .getBroadcast(MainActivity.this, INTENT_ID, notifyAutoUpdateReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_ALARM_HOUR);
        calendar.set(Calendar.MINUTE, DEFAULT_ALARM_MINUTE);
        if (currentDate.after(calendar)) {
            Log.d("onAlarmSet", "afterCalendar");
            timeInMillis = calendar.getTimeInMillis() + 1000 * 60 * 60 * 24;

            //24 h delay if alarm is set after 15:05
        } else {
            timeInMillis = calendar.getTimeInMillis();
            Log.d("onAlarmSet", "beforeCalendar");
        }
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeInMillis,
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private void cancelAlarm() {
        Intent notifyAutoUpdateReceiver = new Intent(getApplicationContext(), AutoNumberUpdateReceiver.class);
        notifyAutoUpdateReceiver.setAction(INTENT_ACTION_ID);
        notifyAutoUpdateReceiver.setData(Uri.parse("custom://" + INTENT_ACTION_ID));
        PendingIntent alarmIntent = PendingIntent
                .getBroadcast(MainActivity.this, INTENT_ID, notifyAutoUpdateReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);
        this.alarmIntent.cancel();
    }

    private boolean isAlarmSet() {
        Intent intent = new Intent(getApplicationContext(), AutoNumberUpdateReceiver.class);
        intent.setAction(INTENT_ACTION_ID);
        intent.setData(Uri.parse("custom://" + INTENT_ACTION_ID));
        return (PendingIntent.getBroadcast(getApplicationContext(), INTENT_ID, intent, PendingIntent.FLAG_NO_CREATE) != null);
    }
}
