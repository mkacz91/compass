package com.mkacz.compass;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

/*
 * Methods responsible for saving and loading places to/from SharedPreferences
 * and Intent.
 */
public class PlacesArchiver
{
	/*
	 * Constants used as Intent extra names.
	 */
	static final String EXTRA_NAME = "_name";
	static final String EXTRA_LATITUDE = "_latitude";
	static final String EXTRA_LONGITUDE = "_longitude";
	static final String EXTRA_COLOR = "_color";
	static final String EXTRA_CHECKED = "_checked";
	
	/*
	 * Separators used to serialize a list of places.
	 */
	static private final char RECORD_SEPARATOR = '@';
	static private final char FIELD_SEPARATOR = ':';
	
	/*
	 * Put a single place as an extra to an intent.
	 * 
	 * Caution: The place is stored as a series of extras corresponding to each
	 * field of the Place class. The name of each component consists of the
	 * string passed as argument and a proper EXTRA_* suffix. Keep that in mind
	 * when choosing names for other extras. 
	 */
	static public void putExtra(Intent intent, String name, Place place)
	{
		intent.putExtra(name + EXTRA_NAME, place.getName());
		intent.putExtra(name + EXTRA_LATITUDE, place.getLatitude());
		intent.putExtra(name + EXTRA_LONGITUDE, place.getLongitude());
		intent.putExtra(name + EXTRA_COLOR, place.getColor());
		intent.putExtra(name + EXTRA_CHECKED, place.isChecked());
	}
	
	/*
	 * Get a place extra from an intent. Complementary to putExtra(..., Place).
	 */
	static public Place getPlaceExtra(Intent intent, String name)
	{
		return new Place(
				intent.getStringExtra(name + EXTRA_NAME),
				intent.getFloatExtra(name + EXTRA_LATITUDE, 0),
				intent.getFloatExtra(name + EXTRA_LONGITUDE, 0),
				intent.getIntExtra(name + EXTRA_COLOR, Color.MAGENTA),
				intent.getBooleanExtra(name + EXTRA_CHECKED, false)
			);
	}
	
	/*
	 * Put a list of places as an extra of an intent. See putExtra(..., Place)
	 * and read the 'Caution' notice.
	 */
	static public void putExtra(Intent intent, String name, List<Place> places)
	{
		String[] names = new String[places.size()];
		float[] latitudes = new float[places.size()];
		float[] longitudes = new float[places.size()];
		int[] colors = new int[places.size()];
		boolean[] checks = new boolean[places.size()];
		
		int p = 0;
		for (Place place : places)
		{
			names[p] = place.getName();
			latitudes[p] = place.getLatitude();
			longitudes[p] = place.getLongitude();
			colors[p] = place.getColor();
			checks[p] = place.isChecked();
			++p;
		}
		
		intent.putExtra(name + EXTRA_NAME, names);
		intent.putExtra(name + EXTRA_LATITUDE, latitudes);
		intent.putExtra(name + EXTRA_LONGITUDE, longitudes);
		intent.putExtra(name + EXTRA_COLOR, colors);
		intent.putExtra(name + EXTRA_CHECKED, checks);
	}
	
	/*
	 * Get a list of places from an intent. Complementary to
	 * putExtra(..., List<Place>)
	 */
	static public List<Place> getPlaceListExtra(Intent intent, String name)
	{
		String[] names = intent.getStringArrayExtra(name + EXTRA_NAME);
		float[] latitudes = intent.getFloatArrayExtra(name + EXTRA_LATITUDE);
		float[] longitudes = intent.getFloatArrayExtra(name + EXTRA_LONGITUDE);
		int[] colors = intent.getIntArrayExtra(name + EXTRA_COLOR);
		boolean[] checks = intent.getBooleanArrayExtra(name + EXTRA_CHECKED);
		
		List<Place> places = new LinkedList<Place>();
		int placeCnt = names.length;
		for (int p = 0; p < placeCnt; ++p)
			places.add(new Place(
					names[p],
					latitudes[p],
					longitudes[p],
					colors[p],
					checks[p]
				));
		
		return places;
	}
	
	/*
	 * Saves the list of places serialized to a string to given
	 * SharedPreferences.
	 */
	static public void putPlaces(SharedPreferences preferences, String key,
			List<Place> places)
	{
    	StringBuffer buffer = new StringBuffer();
    	Iterator<Place> it = places.iterator();
    	while (it.hasNext())
    	{
    		Place place = it.next();
    		buffer.append(escapeSeparators(place.getName()))
    			.append(FIELD_SEPARATOR);
    		buffer.append(place.getLongitude()).append(FIELD_SEPARATOR);
    		buffer.append(place.getLatitude()).append(FIELD_SEPARATOR);
    		buffer.append(place.getColor()).append(FIELD_SEPARATOR);
    		buffer.append(place.isChecked());
    		
    		if (it.hasNext())
    			buffer.append(RECORD_SEPARATOR);
    	}
    	
    	Editor editor = preferences.edit();
    	editor.putString(key, buffer.toString());
    	editor.commit();
	}
	
	/*
	 * Loads list of places from SharedPreferences.
	 */
	static public List<Place> getPlaces(SharedPreferences preferences,
			String key)
	{
		List<Place> places = new LinkedList<Place>();
		String buffer = preferences.getString(key, "");
		if (buffer.length() != 0)
		{
			String[] records = splitUnescaped(buffer, RECORD_SEPARATOR);
			for (String record : records)
			{
				String[] fields = splitUnescaped(record, FIELD_SEPARATOR);
				places.add(new Place(
						fields[0],                      // name
						Float.parseFloat(fields[1]),    // longitude
						Float.parseFloat(fields[2]),    // latitude
						Integer.parseInt(fields[3]),    // color
						Boolean.parseBoolean(fields[4]) // checked
					));
			}
		}
		return places;
	}
	
	/*
	 * Escapes any occurrence of RECORD_SEPARATOR or FIELD_SEPARATOR
	 * in a string.
	 */
	static public String escapeSeparators(String string)
	{
		return string
				.replace(String.valueOf(RECORD_SEPARATOR),
						"\\" + RECORD_SEPARATOR)
				.replace(String.valueOf(FIELD_SEPARATOR),
						"\\" + FIELD_SEPARATOR);
	}
	
	/*
	 * Restores RECORD_SEPARATOR and FIELD_SEPARATOR escaped by
	 * escapeSeparators() to original form.
	 */
	static public String restoreSeparators(String string)
	{
		return string
				.replace("\\" + RECORD_SEPARATOR,
						String.valueOf(RECORD_SEPARATOR))
				.replace("\\" + FIELD_SEPARATOR,
						String.valueOf(FIELD_SEPARATOR));
	}
	
	/*
	 * Splits string at any unescaped occurrence of given character. Meant to be
	 * used with RECORD_SEPARATOR and FIELD_SEPARATOR, but is equally good for
	 * other purposes.
	 * 
	 * I just couldn't come up with a suitable regular expression to
	 * pass to String.split().
	 */
	static private String[] splitUnescaped(String string, char separator)
	{
		int separatorCnt = 0;
		for (int i = 0; i < string.length(); ++i)
			if (string.charAt(i) == separator)
				if (i == 0 || string.charAt(i - 1) != '\\')
					++separatorCnt;
		
		int tokenCnt = separatorCnt + 1;
		String[] tokens = new String[tokenCnt];
		
		int t = 0;
		int start = 0;
		for (int i = 0; i < string.length(); ++i)
			if (string.charAt(i) == separator)
				if (i == 0 || string.charAt(i - 1) != '\\')
				{
					tokens[t++] = string.substring(start, i);
					start = i + 1;
				}
		tokens[t++] = string.substring(start);
		assert t == tokenCnt;
		
		return tokens;
	}
}
