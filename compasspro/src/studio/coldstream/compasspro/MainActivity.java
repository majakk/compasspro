package studio.coldstream.compasspro;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{
    /** Called when the activity is first created. */
	static int MAX_VALUE = 48; 
	static String TAG = "HELLO";
	
	private SensorManager mSensorManager;
    //private Sensor mSensor;
	
	TextView tv1;
	Thermometer therm;
	float sign;
	
	float[] mValues;   

	String[] dir = {"N","NNE","NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tv1 = (TextView)findViewById(R.id.textView1);
        therm = (Thermometer) findViewById(R.id.thermometer);
	    
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    mSensorManager.registerListener(this,
	            mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
	            SensorManager.SENSOR_DELAY_UI);
        
	    sign = 1.0f;
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	      if(accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
	    	  Log.d(TAG, "Unreliable 1");
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		
		mValues = event.values;
		tv1.setText(dir[0] + " (" + Integer.toString((int)mValues[0]) + ".0º)");
		for(int i = 1; i < 16; i++){
			if(mValues[0] > i*22.5-12.25 && mValues[0] < i*22.5+12.25)
				tv1.setText(dir[i] + " (" + Integer.toString((int)mValues[0]) + ".0º)");				
		}
		//tv1.setText(Integer.toString((int)mValues[0]) + ".0º");
		therm.setHandTarget(-(48.0f/360.0f) * mValues[0]);
			
			if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
				Log.d(TAG, "Unreliable 2");
			/*mAzimuth = Math.round(event.values[0]);
			mPitch = Math.round(event.values[1]);
			mRoll = Math.round(event.values[2]);
			abssum = Math.abs(mAzimuth) + Math.abs(mPitch) + Math.abs(mRoll);
			Log.d(TAG,"onSensorChanged:"+abssum);*/	
			//Log.d(TAG,"onSensorChanged:"+Float.toString(mValues[0]));
			
		
         
   }
    
    
    
}