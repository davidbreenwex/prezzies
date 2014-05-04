package com.example.xmasgiftfinder;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

public class SuggestProduct extends Activity implements OnClickListener {
	
	Button AddButton;
	EditText etName, etDescription, etGender, etAge, etPrice, etLink;
	
	
	
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_product);

		// initialise the views
		AddButton = (Button)findViewById(R.id.add_button);
        etName = (EditText) findViewById(R.id.P_name);
        etDescription = (EditText) findViewById(R.id.P_description);
        etGender = (EditText) findViewById(R.id.P_gender);
        etAge = (EditText) findViewById(R.id.P_age);
        etPrice = (EditText) findViewById(R.id.P_price);
        etLink = (EditText) findViewById(R.id.P_link);
        
        AddButton.setOnClickListener(this); 
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
			Intent i = new Intent(SuggestProduct.this, Preferences.class);
			startActivity(i);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public void onClick(View v) {			// onclick of the button
		if (v.getId() == R.id.add_button)
			new executePOST().execute();	// execute the asynchronous httpPost
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TableLayout parentView = (TableLayout) findViewById(R.id.suggestWrapper);
		Preferences.setBackground(parentView, this);
	
}
	
	//class which performs database query in background while displaying a progressDialog then populates the listview
	private class executePOST extends AsyncTask<String, String, JSONObject> {
     
    @Override
    protected JSONObject doInBackground(String... args) {	// called when the previous function finishes
    	
    	try{	// set up the http POST
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.christmasgiftideas.eu/suggestion.php"); //YOUR PHP SCRIPT ADDRESS 
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("sugName", etName.getText().toString()));		// set the values to go in the POST
            nameValuePairs.add(new BasicNameValuePair("sugDescription", etDescription.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("sugGender", etGender.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("sugAge", etAge.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("sugPrice", etPrice.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("sugLink", etLink.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));			// put the values in the POST

            httpclient.execute(httppost);					// execute the request
    }
    catch(Exception e){
            Log.e("log_tag", "Error in http connection  "+e.toString());
    }
    	return null;
    }

	@Override
	protected void onPostExecute(JSONObject json) {			// called when the previous function finished
		clearTextviews();									// clear the text views and show a toast
		Toast.makeText(getApplicationContext(), "Your suggestion has been sent. Thank You!!", Toast.LENGTH_LONG).show();
	}

	private void clearTextviews() {							// clears all the edittexts when a suggestion is submitted
	
		etName.setText("");
        etDescription.setText("");
        etGender.setText("");
        etAge.setText("");
        etPrice.setText("");
        etLink.setText("");
		
		
	}
				
  }
}


