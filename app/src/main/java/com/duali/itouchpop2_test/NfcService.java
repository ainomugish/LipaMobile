package com.duali.itouchpop2_test;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.duali.dualcard.jni.DualCardJni;
import com.duali.dualcard.jni.DualCardResponse;
import com.duali.dualcard.jni.ResponseCode;
import com.duali.itouchpop2_test.utils.Globals;
import com.duali.itouchpop2_test.utils.Hex;
import com.duali.nfc.ndef.Ndef;
import com.duali.nfc.ndef.NdefMessage;
import com.duali.nfc.ndef.NdefMessageDecoder;
import com.duali.nfc.ndef.records.Record;
import com.duali.nfc.ndef.records.TextRecord;

public class NfcService extends Service implements Runnable{
	private static final String TAG = "NfcService";
	public static String SERVICE_NAME = "com.duali.itouchpop2_test.NfcService";

	public static final int PORT = 1000;
	public static final int BAUD = 115200;
	private static final int slotNo = 0;	//Slot 0 1111

	private static final boolean DEBUG = false;
	private DualCardJni dualCard;
	public static boolean retFlag = false;

	public boolean mRunning;
	public static Thread nfcThread = null;

	private String mMode;



	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		mRunning = false;
		dualCard = DualCardJni.getInstance();

		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart");

		if(intent == null)			
			return;
		else {
			mMode = intent.getStringExtra("mode");
		}
		if(mRunning == false) {
			mRunning = true;

			nfcThread = new Thread(this);
			nfcThread.setPriority(Thread.MIN_PRIORITY);
			nfcThread.start();
		}	

		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		mRunning = false;
		dualCard.DE_ClosePort(PORT);
		super.onDestroy();
	}
	@Override
	public void run() {
		Log.d(TAG, "run");
		int port = 0;
		if(MainActivity.isPop2)
			port = dualCard.DE_InitPort(PORT, BAUD);
		else
			port = dualCard.DE_InitPortPath(PORT, BAUD, "/dev/ttyTCC3");
		
		dualCard.DE_SETDBG(0);

		if (port != PORT) {
			return;
		}

		int samFlag = 0;
		String samUID = null;
		DualCardResponse res = null;

		while(mRunning) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}

			if(mMode == null)
				continue;

			if(mMode.equals("SAM"))	{	//SAM READ
				retFlag = false;
				res = dualCard.DE_IC_PowerOn(port, (byte) slotNo);
				Log.i(TAG, "1res " + res.getResponseCode() + " data: " + Hex.bytesToHexString(res.getResponseData()))	;
				retFlag = true;
				if(res.getResponseCode() == ResponseCode.DE_OK) {
					UiUpdater.updateLog("SAM Power ON", Globals.SENDFLAG);
					byte[] data = res.getResponseData();
					UiUpdater.updateLog("SAM : " + Hex.bytesToHexString(data), Globals.RECEIVEFLAG);
					byte[] getChallange = new byte[]{(byte)0x00, (byte)0x84, (byte)0x00, (byte)0x00, (byte)0x10};
					
					res = dualCard.DE_IC_Case4(port, (byte) slotNo, getChallange.length, getChallange);
					UiUpdater.updateLog("SAM Get Challenge", Globals.SENDFLAG);
					if(res.getResponseCode() == ResponseCode.DE_OK) {
						data = res.getResponseData();
						UiUpdater.updateLog("SAM : " + Hex.bytesToHexString(data), Globals.RECEIVEFLAG);
					}
					
					samFlag = Globals.SENDFLAG;
					samUID = Hex.bytesToHexString(data);
				} else {
					UiUpdater.updateLog("SAM Power ON ERROR", Globals.ERRORFLAG);
					samFlag = Globals.ERRORFLAG;
				}
				
				mMode = null;
			}else if(mMode.equals("SAM_CLOSE")){	//SAM Close
				retFlag = false;
				res = dualCard.DE_IC_PowerOff(port, (byte) slotNo);
				Log.i(TAG, "2res " + res.getResponseCode() + " data: " + Hex.bytesToHexString(res.getResponseData()))	;
				retFlag = true;
				if(res.getResponseCode() == ResponseCode.DE_OK){
					if(samFlag != Globals.ERRORFLAG)
						samFlag = Globals.SUCCESSFLAG;
					UiUpdater.updateLog("SAM Power OFF", Globals.SENDFLAG);
				} else {
					samFlag = Globals.ERRORFLAG;
					UiUpdater.updateLog("SAM Power OFF ERROR", Globals.ERRORFLAG);
				}
				UiUpdater.updateSAM(samUID, samFlag);

				mMode = "TIMER";
			} else{
				retFlag = false;
				res = dualCard.LLC_SNEP_BATCH(PORT, (byte)0x0F, 0, null);
				Log.i(TAG, "3res " + res.getResponseCode() + " data: " + Hex.bytesToHexString(res.getResponseData()))	;
				retFlag = true;
				if (res.getResponseCode() != ResponseCode.DE_OK) {
					continue;
				} else {
					byte[] ndef = res.getResponseData();
					if(ndef == null || ndef.length <= 0 || ndef.length > 25)
						continue;
					try {
						NdefMessageDecoder ndefMessageDecoder = Ndef.getNdefMessageDecoder();
						NdefMessage ndefMessage = ndefMessageDecoder.decode(ndef);

						List<Record> records = ndefMessageDecoder.decodeToRecords(ndefMessage);

						for( Record record : records) {
							if (record instanceof TextRecord) {		
								UiUpdater.updateTimer(((TextRecord) record).getText());
							} else{
								continue;
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
