package com.tomek.luckynumber;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tomek.luckynumber.model.LuckyNumber;
import com.tomek.luckynumber.model.utils.SharedPreferencesUtils;
import com.tomek.luckynumber.model.utils.Utils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MaterialDialog mDialog;
    private TextView luckyText;
    private int myNumber;
    private int luckyNumber;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private MaterialEditText mInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        final String PREFS_KEY = "com.tomek.luckynumber";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), PackageManager.PERMISSION_GRANTED)
                    .versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            if (SharedPreferencesUtils.
                    getIntFromSharedPreference(MainActivity.this, SharedPreferencesUtils.MY_NUMBER) == 0) {
                initDialog();

            }
            Log.d("LaunchChecker", "onNormalRun");
            return;
        } else if (savedVersionCode == DOESNT_EXIST) {
            initDialog();
            Log.d("LaunchChecker", "onFirstRun");

        } else if (currentVersionCode > savedVersionCode) {
            if (SharedPreferencesUtils.
                    getIntFromSharedPreference(MainActivity.this, SharedPreferencesUtils.MY_NUMBER) == 0) {
                initDialog();
                Log.d("LaunchChecker", "onUpdateRun");

            }
            // Update the shared preferences with the current version code
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();
        }
    }

    private void initDialog() {
        initInputEditTexT();
                 mDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Wpisz swój numerek")
                .customView((View) mInputText, true)
                .positiveText(R.string.ok)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        myNumber = Integer.valueOf(String.valueOf(mInputText.getText()));
                        Log.d("Int value of ", "" + myNumber);
                        if (myNumber < 1 || myNumber > 36) {
                            Utils.makeShortToast(MainActivity.this, getString(R.string.invalid_number));
                        } else {
                            SharedPreferencesUtils
                                    .putIntInSharedPreferences
                                            (MainActivity.this, SharedPreferencesUtils.MY_NUMBER, myNumber);
                            dialog.dismiss();
                        }
                        super.onPositive(dialog);
                    }
                })
                .autoDismiss(false)
                .show();
    }

    private void initInputEditTexT() {
        mInputText = new MaterialEditText(this);
        mInputText.setPaddings(15, 15, 15 ,15);
        mInputText.setMaxCharacters(2);
        mInputText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mInputText.setHint(R.string.input_your_number_hint);
    }
}
