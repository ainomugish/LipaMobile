package jobques;

/**
 * Created by i on 6/16/16.
 */

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import java.util.List;

import de.greenrobot.event.EventBus;
import entities.Meals;
import eventBusClasses.SyncMealDone;

import mPock.Vars;
import models.Trans;
import okHtClass.OkHTTPClass;
import sugarorms.mealsorm;

public class GetSchoolMealJob extends Job {

    boolean log = true;

    Vars vars;
    Gson gson;


    public int numMealsUpdated = 0;


    public GetSchoolMealJob(Vars vars) {
        super(new Params(5).requireNetwork());
        log("GetSchoolMealJob STARTED");
        this.vars = vars;
        gson = new Gson();
    }


    @Override
    public void onAdded() {
        System.out.println("on added Meal Sync Job");
    }


    @Override
    public void onRun() throws Throwable {
        log("onRun Meal Sync");
        // EventBus.getDefault().post(new SyncStarted("started"));

        //BUILD SENT
        Trans sent = new Trans("GetSchoolMealJob");
        // minput.apiKey = "00:e0:4c:06:65:c4";
        sent.apiKey = vars.macAddress;
        sent.meal_change_log_num = vars.prefs.getInt(Globals.MEAL_CHANGE_LOG, 0);
        sent.schoolId = 5;

        log("sending:" + sent.toString());

        //SEND REQUEST
        String replyString = OkHTTPClass.staticPost(Globals.GET_MEALS_URL, sent.toString());
        log("replyString:" + replyString);

        //GET TRANS
        if (replyString != null) {
            Trans reply = gson.fromJson(replyString, Trans.class);
            log("got reply:" + reply.result);

            if (reply.result) {
                if (reply.mealsTrans != null && !reply.mealsTrans.isEmpty()) {

                    //SET NUMBER OF PINS

                    numMealsUpdated = reply.studentCredzList.size();
                    log("numMealsUpdated:"+numMealsUpdated);

                    //LOOP THROUGH LIST AND SAVE
                    for(Meals m:reply.mealsTrans){
                        log("checking Meal:"+m.getIdmeals());

                        //FIND STUDENT
                        List<mealsorm> meals = mealsorm.find(mealsorm.class,"idmeals= ?",String.valueOf(m.getIdmeals()));

                        if(meals.isEmpty()){

                            //SET CURRENT MEAL
                            mealsorm currentMeal  = meals.get(0);
                            log("current meal set to:"+currentMeal.idmeals);

                            //SET PIN
                            //currentStudent.pin = Integer.valueOf(sc.getStudentPin());
                            currentMeal.save();
                            log("current meal set to:"+currentMeal.type);

                            //SAVE PIN LOG
                            vars.edit.putInt(Globals.MEAL_CHANGE_LOG, reply.meal_change_log_num);
                            vars.edit.commit();
                            log("meal change log updated to:"+vars.prefs.getInt(Globals.MEAL_CHANGE_LOG,0));


                        }else if(!meals.isEmpty()){
                            log("meal already on device:"+m.getIdmeals());
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
        EventBus.getDefault().post(new SyncMealDone(numMealsUpdated));

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
