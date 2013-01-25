package com.mkacz.compass;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ListItem implements OnCheckedChangeListener
{
	private boolean	checked;
	public String	caption;
	public String	description;
	
	public ListItem(String caption, String description)
	{
		this.checked = false;
		this.caption = caption;
		this.description = description;
	}
	
	public ListItem(boolean checked, String caption, String description)
	{
		this.checked = checked;
		this.caption = caption;
		this.description = description; 
		
	}
	
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
	
	public void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	public String getCaption()
	{
		return caption;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
}
