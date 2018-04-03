package com.duali.itouchpop2_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android_serialport_api.SerialPort;

import com.duali.dualcard.jni.DualCardJni;
import com.duali.io.jni.DualIOJni;
import com.duali.itouchpop2_test.helper.FileHelper;
import com.duali.itouchpop2_test.utils.Globals;
import com.duali.itouchpop2_test.utils.Hex;
import com.duali.itouchpop2_test.utils.SoundManager;
import com.duali.itouchpop2_test.utils.Utils;
import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;

import de.jockels.open.Environment2;
import de.jockels.open.NoSecondaryStorageException;



public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	/* UI 1111 */
	private ToggleButton tbtnSerai, tbtnCam, tbtnEther, tbtnWifi, tbtnAging;
	private TextView txtTime;
	private ImageView imgMicroSD, imgRelay, imgOpenSw, imgDoorSw, imgCam, imgEther, imgWifi, imgSpeaker, imgSerial;
	private ImageView imgF1, imgF2, imgF3, imgF4;
	private EditText editLog;
	private Button btnRFTEST;

	/* 111 1111 */
	private SoundManager sManager;	//Sound
	//private DualCardJni dualCard;	//JNI dualCard
	private DualIOJni dualIO;		//JNI deio
	private WifiManager mWifiManager;	//WIFI 

	/* 1⺻ 11 1111 */
	private static final int mPort = 1000;
	private static final int baud = 115200;
	private static final int slotNo = 0;	//Slot 0 1111

	private static final int SENDFLAG = 1;
	private static final int RECEIVEFLAG = 2;
	private static final int ERRORFLAG = 3;
	private static final int BLANKFLAG = 4;
	private static final int SUCCESSFLAG = 5;

	//SYH 2014.05.08
	private static final int NORMALFLAG = 6;

	private static final int DOORSWFLAG = 7;
	private static final int OPENSWFLAG = 8;

	private boolean toggleFlag = false;
	private static final String defaultMsg = "Duali iTouchpop2 sample text";

	//SYH 2014.05.08
	private static final int IO_PORT = 500;
	private int fd;
	//private static final String RS485_PATH = "/dev/s3c2410_serial3";
	//private static final String RS232_PATH = "/dev/s3c2410_serial2";

	private static final String RS485_PATH = "/dev/ttyTCC3";
	private static final String RS232_PATH = "/dev/ttyTCC2";

	private static boolean bStop = false;

	public static boolean isPop2 = false;
	//SYH 2014.05.08
	//Timer 
	Date curMillis;
	int curYear, curMonth, curDay, curHour, curMinute, curNoon, curSecond;
	Calendar c;
	String noon = "";

	//SYH 2014.05.08
	//Serial
	protected Application mApplication;
	private SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;

	private PingTestThread pingTestThread = null;
	private boolean pingFlag = false;

	private ProgressDialog progDaialog;
	private WifiCheckThread wifiCheckThread = null;

	/* UI Handler */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d(TAG, "Handler, Message what: " + msg.what);
			Bundle bundle = null;
			int flag;
			switch(msg.what) {
			case 0:		//updateLog
				bundle = msg.getData();
				String data = bundle.getString("data");
				flag = bundle.getInt("flag");

				Log(data, flag);				
				break;

			case 1: 	//updateSAM
				break;
			case 2:		//updateTimer
				//				bundle = msg.getData();
				//				String time = bundle.getString("time");
				//				String[] values = time.split(":");
				//				try {
				//					for(int i=0; i<values.length; i++) {
				//						Log.d(TAG, "data["+i+"] = " + values[i]);
				//					}
				//
				//					int year = Integer.parseInt(values[0]);
				//					int mon = Integer.parseInt(values[1]) -1 ;
				//					int day = Integer.parseInt(values[2]);
				//					int hour = Integer.parseInt(values[3]);
				//					int min = Integer.parseInt(values[4]);
				//					int sec = Integer.parseInt(values[5]);
				//					Calendar calendar;
				//					calendar = Calendar.getInstance();
				//					calendar.set(year,mon,day,hour,min,sec);
				//					String result = Globals.yyyyMMddHHmmSS.format(calendar.getTime());
				//
				//					long lastTime = calendar.getTimeInMillis();
				//					boolean z = SystemClock.setCurrentTimeMillis(lastTime);
				//
				//					long now = System.currentTimeMillis();
				//					// 1111 1ð111 1111 1Ѵ1.
				//					Date nowDate = new Date(now);
				//					Date lastDate = new Date(lastTime);
				//					if(nowDate.compareTo(lastDate) != -1){
				//						changeImg(imgRTC, SUCCESSFLAG);
				//					} else 
				//						changeImg(imgRTC, ERRORFLAG);
				//
				//					changeImg(imgP2P, SUCCESSFLAG);
				//				} catch(NumberFormatException e){
				//					changeImg(imgP2P, ERRORFLAG);
				//				}
				break;
			case 3: 	// ping thread
				String pingMsg = msg.obj+"";

				if(pingMsg.contains("ERROR")) {
					Log(msg.obj+"", ERRORFLAG);
					if(tbtnEther.isChecked()) {
						changeImg(imgEther, ERRORFLAG);
						tbtnEther.setChecked(false);
					} else if(tbtnWifi.isChecked()) {
						changeImg(imgWifi, ERRORFLAG);
						tbtnWifi.setChecked(false);
					}
				} else {
					Log(msg.obj+"", SENDFLAG);
					if(tbtnEther.isChecked()) {
						changeImg(imgEther, SUCCESSFLAG);
						tbtnEther.setChecked(false);
					} else if(tbtnWifi.isChecked()) {
						changeImg(imgWifi, SUCCESSFLAG);
						tbtnWifi.setChecked(false);
					}
				}

				toggleFlag = false;
				if(pingTestThread != null) {
					pingTestThread.interrupt();
					pingTestThread = null;
				}
				
				if(progDaialog != null && progDaialog.isShowing()) {
					progDaialog.dismiss();
				}
				
				break;
			case 4: 
				int connectWifi = msg.arg1;
				boolean b = mWifiManager.enableNetwork(connectWifi, true);
				if (!b) {
					Log("Wifi ERROR", ERRORFLAG);
					return;
				} else {
					Log.e(TAG, "Wifi Setting OK");
					GetWifiInfo(true);
					
					FileHelper.useWifi();

					if(pingTestThread == null){
						pingTestThread = new PingTestThread();
						pingTestThread.start();
					} 
					
				}	
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* ȭ11 1111111 1111 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED );

		/* Screen Size */
		isPop2 = displaySize();

		/* UI 1111 */
		findViewByID();

		/* Sound */
		soundInit();

		/* JNI */
		//dualCard = DualCardJni.getInstance();
		dualIO = DualIOJni.getInstance();

		//SYH 2014.05.08
		fd = dualIO.DE_Door_Open(IO_PORT);	//TODO IO Door Open
		//dualIO.DE_Door_SETDBG(1);
		//dualIO.DE_Kernel_SETDBG(IO_PORT, 1);
		/* 1ʱ1 111 1׽1Ʈ */
		funcInit();

		/* Wifi Manger 1111 */
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

		//swThread();

		//Application 
		mApplication = (Application) getApplication();

		Log("MAC: " + 
				getMacAddress(), NORMALFLAG);
	}

	private boolean displaySize() {
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();

		display.getMetrics(metrics);

		float width = -1;

		float height = -1;

		/**
		30.
		 *  3.0 1̻󿡼1 NaviBar11 1111111 1ʾ1 11Ȯ11 ġ1111 1˼1 1111.
		31.
		 *  111111 API 1󿡼1 11Ÿ111111 111111 111111 11111ϴ1 getRawWidth(),
		32.
		 *  getRawHeight() 1Լ111 1ҷ11111.
		33.
		 */
		try {

			Method mGetRawW = Display.class.getMethod("getRawWidth");

			Method mGetRawH = Display.class.getMethod("getRawHeight");

			width = (Integer)mGetRawW.invoke(display);

			height = (Integer)mGetRawH.invoke(display);
		} catch (Exception e) {

		}
		// 11 1ڵ忡11 111111 11 111 111111 ġ1111 11111111 11111 111
		if(width < 0){
			width = metrics.widthPixels;
		}

		if(height < 0){
			height = metrics.heightPixels;
		}

		Log.e(TAG, "width " + width + " height " + height);

		if(width == 480 && height == 272)
			return true;
		else 
			return false;
	}

	//SYH 2014.05.08
	private boolean threadSW;
	private SwThreadJob sw;

	//SYH 2014.05.08
	private void swThread() {
		if (fd != IO_PORT) {	//IO Door Open11 1ȉ111 111
			Log.d(TAG, "IO Port : " + fd);
			Log("IO Port Disconnect", ERRORFLAG);
			//return;
		} else {
			Log.d(TAG, "IO Port : " + fd);
		}

		if(sw == null) {
			bStop = false;
			sw = new SwThreadJob();
			sw.execute(0);
		}
	}

	private int doorswFlag = 0;
	private int openswFlag = 0;

	//SYH 2014.05.08
	class SwThreadJob extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected Integer doInBackground(Integer... params) {
			threadSW = true;
			bStop = false;
			int cntSW = 1;
			int cntDoor = 1;
			while(threadSW) {
				try {

					if(bStop == true)
						return 0;

					int ret = dualIO.DE_Open_Switch_Status(IO_PORT);
					if (ret == 0) { 
						openswFlag = SUCCESSFLAG;
						Log("Open Switch Pressed!", OPENSWFLAG);
						changeImg(imgOpenSw, SUCCESSFLAG);
						Thread.sleep(300);	
						cntSW = 0;
					} else {
						if (cntSW == 0) {
							Log("Open Switch Not Pressed!", SENDFLAG);
							cntSW++;
						}
					}
					int ret2 = dualIO.DE_Door_Status(IO_PORT);
					if (ret2 == 0) {
						doorswFlag = SUCCESSFLAG;
						Log("Door Opened!", DOORSWFLAG);
						changeImg(imgDoorSw, SUCCESSFLAG);
						Thread.sleep(300);	
						cntDoor = 0;
					} else {
						if(cntDoor == 0) {
							Log("Door Closed!", SENDFLAG);
							cntDoor++;
						}
					}

					Thread.sleep(100);	

					c = Calendar.getInstance();
					curMillis = c.getTime();
					curYear = c.get(Calendar.YEAR);
					curMonth = c.get(Calendar.MONTH)+1;
					curDay = c.get(Calendar.DAY_OF_MONTH);
					curHour = c.get(Calendar.HOUR_OF_DAY);
					curNoon = c.get(Calendar.AM_PM);
					if(curNoon == 0){
						noon = "AM";
					} else {
						noon = "PM";
						curHour -= 12;
					}
					curMinute = c.get(Calendar.MINUTE);
					curSecond = c.get(Calendar.SECOND);
					//Log.d(TAG, curYear+"."+curMonth+"."+curDay+" "+noon+curHour+":"+curMinute+":"+curSecond);
					//Log(curYear+"11"+curMonth+"11"+curDay+"11 "+noon+""+curHour+":"+curMinute+":"+curSecond, NORMALFLAG);
					//SYH Need Fix
					String strTime;
					strTime = curYear+"/"+curMonth+"/"+curDay+" "+noon+" "+curHour+":"+curMinute+":"+curSecond;
					printTime(strTime);
					//txtTime.setText(curYear+"11"+curMonth+"11"+curDay+"11 "+noon+""+curHour+":"+curMinute+":"+curSecond);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return 0;
		}
	}

	private void funcInit() {
		/* MicroSD */
		microsdTest();

		/* Speaker 1111 111111 1111111111 Door SW / Open SW11 1Ҹ1 11111111 1111!! */
		//sManager.play(0);
		//changeImg(imgSpeaker, SUCCESSFLAG);
	}

	private void printTime(final String strTime){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtTime.setText(strTime);
			}
		});		
	}

	private void microsdTest() {
		try {
			Log.d(TAG, Environment2.getSecondaryExternalStorageState());

			if(! Environment2.getSecondaryExternalStorageState().equals("mounted")) {
				Log("SDCARD ERROR", ERRORFLAG);
				changeImg(imgMicroSD, ERRORFLAG);
				return;
			}

			File TEMP_PATH = Environment2.getSecondaryExternalStorageDirectory();
			String SAVE_FILE = "/test.txt";

			//1111 1111
			File file = FileHelper.makeFile(TEMP_PATH, TEMP_PATH+SAVE_FILE);
			//1111 1111
			FileHelper.writeFile(file, defaultMsg.getBytes());
			//1111 1б1
			String readText = FileHelper.readFile(file);

			if(defaultMsg.equals(readText)) {
				Log("SDCARD OK" , SENDFLAG);
				changeImg(imgMicroSD, SUCCESSFLAG);
			} else {
				Log("SDCARD DATA ERROR", ERRORFLAG);
				changeImg(imgMicroSD, ERRORFLAG);
			}
			//FileHelper.deleteFile(file);
		} catch (NoSecondaryStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toggleButton2:	//tbtnSerial
			//SYH 2014.05.08
			serialTest();
			break;
		case R.id.toggleButton3:	//tbtnCam
			if(tbtnCam.isChecked()){
				if(toggleFlag){
					Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
					tbtnCam.setChecked(false);
					return;
				}

				toggleFlag = true;
				Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
				intentScan.setPackage("com.duali.itouchpop2_test");
				intentScan.addCategory(Intent.CATEGORY_DEFAULT);
				startActivityForResult(intentScan, IntentIntegrator.REQUEST_CODE);
			}
			else{
				toggleFlag = false;
			}
			break;
		case R.id.toggleButton4:	//tbtnEther
			if(tbtnEther.isChecked()){
				if(toggleFlag){
					Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
					tbtnEther.setChecked(false);
					return;
				}

				toggleFlag = true;
				if (mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(false);
				}

				if(! isCarrier() ){
					Toast.makeText(this, "Please, connect the LAN cable.", Toast.LENGTH_SHORT).show(); //111̺111 111111 1ּ111", Toast.LENGTH_SHORT).show();
					tbtnEther.setChecked(false);
					toggleFlag = false;
					return;
				}

				Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.duali.network");
				startActivity(intent);
			}
			else{
				toggleFlag = false;
			}
			break;
		case R.id.toggleButton5:	//tbtnWifi
			if(tbtnWifi.isChecked()){
				if(toggleFlag){
					Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
					tbtnWifi.setChecked(false);
					return;
				}

				toggleFlag = true;
				progDaialog = new ProgressDialog(this);
				progDaialog.setMessage("11ø1 11ٷ1 1ּ111.");
				progDaialog.setCancelable(false);
				progDaialog.setButton("cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(progDaialog!=null && progDaialog.isShowing())
							progDaialog.dismiss();
						if(wifiCheckThread != null) {
							wifiCheckThread.stopping(false);
							wifiCheckThread.interrupt();
						}
						tbtnWifi.setChecked(false);
						toggleFlag = false;
					}
				});
				progDaialog.show();
				wifiCheckThread = new WifiCheckThread();
				wifiCheckThread.start();
			}
			else{
				toggleFlag = false;
			}
			break;
		case R.id.toggleButton6:	//tbtnAging
			agingTest();
			break;
		default:
			break;
		}
	}

	private void agingTest() {

		if(toggleFlag){
			Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
			tbtnAging.setChecked(false);
			return;
		}

		Intent intent = new Intent(this, VideoActivity.class);
		tbtnAging.setChecked(false);
		startActivity(intent);
	}

	private void GetWifiInfo(boolean isWifi) {
		boolean ret = true;
		if(isWifi) {
			final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

			setWifiStateText(mWifiManager.getWifiState());
			Log.i(TAG,wifiInfo.getBSSID());
			Log.i(TAG,String.valueOf(wifiInfo.getHiddenSSID()));
			int ipAddr = wifiInfo.getIpAddress();
			StringBuffer ipBuf = new StringBuffer();
			ipBuf.append(ipAddr & 0xff).append('.').append((ipAddr >>>= 8) & 0xff).append('.').append((ipAddr >>>= 8) & 0xff).append('.')
			.append((ipAddr >>>= 8) & 0xff);

			Log.i(TAG,ipBuf.toString());
			Log.i(TAG,String.valueOf(wifiInfo.getLinkSpeed()) + " Mbps");
			Log.i(TAG,wifiInfo.getMacAddress());
			Log.i(TAG,String.valueOf(wifiInfo.getNetworkId()));
			Log.i(TAG,String.valueOf(wifiInfo.getRssi()));
			Log.i(TAG,wifiInfo.getSSID());

			SupplicantState supplicantState = wifiInfo.getSupplicantState();
			Log.i(TAG,supplicantState.toString());
		}
		// test functions
		String macAddress = Utils.getMACAddress("wlan0");
		String macAddress2 = Utils.getMACAddress("eth0");
		String ipAddress = Utils.getIPAddress(true); // IPv4
		String ipAddress2 = Utils.getIPAddress(false); // IPv6
		Log.i(TAG,"wlan0 > "+ macAddress);
		Log.i(TAG,"eth0 > "+ macAddress2);
		Log.i(TAG,"ipAddress > "+ ipAddress);

		long sTime, eTime;
		sTime = System.currentTimeMillis();
		
		while(ipAddress==null || ipAddress.length() < 5){
			ipAddress = Utils.getIPAddress(true); // IPv4
			Log.i(TAG,"ipAddress > "+ ipAddress);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			eTime = System.currentTimeMillis();
			
			if((eTime-sTime) > 7000){
				ret = false;
				Toast.makeText(this, "Ca't setting IP address.", Toast.LENGTH_SHORT).show(); //IP11 11111111 111߽11ϴ1.", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
		//return ret;
	}

	private void setWifiStateText(int wifiState) {
		String wifiStateString;
		switch (wifiState) {
		case WifiManager.WIFI_STATE_DISABLING:
			wifiStateString = "disabling";
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			wifiStateString = "wifi_state_disabled";
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			wifiStateString = "wifi_state_enabling";
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			wifiStateString = "wifi_state_enabled";
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			wifiStateString = "wifi_state_unknown";
			break;
		default:
			wifiStateString = "BAD";
			break;
		}

		Log.i(TAG,wifiStateString);
	}

	private int connectWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
		WifiConfiguration wc = new WifiConfiguration();
		// This is must be quoted according to the documentation
		// http://developer.android.com/reference/android/net/wifi/WifiConfiguration.html#SSID
		wc.SSID = "\"line\"";
		wc.preSharedKey = "\"aaaaaaaa\"";
//		wc.SSID = "\"Duali_Suwon\"";
//		wc.preSharedKey = "\"dualiduali\"";
		wc.hiddenSSID = true;
		wc.status = WifiConfiguration.Status.ENABLED;
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		int res = mWifiManager.addNetwork(wc);
		mWifiManager.saveConfiguration();
		return res;
	}

	private int samFlag = 0;

	private void changeImg(final ImageView imgView, final int flag) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(flag == SUCCESSFLAG){
					imgView.setImageResource(R.drawable.ok);
				}else if(flag == ERRORFLAG){
					imgView.setImageResource(R.drawable.not_ok);
				}
			}
		});


	}

	//SYH 2014.05.08
	protected OutputStream mRs232OutputStream;
	private InputStream mRs232InputStream;
	protected OutputStream mRs485OutputStream;
	private InputStream mRs485InputStream;

	private int relayFlag = 0;
	private int serialFlag = 0;

	private void serialTest() {
		if(tbtnSerai.isChecked()){
			if(toggleFlag){
				samFlag = ERRORFLAG;
				Toast.makeText(this, "At first, turn off other selected button.", Toast.LENGTH_SHORT).show(); //11111ִ1 111 11ư11 1111 1ٽ1 1õ1111ּ111.", Toast.LENGTH_SHORT).show();
				tbtnSerai.setChecked(false);
				return;
			}

			toggleFlag = true;
			Log(null,BLANKFLAG);

			if (fd != IO_PORT) {
				Log.d(TAG, "IO Port : " + fd);
				Log("IO Port Disconnect", ERRORFLAG);
				return;
			}

			try {
				for (int i=0; i < 2; i++) {

					dualIO.DE_WD0_Out(IO_PORT, 0);
					Thread.sleep(100);
					int v = dualIO.DE_Buz_In(IO_PORT);
					if (v == 1) {
						Log("WD0_Out -> Buz_In 0 OK", NORMALFLAG);
						serialFlag = SUCCESSFLAG;
					}
					else {
						Log("Error (" + v + ")", ERRORFLAG);
						serialFlag = ERRORFLAG;
					}


					dualIO.DE_WD0_Out(IO_PORT, 1);
					Thread.sleep(100);
					v = dualIO.DE_Buz_In(IO_PORT);
					if (v == 0) {
						Log("WD0_Out -> Buz_In 1 OK", NORMALFLAG);
						serialFlag = SUCCESSFLAG;
					}
					else {
						Log("Error (" + v + ")", ERRORFLAG);
						serialFlag = ERRORFLAG;
					}


					dualIO.DE_WD1_Out(IO_PORT, 0);
					Thread.sleep(100);
					v = dualIO.DE_Led_In(IO_PORT);
					if (v == 1) {
						Log("WD1_Out -> Led_In 0 OK", NORMALFLAG);
						serialFlag = SUCCESSFLAG;
					}
					else {
						Log("Error (" + v + ")", ERRORFLAG);
						serialFlag = ERRORFLAG;
					}


					dualIO.DE_WD1_Out(IO_PORT, 1);
					Thread.sleep(100);
					v = dualIO.DE_Led_In(IO_PORT);
					if (v == 0) {
						Log("WD1_Out -> Led_In 1 OK", NORMALFLAG);
						serialFlag = SUCCESSFLAG;
					}
					else {
						Log("Error (" + v + ")", ERRORFLAG);
						serialFlag = ERRORFLAG;
					}
				}

				//Relay
				int ret = dualIO.DE_Door_Relay(IO_PORT, 1);
				if (ret == 0) {
					Log("Relay ON OK", NORMALFLAG);
					relayFlag = SUCCESSFLAG;
				}
				else {
					Log("Relay Error", NORMALFLAG);
					relayFlag = ERRORFLAG;
				}

				changeImg(imgRelay, relayFlag);

				byte[] data = new byte[]{'A', 'B', 'C', '1', '2'};
				dualIO.DE_RS485(IO_PORT, 0);
				flush(mRs485InputStream);	
				//dualIO.DE_RS485(IO_PORT, 1);
				writeSerial(mRs232OutputStream, data);			

				Thread.sleep(200);


				byte[] readData = _readSerial(mRs485InputStream);

				// TODO: 11
				if (readData == null){
					Log("rs232 TX / rs485 RX Fail(null)", ERRORFLAG);
					Log.d(TAG, "data null");
					serialFlag = ERRORFLAG;
				}

				else {
					boolean isCompareResult = false;

					isCompareResult = java.util.Arrays.equals(data, readData);
					if (isCompareResult) {
						Log("rs232 TX / rs485 RX OK", NORMALFLAG);
						serialFlag = SUCCESSFLAG;
					}
					else{
						Log("rs232 TX / rs485 RX Fail(compare)", ERRORFLAG);
						Log.d(TAG, "data fail");
						serialFlag = ERRORFLAG;
					}
				}

				flush(mRs232InputStream);
				dualIO.DE_RS485(IO_PORT, 1);	
				writeSerial(mRs485OutputStream, data);

				Thread.sleep(100);
				byte[] readData2 = _readSerial(mRs232InputStream);
				// TODO: 11
				if (readData2 == null){
					Log("rs485 TX / rs232 RX Fail(null)", ERRORFLAG);
					Log.d(TAG, "data null");
					serialFlag = ERRORFLAG;
				}
				else {
					boolean isCompareResult = false;
					isCompareResult = java.util.Arrays.equals(data, readData2);
					if (isCompareResult) {
						Log("rs485 TX / rs232 RX OK", NORMALFLAG);
						serialFlag = SUCCESSFLAG;
					}
					else{
						Log("rs485 TX / rs232 RX Fail(compare)", ERRORFLAG);
						Log.d(TAG, "data fail");
						serialFlag = ERRORFLAG;
					}
				}



			} catch (InvalidParameterException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}  catch (InterruptedException e) {
				e.printStackTrace();
			}

			changeImg(imgSerial, serialFlag);
		}
		else {
			int ret = dualIO.DE_Door_Relay(IO_PORT, 0);
			//dualIO.DE_RS485(IO_PORT, 0);	

			if (ret == 0)
				Log("Relay OFF OK", NORMALFLAG);
			else
				Log("Relay Error", ERRORFLAG);

			toggleFlag = false;
		}
	}

	//SYH 2014.05.08
	private void flush(InputStream inputStream) {
		int size = -1;
		try {

			if ( ( size = inputStream.available() ) > 0 ) {
				byte[] temp = new byte[size];
				inputStream.read(temp);
				Log.d(TAG, "flush : " + Hex.bytesToASCIIString(temp));
			}
			Log.d(TAG, "flush : null");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//SYH 2014.05.08
	private void writeSerial(OutputStream outputStream, byte[] data) {

		try {
			Log.d(TAG, "write: " + Hex.bytesToASCIIString(data));
			outputStream.write(data);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	//SYH 2014.05.08
	private byte[] _readSerial(InputStream inputStream) {

		int size;
		try {
			byte[] buffer = new byte[64];
			if(inputStream == null) return null;
			if( inputStream.available() > 0 ) {
				size = inputStream.read(buffer);
				if(size > 0) {
					byte[] temp = new byte[size];
					System.arraycopy(buffer, 0, temp, 0, size);
					Log.d(TAG, "read: " + Hex.bytesToASCIIString(temp));
					return temp;					
				}
			}

		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			return null;
		} 
		return null;
	}


	private void Log(final String data, final int flag){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String msg = "";
				if(flag == SENDFLAG){
					msg += "=> " + data +"\n";
				}else if(flag == RECEIVEFLAG){
					msg += "<= " + data +"\n";
				}else if(flag == ERRORFLAG){
					appendColoredText(editLog, data+"\n" , Color.RED);
				}else if(flag == NORMALFLAG){
					//SYH 2014.05.08
					msg += data +"\n";
				}else if(flag == DOORSWFLAG){
					msg += data +"\n";
					sManager.play(0);
					changeImg(imgSpeaker, SUCCESSFLAG);
				}else if(flag == OPENSWFLAG){
					msg += data +"\n";
					sManager.play(1);
					changeImg(imgSpeaker, SUCCESSFLAG);
				}

				else{
					msg += "\n";
				}

				editLog.append(msg);

				if(editLog.getLineCount() > 100) {
					editLog.setText("");
				}

			}
		});
	}

	private void clearLog(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				editLog.setText("");
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null)
			return;

		IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
				resultCode, data);
		String s = result.getContents();
		Log(s, SENDFLAG);
		if(s.contains("duali")){
			changeImg(imgCam, SUCCESSFLAG);
		} else {
			changeImg(imgCam, ERRORFLAG);
		}

		//onResult(s);
	}

	public void Exit() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("1111")
		.setMessage("111α׷111 11111Ͻðڽ11ϱ1?")
		.setPositiveButton("11",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.setNegativeButton("1ƴϿ1", null).show();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown(), keyCode " + keyCode);
		System.out.println(keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			changeImg(imgF1, SUCCESSFLAG);
			Exit();
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			changeImg(imgF4, SUCCESSFLAG);
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			changeImg(imgF3, SUCCESSFLAG);
			clearLog();
			break;
		case KeyEvent.KEYCODE_MENU:
			changeImg(imgF2, SUCCESSFLAG);
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
		dualIO.DE_Door_Relay(IO_PORT, 0);
		dualIO.DE_RS485(IO_PORT, 0);	
		dualIO.DE_Case_Status(IO_PORT);
		dualIO.DE_Door_Close(IO_PORT);
		threadSW = false;

		serial232Port.close();
		serial485Port.close();
		//Serial Close

		mApplication.closeSerialPort();
		mSerialPort = null;
		UiUpdater.unregisterClient(handler);
		//dualCard.DE_ClosePort(mPort);

		System.exit(0);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause()");

		/* stop NfcService */
		//Intent intent = new Intent(NfcService.SERVICE_NAME);		
		//stopService(intent);

		bStop = true;
		sw.cancel(true);
		sw = null;

		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume()");
		UiUpdater.registerClient(handler);	

		/* NfcService start */
		//Intent intent = new Intent(NfcService.SERVICE_NAME);
		//intent.putExtra("mode", "TIMER");
		//startService(intent);	

		/* Ethernet ping check */
		if(tbtnEther.isChecked()) {
			Log.d(TAG, "Ethernet Setting OK");


			GetWifiInfo(false);

			if(pingTestThread == null){
				pingTestThread = new PingTestThread();
				pingTestThread.start();
			}
		} 

		if(tbtnCam.isChecked()){
			tbtnCam.setChecked(false);
			toggleFlag = false;
		}

		swThread();

		super.onResume();


	}

	public boolean isCarrier() {
		Runtime runtime = Runtime.getRuntime(); 
		Process process; 
		String res = "-0-";
		boolean ret = false;
		try { 
			String cmd = "cat /sys/class/net/eth0/carrier"; 
			process = runtime.exec(cmd); 
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream())); 
			String line ; 
			while ((line = br.readLine()) != null) { 
				res = line;
				Log.i(TAG, res);
			} 

			if(res.equals("1"))
				return true;
			else
				return false;

		} catch (Exception e) { 
			e.fillInStackTrace(); 
			Log.e("Process Manager", "Unable to execute top command"); 
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.add(0, 1, 0, "RF TEST");
		//		item = menu.add(0, WIFI, 0, "Wifi");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == 1) {
			Intent intent = new Intent(this, RFActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class WifiCheckThread extends Thread{
		private boolean isRunning = true;
		Message msg = Message.obtain(handler, 4);
		
		@Override
		public void run() {
			while(isRunning) {
				int connectWifi = connectWifi();
				if (connectWifi == -1) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.e(TAG, "Wifi11 111111 11 11111ϴ1.");
					continue;
				} 
				
				msg = Message.obtain(handler, 4);
				msg.arg1 = connectWifi; 
				handler.sendMessage(msg);

				isRunning = false;
				break;
			}
			isRunning = false;
		}

		public void stopping(boolean isRunning) {
			this.isRunning = isRunning;
		}
	}
	
	final static int TIMEOUT = 3000;

	public class PingTestThread extends Thread {

		@Override
		public void run() {
			Socket socket = null;
			Message msg = Message.obtain(handler, 3);
			StringBuffer strBuf = new StringBuffer();
			boolean _isChecked = true;
			for(int i =0; i<4; i++) {
				if(!_isChecked)
					break;
				try {
					long t3 = System.nanoTime();
					long dt_2 = TIMEOUT;

					InetAddress ipAddr = InetAddress.getByName("74.125.235.146");
					int port = 80;
					socket = new Socket();
					socket.setSoTimeout(TIMEOUT);
					socket.connect(new InetSocketAddress(ipAddr, port), 1000);
					long t4 = System.nanoTime();
					socket.close();
					dt_2 = (t4 - t3) / 1000000;
					if(dt_2 >= 0 && dt_2 < 100000){
						if(i == 0)
							strBuf.append(dt_2 + "ms (" + Utils.getIPAddress(true) + " to 74.125.235.146)\n");
						else
							strBuf.append("=> "+ dt_2 + "ms (" + Utils.getIPAddress(true) + " to 74.125.235.146)\n");
					} else {
						strBuf.append("Timeout_ERROR\n");
						//						msg = Message.obtain(handler, 3);
						//						msg.obj = "Timeout ERROR";
						//						handler.sendMessage(msg);
					}
				} catch (SocketException e) {
					strBuf.append("Unreachable_ERROR\n");
					Log.e(TAG, "Unreachable ERROR");
				} catch (UnknownHostException e1) {
					strBuf.append("UnknownHost_ERROR\n");
					Log.e(TAG, "UnknownHost ERROR");
				} catch (IOException e) {
					strBuf.append("I/O_ERROR\n");
					Log.e(TAG, "I/O ERROR");
				} finally{
					if(socket != null){
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			msg = Message.obtain(handler, 3);
			msg.obj = strBuf.toString();
			handler.sendMessage(msg);

			super.run();
		}

	}

	SerialPort serial232Port;
	SerialPort serial485Port;
	private void soundInit() {
		sManager = SoundManager.getInstance();
		sManager.init(this);
		sManager.addSound(0, R.raw.good);
		sManager.addSound(1, R.raw.doorbell);

		//Serial
		try {
			if(isPop2)
				serial232Port =  new SerialPort(new File(RS232_PATH), baud, 0);
			else
				serial232Port =  new SerialPort(new File("/dev/ttyTCC2"), baud, 0);

			mRs232OutputStream = serial232Port.getOutputStream();
			mRs232InputStream = serial232Port.getInputStream();

			if(isPop2)
				serial485Port = new SerialPort(new File(RS485_PATH), baud, 0); 
			else
				serial485Port = new SerialPort(new File("/dev/ttyTCC1"), baud, 0);

			mRs485OutputStream = serial485Port.getOutputStream();
			mRs485InputStream = serial485Port.getInputStream();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


	}

	/*
	 * Load file content to String
	 */
	public static String loadFileAsString(String filePath) throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	/*
	 * Get the STB MacAddress
	 */
	public String getMacAddress(){
		try {
			return loadFileAsString("/sys/class/net/eth0/address")
					.toUpperCase().substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void findViewByID() {
		tbtnSerai = (ToggleButton)findViewById(R.id.toggleButton2);
		tbtnSerai.setOnClickListener(this);
		tbtnCam = (ToggleButton)findViewById(R.id.toggleButton3);
		tbtnCam.setOnClickListener(this);
		if(!isPop2){
			tbtnCam.setEnabled(false);
		}
		tbtnEther = (ToggleButton)findViewById(R.id.toggleButton4);
		tbtnEther.setOnClickListener(this);
		tbtnWifi = (ToggleButton)findViewById(R.id.toggleButton5);
		tbtnWifi.setOnClickListener(this);
		tbtnAging = (ToggleButton)findViewById(R.id.toggleButton6);
		tbtnAging.setOnClickListener(this);
		btnRFTEST = (Button) findViewById(R.id.toggleButton7);
		if(!isPop2){
			LinearLayout.LayoutParams params = (LayoutParams) btnRFTEST.getLayoutParams();
			//Button new width
			params.width = params.MATCH_PARENT;
			params.height = params.FILL_PARENT;
			params.weight = 1;

			btnRFTEST.setLayoutParams(params);
			btnRFTEST.setVisibility(View.VISIBLE);
			btnRFTEST.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, RFActivity.class);
					startActivity(intent);
				}
			});
		}
		txtTime = (TextView)findViewById(R.id.textViewTime);

		editLog = (EditText)findViewById(R.id.editLog);

		imgRelay = (ImageView)findViewById(R.id.ImageView02);
		imgCam = (ImageView)findViewById(R.id.ImageView03);
		imgMicroSD = (ImageView)findViewById(R.id.ImageView05);
		imgOpenSw = (ImageView)findViewById(R.id.ImageView06);
		imgEther = (ImageView)findViewById(R.id.ImageView07);
		imgSpeaker = (ImageView)findViewById(R.id.ImageView08);
		imgDoorSw = (ImageView)findViewById(R.id.ImageView10);
		imgWifi = (ImageView)findViewById(R.id.ImageView11);
		imgSerial = (ImageView)findViewById(R.id.ImageView12);

		imgF1 = (ImageView)findViewById(R.id.ImageView13);
		imgF2 = (ImageView)findViewById(R.id.ImageView14);
		imgF3 = (ImageView)findViewById(R.id.ImageView15);
		imgF4 = (ImageView)findViewById(R.id.ImageView16);
		if(!isPop2){
			imgF1.setImageResource(R.drawable.ok);
			imgF2.setImageResource(R.drawable.ok);
			imgF3.setImageResource(R.drawable.ok);
			imgF4.setImageResource(R.drawable.ok);
			imgCam.setImageResource(R.drawable.ok);
		}

	}
}
