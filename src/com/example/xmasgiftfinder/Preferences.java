package com.example.xmasgiftfinder;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Preferences extends Activity implements OnClickListener{
	// declare global variables
	Button cancelButton, saveButton;
	RadioGroup soundGroup, backgroundGroup, languageGroup;
	RadioButton radioOn, radioOff, radioReindeer, radioSnowman, radioSanta, radioEnglish, radioFrench;
	int soundChoice, backgroundChoice, languageChoice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		initialiseViews();							// function to initialise the buttons, radiobuttons and radiogroups
		checkPrevSettings();						// function to check the previous Shared preferences and initialise the checked radiobuttons to the corrct selection
		cancelButton.setOnClickListener(this);		// set onclick listener for the save and cancel buttons
		saveButton.setOnClickListener(this);
	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LinearLayout parentView = (LinearLayout) findViewById(R.id.prefsWrapper);		// set backround to users choice
		setBackground(parentView, this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId())
		{
		case R.id.action_suggest:
			Intent j = new Intent(Preferences.this, SuggestProduct.class);
			startActivity(j);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_prefs:			// if cancel is clicked exit the activity
			finish();
			break;

		case R.id.save_prefs:			// if save is clicked
			getPrefs();					// get the currently checked radio buttons
			savePrefs();				// save the selections in Saved Preferences
			finish();					// end the activity
			break;
		}	
	}

	private void getPrefs() {			// function to get the currently checked radio buttons
		
		int soundId = soundGroup.getCheckedRadioButtonId();
		if (soundId == R.id.onRadio)
			soundChoice = 1;
		else if (soundId == R.id.offRadio)
			soundChoice = 2;
		
		int backgroundId = backgroundGroup.getCheckedRadioButtonId();
		if (backgroundId == R.id.reindeerRadio)
			backgroundChoice = 1;
		else if (backgroundId == R.id.snowmanRadio)
			backgroundChoice = 2;
		else if (backgroundId == R.id.santaRadio)
			backgroundChoice = 3;
		
		int languageId = languageGroup.getCheckedRadioButtonId();
		if (languageId == R.id.englishRadio)
			languageChoice = 1;
		else if (languageId == R.id.frenchRadio)
			languageChoice = 2;
	}
	
	private void savePrefs() {				// function to save the preferences in Saved Preferences
		SharedPreferences appSettings = this.getSharedPreferences("appSettings", MODE_PRIVATE); // get reference to the shared alarm settings
		Editor editor = appSettings.edit(); // create an editor
		editor.putInt("sound", soundChoice); 
		editor.putInt("background", backgroundChoice);
		editor.putInt("language", languageChoice);
		editor.commit(); // commit the changes
		
		int language = appSettings.getInt("language", 1);	// set the default locale/language to the one selected by the user
		if (language==2){
			Locale locale = new Locale("fr_FR"); 
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			this.getApplicationContext().getResources().updateConfiguration(config, null);
		}
		else {
			Locale locale = new Locale("en_IE"); 
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			this.getApplicationContext().getResources().updateConfiguration(config, null);
		}
		
	}

	// initialise the views to the corrisponding views in the xml layout file
	private void initialiseViews() {
		cancelButton = (Button) findViewById(R.id.cancel_prefs);
		saveButton = (Button) findViewById(R.id.save_prefs);
		soundGroup = (RadioGroup) findViewById(R.id.soundGroup);
		backgroundGroup = (RadioGroup) findViewById(R.id.backgroundGroup);
		languageGroup = (RadioGroup) findViewById(R.id.languageGroup);
		radioOn = (RadioButton) findViewById(R.id.onRadio);
		radioOff = (RadioButton) findViewById(R.id.offRadio);
		radioReindeer = (RadioButton) findViewById(R.id.reindeerRadio);
		radioSnowman = (RadioButton) findViewById(R.id.snowmanRadio);
		radioSanta = (RadioButton) findViewById(R.id.santaRadio);
		radioEnglish = (RadioButton) findViewById(R.id.englishRadio);
		radioFrench = (RadioButton) findViewById(R.id.frenchRadio);
	}

	private void checkPrevSettings() {		// function called oncreate which checks the current preferences and sets the radio buttons to correct initial setting
		SharedPreferences appSettings = this.getSharedPreferences("appSettings", MODE_PRIVATE); // get reference to the shared alarm settings
		int	sound = appSettings.getInt("sound", 1);
		int	background = appSettings.getInt("background", 1);
		int	language = appSettings.getInt("language", 1);
		
		if (sound == 1)
			radioOn.setChecked(true);
		else
			radioOff.setChecked(true);
		
		if (background == 1)
			radioReindeer.setChecked(true);
		else if (background == 2)
			radioSnowman.setChecked(true);
		else
			radioSanta.setChecked(true);
		
		if (language == 1)
			radioEnglish.setChecked(true);
		else
			radioFrench.setChecked(true);
		
	}
									// takes the parentview of the activity and the context as parameters
	public static void setBackground(View v, Context context) {		// public function called by other activities to check the users preference and set the background to that image
		SharedPreferences appSettings = context.getSharedPreferences("appSettings", MODE_PRIVATE); // get reference to the shared alarm settings
		int background = appSettings.getInt("background", 1); // get the colour string from sharedPreferences
		switch (background) {
		case 1:
			v.setBackgroundResource(R.drawable.reindeer_with_tree_200_trans2); 
			break;

		case 2:
			v.setBackgroundResource(R.drawable.snowman_portrait2);
			break;
		
		case 3:
			v.setBackgroundResource(R.drawable.santa_portrait1);
			break;
		}
	}
}
