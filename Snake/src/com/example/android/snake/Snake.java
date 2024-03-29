/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.snake;

import com.example.android.BluetoothGesture.GestureListener;
import com.example.android.BluetoothGesture.GestureManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * Snake: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the classic Game "Snake", in which you control a
 * serpent roaming around the garden looking for apples. Be careful, though,
 * because when you catch one, not only will you become longer, but you'll move
 * faster. Running into yourself or the walls will end the game.
 * 
 */
public class Snake extends Activity {

    private SnakeView mSnakeView;

	private GestureManager gestureMan;
    
    private static String ICICLE_KEY = "snake-view";

    /**
     * Called when Activity is first created. Turns off the title bar, sets up
     * the content views, and fires up the SnakeView.
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.snake_layout);
        mSnakeView = (SnakeView) findViewById(R.id.snake);
        mSnakeView.setTextView((TextView) findViewById(R.id.text));

        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
            mSnakeView.setMode(SnakeView.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mSnakeView.restoreState(map);
            } else {
                mSnakeView.setMode(SnakeView.PAUSE);
            }
        }
        
        setGestureManager(mSnakeView);

    }
    
    @Override
    public void onStart() {
        super.onStart();
        gestureMan.start();

    }    
    
    @Override
    public synchronized void onResume() {
        super.onResume();
        gestureMan.resume();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        gestureMan.destroy();
    }
    

    private class GestureReceiver implements GestureListener{

		private SnakeView view;

		public GestureReceiver(SnakeView snakeView) {
			this.view=snakeView;
		}

		@Override
		public void backFlip() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void bottomUp() {
			view.downPressed();
		}

		@Override
		public void forwardFlip() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void leftSteer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void leftUp() {
			view.leftPressed();
		}

		@Override
		public void rightSteer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void rightUp() {
			view.rightPressed();
			
		}

		@Override
		public void topUp() {
			view.upPressed();
			
		}
    	
    }
	private void setGestureManager(SnakeView snakeView) {
		this.gestureMan=new GestureManager(this);
		GestureListener listener=new GestureReceiver(snakeView);
        gestureMan.register(listener);
	}

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mSnakeView.setMode(SnakeView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }

}
