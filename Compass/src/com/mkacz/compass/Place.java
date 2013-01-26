package com.mkacz.compass;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Place implements OnCheckedChangeListener
{
	private String name;
	private float longitude;
	private float latitude;
	private int	color;
	private boolean checked;
	
	public Place(String name, float longitude, float latitude, int color)
	{
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.color = color;
		this.checked = false;
	}
	
	public Place(String name, float longitude, float latitude, int color,
			boolean checked)
	{
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.color = color;
		this.checked = checked;
	}
	
	/*
	 * Called when items' corresponding CheckBox in ListView changes state.
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		checked = isChecked;
	}
	
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public void setLongitude(float longitude)
	{
		this.longitude = longitude;
	}
	
	public float getLongitude()
	{
		return longitude;
	}
	
	public void setLatitude(float latitude)
	{
		this.latitude = latitude;
	}
	
	public float getLatitude()
	{
		return latitude;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public int getColor()
	{
		return color;
	}
}