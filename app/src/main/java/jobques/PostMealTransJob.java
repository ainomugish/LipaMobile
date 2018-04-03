package jobques;

import android.util.Log;

import com.google.gson.Gson;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import de.greenrobot.event.EventBus;
import entities.MealTransactions;
import mPock.Vars;

import com.duali.itouchpop2_test.utils.Globals;

import java.util.Date;

import models.Trans;
import okHtClass.OkHTTPClass;
import mPock.MySingleton;
import sugarorms.mealtransactionsorm;

/**
 * Created by i on 6/17/16.
 */


public class PostMealTransJob extends Job {


    boolean log = true;
    MealTransactions mt= null;
    Vars vars;
    Gson gson;
    Long transId;


    public PostMealTransJob(Vars vars, MealTransactions mt, Long transId) {
        super(new Params(5).requireNetwork());
        log("PostMealTransJob STARTED");
        this.vars = vars;
        this.mt = mt;
        this.transId = transId;
        gson = new Gson();
    }



    @Override
    public void onAdded() {
        System.out.println("on added Meal job");
    }


    @Override
    public void onRun() throws Throwable {
        log("onRun meal Job");
        // EventBus.getDefault().post(new SyncStarted("started"));

        //BUILD SENT
        Trans sent = new Trans("SendMealTransactionsJob");
        // minput.apiKey = "00:e0:4c:06:65:c4";
        sent.apiKey = vars.macAddress;
        sent.mealTransactions = mt;
        //sent.student =  student;
        //sent.pin_change_log_num = vars.prefs.getInt(Globals.PIN_CHANGE_LOG, 0);

        log("sending:" + sent.toString());

        //SEND REQUEST
        String replyString = OkHTTPClass.staticPost(Globals.SEND_MEALS_URL, sent.toString());
        log("replyString:gatePass" + replyString);

        //GET TRANS
        if (replyString != null) {
            Trans reply = gson.fromJson(replyString, Trans.class);
            log("got reply Gate Pass:" + reply.result);

            if (reply.result) {

                mealtransactionsorm trans = mealtransactionsorm.findById(mealtransactionsorm.class, transId);
                trans.sentdate = new Date();
                trans.senttoserver = "yes";
                trans.save();

            } else if (!reply.result) {

                //CHECK IF REPLY CONTAINTS UNIQUE THEN IT WAS ALREADY SENT LEAVE AS YES
                if (reply.errorMessage.contains("exists")) {
                    mealtransactionsorm trans = mealtransactionsorm.findById(mealtransactionsorm.class, transId);
                    trans.sentdate = new Date();
                    trans.senttoserver = "yes";
                    trans.save();
                } else {
                    mealtransactionsorm trans = mealtransactionsorm.findById(mealtransactionsorm.class, transId);
                    trans.sentdate = new Date();
                    trans.senttoserver = "error:" + reply.errorMessage;
                    trans.save();
                }

            }

        }

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }
}
