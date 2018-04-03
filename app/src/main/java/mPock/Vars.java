package mPock;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;


/**
 * Created by Mac on 2015/10/18.
 */
public class Vars {


    public Context context;
    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;

    public Alerter alerter;

    public String macAddress;

    public int setPin;

    public Gson gson = new Gson();

    public Vars (Context context){
        this.context = context;

        prefs = context.getSharedPreferences(Globals.MPOCK_PREFS, 0);

        edit = prefs.edit();

        alerter = new Alerter(context);

        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        macAddress = info.getMacAddress();
        setCanteenPin(macAddress);
        System.out.println("macadress is:"+macAddress);
    }

    public void setCanteenPin(String macAddress){

        if(macAddress.equalsIgnoreCase("00:e0:4c:06:5c:a2")){
            setPin = 5698;
        }else if(macAddress.equalsIgnoreCase("00:e0:4c:06:5f:37")){
            setPin = 3242;
        }else if(macAddress.equalsIgnoreCase("00:0e:4c:06:5d:b7")){
            setPin = 2341;
        }else if(macAddress.equalsIgnoreCase("00:0e:4c:06:5d:a5")){
            setPin = 2342;
        }else{
            setPin = 1234;
        }


    }
}
