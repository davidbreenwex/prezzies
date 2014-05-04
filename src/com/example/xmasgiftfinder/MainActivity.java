package com.example.xmasgiftfinder;


import java.util.Locale;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;


public class MainActivity extends Activity {
	
	// Declare Variables
	String gender, lowerAge, higherAge, orderBy;
	RadioGroup genderGroup;
	RadioButton genderButton;
	Spinner ageSpinner, sortSpinner;
	int onCreateLanguage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// check the language and store in onCreateLanguage
		SharedPreferences appSettings = this.getSharedPreferences("appSettings", MODE_PRIVATE);
		onCreateLanguage = appSettings.getInt("language", 1);
		
		if (onCreateLanguage==2){						// if french
			Locale locale = new Locale("fr_FR"); 		// set the default locale to french_france
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			this.getApplicationContext().getResources().updateConfiguration(config, null);
		}
		else {											// if english
			Locale locale = new Locale("en_IE"); 		// set to english_ireland
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			this.getApplicationContext().getResources().updateConfiguration(config, null);
		}
		
		setContentView(R.layout.activity_main);
		
		// initialise the variables to their views
		ageSpinner = (Spinner) findViewById(R.id.ageSpinnerMain);
		sortSpinner = (Spinner) findViewById(R.id.sortSpinnerMain);
		genderGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapterAge = ArrayAdapter.createFromResource(this,
		        R.array.age_range_array, android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(this,
		        R.array.sort_by_array, android.R.layout.simple_spinner_dropdown_item);

		// Specify the layout to use when the list of choices appears
		adapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		ageSpinner.setAdapter(adapterAge);
		sortSpinner.setAdapter(adapterSort);
			
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_settings:
			Intent i = new Intent(MainActivity.this, Preferences.class);
			startActivity(i);
			return true;
		
		case R.id.action_suggest:
			Intent j = new Intent(MainActivity.this, SuggestProduct.class);
			startActivity(j);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// change background to the one selected
		RelativeLayout parentView = (RelativeLayout) findViewById(R.id.mainWrapper);
		Preferences.setBackground(parentView, this);
		
		// check to see if the language has been changed since onCreate, restart activity if it has
		SharedPreferences appSettings = this.getSharedPreferences("appSettings", MODE_PRIVATE);
		int onResumeLanguage = appSettings.getInt("language", 1);
		
		// if the language has been changed since the activity has been created, e.g. in another activity
		if (onCreateLanguage != onResumeLanguage){
			Intent intent = getIntent();
			finish();
			startActivity(intent);				// restart this activity, will update the language
		}

	}
	
	public void suggestGifts(View v){			// called when suggestion is clicked in option menu
		Intent i = new Intent(MainActivity.this, SuggestProduct.class);
		startActivity(i);
	}
	
	public void startPrefs(View v){				// called when settings is clicked in option menu
		Intent i = new Intent(MainActivity.this, Preferences.class);
		startActivity(i);
	}
	
	// function called when the find a present button is pressed set in the onclick attribute in xml
	public void findGifts(View v){
		Intent openResults = new Intent(MainActivity.this, SearchResults.class);	// initialise the intent to open the next next activity
		
		getValues();								// function to get the user inputs from the radio buttons and spinners and store them in variables
		Bundle values = new Bundle();				// put the variables in a bundle
		values.putString("gender", gender);
		values.putString("lowerAge", lowerAge);
		values.putString("higherAge", higherAge);
		values.putString("orderBy", orderBy);
		openResults.putExtras(values);			// put the bundle into the intent
		startActivity(openResults);				// open the ReadEmail activity
	}
	
	private void getValues() {
		// Get user choice of gender
		int selectedGender = genderGroup.getCheckedRadioButtonId();
		if (selectedGender == R.id.male)
			gender = "male";
		else
			gender = "female";
		
		// Get user choice of sort order
		String orderChoice = sortSpinner.getSelectedItem().toString();
		if (orderChoice.equals("Price: High to Low") || orderChoice.equals("Prix Ascendant"))
			orderBy =  "price DESC";
		else if (orderChoice.equals("Price: Low to High") || orderChoice.startsWith("Prix D"))
			orderBy = "price";
		else if (orderChoice.equals("Highest Rating") || orderChoice.equals("Note Moyenne"))
			orderBy = "rating";
		
		// Get user choice of Age range from the spinner
		String ageRange = ageSpinner.getSelectedItem().toString();
		if (ageRange.startsWith("0 ")){
			lowerAge = "0"; 
			higherAge= "3";
		}
		else if (ageRange.startsWith("4 ")){
			lowerAge = "4"; 
			higherAge= "6";
		}
		
		else if (ageRange.startsWith("7 ")){
			lowerAge = "7"; 
			higherAge= "9";
		}
		
		else if (ageRange.startsWith("10")){
			lowerAge = "10"; 
			higherAge= "14";
		}
		
		else if (ageRange.startsWith("15")){
			lowerAge = "15"; 
			higherAge= "18";
		}
		
		else if (ageRange.startsWith("19")){
			lowerAge = "19"; 
			higherAge= "24";
		}
		
		else if (ageRange.startsWith("25")){
			lowerAge = "25"; 
			higherAge= "30";
		}
		
		else if (ageRange.startsWith("31")){
			lowerAge = "31"; 
			higherAge= "40";
		}
		
		else if (ageRange.startsWith("41")){
			lowerAge = "41"; 
			higherAge= "55";
		}
		
		else if (ageRange.startsWith("55")){
			lowerAge = "55"; 
			higherAge= "150";
		}			
	}
}