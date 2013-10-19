package com.watch.orientationsensor;

import org.apache.http.params.CoreConnectionPNames;

import com.watch.orientationsensor.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenDisplayActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	private SensorManager sensorManager;
	
	private boolean handDown;
	
	private float ZVALUE = (float)7.0;

	private MySensorEventListener mySensorListener = new MySensorEventListener();
	
	private final class MySensorEventListener implements SensorEventListener{

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			String s = new String(String.format("X: %.1f\nY: %.1f\nZ: %.1f", event.values[0], event.values[1], event.values[2]));
//					"X: "+event.values[0]+"; Y: "+event.values[1]+"; Z: "+event.values[2]+";";
			((TextView)findViewById(R.id.xyz)).setText(s);
            if(handDown && event.values[2] > ZVALUE){
                PowerManager powermanager = ((PowerManager)getSystemService(Context.POWER_SERVICE));
            WakeLock wakeLock = powermanager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
            wakeLock.acquire();
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                handDown = false;
            }else if(!handDown && event.values[2] < ZVALUE){
                handDown = true;
            }
			Log.i("SensorData", String.format("X: %.1f\nY: %.1f\nZ: %.1f", event.values[0], event.values[1], event.values[2]));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen_display);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor_ori = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(mySensorListener, sensor_ori, SensorManager.SENSOR_DELAY_UI);
		handDown = false;
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
		
		Intent intentNotify = new Intent();
		PendingIntent intentContent = PendingIntent.getActivity(this, 0, intentNotify, 0);
		
		Notification notification = new Notification.Builder(getBaseContext())
        	.setContentTitle("Orientation Sensor is Running. ")
        	.setContentText("It works, and please keep our secret. ")
        	.setSmallIcon(R.drawable.ic_launcher)
        	.setOngoing(true)
        	.build();
		
		((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1, notification);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	
	public void cancel_notification(View e) {
		((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(1);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	protected void onResume(){
		super.onResume();
	}
	
	protected void onPause(){
		super.onPause();
	}
	
	protected void onDestroy(){
		cancel_notification(null);
		sensorManager.unregisterListener(mySensorListener);
		super.onDestroy();
	}
	
	protected void onStop() {
		super.onStop();
	}
}
