package com.example.android.BluetoothGesture;

/*This interface provides declarations of the various gesture types available to the user.
 * The user must implement this interface.
 */
public interface GestureListener {

	void topUp();
	
	void bottomUp();
	
	void leftUp();
	
	void rightUp();
	
	void leftSteer();
	
	void rightSteer();
	
	void forwardFlip();
	
	void backFlip();
	
}
