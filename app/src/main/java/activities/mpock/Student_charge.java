package activities.mpock;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duali.itouchpop2_test.R;
import com.duali.itouchpop2_test.RFActivity;
import com.duali.itouchpop2_test.utils.Globals;
import com.google.gson.Gson;

import java.util.Date;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import entities.CanteenTrans;
import entities.Devices;
import entities.Student;
import eventBusClasses.SendStudent;
import eventBusClasses.StudentChargeFinished;
import eventBusClasses.StudentChargeReply;
import jobques.PostCanteenTransJob;
import mPock.MySingleton;
import mPock.Vars;
import models.StudentCharge;
import sUtils.RandomStringGenerator;
import sUtils.StudentUtils;
import sugarorms.studentorm;
import sugarorms.transactionsorm;
import java.util.Calendar;
import java.util.TimeZone;

public class Student_charge extends ActionBarActivity {

    Vars vars;
    boolean log = true;
    Calendar cal;

    //STUDENT ID sent in extra
    Long studentId;

    studentorm student;

    //UI STUFF
    TextView student_names;
    TextView student_balance;
    TextView student_school;
    EditText student_amount;
    EditText student_pin;
    LinearLayout enter_payment_layout;

    Gson gson;

    Context context;

    String transId;

    ProgressBar amountProgressBar;

    StudentChargeReply currentStudentChargeReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_charge);

        vars = new Vars(this);
        gson = new Gson();

        //UI STUFF
        student_names = (TextView) findViewById(R.id.student_names);
        student_balance = (TextView) findViewById(R.id.student_balance);
        student_school = (TextView) findViewById(R.id.student_school);
        student_amount = (EditText) findViewById(R.id.student_amount);
        student_pin = (EditText) findViewById(R.id.student_pin);

        //PRORGRESS BAR STUFF
        amountProgressBar = (ProgressBar) findViewById(R.id.amountProgressBar);

        enter_payment_layout = (LinearLayout) findViewById(R.id.enter_payment_layout);

        //GET EXTRAS
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.getString(Globals.STUDENT_ID) != null){

                //GET TRANSACTION ID
                try {
                    transId =  vars.macAddress+"_"+RandomStringGenerator.generateRandomString();
                    log("transid is:"+transId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                studentId = Long.valueOf(extras.getString(Globals.STUDENT_ID));

                //FIND STUDENT
                student = studentorm.findById(studentorm.class,studentId);
                log("student loaded:" + student.udid);

                //SET UI VARS
                student_names.setText(student.firstnmae + " " + student.lastname);

                //HANDLE BALANCE
                student_balance.setText(String.valueOf(StudentUtils.getBalance(student)));

                //HANDLE SCHOOL NAME
                student_school.setText(String.valueOf(student.studentschoolname));

                log("got student.pin pin:"+student.pin);


                if(student.pin == 0){
                        vars.alerter.alerterAnySuccessActivity("Error", "Invalid Pin, Pls contact Parent to Set Pin", RFActivity.class);
                }

                //SEND STUDENT INFO TO SERVICE TO PUBLISH
                //SendStudent sendStudent = new SendStudent(student, transId);
                StudentCharge studentCharge = new StudentCharge(student, transId);
                EventBus.getDefault().post(studentCharge);


            }else{
                log("error, unable to load student id");
            }

        }else{
            log("unable to load extras");
        }
    }

    //MAKE PAYMENT
    public  void makePayment(View v) throws Exception {
        log("++++++++++make payment");

        if(student_pin.getText().toString().length() < 1){
            log("invalid pin");
            vars.alerter.alerterAny("Error","Invalid Pin");
        }else if(student_amount.getText().toString().length()<2){
            log("invalid amount");
            vars.alerter.alerterAny("Error","Invalid Amount");
        }else if(Integer.valueOf(student_amount.getText().toString()) > student.accbalance){
            log("not enough funds");

            //SET TRANSACTION RESULT
            currentStudentChargeReply.studentCharge.transResult = false;
            log("currentStudentChargeReply.studentCharge.transResult =" + currentStudentChargeReply.studentCharge.transResult);

            //POST FAILURE
            EventBus.getDefault().postSticky(new StudentChargeFinished(currentStudentChargeReply.studentCharge));

            vars.alerter.alerterAny("Error","Not Enough Funds on Card");
        }else if(student.pin != Integer.valueOf(student_pin.getText().toString())){
            vars.alerter.alerterAny("Error","Incorrect Pin");
        }else{
            log("starting_payment");

            //DEDCUT STUDENT AMOUNT
            student.accbalance = student.accbalance - Integer.valueOf(student_amount.getText().toString());
            student.save();

            //SAVE TRANSACTIONS
            transactionsorm torm = new transactionsorm();
            torm.senttoserver = "no";
            torm.amount = Integer.valueOf(student_amount.getText().toString());

            //CANTEEN BALANCES
            torm.canteenbalancebefore = vars.prefs.getInt(Globals.CANTEEN_TOTAL, 0);
            log("torm.canteenbalancebefore :"+torm.canteenbalancebefore);
            torm.canteenbalanceafter = torm.canteenbalancebefore + Integer.valueOf(student_amount.getText().toString());
            vars.edit.putInt(Globals.CANTEEN_TOTAL, torm.canteenbalanceafter);
            vars.edit.commit();
            log("torm.canteenbalanceafter :"+torm.canteenbalanceafter);

            //STUDENT BALANCES
            torm.transdate = new Date();

            //CORRECTED
            torm.studentbalancebefore = student.accbalance;
            log("torm.studentbalancebefore:"+torm.studentbalancebefore);
            torm.studentbalanceafter = student.accbalance - Integer.valueOf(student_amount.getText().toString());

            //bad
//            torm.studentbalancebefore = student.accbalance - Integer.valueOf(student_amount.getText().toString());
//            log("torm.studentbalancebefore:"+torm.studentbalancebefore);
//            torm.studentbalanceafter = student.accbalance;


            log("torm.studentbalanceafter:" + torm.studentbalanceafter);
            torm.studentname = student.lastname;
            torm.studentudid = student.udid;
            torm.studentid = student.studentid;
            torm.deviceTransId = transId;
            log("torm.deviceTransId:"+torm.deviceTransId);
          //  torm.studentjson = gson.toJson()

            torm.save();

            //CREATE STUFF FOR JOB
            CanteenTrans ct = new CanteenTrans();
            ct.setAmount(torm.amount);
            ct.setCanteenbalanceafter(torm.canteenbalanceafter);
            ct.setCanteenbalancebefore(torm.canteenbalancebefore);
            ct.setDateSent(new Date());
            cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3:00"));
            ct.setTimeSent(cal.getTime());
            //SET DEVICE
            Devices device = new Devices();
            device.setMacaddress(vars.macAddress);
            ct.setDevice(device);
            //SET STUDENT
            Student student2 = new Student();
            student2.setStudentId(torm.studentid);
            ct.setStudent(student2);
            ct.setStudentbalanceafter(torm.studentbalanceafter);
            ct.setStudentbalancebefore(String.valueOf(torm.studentbalancebefore));
            ct.setTransDate(new Date());
            ct.setDeviceTransId(torm.deviceTransId);

            //ADD TO JOB QUE
            PostCanteenTransJob pc = new PostCanteenTransJob(vars,ct,torm.getId());
            MySingleton.getInstance(vars.context).jobManager.addJobInBackground(pc);

            log("new student balance:" + student.accbalance);

            //SET TRANSACTION RESULT
            currentStudentChargeReply.studentCharge.transResult = true;
            log("currentStudentChargeReply.studentCharge.transResult ="+currentStudentChargeReply.studentCharge.transResult);

            //POST FAILURE
            EventBus.getDefault().postSticky(new StudentChargeFinished(currentStudentChargeReply.studentCharge));

          //  vars.alerter.alerterAny("Notice", "Transaction Complete, New Balance:" + student.accbalance);
            vars.alerter.alerterAnySuccessActivity("Notice","Transaction Complete, New Balance:"+student.accbalance, RFActivity.class);
          //  vars.alerter.alerterAny("Notice","Transaction Complete, New Balance:"+student.accbalance);

        }
    }

    @Subscribe
    public void onEvent(final StudentChargeReply scr) {
        log("studentChargeonEvent RECEIVED");

        //SET THE currentStudentChargeReply
        currentStudentChargeReply = scr;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //CHECK TRANS Id
                if (scr.studentCharge.transId.equalsIgnoreCase(transId)) {
                    log("transid matches, lets roll");

                    //REMOVE DIALOG
                    amountProgressBar.setVisibility(View.GONE);

                    //SHOW MAEK PAYMENT LAYOUT
                    enter_payment_layout.setVisibility(View.VISIBLE);

                    //UPDATE EDIT TEXT
                    student_amount.setText(String.valueOf(scr.studentCharge.amount));
                    student_amount.setVisibility(View.VISIBLE);
                } else {
                    vars.alerter.alerterAny("Keypad Error", "Trans id does not match");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void cancelC(View v){
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
