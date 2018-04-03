package jobques;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.List;

import entities.StudentCredz;
import mPock.Vars;
import models.Trans;
import okHtClass.OkHTTPClass;
import sugarorms.studentorm;


/**
 * Created by Mac on 2015/10/18.
 */
public class SyncBalanceJob extends Job {

    boolean log = true;

    Vars vars;
    Gson gson;


    public SyncBalanceJob(Vars vars) {
        super(new Params(5).requireNetwork());
        log("SyncBalanceJob STARTED");
        this.vars = vars;
        gson = new Gson();
    }


    @Override
    public void onAdded() {
        System.out.println("on added");
    }


    @Override
    public void onRun() throws Throwable {
        log("onRun");
        // EventBus.getDefault().post(new SyncStarted("started"));

        //BUILD SENT
        Trans sent = new Trans("SyncBalanceJob");
        // minput.apiKey = "00:e0:4c:06:65:c4";
        sent.apiKey = vars.macAddress;

        log("sending:" + sent.toString());

        //SEND REQUEST
        String replyString = OkHTTPClass.staticPost(Globals.SYNC_BALANCE_URL, sent.toString());
        log("replyString:" + replyString);

        //GET TRANS
        if (replyString != null) {
            Trans reply = gson.fromJson(replyString, Trans.class);
            log("got reply:" + reply.result);

            if (reply.result) {


                //SET BALANCE
                vars.edit.putInt(Globals.CANTEEN_TOTAL, reply.deviceBalance);
                vars.edit.commit();
                log("result is true, device balance:"+vars.prefs.getInt(Globals.CANTEEN_TOTAL,0));
            } else if (!reply.result) {
                log("ERROR:" + reply.errorMessage);
            }

        }


    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }
}
