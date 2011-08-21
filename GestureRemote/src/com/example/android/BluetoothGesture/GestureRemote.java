package com.example.android.BluetoothGesture;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class GestureRemote extends Activity{
    // Layout Views
    private TextView mTitle;
    
   private BluetoothGesture connector;
    
	private static Context CONTEXT;
	
	private int calls;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main2);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

    

        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        //mTitle = (TextView) findViewById(R.id.title_right_text);
    
        connector =new BluetoothGesture(this);
        CONTEXT = this;
 
    }
    
    
    @Override
    public void onStart() {
        super.onStart();
      connector.start();
    }    
    
    @Override
    public synchronized void onResume() {
        super.onResume();
       	if (OrientationManager.isSupported()) {
    		OrientationManager.startListening(connector);
    	}
        connector.resume();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    	if (OrientationManager.isListening()) {
    		OrientationManager.stopListening();
    	}
         connector.destroy();
    }
    
    public static Context getContext() {
		return CONTEXT;
	}


	    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
        	connector.scan();
        	return true;
        case R.id.discoverable:
        	connector.ensureDiscoverable();
            return true;
        }
        return false;
    }
	

}
