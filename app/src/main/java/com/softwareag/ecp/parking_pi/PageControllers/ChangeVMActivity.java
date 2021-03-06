package com.softwareag.ecp.parking_pi.PageControllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.softwareag.ecp.parking_pi.R;

public class ChangeVMActivity extends AppCompatActivity {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    private CheckBox checkBox;
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(MESSAGE_LOG, "ChangeVMActivity -> onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_vm);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.i(MESSAGE_LOG, "ChangeVMActivity -> actionBar!= null");
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        final EditText editText = (EditText) findViewById(R.id.editText);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String vmName = preferences.getString("VMName", null);
        editText.setText(vmName);

        checkBox = (CheckBox) findViewById(R.id.chkCloud);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    check = true;
                }
            }
        });

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(MESSAGE_LOG, "ChangeVMActivity -> button.setOnClickListener");
                String vmName = String.valueOf(editText.getText());

                if (check == true) {
                    vmName = String.valueOf(editText.getText()) + "/gateway";
                }

                if (!vmName.isEmpty()) {
                    Log.i(MESSAGE_LOG, "ChangeVMActivity -> !vmName.isEmpty()");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChangeVMActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("VMName", vmName);
                    editor.apply();

                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
