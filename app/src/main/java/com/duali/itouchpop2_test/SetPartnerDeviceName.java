package com.duali.itouchpop2_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.duali.itouchpop2_test.utils.Globals;

import mPock.Vars;

public class SetPartnerDeviceName extends ActionBarActivity {


    Vars vars;

    EditText set_partner_device_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_partner_device_name_layout);


        vars = new Vars(this);

        set_partner_device_name = (EditText) findViewById(R.id.set_partner_device_name);
    }

    public void setpartnerDeviceName(View view){
        log("set Device name");

        String devName = set_partner_device_name.getText().toString();
        log("devName:"+devName);

        if(devName.isEmpty() || devName.length() < 4){
            vars.alerter.alerterAny("Error", "Invalid partner device name, must be atleast 4 characters");
        }else{
            vars.edit.putString(Globals.PARTNER_DEVICE_NAME, devName);
            vars.edit.apply();
            log("devince name set to:" + vars.prefs.getString(Globals.PARTNER_DEVICE_NAME, null));

            vars.alerter.alerterAnySuccessActivity("Success", "Device name set to:" + vars.prefs.getString(Globals.PARTNER_DEVICE_NAME, null), RFActivity.class);
        }
    }

    public void cancel(View view){
        log("++++++: cancel");
        startActivity(new Intent(this, RFActivity.class));
    }

    void log(String msg) {
        //if (log && Globals.log) {
        Log.v(this.getClass().getSimpleName(), msg);
        //}
    }

}
