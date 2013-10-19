package com.dirtstudio.EggGame;

import com.dirtstudio.EggGame.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class EggActivity extends Activity{
	private final static String TAG="EggActivity";
	
	/** Define MediaPlayer for the sound effect.*/
	private MediaPlayer mPlayer=new MediaPlayer();
	
	/** Img1 to control the upper half of the game play screen emotion.
	 *  Img2 to control the lower half of the game play screen emotion.*/
	private ImageView img1,img2;
	
	/** Memorize accelerometer sensor state*/
	private float x, y, z,last_x,last_y,last_z;
    private long lastUpdate;
    
    /** Counter of Broken eggs*/
    private int counter = 0;
    
    /** four numbers to control the level of the game. ie. the speed of player shaking his mobile phone*/
    int number1;
    int number2;
    int number3;
    int number4;
    
    /** jd has value from 0 to 3. Used to memorize the state of the egg. It will always take four strike to break an egg.*/
    int jd=0;
    int i=0;
    
    /** Limit of broken eggs. 10 for Easy, 20 for Hard.*/
    int limit=0;
    
	private Sensor accelerometer_sensor;
	private SensorEventListener accelerometer_listener;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        
        Intent intent = getIntent();
        
        Uri data_uri = intent.getData();
        
        /** Set the hardness of the game with the intent passed from EggGame.java.*/
        if(data_uri != null)
        {
        	limit = Integer.parseInt(data_uri.toString());
        }
        
        if (limit == 20)
        {
        	number1 = 1000;
        	number2 = 1500;
        	number3 = 2000;
        	number4 = 2500;
        }
        else
        {
        	number1 = 1000;
        	number2 = 1200;
        	number3 = 1400;
        	number4 = 1600;
        }
        
        /** Set Game View.*/
        img1=(ImageView)findViewById(R.id.img1);
        img2=(ImageView)findViewById(R.id.img2);
        SensorManager sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE); 
    	accelerometer_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    	if(accelerometer_sensor != null)
    	{
    		accelerometer_listener = new SensorEventListener()
    		{

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}
				/** Core Part 1*/
				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
					long curTime = System.currentTimeMillis();
					if ((curTime - lastUpdate) >= 200)
					{ 
						long diffTime = (curTime - lastUpdate);  
						lastUpdate = curTime;
						x = event.values[SensorManager.DATA_X];   
						y = event.values[SensorManager.DATA_Y];   
						z = event.values[SensorManager.DATA_Z];
						
						/** Calculate the speed.*/
						float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;
						/** Decide whether to run the core part 2.*/
						if (speed > number1)
						{   
							innt(speed);
						}
						last_x = x;
						last_y = y;
						last_z = z;
					}
				}
    		};
    	}    	
        Toast.makeText(EggActivity.this, "Be very careful when shaking your phone!!",Toast.LENGTH_LONG).show();
    }
	
	/** Core Part 2
	 * Different speed lower bound for different state of the egg. Higher the state, higher the speed requirements.*/
	void innt(float a){
		AnimationDrawable anim = null;
		
		if (a > number4 && i==3) {
			/** The 4th state of the egg
			 *  Change the upper half part of the screen.
			 *  custom designed xml file: da1.xml - da4.xml, for movement control of the upper half.*/
			img1.setBackgroundResource(R.anim.da4);
			anim=(AnimationDrawable)img1.getBackground();
			
			/** Play sound*/
			mPlayer=MediaPlayer.create(EggActivity.this, R.raw.sound_dakai);
			mPlayer.start();
			anim.stop();
			anim.start();
			numberOfBrokenEggs();
			jd++;
			i=0;
			counter++;
		}else if (a > number3 && i==2) {
			/** The 3rd state of the egg*/
			img1.setBackgroundResource(R.anim.da3);
			anim=(AnimationDrawable)img1.getBackground();
			mPlayer=MediaPlayer.create(EggActivity.this, R.raw.sound_da);
			mPlayer.start();
			anim.stop();
			anim.start();
			i=3;
		}else if (a > number2 && i==1) {
			/** The 2nd state of the egg*/
			img1.setBackgroundResource(R.anim.da2);
			anim=(AnimationDrawable)img1.getBackground();
			mPlayer=MediaPlayer.create(EggActivity.this, R.raw.sound_da);
			mPlayer.start();
			anim.stop();
			anim.start();
			i=2;
		}else if(a > number1 && i==0){
			/** The 1st state of the egg*/
			img1.setBackgroundResource(R.anim.da1);
			anim=(AnimationDrawable)img1.getBackground();
			mPlayer=MediaPlayer.create(EggActivity.this, R.raw.sound_da);
			mPlayer.start();
			anim.stop();
			anim.start();
			i=1;
		}
		
		/** Check if game ends.*/
		if (counter >= limit)
		{
			Toast.makeText(EggActivity.this, "Congratulations! You have breaked " + String.valueOf(limit) + " eggs.",Toast.LENGTH_LONG).show();
			finish();
		}
	}
	void numberOfBrokenEggs(){
		/** Change the lower half of the screen show once there is change in the number of broken eggs.*/
		img2.setBackgroundDrawable(getResources().getDrawable(imgkai[jd]));
		
		if(jd==imgkai.length-1){
			jd=0;
		}
	}
	
	public int[] imgkai=new int[]{
		R.drawable.kai_1,
		R.drawable.kai_2,
		R.drawable.kai_3,
		R.drawable.kai_4,
		R.drawable.kai_5,
		R.drawable.kai_6,
		R.drawable.kai_7,
		R.drawable.kai_8,
		R.drawable.kai_9,
		R.drawable.kai_10,
		R.drawable.kai_11,
		R.drawable.kai_12,
		R.drawable.kai_13,
		R.drawable.kai_14,
		R.drawable.kai_15,
	};
	 @Override
	public void onStart(){
		super.onStart();
		Log.i(TAG, "EggActivity-->onStart");
	}
	@Override
	public void onResume()
	{
		super.onResume();
		Log.i(TAG, "EggActivity-->onResume");
		
    	SensorManager sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    	
    	/** Resisger Listener in onResume() to save energy, due to the whole running process of android activity.*/
    	if(accelerometer_listener != null)
    	{
    		sensor_manager.registerListener(accelerometer_listener, accelerometer_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    	}
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.i(TAG, "EggActivity-->onPause");
    	
		SensorManager sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    	
		/** Halt the sensor when screen is locked.*/
    	if(accelerometer_listener != null)
    	{
    		sensor_manager.unregisterListener(accelerometer_listener);
    	}
	}
	@Override
	public void onStop(){
		super.onStop();
		Log.i(TAG, "EggActivity-->onStop");
	}
	@Override
	public void onRestart(){
		super.onRestart();
		Log.i(TAG, "EggActivity-->onRestart");
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.i(TAG, "EggActivity-->onDestroy");
	}
}