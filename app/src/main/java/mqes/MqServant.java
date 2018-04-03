package mqes;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;

import mPock.Vars;


/**
 * Created by ivan on 6/15/2015.
 */
public class MqServant {

    Boolean log = true;

    public MqServant(Vars vars){
        log("+++++++++++++:MqServant");

    }


    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }
}
