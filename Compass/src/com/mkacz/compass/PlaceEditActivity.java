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
	static final String EXTRA_REQUEST_CODE = "request_code";
	static final String EXTRA_POSITION = "position";
	
	private int requestCode;
	private int position;
	private EditText nameEditText;
	private EditText latitudeEditText;
	private EditText longitudeEditText;
	private ColorPicker colorPicker;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_place_edit);
	    
	    Intent intent = getIntent();
	    
	    requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1);
	    position = intent.getIntExtra(EXTRA_POSITION, -1);
	    nameEditText = (EditText) findViewById(R.id.place_edit_name_edit_text);
	    latitudeEditText = (EditText) findViewById(
	    		R.id.place_edit_latitude_edit_text);
	    longitudeEditText = (EditText) findViewById(
	    		R.id.place_edit_longitude_edit_text);
	    colorPicker = (ColorPicker) findViewById(R.id.place_edit_color_picker);
	    
	    TextView titleTextView = (TextView) findViewById(
	    		R.id.place_edit_title_text_view);
	    Button confirmButton = (Button) findViewById(R.id.confirm_button);
	    Button cancelButton = (Button) findViewById(R.id.cancel_button);
	    OnClickListener listener = new OnClickListener();
	    
	    cancelButton.setOnClickListener(listener);
	    confirmButton.setOnClickListener(listener);
	    
	    switch (intent.getIntExtra(EXTRA_REQUEST_CODE, -1))
	    {
	    case MainActivity.REQUEST_PLACE_EDIT:
	    {
	    	Place place = PlacesArchiver.getPlaceExtra(intent,
	    			MainActivity.EXTRA_PLACE);
	    	titleTextView.setText(R.string.edit_place);
	    	nameEditText.setText(place.getName());
	    	latitudeEditText.setText(Coordinates.latitudeToString(
	    			place.getLatitude()));
	    	longitudeEditText.setText(Coordinates.longitudeToString(
	    			place.getLongitude()));
	    	colorPicker.setPickedColor(place.getColor());
	    	confirmButton.setText(R.string.update);
	    	break;
	    }
	    case MainActivity.REQUEST_PLACE_ADD:
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
				float latitude = Coordinates.stringToLatitude(
						latitudeEditText.getText().toString());
				float longitude = Coordinates.stringToLongitude(
						longitudeEditText.getText().toString());
				int color = colorPicker.getPickedColor();
				if
				(
					   name.length() == 0
					|| latitude == Coordinates.INVALID_COORDINATE
					|| longitude == Coordinates.INVALID_COORDINATE
				)	
	            {
	            	Toast toast = Toast.makeText(getApplicationContext(),
	            			R.string.invalid_form_alert,
	            			Toast.LENGTH_SHORT);
	            	toast.show();
	            	return;
	            }
				Intent result = new Intent();
				if (requestCode == MainActivity.REQUEST_PLACE_EDIT)
					result.putExtra(EXTRA_POSITION, position);
				PlacesArchiver.putExtra(result, MainActivity.EXTRA_PLACE,
						new Place(name, latitude, longitude, color));
				setResult(RESULT_OK, result);
				break;
			}
			
			finish();
		}
	}
}
