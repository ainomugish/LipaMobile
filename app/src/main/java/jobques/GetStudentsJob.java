package jobques;

import android.util.Log;

import de.greenrobot.event.EventBus;
import entities.Student;

import com.duali.itouchpop2_test.utils.Globals;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import eventBusClasses.SyncDone;
import eventBusClasses.SyncStarted;
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
import sUtils.StudentUtils;
import sugaOrmUtils.StudentSugarUtils;
import sugarorms.studentorm;


/**
 * Created by Mac on 2015/10/18.
 */
public class GetStudentsJob extends Job {

    boolean log = true;
    Vars vars;

    public GetStudentsJob(Vars vars) {
        super(new Params(5).requireNetwork());
        log("++++++++++:GetStudentsJob");
        this.vars = vars;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        log("GetStudentsJob on rugen");
        EventBus.getDefault().post(new SyncStarted("started"));

        Retrofit client = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MpockRetroInterface service = client.create(MpockRetroInterface.class);

        Minput minput = new Minput();
        minput.apiKey = vars.macAddress;
       // Call<GitResult> call = service.getUsersNamedTom("tom");
        Call<Mreply> call = service.getStudentList(minput);

        call.enqueue(new Callback<Mreply>() {
            @Override
            public void onResponse(Response<Mreply> response, Retrofit retrofit) {

                //GET RESPONSE OBJECT
                Mreply mreply = response.body();
                if(mreply != null){
                    log("response finally:"+response.body().result);
                    log("response finally:"+response.body().message);

                    //GET STUDET LIST
                    if(mreply.studentList != null){
                        log("student list is not null:"+mreply.studentList.size());

                        //GET SIZE OF LOCAL LIST
                        Long localStudentListSize = ((Long) studentorm.count(studentorm.class));
                        log("localStudentListSize:"+localStudentListSize);

                        //testing stuff
//                        for(int i=0;i<5;i++){
//                            log("cid sent student id: "+mreply.studentList.get(i).getStudentId());
//                            studentorm saveStudent = new studentorm(mreply.studentList.get(i));
//                            saveStudent.save();
//                            log("cid id of saved student:" + saveStudent.studentid);
//                            log("cid:============");
//                        }

                        if(mreply.studentList.size() != localStudentListSize){
                            //LOOP THROUGH STUDENTS AND CREATE OBJECT
                            for(Student s:mreply.studentList){
                                log("gsj3.2 inside student list:"+s.getStudentId());

                                //check if student exists and if not CREATE AND ADD STUDENT
                              //  studentorm checkStudent = StudentSugarUtils.getStudentById(s.getStudentId());
                                if(s.getAccNumber() != null){
                                    log("accNumber is not null");

                                   studentorm checkStudent = StudentSugarUtils.getStudentById(s.getStudentId());
                                 //   studentorm checkStudent = StudentSugarUtils.getStudentByAccNumber(s.getAccNumber());
                                    if( checkStudent == null){
                                        log("gsj3.1 student not found saving");
                                        studentorm saveStudent = new studentorm(s);
                                        saveStudent.save();
                                        log("gsj3.11 savedstudentid:"+saveStudent.studentid);
                                    }else if(checkStudent != null){
                                        log("gsj3.2 student found:"+checkStudent.studentid);
                                        //CHECK IF BALANCE IS NOT NULL
                                        if(s.getAccBalance() != null){
                                            log("gsj3.3 sent student balanceis not null:"+s.getAccBalance());
                                            log("gsj3.31 udid:"+s.getUdid()+"::"+s.getStudentId());
                                             checkStudent.accbalance = s.getAccBalance();
                                          //  checkStudent.accbalance = StudentUtils.getBalance(checkStudent)+s.getAccBalance();
                                          //  checkStudent.accbalance = StudentUtils.getBalance(checkStudent)+s.getAccBalance();
                                            checkStudent.save();
                                            log("gsj3.311 saved student balance:"+checkStudent.accbalance);
                                        }else if(s.getAccBalance() == null){
                                            log("gsj3.5 sent student balance IS null:");
                                            checkStudent.accbalance = 0;
                                            checkStudent.save();
                                            log("gsj3.51 saved student balance:"+checkStudent.accbalance);
                                        }

                                    }else{
                                        log("gsj3.6 student found in db not saving:"+s.getStudentId());
                                    }
                                }else{
                                    log("acc number is null");
                                }
                            }
                        }else{
                            log("locallist size is equal to size from server");
                            log("localStudentListSize:"+localStudentListSize);
                            log("student list from server:"+mreply.studentList.size());
                        }


                    }

                }

                //SEND SYCN THAT ITS DONE
                SyncDone sd = new SyncDone("this");
                EventBus.getDefault().post(sd);

                //START GET PINS JOB
                GetStudentPinJob getStudentPinJob = new GetStudentPinJob(vars);
                MySingleton.getInstance(vars.context).jobManager.addJobInBackground(getStudentPinJob);
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
        return false;
    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }
}
