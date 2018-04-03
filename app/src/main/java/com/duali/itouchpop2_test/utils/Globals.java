package com.duali.itouchpop2_test.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import sugarorms.studentorm;

public class Globals {

    public static final String appName = "mpocket";

	public static final int SENDFLAG = 1;
	public static final int RECEIVEFLAG = 2;
	public static final int ERRORFLAG = 3;
	public static final int BLANKFLAG = 4;
	public static final int SUCCESSFLAG = 5;
	public static final SimpleDateFormat yyyyMMddHHmmSS = new SimpleDateFormat("yyyyMMddHHmmSS");

	//MY STUFF
	public static final String MPOCK_PREFS = "Mpocket_Prefs";
	public static final String CANTEEN_TOTAL = "Canteen_total";

	public static final boolean log = true;



	//public static final String root = "52.88.221.123";
//	public static final String root = "192.168.1.12";

	//public static final String root = "www.xanthansoftware.com";
	//public static final String BASE_URL = "http://www.xanthansoftware.com:8080";
    public static final String BASE_URL ="http://technical.lipamobile.com:9090";
    //public static final String BASE_URL = "http://54.149.233.142:9090";
    public static final String mqRoot = "207.182.150.74";

  //  public static final String mqRoot = "192.168.10.2";


	public static final String WEB_APP_NAME = "mpocketmav-1.0-SNAPSHOT";
	//public static final String WEB_APP_NAME = "mpocketmav";

	public static final String STUDENT_ID = "studentId";
    public static final String STUDENT_CLASS = "studentClass";
    public static final Date MEAL_DATE = new Date();
    //student
    public static final String STUDENT = "";

	//URLS
	public static String GET_PINS_URL = BASE_URL+"/"+WEB_APP_NAME+"/webresources/app/getStudentPins";
    public static String SYNC_BALANCE_URL = BASE_URL+"/"+WEB_APP_NAME+"/webresources/app/syncBalance";

    //meals
    public static String GET_MEALS_URL = BASE_URL+"/"+WEB_APP_NAME+"/webresources/app/getMeals";
    public static String SEND_MEALS_URL = BASE_URL+"/"+WEB_APP_NAME+"/webresources/app/sendMeals";

    public static String GET_PASS_URL = BASE_URL+"/"+WEB_APP_NAME+"/webresources/app/getStudentPass";
    public static String GET_STUDENTSS_URL = BASE_URL+"/"+WEB_APP_NAME+"/webresources/wegui/listStudents";
    public static String GET_STUDEN_URL ="http://technical.lipamobile.com:9090/mpocketmav-1.0-SNAPSHOT/webresources/webgui/listStudents";
    //public static String GET_STUDEN_URL ="http://54.149.233.142:9090/mpocketmav-1.0-SNAPSHOT/webresources/webgui/listStudents";
    public static final String PIN_CHANGE_LOG = "pin_change_log";
    public static final String MEAL_CHANGE_LOG = "meal_change_log";

	//MESSAGE DELIVERED TO BROKER BRAODCAST..
    public static final boolean clean = false;
    //STORES ADDRESS OF BROKER

    public static final String COMMAND = "command";
    public static final String CHAT_MESSAGE = "chatMessage";
    public static final String NEW_MESSAGE = "new_message";

    //---------------------: MQ COMMANDS
    public static final String COMMAND_LOGIN_PING = "command_login_ping";


    //MESSAGE DELIVERED TO BROKER BRAODCAST..
    public static final String heartbeatString = "mpock_heartbeat";
    public static final int heartBeatMqQos = 0;

    public static final String MESS_REC = "new message received";
    public static final String NEW_REQUEST = "new_request";
    public static final int mqQos = 1;

    public static int MQ_ROOT_PORT = 1883;
  //  public static int MQ_ROOT_PORT = 1883;

	public static final String DEL_TO_SERV = "DEL_TO_SERV";

    //STUDENT CHARGE STUFF
    public static final String NEW_STUDENT_CHARGE = "new_student_charge";
    public static final String STUDENT_CHARGE_REPLY = "student_charge_reply";
    public static final String STUDENT_CHARGE_FINISHED = "student_charge_finished";


    public static final String MPOCKET_KEY_PAD_APP_NAME = "mpocketkeypad";

    public static String PARTNER_KEYPAD = "mpocketkeypad/40:eb:86:53:7c:24";

    public static final String DEVICE_NAME = "device_name";
    public static final String PARTNER_DEVICE_NAME = "partner_device_name";
    public static final String DISCONNECTED = "Disconnected";
    public static final String CONNECTED = "Connected";

    public static final int ADMIN_PIN = 3162;

}
