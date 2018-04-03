package com.duali.itouchpop2_test.utils;


import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

/*
 * 1111 1޴111 Ŭ111111 11111 1111 1ʿ111 11 111ϰ1 1111 1111
 * 1111Ǯ11 111Ⱑ 11111ؼ1 11111ΰ1 1111 11111 1111Ѵ1.
 * 
 * 11111111 1Ҹ111 11 11 1ִ1 1ε111 111111 1ʿ11ϴ1.
 * 
 */
public class SoundManager {
	//1ʿ111 1ʵ1 11111ϱ1
	private static SoundManager sManager;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> map;
	private Context context;

	//1̱111 1111
	private SoundManager(){}
	public static SoundManager getInstance(){
		if(sManager==null){
			sManager = new SoundManager();
		}
		return sManager;
	}

	//1ʱ1ȭ1ϱ1
	public void init(Context context){
		this.context=context;

		//1ʿ111 11ÿ11 11111ؼ1 soundpool 11ü11 11111Ѵ1
		soundPool=new SoundPool(5, AudioManager.STREAM_RING, 0);

		//1111 111ҽ1 id 1111 111111 hashMap 11ü 11111ϱ1
		map = new HashMap<Integer, Integer>();

	}
	//111111 1߰11ϴ1 1޼ҵ1(resourceID=R.raw.1111)
	public void addSound(int index, int resId){
		//111ڷ1 111޵1 resId 1111 1̿11ؼ1 111带 1ε111Ű11 11111 1غ1 1Ѵ1.
		int id = soundPool.load(context, resId, 1);

		//1111ϰ1 111ϵǴ1 111̵1 1ʿ1 11111Ѵ1.
		map.put(index, id);
	}
	//111111 1111ϴ1 1޼ҵ1
	public void play(final int index){
		soundPool.play(map.get(index), 1, 1, 1, 0, 1);
//		//111ڷ1 111޵1 1ε111 1111 1̿11ؼ1 1ش1 111111 1111ϵ111 1Ѵ1.
//		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
//			
//			@Override
//			public void onLoadComplete(SoundPool _soundPool, int sampleId, int status) {
//				soundPool.play(map.get(index), 1, 1, 1, 0, 1);
//			}
//		});
		
	}
	//111111 11111ϴ1 1޼ҵ1
	public void stopSound(int index){

		//111ڷ1 111޵1 1ε111 1111 1̿11ؼ1 1ش1 111111 111111Ų11.
		soundPool.stop(map.get(index));
	}
}