package com.example.xmasgiftfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	// initialise global variables
	RemoteViews v;
	String title, id, name, price,  description, link, totalRating, ratingCount;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		//allow network on main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		// get the number of widgets on the device
		final int N = appWidgetIds.length;
		
		// for each widget on the device
		for(int i = 0; i < N; i++){
			
			int awId = appWidgetIds[i];						// store the appWidget id
			
			String jsonString = queryDatabase();			// query the database and store the returned json string in a global variable
			JSONArray jArray;								// declare a json array, will hold the converted json string			
			try {
				jArray = new JSONArray(jsonString);			// convert jsonstring to array
					
				JSONObject resultArray = jArray.getJSONObject(0);	// convert first row of the array to json object
																	// database query returns 4 random rows
				
				id = resultArray.getString("id");					// get the data from the random row in the database
				price = resultArray.getString("price");				// store all the strings in global variables
				totalRating = resultArray.getString("totalRating");
				ratingCount = resultArray.getString("ratingCount");
				
				// name, description and link change depending on the language so we may check current default language
				int language; 								// variable to store current default language
				SharedPreferences appSettings = context.getSharedPreferences("appSettings",Context.MODE_PRIVATE);
				language = appSettings.getInt("language", 1);		// get the users choice of language from the app, not using default phone locale/language
				
				if(language == 1) {									// if the language is english
					name = resultArray.getString("engName");		// store the english values for name, description and link
					description =  resultArray.getString("engDesc");
					link = resultArray.getString("engLink");
					title = "Gift of The Day"; // set the title of widget to english version
				}
				else												// if french use french values
				{
					name = resultArray.getString("frName");
					description =  resultArray.getString("frDesc");
					link = resultArray.getString("frLink");
					title = "Cadeau Du Jour";	// set the title of widget to french version
				}

				String imageUrl = "http://www.christmasgiftideas.eu/images/" + id + ".jpg";	// set the url for the image
					
				URL newUrl = null;
				Bitmap giftImg = null;
				try {
			   		newUrl = new URL(imageUrl);							// parse the image url
			   		} 
			    catch (MalformedURLException e) {
			    	Log.e("error", "DisplayProduct.java doInBackground");
			   		e.printStackTrace();
			   		}
			   			
			    try {
			   		giftImg = BitmapFactory.decodeStream(newUrl.openConnection() .getInputStream()); // download the image
			       	} 
			    catch (IOException e) {
			   		Log.e("error", "DisplayProduct.java doInBackground");
			   		e.printStackTrace();
			       	} 
			
			    Intent openDisplay = new Intent(context, DisplayProduct.class);	// initialise the intent to open the next next activity
                
                Bundle itemValues = new Bundle();				// put the needed values in a bundle
				itemValues.putString("id", id);
				itemValues.putString("name", name);
				itemValues.putString("price", price);
				itemValues.putString("description", description);
				itemValues.putString("link", link);
				itemValues.putString("totalRating", totalRating);
				itemValues.putString("ratingCount", ratingCount);
				openDisplay.putExtras(itemValues);				// put the bundle in the intent
				
				// pending intent which calls the intent when clicked - passed context, unique identifier, intent, and null
				PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(id), openDisplay, 0);	// declare the pending intent, calls intent onclick of button			    
				
			    v = new RemoteViews(context.getPackageName(),R.layout.activity_my_widget_provider);
			    v.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);  // set the onclick listener for the button to call the pending intent
			
			    v.setImageViewBitmap(R.id.widgetImage, giftImg);			// set the image of the product
			    v.setTextViewText(R.id.widgetProductName, name);			// set the name of the product in the textview
				v.setTextViewText(R.id.widgetTitle, title);					// set the title of the widget	
				} 
			catch (JSONException e) {
					e.printStackTrace();
				}
			
			appWidgetManager.updateAppWidget(awId,v);						// update the widget, set to happen once a day
		}		
	}
	
	// function to query the database and return results as a json String
		private String queryDatabase() {
			
			String result = "";										// will hold the json string to be returned
	    	InputStream isr = null;									// used when converting the response to a string
	    	
	    	try{	// set up the http POST
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost("http://www.christmasgiftideas.eu/widgetQuery.php"); //YOUR PHP SCRIPT ADDRESS 

	            HttpResponse response = httpclient.execute(httppost);					// execute the request and store response
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
	   
	    	return result;		// return the json string
	}
}
