package com.example.xmasgiftfinder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

public class splash extends Activity {

	MediaPlayer bells;
	int sound;
	@Override
	protected void onCreate(Bundle startscreen) {
		// TODO Auto-generated method stub
		super.onCreate(startscreen);
		setContentView(R.layout.splash);
		SharedPreferences appSettings = this.getSharedPreferences("appSettings", MODE_PRIVATE); // get reference to the shared alarm settings
		sound = appSettings.getInt("sound", 1);
		
		if (sound == 1){				// if sound is turned on 
			bells = MediaPlayer.create(splash.this, R.raw.christmas_bells);
			Thread timer = new Thread(){
				public void run(){
					try{
						bells.start();	// start the sound
						sleep(1500);	// do nothing for 1.5 seconds
					} catch (InterruptedException e){
						e.printStackTrace();
					} finally {
						Intent openMain = new Intent("com.example.xmasgiftfinder.MAINACTIVITY");	// then open the mainactivity
						startActivity(openMain);
					}
				}
			};
			timer.start();
		}
		
		else{		// if sound is turned off just wait 1 second and start the main activity
			Thread timer = new Thread(){
				public void run(){
					try{
						sleep(1000);
					} catch (InterruptedException e){
						e.printStackTrace();
					} finally {
						Intent openMain = new Intent("com.example.xmasgiftfinder.MAINACTIVITY");
						startActivity(openMain);
					}
				}
			};
			timer.start();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (sound == 1)
		bells.release();
		finish();
	}
	
}
