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
import entities.StudentCredz;
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
public class GetStudentPinJob extends Job {

    boolean log = true;

    Vars vars;
    Gson gson;


    public int numPinUpdated = 0;


    public GetStudentPinJob(Vars vars) {
        super(new Params(5).requireNetwork());
        log("GetStudentPinJob STARTED");
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
        Trans sent = new Trans("GetStudentPinJob");
        // minput.apiKey = "00:e0:4c:06:65:c4";
        sent.apiKey = vars.macAddress;
        sent.pin_change_log_num = vars.prefs.getInt(Globals.PIN_CHANGE_LOG, 0);

        log("sending:" + sent.toString());

        //SEND REQUEST
        String replyString = OkHTTPClass.staticPost(Globals.GET_PINS_URL, sent.toString());
        log("replyString:" + replyString);

        //GET TRANS
        if (replyString != null) {
            Trans reply = gson.fromJson(replyString, Trans.class);
            log("got reply:" + reply.result);

            if (reply.result) {
                if (reply.studentCredzList != null && !reply.studentCredzList.isEmpty()) {

                    //SET NUMBER OF PINS

                    numPinUpdated = reply.studentCredzList.size();
                    log("numPinUpdated:"+numPinUpdated);

                    //LOOP THROUGH LIST AND SAVE
                    for(StudentCredz sc:reply.studentCredzList){
                        log("checking student:"+sc.getStudentId());

                        //FIND STUDENT
                        List<studentorm> students = studentorm.find(studentorm.class,"studentid = ?",String.valueOf(sc.getStudentId()));

                        if(!students.isEmpty()){

                            //SET CURRENT STUDENT
                            studentorm currentStudent  = students.get(0);
                            log("current student set to:"+currentStudent.studentid);

                            //SET PIN
                            currentStudent.pin = Integer.valueOf(sc.getStudentPin());
                            currentStudent.save();
                            log("current pin set to:"+currentStudent.pin);

                            //SAVE PIN LOG
                            vars.edit.putInt(Globals.PIN_CHANGE_LOG, reply.pin_change_log_num);
                            vars.edit.commit();
                            log("pin change log updated to:"+vars.prefs.getInt(Globals.PIN_CHANGE_LOG,0));


                        }else if(students.isEmpty()){
                            log("unable to find student with:"+sc.getStudentId());
                        }
                    }

                } else {
                    log("ERROR: reply is either NULL or EMPTY");
                }
            } else if (!reply.result) {
                log("ERROR:" + reply.errorMessage);
            }
        }

        //POST THAT ITS DONE
        EventBus.getDefault().post(new SyncPinDone(numPinUpdated));

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
