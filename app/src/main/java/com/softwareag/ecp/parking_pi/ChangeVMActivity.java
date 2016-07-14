package com.softwareag.ecp.parking_pi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeVMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_vm);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if(actionBar!= null){
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        final EditText editText = (EditText)findViewById(R.id.editText);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vmName = String.valueOf(editText.getText());
                if(!vmName.isEmpty()) {
                    Log.v("ChangeVMActivity ", " " + vmName);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChangeVMActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("VMName",vmName);
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
