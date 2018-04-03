package com.duali.itouchpop2_test;

import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.robobunny.SeekBarPreference;

public class VolumeActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        addPreferencesFromResource(R.xml.preferences);
        SeekBarPreference volumeSeekBarPreference = (SeekBarPreference) findPreference(
        		getResources().getString(R.string.prefs_reader_volume));
        final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBarPreference.setValue(nCurrentVolumn);

        volumeSeekBarPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {


        	public boolean onPreferenceChange(Preference preference, Object obj) {
        		//				SeekBarPreference seekBarPreference = (SeekBarPreference) preference;
        		int value = (Integer) obj;

        		am.setStreamVolume(AudioManager.STREAM_MUSIC,
        				value, 0);
        		return true;
        	}
        });
    }
}
