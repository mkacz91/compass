package com.mkacz.compass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Basically a class to convert geographic coordinates to and from a string.
 */
public class Coordinates
{
	public static final float INVALID_COORDINATE = 1000;
	
    /*
     * Returns longitude based on a string.
     * 
     * Proper input consists of a decimal number followed by one of the
     * characters: w, W, e, E.
     * 
     * The result is a signed float from range [-180, 180], negative meaning
     * east and positive west. If the string can't be interpreted as such,
     * INVALID_COORDINATE is returned which is a number out of that range.
     */
    public static float stringToLongitude(String s)
    {
    	Pattern pattern = Pattern.compile("([0-9]+(\\.[0-9]+)?)([wWeE])");
    	Matcher matcher = pattern.matcher(s);
    	
    	if (matcher.matches())
    	{
    		float sign = matcher.group(3).equalsIgnoreCase("e") ? -1 : 1;
    		float value = Float.parseFloat(matcher.group(1));
    		if (value > 180)
    			return INVALID_COORDINATE;
    		else
    			return sign * value;
    	}
    	else return INVALID_COORDINATE;
    }
    
    /*
     * Returns string representation of a longitude. See stringToLongitude().
     */
    public static String longitudeToString(float longitude)
    {
    	return String.valueOf(Math.abs(longitude))
    			+ (longitude < 0 ? "E" : "W");
    }
    
    /*
     * Returns latitude based on a string.
     * 
     * Proper input consists of a decimal number followed by one of the
     * characters: n, N, s, S.
     * 
     * The result is a signed float from range [-90, 90], negative meaning
     * south and positive north. If the string can't be interpreted as such,
     * INVALID_COORDINATE is returned which is a number out of that range.
     */
    public static float stringToLatitude(String s)
    {
    	Pattern pattern = Pattern.compile("([0-9]+(\\.[0-9]+)?)([nNsS])");
    	Matcher matcher = pattern.matcher(s);
    	
    	if (matcher.matches())
    	{
    		float sign = matcher.group(3).equalsIgnoreCase("s") ? -1 : 1;
    		float value = Float.parseFloat(matcher.group(1));
    		if (value > 180)
    			return INVALID_COORDINATE;
    		else
    			return sign * value;
    	}
    	else return INVALID_COORDINATE;
    }
    
    /*
     * Returns string representation of a latitude. See stringToLatitude().
     */
    public static String latitudeToString(float latitude)
    {
    	return String.valueOf(Math.abs(latitude)) + (latitude < 0 ? "S" : "N");
    }
}
