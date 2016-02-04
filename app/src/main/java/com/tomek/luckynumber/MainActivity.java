package com.tomek.luckynumber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.dd.processbutton.iml.ActionProcessButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tomek.luckynumber.model.utils.PrefsUtils;
import com.tomek.luckynumber.model.utils.Utils;
import com.tomek.luckynumber.services.GetLuckyNumberService;
import com.tomek.luckynumber.services.InitAlarmService;
import com.victor.loading.rotate.RotateLoading;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private static final int MAX_CHARACTERS = 2;
    private static final int EDIT_TEXT_VIEW_PADDING = 15;

    private int myNumber;
    private MaterialDialog mDialog;
    private Toolbar toolbar;
    private MaterialEditText mInputText;
    private TextView dialogTitle;
    private TextView luckyText;
    private RotateLoading progressView;
    private CircleImageView arrow;
    private BroadcastReceiver numberReceiver;
    private int receivedNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActionProcessButton button = (ActionProcessButton) findViewById(R.id.ok);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initProgressView();

        startService(new Intent(MainActivity.this, InitAlarmService.class));
        dialogTitle = (TextView) findViewById(R.id.lucky_number_title);
        luckyText = (TextView) findViewById(R.id.lucky_number_text);
        arrow = (CircleImageView) findViewById(R.id.down_arrow);

        button.setMode(ActionProcessButton.Mode.ENDLESS);
        button.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_dark, android.R.color.holo_red_dark, android.R.color.holo_orange_light);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.start();
                startService(new Intent(MainActivity.this, GetLuckyNumberService.class));
                button.setMode(ActionProcessButton.Mode.PROGRESS);
                button.setText("Pobieranie numerka...");

            }
        });
        checkFirstRun();
        initNumberUpdateReceiver(button);
    }

    private void initProgressView() {
        progressView = (RotateLoading) findViewById(R.id.rotateloading);
        progressView.stop();

    }

    @Override
    protected void onDestroy() {
        if (numberReceiver != null) {
            this.unregisterReceiver(this.numberReceiver);
            numberReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (PrefsUtils.getIntFromSharedPreference(getApplicationContext(), PrefsUtils.CURRENT_NUMBER) > 0 && PrefsUtils.getBoolFromSharedPreference(getApplicationContext(), PrefsUtils.IS_NUMBER_UP_TO_DATE)) {
            luckyText.setText(String.valueOf(PrefsUtils.getIntFromSharedPreference(getApplicationContext(), PrefsUtils.CURRENT_NUMBER)));
        } else luckyText.setText("-");
        progressView.stop();
        super.onResume();
    }

    private void updateLuckyText(int lucky_text) {
        String message = "";
        if (lucky_text < 1) {
            message = "Nie udało się pobrać numerka";
        } else {
            message = "Numerek : " + lucky_text;
            luckyText.setText(String.valueOf(lucky_text));
        }
        Snackbar.make(luckyText, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

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

    private void initNumberUpdateReceiver(final ActionProcessButton button) {
        if (numberReceiver == null) {
            IntentFilter ifilter = new IntentFilter("android.intent.action.MAIN");
            numberReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("MainActivity", "onReceived");
                    button.setMode(ActionProcessButton.Mode.ENDLESS);
                    button.setText("Pobierz numerek");
                    receivedNumber = intent.getIntExtra(GetLuckyNumberService.class.getSimpleName(), 0);
                    updateLuckyText(receivedNumber);
                    progressView.stop();
                }
            };
            this.registerReceiver(numberReceiver, ifilter);
        }
    }

}
