package com.example.android.BluetoothGesture;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.style.SuperscriptSpan;

/**
 * Android Orientation Sensor Manager Archetype
 * @author antoine vianey
 * under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class OrientationManager {
	
	private static Sensor sensor;
	private static SensorManager sensorManager;
	// you could use an OrientationListener array instead
	// if you plans to use more than one sender
	private static BluetoothGesture sender;
	
	/** indicates whether or not Orientation Sensor is supported */
	private static Boolean supported;
	/** indicates whether or not Orientation Sensor is running */
	private static boolean running = false;
	
	private static long lastUpdate=-1;
	private static float prev_azimuth,prev_pitch,prev_roll=-1,diff;
	
	/** Sides of the phone */
	enum Side {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT;
	}
	
	/**
	 * Returns true if the manager is listening to orientation changes
	 */
	public static boolean isListening() {
		return running;
	}
	
	/**
	 * Unregisters listeners
	 */
	public static void stopListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
			}
		} catch (Exception e) {}
	}
	
	/**
	 * Returns true if at least one Orientation sensor is available
	 */
	public static boolean isSupported() {
		if (supported == null) {
			if (GestureRemote.getContext() != null) {
				sensorManager = (SensorManager) GestureRemote.getContext()
						.getSystemService(Context.SENSOR_SERVICE);
				List<Sensor> sensors = sensorManager.getSensorList(
						Sensor.TYPE_ORIENTATION);
				supported = new Boolean(sensors.size() > 0);
			} else {
				supported = Boolean.FALSE;
			}
		}
		return supported;
	}
	
	/**
	 * Registers a sender and start listening
	 */
	public static void startListening(
			BluetoothGesture connector) {
		sensorManager = (SensorManager) GestureRemote.getContext()
				.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager.getSensorList(
				Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0) {
			sensor = sensors.get(0);
			running = sensorManager.registerListener(
					sensorEventListener, sensor, 
					SensorManager.SENSOR_DELAY_FASTEST);
			sender = connector;
		}
	}

	
	/**
	 * The sender that listen to events from the orientation sender
	 */
	private static SensorEventListener sensorEventListener = 
		new SensorEventListener() {
		
		/** The side that is currently up */
		private Side currentSide = null;
		private Side oldSide = null;
		private float azimuth;
		private float pitch;
		private float roll;
		
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
			
			public void onSensorChanged(SensorEvent event) {
				
				
				
				long curTime=System.currentTimeMillis(); 
				
				if(lastUpdate==-1 ||(curTime-lastUpdate)> 500)
				{
					lastUpdate=curTime;
				azimuth = event.values[0]; 	// azimuth
				pitch = event.values[1]; 	// pitch
				roll = event.values[2];		// roll
				
				if (pitch < -45 ) {
					// top side up
					currentSide = Side.TOP;
				} else if (pitch > 45 ) {
					// bottom side up
					currentSide = Side.BOTTOM;
				}
				else if (roll > 45 && prev_roll < 45) {
					// right side up
					sender.sendMsg("4");
					currentSide = Side.RIGHT;
				} else if (roll < -45 && prev_roll > -45) {
					// left side up
					sender.sendMsg("3");
					currentSide = Side.LEFT;
				}
				
				if (pitch < -110 && prev_pitch >-45) {
					// Back Flip
					sender.sendMsg("8");
				} else if (prev_pitch < -110 && pitch >-45) {
					// Fwd Flip
					sender.sendMsg("7");
				}
				
			
				
				
				if(prev_azimuth==-1)
				{
					prev_azimuth=azimuth;
					
					
					
				}
				else
				{
					if(prev_azimuth<azimuth)
					{
							if(azimuth-prev_azimuth > 180)
								diff=-((360-azimuth)+prev_azimuth);
							else
								diff=azimuth-prev_azimuth;
					}
					else if(prev_azimuth > azimuth)
					{
							if(prev_azimuth-azimuth >180)
								diff=(360-prev_azimuth)+azimuth;
							else
								diff=azimuth-prev_azimuth;
					
					}
					
					if(diff>45 && diff <90)
					{
						sender.sendMsg("6");
					}
					else if (diff > -90 && diff < -45)
					{
						sender.sendMsg("5");
					
					}
				}
				prev_roll=roll;
				prev_pitch=pitch;
				prev_azimuth=azimuth;
				
				if (currentSide != null && !currentSide.equals(oldSide)) {
					switch (currentSide) {
						case TOP : 
							sender.sendMsg("1");
							break;
						case BOTTOM : 
							sender.sendMsg("2");
							break;
						case LEFT: 
							//listener.onLeftUp();
							break;
						case RIGHT: 
							//listener.onRightUp();
							break;
					}
					oldSide = currentSide;
				}
				
				
			
			}
			
		};		
		
	};

}