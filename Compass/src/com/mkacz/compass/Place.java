package com.mkacz.compass;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/*
 * Class representing a place with geographic coordinates.
 */
public class Place implements OnCheckedChangeListener
{
	private String name;
	private float latitude;
	private float longitude;
	private int	color;
	private boolean checked;
	
	public Place(String name, float latitude, float longitude, int color)
	{
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.color = color;
		this.checked = false;
	}
	
	public Place(String name, float latitude, float longitude, int color,
			boolean checked)
	{
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.color = color;
		this.checked = checked;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setLatitude(float latitude)
	{
		this.latitude = latitude;
	}
	
	public float getLatitude()
	{
		return latitude;
	}
	
	public void setLongitude(float longitude)
	{
		this.longitude = longitude;
	}
	
	public float getLongitude()
	{
		return longitude;
	}

	public void setColor(int color)
	{
		this.color = color;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	/*
	 * Called when places' corresponding CheckBox in ListView changes state.
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		checked = isChecked;
	}
}