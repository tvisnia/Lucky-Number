package com.tomek.luckynumber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.dd.morphingbutton.MorphingButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tomek.luckynumber.model.utils.PrefsUtils;
import com.tomek.luckynumber.model.utils.Utils;
import com.tomek.luckynumber.services.GetLuckyNumberService;
import com.tomek.luckynumber.services.InitAlarmService;

public class MainActivity extends AppCompatActivity {


    private static final int MAX_CHARACTERS = 2;
    private static final int EDIT_TEXT_VIEW_PADDING = 15;

    private int myNumber;
    private int lucky_text;
    private MaterialDialog mDialog;
    private MorphingButton fab;
    private Toolbar toolbar;
    private MaterialEditText mInputText;
    private TextView title;
    private TextView luckyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        startService(new Intent(MainActivity.this, InitAlarmService.class));

        title = (TextView) findViewById(R.id.lucky_number_title);
        luckyText = (TextView) findViewById(R.id.lucky_number_text);
        fab = (MorphingButton) findViewById(R.id.button);
        fab.setText("Pobierz numerek");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckNumber().execute();
            }
        });
        checkFirstRun();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PrefsUtils.getIntFromSharedPreference(getApplicationContext(), PrefsUtils.CURRENT_NUMBER) != 0 && PrefsUtils.getBoolFromSharedPreference(getApplicationContext(), PrefsUtils.IS_NUMBER_UP_TO_DATE)) {
            luckyText.setText(String.valueOf(PrefsUtils.getIntFromSharedPreference(getApplicationContext(), PrefsUtils.CURRENT_NUMBER)));
        }
    }

    class CheckNumber extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
                startService(new Intent(MainActivity.this, GetLuckyNumberService.class));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("onPostExecute", "number : " + lucky_text + " text : " + luckyText);
            String message = "";
            lucky_text = PrefsUtils.getIntFromSharedPreference(MainActivity.this, PrefsUtils.CURRENT_NUMBER);
            if (lucky_text == 0 || lucky_text == -1) {
                message = "Nie udało się pobrać numerka";
            } else {
                message = "Pomyslnie pobrano numerek : " + lucky_text;
                luckyText.setText(String.valueOf(lucky_text));
            }
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


}
