package jobques;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.Date;


import entities.CanteenTrans;
import mPock.MySingleton;
import mPock.Vars;
import models.Minput;
import models.Mreply;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit_mpock.MpockRetroInterface;
import sugarorms.transactionsorm;


/**
 * Created by Mac on 2015/10/18.
 */
public class PostCanteenTransJob extends Job {

    boolean log = true;
    CanteenTrans ct;
    Vars vars;
    Gson gson;
    Long transId;


    public PostCanteenTransJob(Vars vars, CanteenTrans ct, Long transId) {
        super(new Params(5).requireNetwork());
        log("PostCanteenTransJob STARTED");
        this.vars = vars;
        this.ct = ct;
        this.transId = transId;
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

        Retrofit client = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MpockRetroInterface service = client.create(MpockRetroInterface.class);

        Minput minput = new Minput();
       // minput.apiKey = "00:e0:4c:06:65:c4";
        minput.apiKey = vars.macAddress;

        //ADD CT OBJECT
        minput.canteenTrans = ct;

        log("TESTING:"+gson.toJson(minput));

       // Call<GitResult> call = service.getUsersNamedTom("tom");
        Call<Mreply> call = service.canteenPost(minput);

        call.enqueue(new Callback<Mreply>() {
            @Override
            public void onResponse(Response<Mreply> response, Retrofit retrofit) {

                //GET RESPONSE OBJECT
                Mreply mreply = response.body();
                if(mreply != null){
                    log("testing reply:"+gson.toJson(mreply));
                    log("response finally:"+response.body().result);
                    log("response finally:"+response.body().message);

                    //PROCESS REPLY
                    if(mreply.result){
                        transactionsorm trans = transactionsorm.findById(transactionsorm.class,transId);
                        trans.sentdate = new Date();
                        trans.senttoserver = "yes";
                        trans.save();
                        //sync balance
                        //TEST SYNC
                        SyncBalanceJob sj = new SyncBalanceJob(vars);
                        MySingleton.getInstance(vars.context).jobManager.addJobInBackground(sj);

                    }else if(!mreply.result){

                        //CHECK IF REPLY CONTAINTS UNIQUE THEN IT WAS ALREADY SENT LEAVE AS YES
                        if(mreply.errorMessage.contains("exists")){
                            transactionsorm trans = transactionsorm.findById(transactionsorm.class,transId);
                            trans.sentdate = new Date();
                            trans.senttoserver = "yes";
                            trans.save();
                        }else{
                            transactionsorm trans = transactionsorm.findById(transactionsorm.class,transId);
                            trans.sentdate = new Date();
                            trans.senttoserver = "error:"+mreply.errorMessage;
                            trans.save();
                        }

                    }


                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });



//
//        Retrofit restAdapter = new Retrofit.Builder().baseUrl(Globals.BASE_URL).build();
////                .setEndpoint(Globals.BASE_URL)
////                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.github.com")
//                .build();
//
//       // RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)setEndpoint(API).build();
//
//        MpockRetroInterface apiService =
//                retrofit.create(MpockRetroInterface.class);
//
//
//        Call<Mreply> call =
//                gitHubService.repoContributors("square", "retrofit");
//
//
//        Call<Mreply> call =
//                apiService.getStudentList("square", "retrofit");
//
//        Minput minput = new Minput();
//        minput.apiKey = "androidapp";
//
//        log("retro: calling retorfit");
//        log("retro: rating: base_url:" + Globals.BASE_URL);
//
//        response = call.execute();


      //  Call<List<Repo>> repos = service.listRepos("octocat");
//        Call<Mreply> theReply = apiService.getStudentList(minput,new Callback<Mreply>(){
//            @Override
//            public void onResponse(Response<Mreply> response, Retrofit retrofit) {
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//            }
//        });


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
