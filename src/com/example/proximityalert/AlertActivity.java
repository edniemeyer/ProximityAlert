
package com.example.proximityalert;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class AlertActivity extends Activity {

	private MediaPlayer mp;
	private AudioManager audioManager;
	private Vibrator v;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Button stopButton;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.alert);
		
		stopButton = (Button)findViewById(R.id.stop);

		stopButton.setOnClickListener(buttonAddOnClickListener);	
		
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		mp = MediaPlayer.create(getApplicationContext(), notification);
		//Setting volume to maximum
		
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,100,16);
		
		startAlarm();
		
		/*
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

		if(alert == null){
		    // alert is null, using backup
		    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		    // I can't see this ever being null (as always have a default notification)
		    // but just incase
		    if(alert == null) {  
		        // alert backup is null, using 2nd backup
		        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);                
		    }
		}
		
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
		
		
		//Setting volume to maximum
		
		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,100,2);
		
		r.play();
		*/
}
	
	
	Button.OnClickListener buttonAddOnClickListener  = new Button.OnClickListener(){
	    @Override
	    public void onClick(View v) {
	    	stopAlarm();
	        finish();
	        }
	};
	
	private void startAlarm(){
		
		
		mp.start();
		v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = {0,1000,500};
		v.vibrate(pattern, 0);
		
	};
	
private void stopAlarm(){
		
		//audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,40,16);
		v.cancel();
		mp.release();
		
	};
	
}