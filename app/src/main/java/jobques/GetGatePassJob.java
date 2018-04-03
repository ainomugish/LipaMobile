package jobques;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import entities.CanteenTrans;
import entities.Student;
import entities.StudentCredz;
import eventBusClasses.SyncGatePassDone;
import eventBusClasses.SyncPinDone;
import mPock.Vars;
import models.Minput;
import models.Mreply;
import models.Trans;
import okHtClass.OkHTTPClass;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit_mpock.MpockRetroInterface;
import sugarorms.studentorm;
import sugarorms.transactionsorm;
import utils.Pj;


/**
 * Created by Mac on 2015/10/18.
 */
public class GetGatePassJob extends Job {

    boolean log = true;

    Vars vars;
    Gson gson;
    Student student= null;

    public boolean passResult = false;



    public GetGatePassJob(Vars vars, Student stdnt) {
        super(new Params(5).requireNetwork());
        log("GetGatePassJob STARTED");
        this.vars = vars;
        gson = new Gson();
        student = stdnt;
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
        Trans sent = new Trans("GetGatePassJob");
        // minput.apiKey = "00:e0:4c:06:65:c4";
        sent.apiKey = vars.macAddress;
        sent.student =  student;
        //sent.pin_change_log_num = vars.prefs.getInt(Globals.PIN_CHANGE_LOG, 0);

        log("sending:" + sent.toString());

        //SEND REQUEST
        String replyString = OkHTTPClass.staticPost(Globals.GET_PASS_URL, sent.toString());
        log("replyString:gatePass" + replyString);

        //GET TRANS
        if (replyString != null) {
            Trans reply = gson.fromJson(replyString, Trans.class);
            log("got reply Gate Pass:" + reply.result);

            if (reply.result) {

                if (reply.gatePassTrans != null && !reply.gatePassTrans.isEmpty()) {

                    //SET NUMBER OF PINS

                    passResult = reply.gatePassTrans.get(0).getPermission();
                    log("passResult:"+passResult);



                } else {
                    log("ERROR: reply is either NULL or EMPTY");
                }
            } else if (!reply.result) {
                log("ERROR:" + reply.errorMessage);
            }
        }

        //POST THAT ITS DONE
        EventBus.getDefault().post(new SyncGatePassDone(passResult));

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
