package com.duali.itouchpop2_test;

import java.util.ArrayList;
import java.util.List;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class UiUpdater {
	protected static List<Handler> clients = new ArrayList<Handler>();
	
	public static void registerClient(Handler client) {
		if(!clients.contains(client)) {
			clients.add(client);
		}
	}
	
	public static void unregisterClient(Handler client) {
		while(clients.contains(client)) {
			clients.remove(client);
		}
	}
	
	public static void updateLog(String data, int flag){
		for (Handler client : clients) {
			Message msg = Message.obtain(client, 0);
			Bundle bundle = new Bundle();
			bundle.putString("data", data);
			bundle.putInt("flag", flag);
			msg.setData(bundle);
			
			client.sendMessage(msg);
		}
	}
	
	public static void updateSAM(String id, int flag){
		for (Handler client : clients) {
			Message msg = Message.obtain(client, 1);
			Bundle bundle = new Bundle();
			bundle.putString("uid", id);
			bundle.putInt("flag", flag);
			msg.setData(bundle);
			
			client.sendMessage(msg);
		}
	}
	
	public static void updateTimer(String time){
		for (Handler client : clients) {
			Message msg = Message.obtain(client, 2);
			Bundle bundle = new Bundle();
			bundle.putString("time", time);
			msg.setData(bundle);
			
			client.sendMessage(msg);
		}
	}
	
	public static void updateSamP2p(boolean isSuccess) {
		for (Handler client : clients) {
			Message msg = Message.obtain(client, 4);
			Bundle bundle = new Bundle();
			bundle.putBoolean("SUCCESS", isSuccess);
			msg.setData(bundle);
			
			client.sendMessage(msg);
		}
	}
}
