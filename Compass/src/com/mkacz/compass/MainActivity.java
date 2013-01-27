package com.mkacz.compass;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity
{
	/*
	 * Request codes passed to PlaceEditActivity.
	 */
	public static final int	REQUEST_EDIT = 1;
	public static final int	REQUEST_ADD = 2;
	
	private List<Place> places;
	private PlacesAdapter adapter;
	private EditText filterEditText;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        filterEditText = (EditText) findViewById(R.id.filter_edit_text);
        filterEditText.addTextChangedListener(new FilterTextWatcher());
        
        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        places = PlacesArchiver.restore(preferences);
        
        adapter = new PlacesAdapter(this, places);
        ListView listView = (ListView) findViewById(R.id.list_view);
        
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnPlaceClickListener());
        listView.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onPause()
    {
    	SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
    	PlacesArchiver.store(places, preferences);
    	super.onPause();
    }
       
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    	case R.id.menu_populate:
    	{
    		populateList();
    		return true;
    	}
    		
    	case R.id.menu_show_selected:
    	{
    		Intent intent = new Intent(this, CompassActivity.class);
    		startActivity(intent);
    		return true;
    	}
    		
    	case R.id.menu_add_place:
    	{
    		Intent intent = new Intent(this, PlaceEditActivity.class);
    		intent.putExtra(PlaceEditActivity.EXTRA_REQUEST_CODE, REQUEST_ADD);
    		startActivityForResult(intent, REQUEST_ADD);
    		return true;
    	}
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    /*
     *  Handles Edit and Add information provided by PlaceEditActivity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if (resultCode != RESULT_OK) return;
    	
    	String name = data.getStringExtra(PlaceEditActivity.EXTRA_NAME);
    	float latitude = data.getFloatExtra(
    			PlaceEditActivity.EXTRA_LATITUDE, 0);
    	float longitude = data.getFloatExtra(
    			PlaceEditActivity.EXTRA_LONGITUDE, 0);
    	int color = data.getIntExtra(PlaceEditActivity.EXTRA_COLOR, 0);
    	
    	switch (requestCode)
    	{
    	case REQUEST_EDIT:
    		Place place = adapter.get(data.getIntExtra(
    				PlaceEditActivity.EXTRA_POSITION, -1));
    		place.setName(name);
    		place.setLatitude(latitude);
    		place.setLongitude(longitude);
    		place.setColor(color);
    		break;
    		
    	case REQUEST_ADD:
    		adapter.add(new Place(name, latitude, longitude, color));
    		break;
    	}
    	
    	adapter.notifyDataSetChanged();
    }
    
    /*
     * Handles the clicks on a place. Displays dialog to ask the user
     * what to do.
     */
    private class OnPlaceClickListener implements OnItemClickListener
    {
    	public void onItemClick(AdapterView<?> parent, View view, int position,
    			long id)
    	{
    		AlertDialog.Builder builder = new AlertDialog.Builder(
    				MainActivity.this);
    		OnPlaceActionListener listener = new OnPlaceActionListener(
    				position);
    		builder.setPositiveButton(R.string.edit, listener);
    		builder.setNegativeButton(R.string.remove, listener);
    		AlertDialog dialog = builder.create();
    		dialog.show();
    	}
    }
    
    /*
     * Handles action chosen by the user from the dialog created by
     * OnPlaceClickListener. Starts PlaceEditActivity if necessary.
     */
    private class OnPlaceActionListener
    		implements DialogInterface.OnClickListener
    {
    	private final int position;
    	
    	public OnPlaceActionListener(int position)
    	{
    		this.position = position;
    	}
    	
    	public void onClick(DialogInterface dialog, int which)
    	{
    		Place place = adapter.get(position);
    		
    		switch (which)
    		{
    		case DialogInterface.BUTTON_POSITIVE:
    			Intent intent = new Intent(getApplicationContext(),
    					PlaceEditActivity.class);
    			intent.putExtra(PlaceEditActivity.EXTRA_REQUEST_CODE,
    					REQUEST_EDIT);
    			intent.putExtra(PlaceEditActivity.EXTRA_POSITION, position);
    			intent.putExtra(PlaceEditActivity.EXTRA_NAME, place.getName());
    			intent.putExtra(PlaceEditActivity.EXTRA_LATITUDE,
    					place.getLatitude());
    			intent.putExtra(PlaceEditActivity.EXTRA_LONGITUDE,
    					place.getLongitude());
    			intent.putExtra(PlaceEditActivity.EXTRA_COLOR,
    					place.getColor());
    			startActivityForResult(intent, REQUEST_EDIT);
    			break;
    			
    		case DialogInterface.BUTTON_NEGATIVE:
    			adapter.remove(place);
    			adapter.notifyDataSetChanged();
    			break;
    		}
    	}
    }
    
    /*
     * Reacts on the changes of the filter text.
     */
    private class FilterTextWatcher implements TextWatcher
    {
    	public void afterTextChanged(Editable s) {}
    	public void beforeTextChanged(CharSequence s, int start, int count,
    			int after) {}
    	
    	public void onTextChanged(CharSequence s, int start, int count,
    			int after)
    	{
    		adapter.filter(s.toString());
    	}
    };
    
    /*
     * Called when Populate menu item is chosen. Adds some generic places to the
     * list. For debugging purposes.
     */
    private void populateList()
    {
    	adapter.add(new Place("Mój dom", -17, 50, 0));
    	adapter.add(new Place("Grunwald", 25, 40, 0));
    	adapter.notifyDataSetChanged();
    }
    
    /*
     * Called when the Clear button on the right of filter input is clicked.
     */
    public void clearFilter(View view)
    {
    	filterEditText.setText(null);
    }
}
