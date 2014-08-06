package studio.coldstream.compasspro;

/*
* Add settings
* Add Wakelock option
* Add rotate adjustment in steps of 90 degrees (for certain tablets and alike)
* Check landscape and other phones
* Improve precision
*
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity implements SensorEventListener{
    /** Called when the activity is first created. */
	static String TAG = "HELLO";

    //PowerManager pm;
    //PowerManager.WakeLock wl;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    CustomDrawableView mCustomDrawableView;
    float[] mGravity;
    float[] mGeomagnetic;
    Float azimut, old_azimut;  // View to draw a compass

    Boolean wakelock_flag;
    int rotation_flag = 0;

	String[] dir = {"N","NNE","NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        /*pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
        wl.acquire();*/



    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	      if(accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
	    	  Log.d(TAG, "Unreliable 1");
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                azimut = (azimut - ((float)Math.PI/2) * rotation_flag);
            }
        }
        if(azimut != old_azimut)
            mCustomDrawableView.invalidate();

        old_azimut = azimut;
			
		if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			Log.d(TAG, "Unreliable 2");

   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_wakelock) {
            Log.d(TAG,"Wakelock Pressed");
            item.setChecked(!item.isChecked());
            wakelock_flag = item.isChecked();
            if(item.isChecked())
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            return true;
        }
        if (id == R.id.r0) {
            Log.d(TAG,"R0 Pressed");
            item.setChecked(true);
            rotation_flag = 0;
        }
        if (id == R.id.r90) {
            Log.d(TAG,"R90 Pressed");
            item.setChecked(true);
            rotation_flag = 1;
        }
        if (id == R.id.r180) {
            Log.d(TAG,"R180 Pressed");
            item.setChecked(true);
            rotation_flag = 2;
        }
        if (id == R.id.r270) {
            Log.d(TAG,"R270 Pressed");
            item.setChecked(true);
            rotation_flag = 3;
        }

        mCustomDrawableView.invalidate();

        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        /* may as well just finish since saving the state is not important for this toy app */
        //wl.release();
        //Log.v(tag, "Released wlock??");

        finish();
        super.onStop();
    }


    public class CustomDrawableView extends View {
        Paint paint = new Paint();
        Path path = new Path();

        private Paint handPaint1;
        private Paint handPaint2;
        private Paint handPaint3;
        private Paint handPaint4;
        Path handPath1 = new Path();
        Path handPath2 = new Path();
        Path handPath3 = new Path();
        Path handPath4 = new Path();
        private Paint handScrewPaint;

        private float az;

        private String dirtext;

        public CustomDrawableView(Context context) {
            super(context);
            paint.setColor(0xffffffff);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);

            initDrawingTools();
        };

        private void initDrawingTools() {
            handPaint1 = new Paint();
            handPaint1.setAntiAlias(true);
            handPaint1.setColor(0xffff6666);
            handPaint1.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
            handPaint1.setStyle(Paint.Style.FILL);

            handPaint2 = new Paint();
            handPaint2.setAntiAlias(true);
            handPaint2.setColor(0xffff4444);
            handPaint2.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
            handPaint2.setStyle(Paint.Style.FILL);

            handPaint3 = new Paint();
            handPaint3.setAntiAlias(true);
            handPaint3.setColor(0xffcccccc);
            handPaint3.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
            handPaint3.setStyle(Paint.Style.FILL);

            handPaint4 = new Paint();
            handPaint4.setAntiAlias(true);
            handPaint4.setColor(0xff999999);
            handPaint4.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
            handPaint4.setStyle(Paint.Style.FILL);

            handScrewPaint = new Paint();
            handScrewPaint.setAntiAlias(true);
            handScrewPaint.setColor(0xff493f3c);
            handScrewPaint.setStyle(Paint.Style.FILL);
        }

        protected void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;

            paint.setColor(0xffeeeeee);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0,0,width,height,paint);

            paint.setColor(Color.DKGRAY);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);
            canvas.drawPaint(paint);

            //Cross
            canvas.drawLine(centerx, 0, centerx, height, paint);
            canvas.drawLine(0, centery, width, centery, paint);

            //Circle rim
            canvas.drawCircle(centerx, centery, 0.42f * width, paint);
            canvas.drawCircle(centerx, centery, 0.38f * width, paint);
            //canvas.rotate(11.25f, centerx, centery);
            paint.setTextSize(30);
            paint.setFakeBoldText(false);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            //canvas.rotate(180,centerx,centery);
            for (int i = 0; i < 16; i++) {
                canvas.rotate(-3.5f,centerx,centery);
                if (i != 0 && i != 4 && i != 8 && i != 12) {

                    canvas.drawText(Float.toString(i * 22.5f) + "º", centerx, centery - 0.44f * width, paint);
                }
                canvas.rotate(26.0f,centerx,centery);
            }
            //canvas.rotate(180,centerx,centery);

            for (int i = 0; i < 32; i++) {
                if(i % 2 == 0) {
                    paint.setStrokeWidth(8);
                    canvas.drawLine(centerx, centery + 0.38f * width, centerx, centery + 0.42f * width, paint);

                }
                if (i % 4 == 0){
                    paint.setStrokeWidth(2);
                    canvas.drawLine(centerx, centery, centerx, centery + 0.42f * width, paint);
                }
                else{
                    paint.setStrokeWidth(2);
                    canvas.drawLine(centerx, centery + 0.38f * width, centerx, centery + 0.42f * width, paint);
                }
                canvas.rotate(11.25f, centerx, centery);
            }

            //Top Figure
            //canvas.drawCircle(centerx, 0.1f * height, 0.05f * height, paint);
            //paint.setStyle(Paint.Style.FILL);


            path.moveTo(centerx, 0.1f * height);
            path.lineTo(centerx-width*0.025f,0.1f * height);
            path.lineTo(centerx,0.005f * height);
            path.lineTo(centerx+width*0.025f,0.1f * height);
            path.close();

            canvas.drawPath(path,paint);

            paint.setTextSize(64);
            paint.setFakeBoldText(true);

            //canvas.drawText("N", centerx, 0.1f * height, handPaint3);
            try {
                if(azimut < 0.001f && azimut > -0.001f) azimut = 0.001f;
                az = (azimut + (float)Math.PI) * 360 / (2 * (float) Math.PI);
                az = (az + 180) % 360;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            //dirtext = "N" + " (" + Integer.toString((int)(az)) + ".0º)";

            dirtext = "N" + " (" + String.format("%.1f", az) + "º)";
            for (int i = 1; i < 16; i++) {
                if (az > i * 22.5f - 12.25f && az < i * 22.5f + 12.25f)
                    dirtext = dir[i] + " (" + String.format("%.1f", az) + "º)";
            }
            canvas.drawText(dirtext, centerx + 0.05f * width, 0.1f * height, paint);

            // Rotate the canvas with the azimut
            if (azimut != null)
                canvas.rotate(-azimut*360/(2*(float)Math.PI), centerx, centery);

            handPath1.moveTo(centerx, centery);
            handPath1.lineTo(centerx-width*0.025f,centery);
            handPath1.lineTo(centerx,centery-width*0.4f);
            handPath1.close();

            handPath2.moveTo(centerx, centery);
            handPath2.lineTo(centerx+width*0.025f,centery);
            handPath2.lineTo(centerx,centery-width*0.4f);
            handPath2.close();

            handPath3.moveTo(centerx, centery);
            handPath3.lineTo(centerx-width*0.025f,centery);
            handPath3.lineTo(centerx,centery+width*0.4f);
            handPath3.close();

            handPath4.moveTo(centerx, centery);
            handPath4.lineTo(centerx+width*0.025f,centery);
            handPath4.lineTo(centerx,centery+width*0.4f);
            handPath4.close();

            canvas.drawPath(handPath1, handPaint1);
            canvas.drawPath(handPath2, handPaint2);
            canvas.drawPath(handPath3, handPaint3);
            canvas.drawPath(handPath4, handPaint4);

            canvas.drawCircle(centerx, centery, 0.01f * width, handScrewPaint);

            paint.setColor(0xff00ff00);
            canvas.restore();
        }
    }
    
    
}