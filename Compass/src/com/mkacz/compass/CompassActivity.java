package com.mkacz.compass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

public class CompassActivity extends Activity implements SensorEventListener
{
	private SensorManager sensorManager;
	private Sensor magneticSensor;
	private Sensor accelerationSensor;
	
	private float[] rotation = new float[9];
	private float[] inclination = new float[9];
	private float[] gravity = new float[3];
	private float[] geomagnetic = new float[3];
	
	CompassView compassView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_compass);
	    
	    compassView = (CompassView) findViewById(R.id.compass_view);
	
	    sensorManager = (SensorManager) getSystemService(
	    		Context.SENSOR_SERVICE);
	    magneticSensor = sensorManager.getDefaultSensor(
	    		Sensor.TYPE_MAGNETIC_FIELD);
	    accelerationSensor = sensorManager.getDefaultSensor(
	    		Sensor.TYPE_ACCELEROMETER);
	    
	    if (magneticSensor == null || accelerationSensor == null)
	    {
	    	Toast toast = Toast.makeText(this, R.string.sensor_error,
	    			Toast.LENGTH_LONG);
	    	toast.show();
	    }
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if (magneticSensor != null && accelerationSensor != null)
		{
			sensorManager.registerListener(this, magneticSensor,
					SensorManager.SENSOR_DELAY_UI);
			sensorManager.registerListener(this, accelerationSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (magneticSensor != null && accelerationSensor != null)
			sensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// Do nothing.
	}

	public void onSensorChanged(SensorEvent event)
	{
		switch (event.sensor.getType())
		{
		case Sensor.TYPE_MAGNETIC_FIELD:
			System.arraycopy(event.values, 0, geomagnetic, 0, 3);
			break;
			
		case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, gravity, 0, 3);
			break;
		}
		
		if (SensorManager.getRotationMatrix(rotation, inclination, gravity,
				geomagnetic))
		{
			compassView.setRotationMatrix(rotation);
		}
	}

}