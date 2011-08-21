package com.example.android.BluetoothGesture;

import android.app.Activity;

public class GestureManager {
	private BluetoothGesture connector;
	
	public GestureManager(Activity activity) {
		super();
		connector=new BluetoothGesture(activity);
		// TODO Auto-generated constructor stub
	}
	
	public void register(GestureListener listener){
		connector.setListener(listener);
	}

	public void start() {
        connector.start();
		
	}

	public void resume() {
        connector.resume();
	}

	public void destroy() {
         connector.destroy();
	}
	

}
