package com.duali.itouchpop2_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.duali.dualcard.jni.DualCardJni;
import com.duali.dualcard.jni.DualCardResponse;
import com.duali.dualcard.jni.ResponseCode;
import com.duali.itouchpop2_test.utils.Globals;
import com.duali.itouchpop2_test.utils.Hex;
import com.duali.nfc.ndef.Ndef;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import activities.mpock.SearchStudent;
import activities.mpock.Student_charge;
import activities.mpock.Student_gate;
import activities.mpock.Student_meal;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import entities.CanteenTrans;
import eventBusClasses.MqConnected;
import eventBusClasses.MqDisonnected;
import eventBusClasses.StudentChargeFinished;
import eventBusClasses.StudentChargeReply;
import eventBusClasses.SyncDone;
import eventBusClasses.SyncMealDone;
import eventBusClasses.SyncPinDone;
import eventBusClasses.SyncStarted;
import jobques.GetSchoolMealJob;
import jobques.GetStudentPinJob;
import jobques.GetStudentsJob;
import jobques.PostCanteenTransJob;
import mPock.MySingleton;
import mPock.Vars;
import me.drakeet.materialdialog.MaterialDialog;
import models.Minput;
import models.StudentCharge;
import mqes.MpockMqService;
import sUtils.StudentUtils;
import sugaOrmUtils.StudentSugarUtils;
import sugarorms.mealsorm;
import sugarorms.studentorm;
import sugarorms.transactionsorm;
import utils.MiscUtilz;

public class RFActivity extends Activity implements
        OnClickListener, Handler.Callback {

    private ToggleButton tbtnrfa, sync_students_button, canteen_button, search_button, gate_pass_button;

    private Button reset_students_button, tbtnexit, sync_pin_button, sync_meal_button;
    private Button change_device_name_button;
    private Button change_partner_device_name_button;

    // private ImageView imgSAM, imgP2P, imgRTC;
    // private ImageView imgRTC;

    private EditText editLog;
    private TextView numStudentsTextView, version;
    // private Spinner spinner;

    //DEVICE STUFF
    TextView device_name;
    TextView partner_device_name;

    //Timer
    Date curMillis;
    int curYear, curMonth, curDay, curHour, curMinute, curNoon, curSecond;
    Calendar c;
    String noon = "";


    //   public receiveThread mReceiveThread;
    private static final String TAG = MainActivity.class.getSimpleName();

    /* 1⺻ 11 1111 */
    private static final int mPort = 1000;
    private static final int baud = 115200;
    private static final int slotNo = 0;    //Slot 0 1111

    private static final int SENDFLAG = 1;
    private static final int RECEIVEFLAG = 2;
    private static final int ERRORFLAG = 3;
    private static final int BLANKFLAG = 4;
    private static final int SUCCESSFLAG = 5;
    private static final int NORMALFLAG = 6;
    private static final int RESULT_CONN_CODE = 0;
    private static int nSleep;
    private static boolean bStop = false;

    private boolean toggleFlag = false;
    private DualCardJni dualCard;    //JNI dualCard

    ArrayAdapter<CharSequence> adspin;

    private int cnt = 0;
    private int fd;
    private int count = 0;

    private boolean loopFindCard;

    private rfFindCard testRf = null;

    Context context;

    MaterialDialog mMaterialDialog;

    Long numStudents = 0L;
    Long numMeals = 0L;

    Vars vars;

    // receiver that notifies the Service when the phone gets data connection
    private NetworkConnectionIntentReceiver netConnReceiver;

    Button new_trans_button;
    Button new_pass_button;
    Button new_meal_button;

    //CONNECTION PROGRESS BAR
    ProgressDialog progressBar;

    TextView connected;

    ProgressBar getCardProgressBar;
    ProgressBar getPassProgressBar;
    ProgressBar getMealProgressBar;

    // NFC handling stuff
    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    IntentFilter[]  mFilters;
    String[][] mTechLists;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private boolean mDeviceConnected = false;
    private Handler handler = new Handler(this);
    private ChatManager chatManager;

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        context = this;
        vars = new Vars(this);


        //CHECK IF DEVICE NAME IS SET
//        if (vars.prefs.getString(Globals.DEVICE_NAME, null) == null) {
        if (false) {
            log("DEVICE NAME NOT SET");

            vars.alerter.alerterAnySuccessActivity("Error", "Device name not set!", SetDeviceName.class);
//        } else if (vars.prefs.getString(Globals.PARTNER_DEVICE_NAME, null) == null) {
        } else if (false) {
            log("PARTNER DEVICE NAME NOT SET");

            vars.alerter.alerterAnySuccessActivity("Error", "PARTNER Device name not set!", SetPartnerDeviceName.class);
        } else {

            setContentView(R.layout.activity_rftest);

            //INIT SINGLETON
            MySingleton.getInstance(context);

            //START MQ SERVICE
//            startService(new Intent(this, MpockMqService.class));

            /* ȭ11 1111111 1111 */
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

            /* JNI */
            dualCard = DualCardJni.getInstance();

            //nfc

            detectNfc();




            /* UI 1111 */
            findViewByID();

            //  spinner.setPrompt("Select reading interval");

            adspin = ArrayAdapter.createFromResource(this, R.array.spinnerArray, android.R.layout.simple_spinner_item);

            adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //  spinner.setAdapter(adspin);

            version = (TextView) findViewById(R.id.version);
            version.setText("ver1.19");

            //DEVICE TEXT VIEWS
            device_name = (TextView) findViewById(R.id.device_name);
            partner_device_name = (TextView) findViewById(R.id.partner_device_name);

            //BIND DEVICE NAMES
            device_name.setText("DEVICE NAME:" + vars.prefs.getString(Globals.DEVICE_NAME, null));
            partner_device_name.setText("PARTNER DEVICE NAME:" + vars.prefs.getString(Globals.PARTNER_DEVICE_NAME, null));

            //SET NEW TRANS BUTTON
            new_trans_button = (Button) findViewById(R.id.new_trans_button);
            new_trans_button.setOnClickListener(this);

            //SET NEW TRANS BUTTON
                /*new_meal_button = (Button) findViewById(R.id.new_meal_button);
                new_meal_button.setOnClickListener(this);*/

            //SET NEW GATE PASS BUTTON
            new_pass_button = (Button) findViewById(R.id.new_pass_button);
            new_pass_button.setOnClickListener(this);
            new_pass_button.setClickable(true);


            //SET NEW TRANS BUTTON
            change_device_name_button = (Button) findViewById(R.id.change_device_name_button);
            change_device_name_button.setOnClickListener(this);

            //SET NEW TRANS BUTTON
            change_partner_device_name_button = (Button) findViewById(R.id.change_partner_device_name_button);
            change_partner_device_name_button.setOnClickListener(this);

            //CONNECTED BUTTON
            connected = (TextView) findViewById(R.id.connected);
            connected.setText(Globals.DISCONNECTED);

            getCardProgressBar = (ProgressBar) findViewById(R.id.getCardProgressBar);
            getCardProgressBar.setVisibility(View.GONE);

            getPassProgressBar = (ProgressBar) findViewById(R.id.getPassProgressBar);
            getPassProgressBar.setVisibility(View.GONE);

                /*getMealProgressBar = (ProgressBar) findViewById(R.id.getMealProgressBar);
                getMealProgressBar.setVisibility(View.GONE);*/

            int ret = 0;
            if (MainActivity.isPop2)
                ret = dualCard.DE_InitPort(mPort, baud);
            else
                ret = dualCard.DE_InitPortPath(mPort, baud, "/dev/ttyTCC3");

            if (mPort == ret) {
                Log("Port Connect", NORMALFLAG);
            } else {
                Log("Port Error", ERRORFLAG);
            }

            //TEST STUDENT ORM
            //testStudent();

            //  GetStudentsJob gs = new GetStudentsJob();
            // MySingleton.getInstance(context).jobManager.addJobInBackground(gs);

            //LOAD NUM STUDENTS
            numStudents = ((Long) studentorm.count(studentorm.class));
            log("found numStudents:" + numStudents);

            numStudentsTextView.setText(String.valueOf(numStudents));

            //TESTING
            //        List<studentorm> testList = studentorm.listAll(studentorm.class);
            //
            //        if(testList.size()>400){
            //            for(int i=0;i<900;i++){
            //                log("testing:"+testList.get(i).studentid+"::"+testList.get(i).udid+"::"+testList.get(i).lastname);
            //            }
            //        }

            if (netConnReceiver == null) {
                netConnReceiver = new NetworkConnectionIntentReceiver();
                registerReceiver(netConnReceiver, new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
                log("network receiver registerd");
            }

            //SEND UNSENT TRANS
            log("unsent - calling unsent");
            sendUnsentTransactions();

            //LOAD CONSTANTS
            MiscUtilz.loadConstants(vars);
        }

        if(!mDeviceConnected) {
            Intent i = DevicesActivity.newIntent(RFActivity.this, mDeviceConnected);
            startActivityForResult(i, RESULT_CONN_CODE);
        } else {
            connected.setText(Globals.CONNECTED);
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(DevicesActivity.TAG, "Returned from Device Activity");
        Log.d(DevicesActivity.TAG, "resultCode: " + resultCode);
        Log.d(DevicesActivity.TAG, "RESULT_OK: " + Activity.RESULT_OK);
        if (data == null) {
            Log.d(DevicesActivity.TAG, "RESULT_CONN_CODE: " + requestCode);
        }
        Log.d(DevicesActivity.TAG, "data: " + data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == RESULT_CONN_CODE) {
            if (data == null) {
                return;
            }
            if (mDeviceConnected = DevicesActivity.wasDeviceConnected(data)) {
                Toast.makeText(this, "Devices has been successfully connected!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showProgress() {
        //SHOW CONNECTING PROGRESS DIALOG
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Connecting to Note Server...");
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(ConnCreated evt) {
        Thread handler = null;
        WifiP2pInfo info = evt.getDeviceInfo();
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */
        Log.d(DevicesActivity.TAG, "We have received a connection created event!");
        if (info.isGroupOwner) {
            Log.d(DevicesActivity.TAG, "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler(
                        (this).getHandler());
                handler.start();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(
                    (this).getHandler(),
                    info.groupOwnerAddress);
            handler.start();
        }
                connected.setText(Globals.CONNECTED);
                if (progressBar != null) {
                    progressBar.dismiss();
                }

        Log.d(DevicesActivity.TAG, "Socket Streams have been established");
////        runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                connected.setText(Globals.CONNECTED);
////                if (progressBar != null) {
////                    progressBar.dismiss();
////                }
////            }
////        });
    }

//    Note: We want to replace this with WiFiP2P since slow Internet Connections
//    Can make Mqtt conversations slow. Bad for business
//
//    CONNECTED EVENT BUS
//
//    @Subscribe
//     */
//    public void onEvent(MqConnected conn) {
//        log("++++++++++++: MqConnected post received");
//
//        //UPDATE UI
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                //CHANGE TEXT
//                connected.setText(Globals.CONNECTED);
//
//                //REMOVE BUTTON
//                // connectButton.setVisibility(View.INVISIBLE);
//
//                //dismiss connecting
//                if (progressBar != null) {
//                    progressBar.dismiss();
//                }
//
//
//            }
//        });
//    }
//
//    //DISCONNECTED EVENTBUS
//    @Subscribe
//    public void onEvent(MqDisonnected disConn) {
//        log("++++++++++++: MqConnected post received");
//
//        //UPDATE UI
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                connected.setText(Globals.DISCONNECTED);
//
//                //SHOW CONNECT BUTTON
//                //  connectButton.setVisibility(View.VISIBLE);
//
//                //SHOW PROGRESS DIALOG
//                showProgress();
//            }
//        });
//
//
//    }

    @Subscribe
    public void onEvent(StudentCharge studentCharge ) {
        log("chat on eventbus studentCharge post receieved");
        log("student:"+studentCharge.lastname);
        log("transid:"+studentCharge.transId);

        if(chatManager != null) {
            chatManager.write((studentCharge.toString()
                    + "---" + studentCharge.getClass().getSimpleName()).getBytes());
        }
    }

    @Subscribe
    public void onEvent(StudentChargeFinished studentChargeFinished ) {
        log("studentChargeFinished post receieved");
        log("studentChargeFinished result:"+studentChargeFinished.studentCharge.transResult);
        log("transid:"+studentChargeFinished.studentCharge.transId);

        //GET STUDENT STRING
        // StudentCharge sorm = new StudentCharge(StudentCharge studentCharge);
        //  log("got json string of Student:"+sorm.toString());

        if(chatManager != null) {
            chatManager.write((studentChargeFinished.toString()
                    + "---" + studentChargeFinished.getClass().getSimpleName()).getBytes());
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }

        return false;
    }

    public void startSearch(View v) {
        log("search+++++++");
        startActivity(new Intent(this, SearchStudent.class));
    }

    public void testStudent() {
        studentorm student = new studentorm();
        student.firstnmae = "ivan";
        student.save();
    }

    //TESTING
    public void testStudetnt() {
        log("+++++++ teststuddent");
        checkForUdid("4D008E4A9876");
    }

    private void CheckGatePass() {
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RF Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.duali.itouchpop2_test/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RF Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.duali.itouchpop2_test/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void setChatManager(ChatManager connManager) {
        this.chatManager = connManager;
    }

    // There is a hack on event dispatching that needs to be refactored
    // We are trying to figure out the class to give to fromJson
    // right now this is appended to the received Json string
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case DevicesActivity.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(DevicesActivity.TAG, readMessage);
                String [] parts = readMessage.split("---");
                // receive json object
                Gson fromGson = new Gson();
                Log.d(DevicesActivity.TAG, "Message: " + parts[0] + "Class: " + parts[1]);
                Log.d(DevicesActivity.TAG, "We are going to send and event");
                Log.d(DevicesActivity.TAG, "If changes have been made we should see this message");
                if(parts[1].equals("StudentChargeReply")) {
                    EventBus.getDefault()
                            .post(fromGson.fromJson(parts[0], StudentChargeReply.class));
                } else {
                    Log.d(DevicesActivity.TAG, "Received unidentified event message");
                }
                Log.d(DevicesActivity.TAG, "Posted SCR Event on the Bus");
                break;

            case DevicesActivity.MY_HANDLE:
                Object obj = msg.obj;
                this.setChatManager((ChatManager) obj);

        }
        return true;
    }

    class rfFindCard extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(TAG, "onPreExecute()");
            getCardProgressBar.setVisibility(View.VISIBLE);
            bStop = false;
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
                        } else if (s.equals("G")) {
                            opt = (byte) 0x41;
                        } else if (s.equals("M")) {
                            opt = (byte) 0x41;
                        } else if (s.equals("B")) {
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
                            if (s.equals("A")) {
                                checkForUdid(Hex.bytesToHexString(data));
                            } else if (s.equals("G")) {
                                checkForUdidG(Hex.bytesToHexString(data));
                            } else if (s.equals("M")) {
                                checkForUdidM(Hex.bytesToHexString(data));
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

    //CHECK FOR UDID
    public void checkForUdid(final String udid) {
        log("++++++++ checkForUdid");
        log("udid:" + udid);

        //GET STUDENT
        studentorm student = StudentSugarUtils.getStudentByUdid(udid);

        if (student != null) {
            log("student found udid:" + student.udid);

            //SEND TO SEND CHARGE
            Intent i = new Intent(this, Student_charge.class);
            i.putExtra(Globals.STUDENT_ID, String.valueOf(student.getId()));
            log("sending student with extra id:" + student.getId());
            startActivity(i);


        } else if (student == null) {

            //  vars.alerter.alerterAny("Error","Unable to Find Student by that Udid:" + udid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    vars.alerter.alerterAny("Error", "Unable to Find Student by that Udid:" + udid);

//                    mMaterialDialog = new MaterialDialog(context)
//                            .setTitle("Student Card Details")
//                            .setMessage("Unable to Find Student by that Udid:" + udid)
//                            .setPositiveButton("OK", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    mMaterialDialog.dismiss();
//
//                                }
//                            });
////                                .setNegativeButton("CANCEL", new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        mMaterialDialog.dismiss();
////
////                                    }
////                                });
//
//                    mMaterialDialog.show();
                    //  }
                }
            });
        }

//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (student != null) {
//					//	final	MaterialDialog mMaterialDialog = null;
//					mMaterialDialog = new MaterialDialog(context)
//							.setTitle("Student Card Details")
//							.setMessage("Student:" + student.firstnmae)
//							.setPositiveButton("OK", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							})
//							.setNegativeButton("CANCEL", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							});
//
//					mMaterialDialog.show();
//				} else if (student == null) {
//					mMaterialDialog = new MaterialDialog(context)
//							.setTitle("Student Card Details")
//							.setMessage("Unable to Find Student by that Udid:" + udid)
//							.setPositiveButton("OK", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							})
//							.setNegativeButton("CANCEL", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							});
//
//					mMaterialDialog.show();
//				}
//			}
//		});


    }

    public void checkForUdidG(final String udid) {
        log("++++++++ checkForUdidG");
        log("udid:" + udid);

        //GET STUDENT
        studentorm student = StudentSugarUtils.getStudentByUdid(udid);

        if (student != null) {
            log("student found udid:" + student.udid);

            //SEND TO SEND CHARGE
            Intent i = new Intent(this, Student_gate.class);
            i.putExtra(Globals.STUDENT_ID, String.valueOf(student.getId()));
            log("sending student with extra id:" + student.getId());
            startActivity(i);


        } else if (student == null) {

            //  vars.alerter.alerterAny("Error","Unable to Find Student by that Udid:" + udid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    vars.alerter.alerterAny("Error", "Unable to Find Student by that Udid:" + udid);

//                    mMaterialDialog = new MaterialDialog(context)
//                            .setTitle("Student Card Details")
//                            .setMessage("Unable to Find Student by that Udid:" + udid)
//                            .setPositiveButton("OK", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    mMaterialDialog.dismiss();
//
//                                }
//                            });
////                                .setNegativeButton("CANCEL", new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        mMaterialDialog.dismiss();
////
////                                    }
////                                });
//
//                    mMaterialDialog.show();
                    //  }
                }
            });
        }

//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (student != null) {
//					//	final	MaterialDialog mMaterialDialog = null;
//					mMaterialDialog = new MaterialDialog(context)
//							.setTitle("Student Card Details")
//							.setMessage("Student:" + student.firstnmae)
//							.setPositiveButton("OK", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							})
//							.setNegativeButton("CANCEL", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							});
//
//					mMaterialDialog.show();
//				} else if (student == null) {
//					mMaterialDialog = new MaterialDialog(context)
//							.setTitle("Student Card Details")
//							.setMessage("Unable to Find Student by that Udid:" + udid)
//							.setPositiveButton("OK", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							})
//							.setNegativeButton("CANCEL", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							});
//
//					mMaterialDialog.show();
//				}
//			}
//		});


    }

    public void checkForUdidM(final String udid) {
        log("++++++++ checkForUdidM");
        log("udid:" + udid);

        //GET STUDENT
        studentorm student = StudentSugarUtils.getStudentByUdid(udid);

        if (student != null) {
            log("student found udid:" + student.udid);

            //SEND TO SEND CHARGE
            Intent i = new Intent(this, Student_meal.class);
            i.putExtra(Globals.STUDENT_ID, String.valueOf(student.getId()));
            i.putExtra(Globals.STUDENT_CLASS, String.valueOf(student.studentclass));
            //i.putExtra(Globals.MEAL_DATE, String.valueOf(new Date()));
            log("sending student with extra id:" + student.getId());
            startActivity(i);
        } else if (student == null) {

            //  vars.alerter.alerterAny("Error","Unable to Find Student by that Udid:" + udid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    vars.alerter.alerterAny("Error", "Unable to Find Student by that Udid:" + udid);

//                    mMaterialDialog = new MaterialDialog(context)
//                            .setTitle("Student Card Details")
//                            .setMessage("Unable to Find Student by that Udid:" + udid)
//                            .setPositiveButton("OK", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    mMaterialDialog.dismiss();
//
//                                }
//                            });
////                                .setNegativeButton("CANCEL", new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        mMaterialDialog.dismiss();
////
////                                    }
////                                });
//
//                    mMaterialDialog.show();
                    //  }
                }
            });
        }

//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (student != null) {
//					//	final	MaterialDialog mMaterialDialog = null;
//					mMaterialDialog = new MaterialDialog(context)
//							.setTitle("Student Card Details")
//							.setMessage("Student:" + student.firstnmae)
//							.setPositiveButton("OK", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							})
//							.setNegativeButton("CANCEL", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							});
//
//					mMaterialDialog.show();
//				} else if (student == null) {
//					mMaterialDialog = new MaterialDialog(context)
//							.setTitle("Student Card Details")
//							.setMessage("Unable to Find Student by that Udid:" + udid)
//							.setPositiveButton("OK", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							})
//							.setNegativeButton("CANCEL", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									mMaterialDialog.dismiss();
//
//								}
//							});
//
//					mMaterialDialog.show();
//				}
//			}
//		});


    }

    @Override
    protected void onResume() {
        //swThread();//isaac writing nfc
        super.onResume();

        log("RFActivity ON RESUME");

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
        //SET CONNECTED TEXT
        //  connected.setText(MySingleton.getInstance(vars.context).connectionStatus);
        if (MySingleton.getInstance(vars.context).connectionStatus.equalsIgnoreCase(Globals.CONNECTED)) {
            log("singleton connected is CONNECTED SO REMOVING PROGRESS BAR");
            progressBar.dismiss();
        } else {
            log("not connected progress bar remains");
        }
    }

    private void swThread() {
        if (sw == null) {
            timerStop = false;
            sw = new SwThreadJob();
            sw.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
        }

    }

    @Subscribe
    public void onEvent(SyncDone sd) {
        log("sync done onEvent RECEIVED");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //LOAD NUM STUDENTS
                numStudents = ((Long) studentorm.count(studentorm.class));
                log("found numStudents:" + numStudents);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        numStudentsTextView.setText(String.valueOf(numStudents));
                        vars.alerter.alerterAny("Notice", "Student Sync Complete");
                        //  }
                    }
                });
            }
        });
    }

    @Subscribe
    public void onEvent(final SyncPinDone spd) {
        log("SyncPinDone done onEvent RECEIVED");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //LOAD NUM STUDENTS
                numStudents = ((Long) studentorm.count(studentorm.class));
                log("found numStudents:" + numStudents);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        vars.alerter.alerterAny("Notice", "Pin Sync Done," + spd.numPinUpdated + " pins updated");
                        sync_pin_button.setText("SYNC PINS");
                        //  }
                    }
                });
            }
        });

    }

    @Subscribe
    public void onEvent(final SyncMealDone spd) {
        log("SyncMealDone done onEvent RECEIVED");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //LOAD NUM MEALS
                numMeals = ((Long) mealsorm.count(mealsorm.class));
                log("found mealss:" + numMeals);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        vars.alerter.alerterAny("Notice", "Meal Plan Sync Done," + spd.numMealUpdated + " meals updated");
                        sync_meal_button.setText("SYNC MEALS");
                        //  }
                    }
                });
            }
        });

    }


    @Subscribe
    public void onEvent(SyncStarted sc) {
        log("sync START onEvent RECEIVED");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        vars.alerter.alerterAny("Notice", "Student Sync STARTED");
                        //  }
                    }
                });
            }
        });

    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        /*bStop = true;
        if (testRf != null) {
            testRf.cancel(true);
            testRf = null;
        }

        if (testC != null) {
            testC.cancel(true);
            testC = null;
        }
        EventBus.getDefault().unregister(this);*///isaac writing nfc

        nfcAdapter.disableForegroundDispatch(this);
//        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Toast.makeText(context, "Card Detected", Toast.LENGTH_SHORT).show();

        /*if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            NfcRead1(intent, "A");
        }*/
        EventBus.getDefault().unregister(this);
    }

    public void NfcRead1(Intent intent, String x) {
        log("ZZZZZZZZZZZZZZZZZZZZZZZZZ++++++++++++++++" + intent.getAction());
        /*if (intent.getAction() == null) {
            detectNfc();
        } else {*/
            if (intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
                log("UUUUUUUUUUTECHTECHUUIIIIIIIIIIIDDDDDDDDD" + ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                if (x.equals("A")) {
                    checkForUdid(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                } else if (x.equals("G")) {
                    checkForUdidG(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));

                } else if (x.equals("M")) {
                    checkForUdidM(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                }
            } else if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                log("UUUUUUUUUUUUTAGTAGIIIIIIIIIIIDDDDDDDDD" + ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                if (x.equals("A")) {
                    checkForUdid(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                } else if (x.equals("G")) {
                    checkForUdidG(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));

                } else if (x.equals("M")) {
                    checkForUdidM(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                }

            }

        }
   // }

    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0xff;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return "4D00"+out;
    }


    public void NfcRead(Intent intent) {

        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        log("NNNNNNNNNNNREAD"+action.toString());

        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;

            try {       //  5.1) Connect to card
                try {
                    mfc.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean auth = false;
                String cardData = null;
                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                for (int j = 0; j < secCount; j++) {
                    // 6.1) authenticate the sector
                    auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
                    if (auth) {
                        // 6.2) In each sector - get the block count
                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;
                        for (int i = 0; i < bCount; i++) {
                            bIndex = mfc.sectorToBlock(j);
                            // 6.3) Read the block
                            data = mfc.readBlock(bIndex);
                            Log("[" + count + "] " + Hex.bytesToHexString(data), RECEIVEFLAG);
                            log("hope hope hope hope:" + Hex.bytesToHexString(data));
                            //Toast.makeText(context, "SHORT:" + StudentUtils.shortUdid(Hex.bytesToHexString(data)),Toast.LENGTH_LONG).show();

                            /*log("before loopFindCard:" + loopFindCard);
                            loopFindCard = false;*/

                            //CHECK FOR UDID
                            log("before checking for hex");
                            //if (s.equals("A")) {
                                checkForUdid(Hex.bytesToHexString(data));
                            //} else if (s.equals("G")) {
                            //    checkForUdidG(Hex.bytesToHexString(data));
                           // } else if (s.equals("M")) {
                            //    checkForUdidM(Hex.bytesToHexString(data));
                          //  }

                            log("after checking for hex");


                            log("after loopFindCard:" + loopFindCard);
                            // 7) Convert the data into a string from Hex format.
                            //Log.i(TAG, getHexString(data, data.length));
                            bIndex++;
                        }
                    } else { // Authentication failed - Handle it
                        log("-----------authentication failed");

                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
                log("Error connecting to card" + e.getMessage());
                //showAlert(3);
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tbtn_rfa:    //tbtn_rfa
                //testing ivan
                readTypeA();

                // checkForUdid("4D009E958F76");
                //  checkForUdid("9e ee 99 76");
                // checkForUdid("4D00FEEF9A76");
                // checkForUdid("4D00FEEF9A76");
                //  checkForUdid("4D009e0f9276");
                //    checkForUdid("4D00FE089076");


                // 4D00FEEF9A76

                //  4D009E958F76
                //  4D009E958F76

                break;
            case R.id.new_trans_button:    //tbtn_rfa
                //detectNfc();
                //testing ivan
                /*log("R.id.new_trans_button");
                readTypeA();isaaac writing nfc*/
                log("R.id.new_trans_button");
                log("nfc------------"+nfcAdapter.toString());
                Intent intent = getIntent();
                if (intent.getAction() == null) {
                    vars.alerter.alerterAny("Warning", "Remove Card, Press OK, Tap Again");
                    break;
                }
                log("Intent"+getIntent().toString());
                NfcRead1(intent, "A");

                // checkForUdid("4D009E958F76");
                //  checkForUdid("9e ee 99 76");
                // checkForUdid("4D00FEEF9A76");
                // checkForUdid("4D00FEEF9A76");
                //  checkForUdid("4D009e0f9276");
                //    checkForUdid("4D00FE089076");


                // 4D00FEEF9A76

                //  4D009E958F76
                //  4D009E958F76

                break;

            case R.id.sync_students_button:    //sync_students_button
                //readTypeB();

                startSyncStudents();

                break;
            case R.id.canteen_button:    //tbtn_rfc
                //readTypeC();

                //IVAN PUT THIS BACK TO START CANTEEN
                startCanteen();

                // startActivity(new Intent(this, SearchStudent.class));

                break;
            case R.id.gate_pass_button:    //Gate Pass button

                startGatePass();


                break;
            case R.id.new_pass_button:    //Gate Pass button
                //detectNfc();
                log("R.id.new_pass_button");
                log("nfc------------"+nfcAdapter.toString());
                Intent intentP = getIntent();
                if (intentP.getAction() == null) {
                   // log("++++++++++++"+nfcAdapter.toString());
                   // log("++++++++++++"+pendingIntent.toString());
                    vars.alerter.alerterAny("Warning", "Remove Card, Press OK, Tap Again");
                    break;
                    //intentP = getIntent();
                    //intentP.setAction(NfcAdapter.ACTION_TECH_DISCOVERED);

                }
                log("Intent"+getIntent().toString());
                NfcRead1(intentP, "G");

                break;

            /*case R.id.new_meal_button:    //meal button

                readTypeM();

                break;*/

            case R.id.reset_students_button: //reset_students_button
                clearLog();

                startResetStudents();

                //CLEAR STUDENT AND SYNC.

                break;
            case R.id.btn_exit:    //btn_exit
                exitRF();
                break;

            case R.id.sync_pin_button:
                startSyncPin();

                break;
           /* case R.id.sync_meal_button:
                startSyncMeals();
                //readTypeM();

                break;*/
            case R.id.btn_p2p:
                //sendP2P();
                startActivity(new Intent(this, SearchStudent.class));
                break;

            case R.id.search_button:
                startActivity(new Intent(this, SearchStudent.class));
                break;

            case R.id.change_partner_device_name_button:
                changePartnerDeviceName();
                break;

            case R.id.change_device_name_button:
                changeDeviceName();
                break;
            default:
                break;
        }
    }


    //NETWORK CHANGE RECIEVER
    public class NetworkConnectionIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            log("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnn  online  ---------NetworkConnectionIntentReceiver");
            // we protect against the phone switching off while we're doing this
            // by requesting a wake lock - we request the minimum possible wake
            // lock - just enough to keep the CPU running until we've finished
//            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//            PowerManager.WakeLock wl = pm
//                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");
//            wl.acquire();
//
//            superReconnect();
//            // }
//
//            // we're finished - if the phone is switched off, it's okay for the
//            // CPU
//            // to sleep now
//            wl.release();
            if (isOnline()) {
                log("unsent WE ARE ONLINE... we can sync now");
                vars.alerter.alerterAny("NOTICE", "System is OnLINE");
                sendUnsentTransactions();
            } else {
                //  vars.alerter.alerterAny("NOTICE","System is OFFLINE");
            }
        }
    }

    //CHANGE DEVICES
    public void changeDeviceName() {
        log("++++++++++++++: changeDeviceName");

        LayoutInflater li = LayoutInflater.from(vars.context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                vars.context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
                            startActivity(new Intent(vars.context, SetDeviceName.class));
                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//        final EditText contentView = new EditText(this);
//        contentView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mMaterialDialog = new MaterialDialog(context)
//                .setTitle("Canteen Pin")
//                .setView(contentView)
//                .setMessage("Please enter your Pin:")
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String pin = contentView.getText().toString();
//                        log("edit text value:" + pin);
//                        //  if (pin.equalsIgnoreCase("1234")) {
//                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
//                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
//                        } else {
//                            mMaterialDialog.dismiss();
//                        }
//                    }
//                })
//                .setNegativeButton("CANCEL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//        mMaterialDialog.show();

    }


    public void changePartnerDeviceName() {
        log("++++++++++++++: changePartnerDeviceName");

        LayoutInflater li = LayoutInflater.from(vars.context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                vars.context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
                            startActivity(new Intent(vars.context, SetPartnerDeviceName.class));
                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//        final EditText contentView = new EditText(this);
//        contentView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mMaterialDialog = new MaterialDialog(context)
//                .setTitle("Canteen Pin")
//                .setView(contentView)
//                .setMessage("Please enter your Pin:")
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String pin = contentView.getText().toString();
//                        log("edit text value:" + pin);
//                        //  if (pin.equalsIgnoreCase("1234")) {
//                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
//                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
//                        } else {
//                            mMaterialDialog.dismiss();
//                        }
//                    }
//                })
//                .setNegativeButton("CANCEL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//        mMaterialDialog.show();

    }


    //GET ALL NON SENT TRANSACTIONS AND SENT
    public void sendUnsentTransactions() {
        log("+++++++++++++: sendUnsent");

        if (isOnline()) {
            //GET UNSENT
            //    List<studentorm> studentlist = studentorm.find(studentorm.class, "studentid = ?", String.valueOf(studentId));
            List<transactionsorm> unsent = transactionsorm.find(transactionsorm.class, "senttoserver = ?", "no");
            log("unsent: found unsent trans:" + unsent.size());

            //SEND THEM
            for (transactionsorm t : unsent) {
                log("unsent sending trans:" + t.deviceTransId);
                CanteenTrans ct = StudentUtils.createCanteenTransFromTorm(vars, t);
                log("unsent created ct:" + ct.getAmount());
                //ADD TO JOB QUE
                PostCanteenTransJob pc = new PostCanteenTransJob(vars, ct, t.getId());
                MySingleton.getInstance(vars.context).jobManager.addJobInBackground(pc);
            }
        } else {
            // vars.alerter("Notice De")
            log("unsent OFFLINE unable to send trans");
        }


    }

    //RESET STUDENTS DIALOG
    public void resetStudents() {

        //SHOW DIALOG TO RESET STUDENT
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);

        headerTxt.setText("ATTENTION!");
        messageTxt.setText("Are you sure you want to resync all students?");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //clear students.
                        studentorm.deleteAll(studentorm.class);

                        //CLEAR PINS
                        vars.edit.putInt(Globals.PIN_CHANGE_LOG, 0);
                        vars.edit.apply();
                        log("cleared pin change log");

                        //START GET STUDENTS JOB
                        GetStudentsJob getStudentsJob = new GetStudentsJob(vars);
                        MySingleton.getInstance(vars.context).jobManager.addJobInBackground(getStudentsJob);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void startCanteen() {
        log("++++++++++++++: starCanteen");

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//        final EditText contentView = new EditText(this);
//        contentView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mMaterialDialog = new MaterialDialog(context)
//                .setTitle("Canteen Pin")
//                .setView(contentView)
//                .setMessage("Please enter your Pin:")
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String pin = contentView.getText().toString();
//                        log("edit text value:" + pin);
//                        //  if (pin.equalsIgnoreCase("1234")) {
//                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
//                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
//                        } else {
//                            mMaterialDialog.dismiss();
//                        }
//                    }
//                })
//                .setNegativeButton("CANCEL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//        mMaterialDialog.show();

    }

    public void startGatePass() {
        log("++++++++++++++: starGatePass");

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
                            //make a toast
                           // new_pass_button.setClickable(true);
                            Toast.makeText(context, "Tap START GATE PASS", Toast.LENGTH_SHORT).show();

                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            Toast.makeText(context, "WRONG PIN", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//        final EditText contentView = new EditText(this);
//        contentView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mMaterialDialog = new MaterialDialog(context)
//                .setTitle("Canteen Pin")
//                .setView(contentView)
//                .setMessage("Please enter your Pin:")
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String pin = contentView.getText().toString();
//                        log("edit text value:" + pin);
//                        //  if (pin.equalsIgnoreCase("1234")) {
//                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
//                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
//                        } else {
//                            mMaterialDialog.dismiss();
//                        }
//                    }
//                })
//                .setNegativeButton("CANCEL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//        mMaterialDialog.show();

    }


    public void startSyncPin() {
        log("++++++++++++++: startSyncPin");

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("ppppppppppppppppppppppppppp" + Globals.ADMIN_PIN);
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        log("ppppppppppppppppppppppppppp" + Globals.ADMIN_PIN);
                        if (pin.equalsIgnoreCase(String.valueOf(Globals.ADMIN_PIN))) {
                            // startActivity(new Intent(vars.context, CanteenViewTrans.class));
                            // readSAM();
                            GetStudentPinJob gsj = new GetStudentPinJob(vars);
                            MySingleton.getInstance(vars.context).jobManager.addJobInBackground(gsj);

                            //CHANGE BUTTON TEXT
                            sync_pin_button.setText("GETTING PINS...");
                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//        final EditText contentView = new EditText(this);
//        contentView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mMaterialDialog = new MaterialDialog(context)
//                .setTitle("Canteen Pin")
//                .setView(contentView)
//                .setMessage("Please enter your Pin:")
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String pin = contentView.getText().toString();
//                        log("edit text value:" + pin);
//                        //  if (pin.equalsIgnoreCase("1234")) {
//                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
//                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
//                        } else {
//                            mMaterialDialog.dismiss();
//                        }
//                    }
//                })
//                .setNegativeButton("CANCEL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//        mMaterialDialog.show();

    }

    public void startSyncMeals() {
        log("++++++++++++++: startSyncMeals");

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("ppppppppppppppppppppppppppp" + Globals.ADMIN_PIN);
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        log("ppppppppppppppppppppppppppp" + Globals.ADMIN_PIN);
                        if (pin.equalsIgnoreCase(String.valueOf(Globals.ADMIN_PIN))) {
                            // startActivity(new Intent(vars.context, CanteenViewTrans.class));
                            // readSAM();
                            GetSchoolMealJob gsj = new GetSchoolMealJob(vars);
                            MySingleton.getInstance(vars.context).jobManager.addJobInBackground(gsj);

                            //CHANGE BUTTON TEXT
                            sync_meal_button.setText("GETTING MEAL PLANS...");
                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//        final EditText contentView = new EditText(this);
//        contentView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mMaterialDialog = new MaterialDialog(context)
//                .setTitle("Canteen Pin")
//                .setView(contentView)
//                .setMessage("Please enter your Pin:")
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String pin = contentView.getText().toString();
//                        log("edit text value:" + pin);
//                        //  if (pin.equalsIgnoreCase("1234")) {
//                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
//                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
//                        } else {
//                            mMaterialDialog.dismiss();
//                        }
//                    }
//                })
//                .setNegativeButton("CANCEL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//        mMaterialDialog.show();

    }
    public void detectNfc() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this,
                    "NFC NOT supported on this devices!",
                    Toast.LENGTH_LONG).show();
            finish();
        }else if(!nfcAdapter.isEnabled()){
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            tech.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            tag.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

       /* // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);*/

        mFilters = new IntentFilter[]{tech,tag};

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][]{new String[]{ NfcA.class.getName(),
                    //NfcB.class.getName(), NfcF.class.getName(),
                    //NfcV.class.getName(), IsoDep.class.getName(),
                MifareClassic.class.getName(),
                    /*MifareUltralight.class.getName(),*/ Ndef.class.getName()}};

    }

    public void startResetStudents() {
        log("++++++++++++++: startSyncPin");

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        if (pin.equalsIgnoreCase(String.valueOf(Globals.ADMIN_PIN))) {

                            //CHECK FOR UNSENT TRANSACTIONS
                            List<transactionsorm> unsent = transactionsorm.find(transactionsorm.class, "senttoserver = ?", "no");
                            log("unsent: found unsent trans:" + unsent.size());

                            if (!unsent.isEmpty()) {
                                vars.alerter.alerterAny("Error", "There are still " + unsent.size() + " transaction, please send these B4 reseting students");
                                sendUnsentTransactions();
                            } else {
                                log("no unsent transactions");
                                resetStudents();
                            }
                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


//        final EditText contentView = new EditText(this);
//        contentView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        mMaterialDialog = new MaterialDialog(context)
//                .setTitle("Canteen Pin")
//                .setView(contentView)
//                .setMessage("Please enter your Pin:")
//                .setPositiveButton("OK", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String pin = contentView.getText().toString();
//                        log("edit text value:" + pin);
//                        //  if (pin.equalsIgnoreCase("1234")) {
//                        if (pin.equalsIgnoreCase(String.valueOf(vars.setPin))) {
//                            startActivity(new Intent(vars.context, CanteenViewTrans.class));
//                        } else {
//                            mMaterialDialog.dismiss();
//                        }
//                    }
//                })
//                .setNegativeButton("CANCEL", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//        mMaterialDialog.show();

    }

    public void startSyncStudents() {
        log("++++++++++++++: startSyncPin");

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.canteen_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);
        final EditText pinNumber = (EditText) promptsView.findViewById(R.id.canteen_pin);

        headerTxt.setText("Enter Pin");
        messageTxt.setText("Enter Pin");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String pin = pinNumber.getText().toString();
                        log("edit text value:" + pin);
                        //  if (pin.equalsIgnoreCase("1234")) {
                        if (pin.equalsIgnoreCase(String.valueOf(Globals.ADMIN_PIN))) {

                            log("sync students pressed//////////////////////////////////////////////////////////////////");
                            GetStudentsJob gs = new GetStudentsJob(vars);
                            MySingleton.getInstance(context).jobManager.addJobInBackground(gs);
                        } else {
                            //mMaterialDialog.dismiss();
                            // dialog.cancel();
                            dialog.cancel();
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        log("sync created=============================================");


    }

    private void readTypeA() {

        //START TASK TO READ FOR CARD..
        if (testRf == null) {
            testRf = new rfFindCard();
            testRf.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "A");
            toggleFlag = true;
        }


        if (!tbtnrfa.isChecked()) {
            sync_students_button.setEnabled(true);
            canteen_button.setEnabled(true);
            tbtnexit.setEnabled(true);
            sync_pin_button.setEnabled(true);
            search_button.setEnabled(true);
            gate_pass_button.setEnabled(true);

            loopFindCard = false;
            bStop = true;
            //    testRf.cancel(true);
            testRf = null;
            //dualCard.DE_ClosePort(mPort);
            toggleFlag = false;
            //Log("Port Disconnect", NORMALFLAG);
        } else {
            if (toggleFlag) {
                Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
                tbtnrfa.setChecked(false);
                return;
            }

            sync_students_button.setEnabled(false);
            canteen_button.setEnabled(false);
            tbtnexit.setEnabled(false);
            sync_pin_button.setEnabled(false);
            search_button.setEnabled(false);
            gate_pass_button.setEnabled(true);
            //			int ret = dualCard.DE_InitPort(mPort, baud);
            //			if(mPort == ret) {
            //				Log("Port Connect", NORMALFLAG);
            //			}
            //			else {
            //				Log("Port Error", ERRORFLAG);
            //				tbtnrfa.setChecked(false);
            //				toggleFlag = false;
            //				return;
            //			}
            //
//            if (testRf == null) {
//                testRf = new rfFindCard();
//                testRf.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "A");
//                toggleFlag = true;
//            }
        }
    }

    private void readTypeG() {

        //START TASK TO READ FOR CARD..
        if (testRf == null) {
            testRf = new rfFindCard();
            testRf.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "G");
            toggleFlag = true;
        }


        if (!tbtnrfa.isChecked()) {
            sync_students_button.setEnabled(true);
            canteen_button.setEnabled(true);
            tbtnexit.setEnabled(true);
            sync_pin_button.setEnabled(true);
            search_button.setEnabled(true);
            gate_pass_button.setEnabled(true);

            loopFindCard = false;
            bStop = true;
            //    testRf.cancel(true);
            testRf = null;
            //dualCard.DE_ClosePort(mPort);
            toggleFlag = false;
            //Log("Port Disconnect", NORMALFLAG);
        } else {
            if (toggleFlag) {
                Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
                tbtnrfa.setChecked(false);
                return;
            }

            sync_students_button.setEnabled(false);
            canteen_button.setEnabled(false);
            tbtnexit.setEnabled(false);
            sync_pin_button.setEnabled(false);
            search_button.setEnabled(false);
            gate_pass_button.setEnabled(true);
            //			int ret = dualCard.DE_InitPort(mPort, baud);
            //			if(mPort == ret) {
            //				Log("Port Connect", NORMALFLAG);
            //			}
            //			else {
            //				Log("Port Error", ERRORFLAG);
            //				tbtnrfa.setChecked(false);
            //				toggleFlag = false;
            //				return;
            //			}
            //
//            if (testRf == null) {
//                testRf = new rfFindCard();
//                testRf.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "A");
//                toggleFlag = true;
//            }
        }
    }

    private void readTypeM() {

        //START TASK TO READ FOR CARD..
        if (testRf == null) {
            testRf = new rfFindCard();
            testRf.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "M");
            toggleFlag = true;
        }


        if (!tbtnrfa.isChecked()) {
            sync_students_button.setEnabled(true);
            canteen_button.setEnabled(true);
            tbtnexit.setEnabled(true);
            sync_pin_button.setEnabled(true);
            //sync_meal_button.setEnabled(true);
            search_button.setEnabled(true);
            gate_pass_button.setEnabled(true);

            loopFindCard = false;
            bStop = true;
            //    testRf.cancel(true);
            testRf = null;
            //dualCard.DE_ClosePort(mPort);
            toggleFlag = false;
            //Log("Port Disconnect", NORMALFLAG);
        } else {
            if (toggleFlag) {
                Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
                tbtnrfa.setChecked(false);
                return;
            }

            sync_students_button.setEnabled(false);
            canteen_button.setEnabled(false);
            tbtnexit.setEnabled(false);
            sync_pin_button.setEnabled(false);
            search_button.setEnabled(false);
            //sync_meal_button.setEnabled(false);
            gate_pass_button.setEnabled(false);

        }
    }

    //CHINESE SHIT REMOVED

//    private void readSAM() {
//
//        DualCardResponse res = dualCard.DE_IC_PowerOn(mPort, (byte) slotNo);
//        Log.i(TAG, "1res " + res.getResponseCode() + " data: " + Hex.bytesToHexString(res.getResponseData()));
//        if (res.getResponseCode() == ResponseCode.DE_OK) {
//            Log("SAM Power ON ", SENDFLAG);
//            byte[] data = res.getResponseData();
//            //UiUpdater.updateLog("SAM : " + Hex.bytesToHexString(data), Globals.RECEIVEFLAG);
//            Log("SAM : " + Hex.bytesToHexString(data), RECEIVEFLAG);
//            byte[] getChallange = new byte[]{(byte) 0x00, (byte) 0x84, (byte) 0x00, (byte) 0x00, (byte) 0x10};
//
//            res = dualCard.DE_IC_Case4(mPort, (byte) slotNo, getChallange.length, getChallange);
//            Log("SAM Get Challenge", SENDFLAG);
//            if (res.getResponseCode() == ResponseCode.DE_OK) {
//                data = res.getResponseData();
//                Log(Hex.bytesToHexString(data), RECEIVEFLAG);
//
//                res = dualCard.DE_IC_PowerOff(mPort, (byte) slotNo);
//                Log.i(TAG, "2res " + res.getResponseCode() + " data: " + Hex.bytesToHexString(res.getResponseData()));
//                if (res.getResponseCode() == ResponseCode.DE_OK) {
//                    changeImg(imgSAM, SUCCESSFLAG);
//                    Log("SAM Power OFF", Globals.SENDFLAG);
//                } else {
//                    changeImg(imgSAM, ERRORFLAG);
//                    Log("SAM Power OFF ERROR", Globals.ERRORFLAG);
//                }
//            } else {
//                changeImg(imgSAM, ERRORFLAG);
//
//                Log("SAM Get Challenge ERROR", ERRORFLAG);
//                res = dualCard.DE_IC_PowerOff(mPort, (byte) slotNo);
//                Log.i(TAG, "2res " + res.getResponseCode() + " data: " + Hex.bytesToHexString(res.getResponseData()));
//                if (res.getResponseCode() == ResponseCode.DE_OK) {
//                    Log("SAM Power OFF", Globals.SENDFLAG);
//                } else {
//                    Log("SAM Power OFF ERROR", Globals.ERRORFLAG);
//                }
//            }
//
//        } else {
//            changeImg(imgSAM, ERRORFLAG);
//            Log("SAM Power ON ERROR", ERRORFLAG);
//        }
//    }


    //CHINISE SHIT REMOVE
//    private void sendP2P() {
//
//        if (!search_button.isChecked()) {
//            sync_students_button.setEnabled(true);
//            canteen_button.setEnabled(true);
//            tbtnexit.setEnabled(true);
//            sync_pin_button.setEnabled(true);
//            tbtnrfa.setEnabled(true);
//
//            toggleFlag = false;
//            if (mReceiveThread != null) {
//                mReceiveThread.stopping();
//                mReceiveThread.interrupt();
//                mReceiveThread = null;
//            }
//        } else {
//            if (toggleFlag) {
//                Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //"11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
//                search_button.setChecked(false);
//                return;
//            }
//
//            sync_students_button.setEnabled(false);
//            canteen_button.setEnabled(false);
//            tbtnexit.setEnabled(false);
//            sync_pin_button.setEnabled(false);
//            tbtnrfa.setEnabled(false);
//
//            if (mReceiveThread == null) {
//                mReceiveThread = new receiveThread();
//                mReceiveThread.start();
//            } else {
//                mReceiveThread.interrupt();
//                mReceiveThread = new receiveThread();
//                mReceiveThread.start();
//            }
//        }
//    }

    //remove more chinese shit
//    class receiveThread extends Thread {
//        boolean isRunning = true;
//        DualCardResponse res;
//
//        @Override
//        public void run() {
//            while (isRunning) {
//                res = dualCard.LLC_SNEP_BATCH(mPort, (byte) 0x0F, 0, null);
//                Log.i(TAG, "3res " + res.getResponseCode() + " data: " + Hex.bytesToHexString(res.getResponseData()));
//
//                if (res.getResponseCode() != ResponseCode.DE_OK) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                } else {
//                    byte[] ndef = res.getResponseData();
//                    if (ndef == null || ndef.length <= 0 || ndef.length > 25)
//                        continue;
//                    try {
//                        NdefMessageDecoder ndefMessageDecoder = Ndef.getNdefMessageDecoder();
//                        NdefMessage ndefMessage = ndefMessageDecoder.decode(ndef);
//
//                        List<Record> records = ndefMessageDecoder.decodeToRecords(ndefMessage);
//
//                        for (Record record : records) {
//                            if (record instanceof TextRecord) {
//                                String time = ((TextRecord) record).getText();
//                                String[] values = time.split(":");
//                                try {
//                                    for (int i = 0; i < values.length; i++) {
//                                        Log.d(TAG, "data[" + i + "] = " + values[i]);
//                                    }
//
//                                    int year = Integer.parseInt(values[0]);
//                                    int mon = Integer.parseInt(values[1]) - 1;
//                                    int day = Integer.parseInt(values[2]);
//                                    int hour = Integer.parseInt(values[3]);
//                                    int min = Integer.parseInt(values[4]);
//                                    int sec = Integer.parseInt(values[5]);
//                                    Calendar calendar;
//                                    calendar = Calendar.getInstance();
//                                    calendar.set(year, mon, day, hour, min, sec);
//                                    String result = Globals.yyyyMMddHHmmSS.format(calendar.getTime());
//
//                                    long lastTime = calendar.getTimeInMillis();
//                                    boolean z = SystemClock.setCurrentTimeMillis(lastTime);
//
//                                    long now = System.currentTimeMillis();
//                                    // 1111 1ð111 1111 1Ѵ1.
//                                    Date nowDate = new Date(now);
//                                    Date lastDate = new Date(lastTime);
//                                    if (nowDate.compareTo(lastDate) != -1) {
//                                        changeImg(imgRTC, SUCCESSFLAG);
//                                    } else
//                                        changeImg(imgRTC, ERRORFLAG);
//
//                                    changeImg(imgP2P, SUCCESSFLAG);
//                                } catch (NumberFormatException e) {
//                                    changeImg(imgP2P, ERRORFLAG);
//                                }
//
//                                break;
//                            } else {
//                                changeImg(imgP2P, ERRORFLAG);
//                                continue;
//                            }
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//            }
//        }
//
//        public void stopping() {
//            isRunning = false;
//        }
//    }

    private void readTypeB() {
        if (!sync_students_button.isChecked()) {
            tbtnrfa.setEnabled(true);
            canteen_button.setEnabled(true);
            tbtnexit.setEnabled(true);
            sync_pin_button.setEnabled(true);
            search_button.setEnabled(true);
            loopFindCard = false;
            bStop = true;
            testRf.cancel(true);
            testRf = null;
            //dualCard.DE_ClosePort(mPort);
            toggleFlag = false;
            //Log("Port Disconnect", NORMALFLAG);
        } else {
            if (toggleFlag) {
                Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
                sync_students_button.setChecked(false);
                return;
            }
            tbtnrfa.setEnabled(false);
            canteen_button.setEnabled(false);
            tbtnexit.setEnabled(false);
            sync_pin_button.setEnabled(false);
            search_button.setEnabled(false);
            //			int ret = dualCard.DE_InitPort(mPort, baud);
            //			if(mPort == ret) {
            //				Log("Port Connect", NORMALFLAG);
            //			}
            //			else {
            //				Log("Port Error", ERRORFLAG);
            //				sync_students_button.setChecked(false);
            //				toggleFlag = false;
            //				return;
            //			}

            if (testRf == null) {
                testRf = new rfFindCard();
                testRf.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "B");
                toggleFlag = true;
            }
        }
    }

    private void clearLog() {
        editLog.setText("");
        count = 0;
        toggleFlag = false;
    }

    private void exitRF() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("1111");
		builder.setMessage("1׽1Ʈ11 1111111111 1Ϸ1Ǿ1111ϱ1?");
		builder.setPositiveButton("Ȯ11", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				UiUpdater.updateSamP2p(true);
				dualCard.DE_ClosePort(mPort);
				finish();
			}
		});
		builder.setNegativeButton("111", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});

		AlertDialog alert = builder.create();
		builder.show();
		 */
        timerStop = true;
        sw.cancel(true);
        sw = null;

        dualCard.DE_ClosePort(mPort);
        finish();
    }

    private boolean loopTestC;
    private LoopTestCJob testC;

    private void readTypeC() {
        if (!canteen_button.isChecked()) {
            tbtnrfa.setEnabled(true);
            sync_students_button.setEnabled(true);
            tbtnexit.setEnabled(true);
            sync_pin_button.setEnabled(true);
            search_button.setEnabled(true);
            loopFindCard = false;
            bStop = true;
            testC.cancel(true);
            testC = null;
            //dualCard.DE_ClosePort(mPort);

            toggleFlag = false;
            //Log("Port Disconnect", NORMALFLAG);
        } else {
            if (toggleFlag) {
                Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
                canteen_button.setChecked(false);
                return;
            }
            tbtnrfa.setEnabled(false);
            sync_students_button.setEnabled(false);
            tbtnexit.setEnabled(false);
            sync_pin_button.setEnabled(false);
            search_button.setEnabled(false);
            //			int ret = dualCard.DE_InitPort(mPort, baud);
            //			if(mPort == ret) {
            //				Log("Port Connect", NORMALFLAG);
            //			}
            //			else {
            //				Log("Port Error", ERRORFLAG);
            //				sync_students_button.setChecked(false);
            //				toggleFlag = false;
            //				return;
            //			}

            if (testC == null) {
                testC = new LoopTestCJob();
                testC.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
                toggleFlag = true;
            }
        }
    }

    class LoopTestCJob extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            Log.e(TAG, "onPreExecute()");
            super.onPreExecute();
            bStop = false;
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            count = 0;
            loopTestC = true;
            byte[] systemcode = new byte[2];
            systemcode[0] = (byte) 0xFF;
            systemcode[1] = (byte) 0xFF;

            while (loopTestC) {
                //Log("["+(++count) + "] Polling_NoENC (Felica)", SENDFLAG);
                ++count;
                DualCardResponse ret = dualCard.DEC_Polling_NoENC(mPort, systemcode, (byte) 0x00, (byte) 0x01, (byte) 0x50);
                if (ret.getResponseCode() == ResponseCode.DE_OK) {
                    Log.d(TAG, "Type C Polling");

                    byte[] data = ret.getResponseData();

                    String idm = Hex.bytesToHexString(data);
                    Log("[" + count + "] " + idm, RECEIVEFLAG);
                } else
                    Log("[" + count + "] " + "NULL", ERRORFLAG);    //ERROR

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
    }

    private void Log(final String data, final int flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = "";
                if (flag == SENDFLAG) {
                    msg += "=> " + data + "\n";
                } else if (flag == RECEIVEFLAG) {
                    msg += data + "\n";
                } else if (flag == ERRORFLAG) {
                    appendColoredText(editLog, data + "\n", Color.RED);
                } else if (flag == NORMALFLAG) {
                    //SYH 2014.05.08
                    msg += data + "\n";
                } else {
                    msg += "\n";
                }


                editLog.append(msg);

                if (editLog.getLineCount() > 100) {
                    editLog.setText("");
                }
            }
        });
    }

    public static void appendColoredText(EditText tv, String text, int color) {
        int start = tv.getText().length();
        tv.append(text);
        int end = tv.getText().length();

        Spannable spannableText = (Spannable) tv.getText();
        spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
    }

    private void changeImg(final ImageView imgView, final int flag) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (flag == SUCCESSFLAG) {
                    imgView.setImageResource(R.drawable.ok);
                } else if (flag == ERRORFLAG) {
                    imgView.setImageResource(R.drawable.not_ok);
                }
            }
        });
    }

    //SYH 2014.05.08
    private boolean threadSW;
    private SwThreadJob sw;
    private boolean timerStop = false;

    class SwThreadJob extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            threadSW = true;
            timerStop = false;
            while (threadSW) {
                if (timerStop == true)
                    return 0;

                c = Calendar.getInstance();
                curMillis = c.getTime();
                curYear = c.get(Calendar.YEAR);
                curMonth = c.get(Calendar.MONTH) + 1;
                curDay = c.get(Calendar.DAY_OF_MONTH);
                curHour = c.get(Calendar.HOUR_OF_DAY);
                curNoon = c.get(Calendar.AM_PM);
                if (curNoon == 0) {
                    noon = "AM";
                } else {
                    noon = "PM";
                    curHour -= 12;
                }
                curMinute = c.get(Calendar.MINUTE);
                curSecond = c.get(Calendar.SECOND);
                String strTime;
                strTime = curYear + "/" + curMonth + "/" + curDay + " " + noon + " " + curHour + ":" + curMinute + ":" + curSecond;
                //printTime(strTime);
            }
            return 0;
        }
    }

    private void printTime(final String strTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                numStudentsTextView.setText(strTime);
            }
        });
    }

    private void findViewByID() {
        tbtnrfa = (ToggleButton) findViewById(R.id.tbtn_rfa);
        tbtnrfa.setOnClickListener(this);
        sync_students_button = (ToggleButton) findViewById(R.id.sync_students_button);
        sync_students_button.setOnClickListener(this);
        canteen_button = (ToggleButton) findViewById(R.id.canteen_button);
        canteen_button.setOnClickListener(this);
        gate_pass_button = (ToggleButton) findViewById(R.id.gate_pass_button);
        gate_pass_button.setOnClickListener(this);
        sync_pin_button = (Button) findViewById(R.id.sync_pin_button);
        sync_pin_button.setOnClickListener(this);
       /* sync_meal_button = (Button) findViewById(R.id.sync_meal_button);
        sync_meal_button.setOnClickListener(this);*/
        search_button = (ToggleButton) findViewById(R.id.btn_p2p);
        search_button.setOnClickListener(this);

        reset_students_button = (Button) findViewById(R.id.reset_students_button);
        reset_students_button.setOnClickListener(this);
        tbtnexit = (Button) findViewById(R.id.btn_exit);
        tbtnexit.setOnClickListener(this);

        //  imgSAM = (ImageView) findViewById(R.id.ImageViewSAM);
        //  imgP2P = (ImageView) findViewById(R.id.ImageViewP2P);
        //  imgRTC = (ImageView) findViewById(R.id.ImageViewRTC);
        numStudentsTextView = (TextView) findViewById(R.id.numStudentsTextView);
        editLog = (EditText) findViewById(R.id.edit_log);

        // spinner = (Spinner) findViewById(R.id.spinnerTime);
    }

    void log(String msg) {
        //if (log && Globals.log) {
        Log.v(TAG, msg);
        //}
    }

}
