package com.example.xmasgiftfinder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

public class SearchResults extends Activity {
	
	// declare global variables
	int language;
	String gender, lowerAge, higherAge, orderBy, jsonString;
    ArrayList<HashMap<String, String>> giftList = new ArrayList<HashMap<String, String>>();	// used in populating listview

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);	// set the layout to be used
		
		if (Locale.getDefault().getDisplayLanguage().startsWith("fr"))		// if the language id french
			language = 2;													// set the language code to 2
		else
			language = 1;													// otherwise set to 1 for english
		
		Bundle values = getIntent().getExtras();	// get the values from the bundle
		gender = values.getString("gender");		// gender chosen
		lowerAge = values.getString("lowerAge");	// lower age range
		higherAge = values.getString("higherAge");	// higher age range
		orderBy = values.getString("orderBy");		// order by 
		
		new JSONParse().execute();					// execute the Asynchronous task, needed for progress bar. Query database, populate listview
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
			Intent i = new Intent(SearchResults.this, Preferences.class);
			startActivity(i);
			return true;
		
		case R.id.action_suggest:
			Intent j = new Intent(SearchResults.this, SuggestProduct.class);
			startActivity(j);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RelativeLayout parentView = (RelativeLayout) findViewById(R.id.resultsWrapper);
		Preferences.setBackground(parentView, this);
		
		// check to see if the language has been changed since onCreate, restart activity if it has
		SharedPreferences appSettings = this.getSharedPreferences("appSettings", MODE_PRIVATE);
		int onResumeLanguage = appSettings.getInt("language", 1);
				
		if (language != onResumeLanguage){
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
		
	}

	// class which performs database query in background while displaying a progressDialog then populates the listview
	private class JSONParse extends AsyncTask<String, String, JSONObject> {
        
		private ProgressDialog pDialog;		// declare the progress dialog variable
       
		@Override
       protected void onPreExecute() {							// function called before the task executes
           super.onPreExecute();	
           pDialog = new ProgressDialog(SearchResults.this);	// set the context
           pDialog.setMessage("Finding Gifts...");				// set the message to be shown
           pDialog.setIndeterminate(false);						// the progress not shown in percent
           pDialog.setCancelable(true);							// if the user presses back, the process is canceled
           pDialog.show();										// show the dialog box
       }

       @Override
       protected JSONObject doInBackground(String... args) {	// called when the previous function finishes
    	   jsonString = queryDatabase();						// query the database and store the json string in a global variable
    	   downloadImages(jsonString);
    	   JSONObject obj = null;								// needs to return a JSONObject
    	   return obj;											// return the null object
       }

		@Override
        protected void onPostExecute(JSONObject json) {			// called when the previous function finishes
            pDialog.dismiss();									// dismiss the dialog box
            populateListView(jsonString);						// function to parce the json string and populate listview
        }
		
        private void downloadImages(String jsonString) {
    		try {
    			JSONArray jArray = new JSONArray(jsonString);				// convert the string to a json array
    			   
    			for(int i=0; i<jArray.length();i++){						// iterate through the array (i.e. row by row)
    				JSONObject resultArray = jArray.getJSONObject(i);		// for each row convert to a json object

    				String id = resultArray.getString("id");				// store the values in variables
    				String imagePath = getFilesDir() + "/" + id + ".jpg";	// initialise the image path to check if already exists
    				File file = new File(imagePath);						// check if already exists
	    	   	    
    				if (!file.exists()){									// if it doesnt exist: download and store it
    					
	    	   	    	String imageUrl = "http://www.christmasgiftideas.eu/images/" + id + ".jpg";		// initialise the url on the server
	    	   	    	URL newUrl = null;																// encode theurl
	    	   	    	try {
	    	   	    		newUrl = new URL(imageUrl);							// parse the image url
        	   			} 
        	    	   	catch (MalformedURLException e) {
        	    	   		Log.e("error", "DisplayProduct.java doInBackground");
        	    	   		e.printStackTrace();
        	    	   	}
        	   			
	   	   	    		try {
       	    	   	    	BitmapFactory.Options options=new BitmapFactory.Options();	// change options for the download
       	    	   	    	options.inSampleSize = 3;						// makes the download one fifth the actual image size
       	    	   	    	Bitmap giftImg = BitmapFactory.decodeStream(newUrl.openConnection() .getInputStream(), null, options); // download the image
       	    	   	    	bitmapToFile(giftImg, id);						// call the function to save the file to internal memory
       	    	   	    }
	   	   	    		catch (IOException e) {
	    	   	    		Log.e("error", "downloadImages() doInBackground");
	    	   	   			e.printStackTrace();
	    	   	   		}
	    	   	    }
     			}			   
    			
    		} catch (Exception e) {
    			// TODO: handle exception
    			Log.e("log_tag", "Error in download Images "+e.toString());
    		}
		
        }
	}
	
	// function to query the database and return results as a json String
	private String queryDatabase() {
		
		String result = "";										// will hold the json string to be returned
    	InputStream isr = null;									// used when converting the response to a string
    	
    	try{	// set up the http POST
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.christmasgiftideas.eu/queryAll.php"); //YOUR PHP SCRIPT ADDRESS 
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("lowerAge", lowerAge));		// set the values to go in the POST
            nameValuePairs.add(new BasicNameValuePair("higherAge", higherAge));
            nameValuePairs.add(new BasicNameValuePair("gender", gender));
            nameValuePairs.add(new BasicNameValuePair("orderBy", orderBy));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));			// put the values in the POST

            HttpResponse response = httpclient.execute(httppost);					// execute the request
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
    }
    catch(Exception e){
            Log.e("log_tag", "Error in http connection  "+e.toString());
    }
    	
    // convert response to string
    try{
    	BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
    	StringBuilder sb = new StringBuilder();
    	String line = null;
    	while ((line = reader.readLine()) != null) {
    		sb.append(line + "\n");
            }
    	isr.close();
        result=sb.toString();
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error  converting result "+e.toString());
    	}
   
    	return result;
}
	
	// parse the json string and populate the list view
	private void populateListView(String result) {
		
		String id, name, price, description, link, totalRating, ratingCount, imgPath;
		try {
			JSONArray jArray = new JSONArray(result);				// convert the string to a json array
			   
			for(int i=0; i<jArray.length();i++){					// iterate through the array (i.e. row by row)
				JSONObject resultArray = jArray.getJSONObject(i);	// for each row convert to a json object

				if(language == 1) {									// if the language is english, use english values
					name = resultArray.getString("engName");
					description =  resultArray.getString("engDesc");
					link = resultArray.getString("engLink");
				}
				else												// if french use french values
				{
					name = resultArray.getString("frName");
					description =  resultArray.getString("frDesc");
					link = resultArray.getString("frLink");
				}
				
				id = resultArray.getString("id");					// store the rest of the values in variables
                price = "€" + resultArray.getString("price");
                totalRating = resultArray.getString("totalRating");
                ratingCount = resultArray.getString("ratingCount");
                imgPath = getFilesDir() + "/" + id + ".jpg";
                                
                
                // Adding value HashMap key => value, used in the list adapter
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", id);		
                map.put("name", name);
                map.put("price", price);
                map.put("description", description);
                map.put("link", link);
                map.put("totalRating", totalRating);
                map.put("ratingCount", ratingCount);
                map.put("imgPath", imgPath);

                giftList.add(map);										// add the hash map to ArrayList
                ListView list=(ListView)findViewById(R.id.listView);	// assign the variable to the listview in the xml layout 

                ListAdapter adapter = new SimpleAdapter(				// set up the list adapter to populate the listview
                			SearchResults.this,							// context
                			giftList,									// ArrayList
                			R.layout.list_item,							// xml file for the listview
                			new String[] {"name", "price", "imgPath" }, 			// array of the values to put in
                			new int[] {R.id.listName ,R.id.listPrice, R.id.listImage});	// array of the textViews to populate

                list.setAdapter(adapter);								// set the adapter to the ListView
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override											// when a list item is clicked
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
                        Intent openDisplay = new Intent(SearchResults.this, DisplayProduct.class);	// initialise the intent to open the next next activity
                        
                        Bundle itemValues = new Bundle();				// put the needed values in a bundle
						itemValues.putString("id", giftList.get(+position).get("id"));
						itemValues.putString("name", giftList.get(+position).get("name"));
						itemValues.putString("price", giftList.get(+position).get("price"));
						itemValues.putString("description", giftList.get(+position).get("description"));
						itemValues.putString("link", giftList.get(+position).get("link"));
						itemValues.putString("totalRating", giftList.get(+position).get("totalRating"));
						itemValues.putString("ratingCount", giftList.get(+position).get("ratingCount"));
						
						openDisplay.putExtras(itemValues);				// put the bundle in the intent
						startActivity(openDisplay);						// start the display product
					}
                });
                   
			}			   
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("log_tag", "Error Parsing Data "+e.toString());
			}
	}
	
	public void bitmapToFile(Bitmap bmp, String id) {		// function to save the small bitmaps to internal memory
		try
		{
	     int size = 1;
	     ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
	     bmp.compress(Bitmap.CompressFormat.PNG, 80, bos);
	     byte[] bArr = bos.toByteArray();
	     bos.flush();
	     bos.close();
	 
	     FileOutputStream fos = openFileOutput( id + ".jpg", Context.MODE_PRIVATE);
	     fos.write(bArr);
	     fos.flush();
	     fos.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}