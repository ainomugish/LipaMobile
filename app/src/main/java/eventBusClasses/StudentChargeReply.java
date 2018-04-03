package eventBusClasses;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.StudentCharge;


/**
 * Created by Mac on 2015/12/06.
 */
public class StudentChargeReply {

    public boolean log = true;

    public StudentCharge studentCharge;

    //public Gson gson;
    static Gson toStringGson = new GsonBuilder().setPrettyPrinting().create();

    public StudentChargeReply(StudentCharge sm){
        log("++++++++:StudentChargeReply created ");
        this.studentCharge = sm;
      //  log("PubMessage sentMessage recep:"+sm.recep);
       // this.sm = sm;

    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }

    @Override
    public String toString() {
        return toStringGson.toJson(this);
    }
}
