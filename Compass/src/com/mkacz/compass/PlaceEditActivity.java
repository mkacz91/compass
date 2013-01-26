package com.mkacz.compass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Activity started when the user wants to edit or add a new place.
 */
public class PlaceEditActivity extends Activity
{	
	/*
	 * Constants used as Intent extra keys.
	 */
	static final String EXTRA_REQUEST_CODE = "reqc";
	static final String EXTRA_POSITION = "pos";
	static final String EXTRA_NAME = "name";
	static final String EXTRA_LONGITUDE = "lon";
	static final String EXTRA_LATITUDE = "lat";
	
	private int position;
	private int requestCode;
	private EditText nameEditText;
	private EditText longitudeEditText;
	private EditText latitudeEditText;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_edit);
        
        Intent intent = getIntent();
        position = intent.getIntExtra(EXTRA_POSITION, -1);
        requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        longitudeEditText = (EditText) findViewById(R.id.longitude_edit_text);
        latitudeEditText = (EditText) findViewById(R.id.latitude_edit_text);
        
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        Button confirmButton = (Button) findViewById(R.id.confirm_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        OnClickListener listener = new OnClickListener();
        
        cancelButton.setOnClickListener(listener);
        confirmButton.setOnClickListener(listener);
        
        switch (intent.getIntExtra(EXTRA_REQUEST_CODE, -1))
        {
        case MainActivity.REQUEST_EDIT:
        	titleTextView.setText(R.string.edit_place);
        	nameEditText.setText(intent.getStringExtra(EXTRA_NAME));
        	longitudeEditText.setText(Coordinates.longitudeToString(
        		intent.getFloatExtra(EXTRA_LONGITUDE, 0)));
        	latitudeEditText.setText(Coordinates.latitudeToString(
        		intent.getFloatExtra(EXTRA_LATITUDE, 0)));
        	confirmButton.setText(R.string.update);
        	break;
        	
        case MainActivity.REQUEST_ADD:
        	titleTextView.setText(R.string.add_place);
        	confirmButton.setText(R.string.add);
        	break;
        }
    }
    
    /*
     * Handles the clicks on the Cancel and Update/Add buttons.
     */
    private class OnClickListener implements View.OnClickListener
    {
    	public void onClick(View view)
    	{
    		switch (view.getId())
    		{
    		case R.id.cancel_button:
    			setResult(Activity.RESULT_CANCELED);
    			break;
    		
    		case R.id.confirm_button:
    			String name = nameEditText.getText().toString();
    			float longitude = Coordinates.stringToLongitude(
    					longitudeEditText.getText().toString());
    			float latitude = Coordinates.stringToLatitude(
    					latitudeEditText.getText().toString());
    			if
    			(
    				   name.length() == 0
    				|| longitude == Coordinates.INVALID_COORDINATE
    				|| latitude == Coordinates.INVALID_COORDINATE
    			)	
	            {
	            	Toast toast = Toast.makeText(getApplicationContext(),
	            			R.string.invalid_form_alert,
	            			Toast.LENGTH_SHORT);
	            	toast.show();
	            	return;
	            }
    			Intent result = new Intent();
    			if (requestCode == MainActivity.REQUEST_EDIT)
    				result.putExtra(EXTRA_POSITION, position);
    			result.putExtra(EXTRA_NAME, name);
    			result.putExtra(EXTRA_LONGITUDE, longitude);
    			result.putExtra(EXTRA_LATITUDE, latitude);
    			setResult(RESULT_OK, result);
    			break;
    		}
    		
    		finish();
    	}
    }
}
