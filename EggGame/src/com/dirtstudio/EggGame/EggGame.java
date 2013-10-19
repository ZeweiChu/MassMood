package com.dirtstudio.EggGame;

import com.dirtstudio.EggGame.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class EggGame extends Activity {
    /** Called when the activity is first created. */
	
	/** Four buttons on the start screen.
	 *  btn1 for Hard Level; btn4 for Easy Level; btn2 for Help; btn3 for Exit.*/
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private final static String TAG="EggGame";
	private final static int REQUEST_CODE = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /**Initialize the main screen, four buttons as well as their listeners.*/
        setContentView(R.layout.main);        
        btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn1.setOnClickListener(listener);
		btn2.setOnClickListener(listener2);
		btn3.setOnClickListener(listener3);
		btn4.setOnClickListener(listener4);
    }
    
	/** Click btn1 for Hard Level.
	 *  Game Level determined by the intent sent to the EggActivity.
	 *  20 for Hard, 10 for Easy.*/    
    private OnClickListener listener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(EggGame.this,EggActivity.class);
			intent.setData(Uri.parse("20"));
			startActivityForResult(intent, REQUEST_CODE);
		}
	};
	
	/** Click btn4 for Easy Level*/
    private OnClickListener listener4=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(EggGame.this,EggActivity.class);
			intent.setData(Uri.parse("10"));
			startActivityForResult(intent, REQUEST_CODE);
		}
	};
	
	/** btn2 for Help*/
    private OnClickListener listener2=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String str="Shake your phone to break the eggs!";
			Toast.makeText(EggGame.this, str, Toast.LENGTH_LONG).show();
		}
	};
	
	/**btn3 for Exit*/
	private OnClickListener listener3=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.exit(0);
		}
	};
    
    @Override
	public void onStart(){
		super.onStart();
		Log.i(TAG, "EggGame-->onStart");
	}
	@Override
	public void onResume(){
		super.onResume();
		Log.i(TAG, "EggGame-->onResume");
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.i(TAG, "EggGame-->onPause");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	public void onStop(){
		super.onStop();
		Log.i(TAG, "EggGame-->onStop");
	}
	@Override
	public void onRestart(){
		super.onRestart();
		Log.i(TAG, "EggGame-->onRestart");
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.i(TAG, "EggGame-->onDestroy");
	}
}