package utils;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;

import mPock.Vars;

/**
 * Created by Mac on 2016/02/20.
 */
public class MiscUtilz {

    public static void loadConstants(Vars vars){

        //CHANGE LOG
        if(vars.prefs.getInt(Globals.PIN_CHANGE_LOG, 0) == 0){
            log("Globals.PIN_CHANGE_LOG is zero");

            //SET LOG
            vars.edit.putInt(Globals.PIN_CHANGE_LOG,0);
            vars.edit.commit();
            log("PIN_CHANGE_LOG NOW set it is:"+vars.prefs.getInt(Globals.PIN_CHANGE_LOG,0));
        }else{
            log("PIN_CHANGE_LOG was already set it is:"+vars.prefs.getInt(Globals.PIN_CHANGE_LOG,0));
        }
    }

    static void log(String msg){
        Log.v("MiscUtilz",msg);
    }

}
