package com.duali.itouchpop2_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends Activity implements OnClickListener {
	private static final String TAG = "VideoActivity";

	private static final boolean DEBUG = true;
	private int cnt = 1;
	private boolean isExistVideo = false;
	AssetFileDescriptor afd = null;
	private View txtNoVideo, videoView;
	private VideoView video;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		setContentView(R.layout.video);
		
		// Sleep mode Setting
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED );
		
	}

	private void resolveIntent() {
		final LinearLayout rootView = (LinearLayout)
		LayoutInflater.from(VideoActivity.this).inflate(R.layout.video, null);

		txtNoVideo = rootView.findViewById(R.id.txtNoVideo);
		videoView = rootView.findViewById(R.id.videoView);

		txtNoVideo.setVisibility(View.GONE);
		videoView.setVisibility(View.GONE);
	
		setContentView(rootView);
		
		Runtime.getRuntime().gc(); 
		video = (VideoView) rootView.findViewById(R.id.videoView);
//		video.setOnTouchListener(this);
		video.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				TextView txtNoVideo = (TextView) rootView.findViewById(R.id.txtNoVideo);
				View videoView = rootView.findViewById(R.id.videoView);

				txtNoVideo.setText("Unsupported video.");
				txtNoVideo.setVisibility(View.VISIBLE);
				videoView.setVisibility(View.GONE);
				isExistVideo = false;
				return true;
			}
		});

		video.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				prepareVideo();
			}
		});
		
		
		
	}
	
	
	private static String videoPath = null;
	private void prepareVideo() {
		// TODO Auto-generated method stub
		//videoPath = readMvFile(cnt);
		//Log.i(TAG, "video Path: "+videoPath);
		final String videoPath = "/data/duali/video_0.mp4";
		final Uri video_uri = Uri.parse("android.resource://"+getPackageName()+"/raw/video_0");
		//final String videoPath = "/mnt/sdcard/sd1_1/adpop/1368579507194_6.mp4";
		//			String videoPath = "";
		//			String videoPath = "/sdcard/video_" + no + ext;

		File tempFile = new File(videoPath);

		if (!tempFile.exists()) {
			isExistVideo = false;
			txtNoVideo.setVisibility(View.VISIBLE);
		} else {
			
			isExistVideo = true;
			videoView.setVisibility(View.VISIBLE);
			
			
			video.setVideoURI(video_uri);
			//video.setVideoPath(videoPath);

			//				MediaController mc = new MediaController(this);
			//				mc.setAnchorView(video);
			final MediaController controller = new MediaController(VideoActivity.this);
			controller.setAnchorView(video);	
			controller.setMediaPlayer(video);
			
			video.setMediaController(controller);

			video.requestFocus();	
			video.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer arg0) {
					Log.d("Memory", Debug.getNativeHeapAllocatedSize() +":" + cnt++);
					saveMem();
					if(cnt > 7)
						cnt = 1;
					//videoPath = readMvFile(cnt);
					//Log.i(TAG, "video Path: "+videoPath);
					//video.setVideoPath(videoPath);
					video.setVideoURI(video_uri);
					video.requestFocus();
					SystemClock.sleep(200);
					//resolveIntent();
					video.start();
					controller.hide();
				}
			});
			video.start();
			controller.hide();
		}			
	}

	static public void saveMem()
	{
		String string = "";
		BufferedReader br = null;
		try{
			String[] cmd = {"/system/bin/sh", "-c", "cat /proc/meminfo"};
			Runtime operator = Runtime.getRuntime();
			Process process = operator.exec(cmd);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String tmp="";
			int index =0;
			while((tmp = br.readLine()) !=null)
			{
				string+=tmp +"\n";
				//Log.d("maluchi", "\n"+tmp+"\n");
			};



			long now = System.currentTimeMillis();
			// 1111 1ð111 1111 1Ѵ1.
			Date date = new Date(now);
			// 1ð1 11111111 11111.
			SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String strNow = sdfNow.format(date);
			BufferedWriter file = new BufferedWriter(new FileWriter("/data/duali/memInfo.txt", true));
			file.write(strNow);
			file.newLine();
			file.write(string);
			file.newLine();
			file.close();
		
		}catch(IOException e)
		{
			Log.e("maluchi", "error occurred(message: "+e.getMessage());
		}
		finally{
			try
			{
				br.close();
			}catch (IOException e) {
			}
		}
	}


	@Override
	protected void onResume(){
		super.onResume();
		resolveIntent();
	}
	@Override
	protected void onStop() {
		VideoView video = (VideoView) findViewById(R.id.videoView);
		video.stopPlayback();
		super.onStop();
	}	

	private boolean toggle = false;

	/*@Override
	public boolean onTouch(View view, MotionEvent motionevent) {

		View dialog = findViewById(R.id.video_dialog);
		TextView message = (TextView) findViewById(R.id.message);

		if (isExistVideo)
			message.setText("Stop the video?");
		else
			message.setText("Exit?");

		if (toggle) {
			dialog.setVisibility(View.GONE);	
			toggle = false;
		} else {
			dialog.setVisibility(View.VISIBLE);
			toggle = true;
		}

		return false;
	}*/
	
	public String readMvFile(int num) {
		String filePath = Environment.getExternalStorageDirectory()+"/video/"+cnt+".mp4";
		return filePath;
	}
	@Override
	public void onClick(View view) {

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(0, 0, 0, "Volume Setting");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case 0:
			Intent i = new Intent(VideoActivity.this, VolumeActivity.class);
			startActivity(i);
			return true;
		}
		return false;
		
	}

}
