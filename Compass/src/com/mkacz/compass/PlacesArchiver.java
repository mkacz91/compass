package com.mkacz.compass;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * Methods responsible for saving and loading a list of places to/from
 * SharedPreferences
 */
public class PlacesArchiver
{
	static private final String KEY_PLACES = "places";
	static private final char RECORD_SEPARATOR = '@';
	static private final char FIELD_SEPARATOR = ':';
	
	/*
	 * Saves the list of places serialized to a string to given
	 * SharedPreferences.
	 */
	static public void store(List<Place> places, SharedPreferences preferences)
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
    	editor.putString(KEY_PLACES, buffer.toString());
    	editor.commit();
	}
	
	/*
	 * Loads list of places from SharedPreferences.
	 */
	static public List<Place> restore(SharedPreferences preferences)
	{
		List<Place> places = new LinkedList<Place>();
		String buffer = preferences.getString(KEY_PLACES, "");
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
	 * Escapes any occurrence of RECORD_SEPARATOR or FIELD_SEPARATOR in a string.
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
