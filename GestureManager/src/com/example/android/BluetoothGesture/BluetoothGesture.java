
package com.example.android.BluetoothGesture;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity.
 */
public class BluetoothGesture extends Activity{
	
	private GestureListener listener;

	// Debugging
    private static final String TAG = "BluetoothGesture";
    private static final boolean D = true;

    // Message types sent from the BluetoothGestureService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
  
    // Key names received from the BluetoothGestureService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
  
   
    // Layout Views
    private TextView mTitle;
    
   
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothGestureService mGestureService = null;

  

    public BluetoothGesture(Activity hostActivity) {
		super();

	       // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(hostActivity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }// TODO Auto-generated constructor stub
	}
    


    public void setListener(GestureListener listener) {
		this.listener = listener;
	}

    
    public void start() {

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mGestureService == null) setupRemote();
        }
    }

    public synchronized void resume() {
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mGestureService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mGestureService.getState() == BluetoothGestureService.STATE_NONE) {
              // Start the Bluetooth chat services
              mGestureService.start();
             
            }
        }
    }

    private void setupRemote() {
    
        // Initialize the BluetoothGestureService to perform bluetooth connections
        mGestureService = new BluetoothGestureService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        
        
    }


    public void destroy() {
        // Stop the Bluetooth services
        if (mGestureService != null) mGestureService.stop();
    }

    void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    public void sendMsg(String message) {
        // Check that we're actually connected before trying anything
        if (mGestureService.getState() != BluetoothGestureService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothGestureService to write
            byte[] send = message.getBytes();
            mGestureService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
          
        }
    }

    // The Handler that gets information back from the BluetoothGestureService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothGestureService.STATE_CONNECTED:
//                    mTitle.setText(R.string.title_connected_to);
//                    mTitle.append(mConnectedDeviceName);
                  //  mConversationArrayAdapter.clear();
                    break;
                case BluetoothGestureService.STATE_CONNECTING:
//                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothGestureService.STATE_LISTEN:
            
                case BluetoothGestureService.STATE_NONE:
//                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                /*byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                */
                break;
            case MESSAGE_READ:

                byte[] readBuf = (byte[]) msg.obj;
                int m;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                m=Integer.parseInt(readMessage);
                invokeAction(listener,m);
                
                break;
            
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }

		private void showToast(String readMessage) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
		}
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mGestureService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupRemote();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void scan(){
        // Launch the DeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    
	
    //Invokes the appropriate user-implemented method for the gesture performed
    void invokeAction(GestureListener gListener, int msg)
    {
    	if(gListener == null)
    		return;
    	
    	switch(msg)
    	{
    	case 1:
    		gListener.topUp();
    		break;
    	case 2:
    		gListener.bottomUp();
    		break;
    	case 3:
    		gListener.leftUp();
    		break;
    	case 4:
    		gListener.rightUp();
    		break;
    	case 5:
    		gListener.leftSteer();
    		break;
    	case 6:
    		gListener.rightSteer();
    		break;
    	case 7:
    		gListener.forwardFlip();
    		break;
    	case 8:
    		gListener.backFlip();
    		break;
    		
    		
    	}
    }


}