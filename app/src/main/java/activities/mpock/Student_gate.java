package activities.mpock;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
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



import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import entities.Student;
import eventBusClasses.SyncGatePassDone;
import jobques.GetGatePassJob;

import mPock.MySingleton;
import mPock.Vars;

import sugarorms.studentorm;

//sms
import java.io.*;
import java.net.*;




public class Student_gate extends ActionBarActivity {

    private static final String TAG = Student_gate.class.getSimpleName();

    Vars vars;
    boolean log = true;

    //STUDENT ID sent in extra
    Long studentId;

    studentorm student;

    //UI STUFF
    TextView student_names;

    TextView student_school;

    EditText student_pin;

    Button btn;

    Context context;

    Gson gson;

    String transId;



    SyncGatePassDone currentStudentGatePassReply;

    LinearLayout enter_gate_layout;
    ProgressBar gateProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_gate);

        vars = new Vars(this);
        gson = new Gson();
        context = this;

        //UI STUFF
        student_names = (TextView) findViewById(R.id.student_names);

        student_school = (TextView) findViewById(R.id.student_school);
        btn = (Button) findViewById(R.id.buttonGate);
        student_pin = (EditText) findViewById(R.id.student_pin);

        //PRORGRESS BAR STUFF
        gateProgressBar = (ProgressBar) findViewById(R.id.gateProgressBar);
        enter_gate_layout = (LinearLayout) findViewById(R.id.enter_gate_layout);
        //SHOW MAEK PAYMENT LAYOUT
        enter_gate_layout.setVisibility(View.VISIBLE);



        //GET EXTRAS
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(!extras.getString(Globals.STUDENT_ID).isEmpty()){


                studentId = Long.valueOf(extras.getString(Globals.STUDENT_ID));

                //FIND STUDENT
                student = studentorm.findById(studentorm.class,studentId);
                log("student loaded:" + student.udid);

                //SET UI VARS
                student_names.setText(student.firstnmae + " " + student.lastname);


                //HANDLE SCHOOL NAME
                student_school.setText(String.valueOf(student.studentschoolname));

                gateProgressBar.setVisibility(View.GONE);



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

    //MAKE PAYMENT
    public  void makePayment(View v) throws Exception {
        log("++++++++++Check Gate Pass");

        if(student_pin.getText().toString().length() < 1){
            log("invalid pin");
            vars.alerter.alerterAny("Error","Invalid Pin");
        }else if(student.pin != Integer.valueOf(student_pin.getText().toString())){
            vars.alerter.alerterAny("Error","Incorrect Pin");
        }else{
            log("starting_checking_gate_pass");
            Student std = new Student();
            std.setAccNumber(student.accnumber);
            GetGatePassJob gsj = new GetGatePassJob(vars,std);
            MySingleton.getInstance(vars.context).jobManager.addJobInBackground(gsj);
            gateProgressBar.setVisibility(View.VISIBLE);
            btn.setText("CHECKING GATE PASS...");
        }
    }



/*
    class rfFindGatePass extends AsyncTask<String, Boolean,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(TAG, "onPreExecute()");
            gateProgressBar.setVisibility(View.VISIBLE);
            //bStop = false;
        }

        @Override
        protected Integer doInBackground(String... params) {
            log("doInBackground..............");
            count = 0;
            loopFindCard = true;
            byte opt = 0;
            Log.e(TAG, "RFFindCard Running...");
            while (loopFindCard) {

                log("inside   while (loopFindCard) {");

                if (params != null) {
                    for (String s : params) {
                        if (s.equals("A")) {
                            opt = (byte) 0x41;
                        }
                        else if (s.equals("G")) {
                            opt = (byte) 0x41;
                        }else if (s.equals("B")) {
                            opt = (byte) 0x42;
                        } else
                            opt = (byte) 0;

                        //Log.d(TAG, "Type "+s);
                        DualCardResponse ret = dualCard.DE_FindCard(mPort, (byte) 0, (byte) 0x01, (byte) 0x01, opt);
                        log("[" + (++count) + "] FindCard(" + s + ")");
                        ++count;
                        if (ret.getResponseCode() == ResponseCode.DE_OK) {


                            byte[] data = ret.getResponseData();
                            Log("[" + count + "] " + Hex.bytesToHexString(data), RECEIVEFLAG);
                            log("hope hope hope hope:" + Hex.bytesToHexString(data));
                            //Toast.makeText(context, "SHORT:" + StudentUtils.shortUdid(Hex.bytesToHexString(data)),Toast.LENGTH_LONG).show();

                            log("before loopFindCard:" + loopFindCard);
                            loopFindCard = false;

                            //CHECK FOR UDID
                            log("before checking for hex");
                            if(s.equals("A")) {
                                checkForUdid(Hex.bytesToHexString(data));
                            }else if(s.equals("G")) {
                                checkForUdidG(Hex.bytesToHexString(data));
                            }

                            log("after checking for hex");


                            log("after loopFindCard:" + loopFindCard);
                        } else if (ret.getResponseCode() == ResponseCode.DE_NO_TAG_ERROR) {
                            Log("[" + count + "] No Tag Error", ERRORFLAG);
                        } else {
                            Log("[" + count + "] Error", ERRORFLAG);//ERROR
                        }
                    }
                } else {
                    log("params are null");
                }

                if (bStop == true)
                    return 0;

                try {
                    Thread.sleep(nSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return 0;
        }


        protected void onPostExecute(Integer result) {
            //REMOVE PROGRESS BAR
            getCardProgressBar.setVisibility(View.GONE);
        }


    }
*/
    @Subscribe
    public void onEvent(final SyncGatePassDone sgr) {
        log("studentGateEvent RECEIVED");

        //SET THE currentStudentChargeReply
        currentStudentGatePassReply = sgr;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //CHECK TRANS Id
                if (sgr.numPinUpdated) {
                    log("get out of school, lets roll");
                    sendSms sms = new sendSms();
                    sms.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "true");
                    //REMOVE DIALOG
                    //gateProgressBar.setVisibility(View.GONE);
                    //btn.setBackgroundColor(16711936);
                    //btn.setTextColor(16777216);
                    //log("=====================gate passs allowed");
                    //btn.setText("GATE PASS ALLOWED");

                    //student = studentorm.findById(studentorm.class,studentId);


/*
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        log("GATE EVENT: Waiting Seconds");
                    }
                    vars.alerter.alerterAnySuccessActivity("Notice","Go Back to Main Screen", RFActivity.class);*/
                    //SHOW MAEK PAYMENT LAYOUT
                    //enter_payment_layout.setVisibility(View.VISIBLE);

                    //UPDATE EDIT TEXT
                    //student_amount.setText(String.valueOf(scr.studentCharge.amount));
                    //student_amount.setVisibility(View.VISIBLE);
                } else {

                    sendSms sms = new sendSms();
                    sms.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "false");
                    //gateProgressBar.setVisibility(View.GONE);
                    //btn.setTextColor(16777216);
                    //btn.setHighlightColor(65536);
                    //log("=====================gate passs denied");
                    //btn.setTextColor(16777216);
                    //btn.setText("GATE PASS DENIED");
/*
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        log("GATE EVENT: Waiting Seconds");
                    }
                    vars.alerter.alerterAnySuccessActivity("Notice","Go Back to Main Screen", RFActivity.class);*/
                }
            }
        });
    }


    class sendSms extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(TAG, "onPreExecute()");

        }

        @Override
        protected Boolean doInBackground(String... params) {
            log("doInBackground...........SMS...");
            String reply="";
            Boolean rep=false;
            Log.e(TAG, "Send SMS Running...");

            String msg="Dear parent, "+student.firstnmae+" "+
                    student.lastname+", is now leaving the school premises. "+

                    ". School mgt.";
            if(!student.parentphone.isEmpty() && student.parentphone != null) {

                try {
                    String data = URLEncoder.encode("mocean-username", "UTF-8") + "=" +
                            URLEncoder.encode("lipa", "UTF-8");
                    data += "&" + URLEncoder.encode("mocean-password", "UTF-8") + "=" +
                            URLEncoder.encode("LiP4", "UTF-8");
                    data += "&" + URLEncoder.encode("mocean-to", "UTF-8") + "=" +
                            URLEncoder.encode(student.parentphone, "UTF-8");
                    data += "&" + URLEncoder.encode("mocean-from", "UTF-8") + "=" +
                            URLEncoder.encode("LipaMobile", "UTF-8");
                    data += "&" + URLEncoder.encode("mocean-url-text", "UTF-8") + "=" +
                            URLEncoder.encode(msg, "UTF-8");


                    // Send data
                    URL url = new URL("http://sms.smsone.co.ug:8866/cgi-bin/sendsms");
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new
                            OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the response
                    BufferedReader resp = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));
                    //System.out.println(resp.readLine().substring(7));

                    reply = resp.readLine();
                    System.out.println(reply.substring(7, 9));
                    String p = reply.substring(7, 9);
                    System.out.println(reply);

                    resp.close();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }


            for (String s : params) {
                        if (s.equalsIgnoreCase("true")) {
                            rep =true;
                        }
                        else{
                            rep =true;
                        }
                    }


                // Display the string.


            return rep;
        }


        protected void onPostExecute(Boolean result) {
            //REMOVE PROGRESS BAR
            if (result)
            {
                gateProgressBar.setVisibility(View.GONE);
                btn.setText("GATE PASS ALLOWED");
                btn.setBackgroundColor(Color.GREEN);
                btn.setTextColor(Color.BLACK);
                //btn.setHighlightColor(555);
            }else{
                gateProgressBar.setVisibility(View.GONE);
                btn.setText("GATE PASS DENIED");
                btn.setTextColor(Color.WHITE);
                btn.setBackgroundColor(Color.RED);
            }

        }


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

    public void cancelG(View v) throws InterruptedException {
        EventBus.getDefault().unregister(this);
        context = this;
        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);*/
        //vars.alerter.alerterAny("Warning", "Remove card to tap again");

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
