package com.gaga.project;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class newA extends Activity implements OnClickListener {
	
	
	private static final String TAG = "MyPost";
	
	private boolean post_is_running = false;
	
	private doSomethingDelayed doSth;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        setContentView(R.layout.main);
        
        Button pushButton = (Button) findViewById(R.id.push_button);
        pushButton.setOnClickListener(this);
        
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if(post_is_running){ // stop async task if it's running if app gets paused
			Log.v(TAG, "Stopping Async Task onPause");
			doSth.cancel(true);
		}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
		if(post_is_running) { // start async task if it was running previously and was stopped by onPause()
			Log.v(TAG, "Starting Async Task onResume");
			doSth = (doSomethingDelayed) new doSomethingDelayed().execute();
			((Button) findViewById(R.id.push_button)).setText("Resuming..");
		}
    }
    
	public void onClick(View v) {
		if(post_is_running == false) {
			post_is_running = true;
			Log.v(TAG, "Starting Async Task onClick");
			doSth = (doSomethingDelayed) new doSomethingDelayed().execute();
			

			((Button) findViewById(R.id.push_button)).setText("Starting..");
		}
		else {
			Log.v(TAG, "Stopping Async Task onClick");
			post_is_running = false;
			doSth.cancel(true);
			((Button) findViewById(R.id.push_button)).setText("Stopping..");
		}
	}
    
    private class doSomethingDelayed extends AsyncTask<Void, Integer, Void> {
    	
    	private int num_runs = 0;
    	
		@Override
		protected Void doInBackground(Void... gurk) {
			
			while(!this.isCancelled()) {
		        Log.v(TAG, "going into postData");
		        
		        
		        long ms_before = SystemClock.uptimeMillis();
		        Log.v(TAG, "Time Now is " + ms_before);
		        
		        postData();


		        long ms_after = SystemClock.uptimeMillis();
		        
		        long time_passed = ms_after - ms_before;
		        
		        Log.v(TAG, "coming out of postData");
		        Log.i(TAG,  "RTT: " + time_passed + " ms");
		        
		        num_runs++;
		        
		        // publish to UI
		        if(!this.isCancelled()) {
		        	publishProgress(num_runs, (int) time_passed);
		        }
		        
//		        try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					Log.e(TAG, e.toString());
//				}
			}
			return null;
		}

		
		@Override
		protected void onCancelled() {
			Context context = getApplicationContext();
			CharSequence text = "Cancelled BG-Thread";
			int duration = Toast.LENGTH_LONG;
			
			Toast.makeText(context, text, duration).show();
			
			((Button) findViewById(R.id.push_button)).setText("Stopped. Tap to Start!");
		}
		
		@Override
		protected void onProgressUpdate(Integer... num_runs) {
			Context context = getApplicationContext();
			CharSequence text = "Looped " + num_runs[0].toString() + " Times";
			int duration = Toast.LENGTH_SHORT;

			Toast.makeText(context, text + "\nRTT: " + num_runs[1].toString() +" ms", duration).show();
			
			((Button) findViewById(R.id.push_button)).setText(text + "\nTap to Stop");
			
		}
    }
    
    
    /**
     * stupid function that posts hardcoded data to hardcoded address
     */
    
    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost("http://129.132.131.73:8880/form");
        HttpPost httppost = new HttpPost("http://192.168.1.137:8880/form");
//        HttpPost httppost = new HttpPost("http://disney.com/");
//        HttpGet httppost = new HttpGet("http://192.168.1.137:8880/form");

        String resp = null;
        long time_passed = 0;
        try {
        	// create data to POST
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
	        long ms_before = SystemClock.uptimeMillis();
	        
	        // Execute HTTP Post Request
            httpclient.execute(httppost);
            
	        long ms_after = SystemClock.uptimeMillis();
	        time_passed = ms_after - ms_before;
	        
            //resp = response.toString();
            
//            Log.i(TAG, "http post response is: " + response.toString());
            
        } catch (ClientProtocolException e) {
        	Log.e(TAG,e.toString());
        } catch (IOException e) {
        	Log.e(TAG,e.toString());
        }

		Log.i(TAG, "RTT inside post-data: " + time_passed);
		
		//Log.i(TAG, resp.toString());
    } 
    
}    