package activities.mpock;

/**
 * Created by i on 6/16/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duali.itouchpop2_test.R;
import com.duali.itouchpop2_test.RFActivity;
import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;

import com.orm.query.Condition;
import com.orm.query.Select;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import entities.Devices;
import entities.MealTransactions;
import entities.Meals;
import entities.Student;
import eventBusClasses.MealDone;
import eventBusClasses.SyncGatePassDone;
import jobques.GetGatePassJob;

import jobques.PostMealTransJob;
import mPock.MySingleton;
import mPock.Vars;

import sUtils.RandomStringGenerator;
import sugarorms.studentorm;
import sugarorms.mealsorm;
import sugarorms.mealtransactionsorm;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;


public class Student_meal extends ActionBarActivity {

    private static final String TAG = Student_meal.class.getSimpleName();

    Vars vars;
    boolean log = true;
    Context context;

    //STUDENT ID sent in extra
    Long studentId;
    String studentClass;

    studentorm student;
    mealsorm meals;
    mealtransactionsorm mealTrans;

    //UI STUFF
    TextView student_names;

    TextView student_school;

    EditText student_pin;

    Button btn;

    Boolean result = true;
    Gson gson;

    String transId;
    public boolean mealResult = false;



    MealDone mealReply;

    LinearLayout enter_meal_layout;
    ProgressBar mealProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_meal);

        vars = new Vars(this);
        gson = new Gson();

        //UI STUFF
        student_names = (TextView) findViewById(R.id.student_name);

        student_school = (TextView) findViewById(R.id.student_school);
        btn = (Button) findViewById(R.id.buttonMeal);
        student_pin = (EditText) findViewById(R.id.student_pin);

        //PRORGRESS BAR STUFF
        mealProgressBar = (ProgressBar) findViewById(R.id.mealProgressBar);
        enter_meal_layout = (LinearLayout) findViewById(R.id.enter_meal_layout);
        //SHOW MAEK PAYMENT LAYOUT
        enter_meal_layout.setVisibility(View.VISIBLE);



        //GET EXTRAS
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(!extras.getString(Globals.STUDENT_ID).isEmpty()){

                try {
                    transId =  vars.macAddress+"_"+ RandomStringGenerator.generateRandomString();
                    log("transid is:"+transId);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                studentId = Long.valueOf(extras.getString(Globals.STUDENT_ID));
                studentClass = String.valueOf(extras.getString(Globals.STUDENT_CLASS));
                //FIND STUDENT
                student = studentorm.findById(studentorm.class,studentId);
                log("student loaded:" + student.udid);

                //SET UI VARS
                student_names.setText(student.firstnmae + " " + student.lastname+" "+student.studentclass);


                //HANDLE SCHOOL NAME
                student_school.setText(String.valueOf(student.studentschoolname));

                mealProgressBar.setVisibility(View.GONE);



                log("got student.pin pin:"+student.pin);


                if(student.pin == 0){
                    vars.alerter.alerterAnySuccessActivity("Error", "Invalid Pin, Pls contact Parent to Set Pin", RFActivity.class);
                }

            }else{
                log("error, unable to load student id");
            }

        }else{
            log("unable to load extras");
        }
    }



    //MCHECK MEAL
    public  void checkMeal(View v) throws Exception {
        log("++++++++++Check Meal Status");

        if(student_pin.getText().toString().length() < 1){
            log("invalid pin");
            vars.alerter.alerterAny("Error","Invalid Pin");
        }else if(student.pin != Integer.valueOf(student_pin.getText().toString())){
            vars.alerter.alerterAny("Error","Incorrect Pin");
        }else{
            log("starting_checking_meal");

            mealProgressBar.setVisibility(View.VISIBLE);
            btn.setText("CHECKING MEAL STATUS...");

            //Date now = now();//get current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date mealDate = sdf.parse(sdf.format(new Date()));

            Select select = Select.from(mealsorm.class)
                    .where(Condition.prop("dateCreated").eq(mealDate.getTime()),
                            Condition.prop("class1").eq(studentClass));
            //List<mealsorm> mls = mealsorm.find(mealsorm.class,"class1 = ?",studentClass);
            List<mealsorm> mls = select.list();
            LocalTime ld = new LocalTime("Africa/Kampala");
            while(!mls.isEmpty())
            //for(mealsorm m:mls)
            {
                mealsorm m = mls.get(0);
                //Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3:00"));

                if(ld.getHourOfDay()<=11){
                    List<mealtransactionsorm> mtorm =checkMealTransaction(m);

                    if(!mtorm.isEmpty())
                    {
                        result=false;
                    }else{
                        mealtransactionsorm mto = new mealtransactionsorm();
                        Boolean result = saveMealTransaction(m,mto);
                        //SET TRANSACTION RESULT
                        //log("currentStudentChargeReply.studentCharge.transResult ="+currentStudentChargeReply.studentCharge.transResult);
                        //***************************************************************************************************************

                        EventBus.getDefault().post(new MealDone(result));

                    }

                }else if(ld.getHourOfDay()<=18){
                    List<mealtransactionsorm> mtorm =checkMealTransaction(m);

                    if(!(mtorm.isEmpty() && mtorm.get(0).lunch))
                    {
                        Boolean result = saveMealTransaction(m,mtorm.get(0));
                        //SET TRANSACTION RESULT
                        //log("currentStudentChargeReply.studentCharge.transResult ="+currentStudentChargeReply.studentCharge.transResult);
                        //***************************************************************************************************************

                        EventBus.getDefault().post(new MealDone(result));

                    }else if(mtorm.isEmpty()){
                        mealtransactionsorm mto = new mealtransactionsorm();
                        Boolean result = saveMealTransaction(m,mto);
                        //SET TRANSACTION RESULT
                        //log("currentStudentChargeReply.studentCharge.transResult ="+currentStudentChargeReply.studentCharge.transResult);
                        //***************************************************************************************************************

                        EventBus.getDefault().post(new MealDone(result));


                    }else{
                        result=false;
                    }


                }else{
                    List<mealtransactionsorm> mtorm =checkMealTransaction(m);

                    if(!(mtorm.isEmpty() && mtorm.get(0).dinner))
                    {
                        Boolean result = saveMealTransaction(m,mtorm.get(0));
                        //SET TRANSACTION RESULT
                        //log("currentStudentChargeReply.studentCharge.transResult ="+currentStudentChargeReply.studentCharge.transResult);
                        //***************************************************************************************************************

                        EventBus.getDefault().post(new MealDone(result));

                    }else if(mtorm.isEmpty()){
                        mealtransactionsorm mto = new mealtransactionsorm();
                        Boolean result = saveMealTransaction(m,mto);
                        //SET TRANSACTION RESULT
                        //log("currentStudentChargeReply.studentCharge.transResult ="+currentStudentChargeReply.studentCharge.transResult);
                        //***************************************************************************************************************

                        EventBus.getDefault().post(new MealDone(result));


                    }else{
                        result=false;
                    }

                }
                //ct.setTimeSent(cal.getTime());
            }
            //Student std = new Student();
            //std.setAccNumber(student.accnumber);
            //**************************************************************************************************
            //SAVE TRANSACTIONS


        }
    }

    public List<mealtransactionsorm> checkMealTransaction(mealsorm m){
        Select select1 = Select.from(mealtransactionsorm.class)
                .where(Condition.prop("dateTap").eq(m.dateCreated),
                        Condition.prop("studentId").eq(student.studentid));
        List<mealtransactionsorm> mtorm = select1.list();
        return mtorm;

    }

    public Boolean saveMealTransaction(mealsorm m, mealtransactionsorm mto){
        //mealtransactionsorm morm = new mealtransactionsorm();
        mto.senttoserver = "no";
        //get current meal type from mls
        if (m.type == "BF") {
            mto.breakFast = true;
            mto.tapTimeBf = new Date();

        } else if (m.type == "LUNCH") {
            mto.lunch = true;
            mto.tapTimeLunch = new Date();

        } else if (m.type == "DINNER") {
            mto.dinner = true;
            mto.tapTimeDinner = new Date();

        }
        mto.dateTap = new Date();
        mto.deviceTransId = transId;
        mto.studentId = student.studentid;
        mto.studentudid = student.udid;
        mto.mealsId = m.idmeals;

        mto.save();

        //CREATE STUFF FOR JOB
        MealTransactions mt = new MealTransactions();
        if (mto.breakFast) {
            mt.setBreakFast(true);
            mt.setTapTimeBf(mto.tapTimeBf);

        } else if (mto.lunch) {
            mt.setLunch(true);
            mt.setTapTimeLunch(mto.tapTimeLunch);

        } else if (mto.dinner) {
            mt.setDinner(true);
            mt.setTapTimeDinner(mto.tapTimeDinner);

        }
        mt.setDateTap(mto.dateTap);
        //SET DEVICE
        Devices device = new Devices();
        device.setMacaddress(vars.macAddress);
        mt.setDevicesIddevices(device);
        //SET Meals
        Meals meal = new Meals();
        meal.setIdmeals(mto.mealsId);
        mt.setMealsIdmeals(meal);
        Student sttd = new Student();
        sttd.setAccNumber(student.accnumber);
        sttd.setStudentId(student.studentid);
        mt.setStudentstudentId(sttd);


            /*ct.setDateSent(new Date());
            cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3:00"));
            ct.setTimeSent(cal.getTime());
            ct.setDeviceTransId(torm.deviceTransId);*/

        //ADD TO JOB QUE
        PostMealTransJob pc = new PostMealTransJob(vars, mt, mto.getId());
        MySingleton.getInstance(vars.context).jobManager.addJobInBackground(pc);

        return true;

    }

    @Subscribe
    public void onEvent(final MealDone sgr) {
        log("studentGateEvent RECEIVED");

        //SET THE currentStudentChargeReply
        mealReply = sgr;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //CHECK TRANS Id
                if (sgr.mealResult) {
                    log("Enjoy Meal");
                    mealProgressBar.setVisibility(View.GONE);
                    btn.setText("MEAL ALLOWED");
                    btn.setBackgroundColor(Color.GREEN);
                    btn.setTextColor(Color.BLACK);
                    //btn.setHighlightColor(555);

                } else {
                    mealProgressBar.setVisibility(View.GONE);
                    btn.setText("MEAL DENIED");
                    btn.setTextColor(Color.WHITE);
                    btn.setBackgroundColor(Color.RED);

                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    public void cancelM(View v){
        context =this;
        Toast.makeText(context, "PLEASE REMOVE CARD AND TAP AGAIN", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, RFActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_charge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }
}
