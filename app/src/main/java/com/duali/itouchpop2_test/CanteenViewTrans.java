package com.duali.itouchpop2_test;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.duali.itouchpop2_test.utils.Globals;

import java.util.ArrayList;
import java.util.List;

import adapters.CanteenAdapter;
import mPock.Vars;
import sugarorms.transactionsorm;

public class CanteenViewTrans extends ActionBarActivity {

    Vars vars;
    TextView cant_currentbalance;
    ListView canteen_trans_listview;
    List<transactionsorm> transList = new ArrayList();
    Boolean log = true;
    CanteenAdapter canteenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen_view_trans);

        vars  = new Vars(this);

        //SET CURRENT BALANCE
        cant_currentbalance = (TextView) findViewById(R.id.cant_currentbalance);
        cant_currentbalance.setText("CURRENT BALANCE:"+String.valueOf(vars.prefs.getInt(Globals.CANTEEN_TOTAL,0)));

        canteen_trans_listview = (ListView) findViewById(R.id.canteen_trans_listview);

        //INIT ADAPTER
        transList = transactionsorm.listAll(transactionsorm.class);
        log("found trans:"+transList.size());
        canteenAdapter  = new CanteenAdapter(vars, transList);
        canteen_trans_listview.setAdapter(canteenAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_canteen_view_trans, menu);
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

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }
}
