package com.mkacz.compass;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

/*
 * An adapter class to use with ListView to display list of places.
 */
public class PlacesAdapter extends BaseAdapter
{
	private final LayoutInflater inflater;
	
	private List<Place> places;
	private List<Place> displayedPlaces;
	private PlacesFilter filter = new PlacesFilter();
	private String constraint = null;
	
	public PlacesAdapter(Context context, List<Place> places)
	{
		this.inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.places = places;
		this.displayedPlaces = places;
	}
	
	/*
	 * Mandatory methods
	 */
	
	public int getCount()
	{
		return displayedPlaces.size();
	}

	public Place getItem(int position)
	{
		return displayedPlaces.get(position);
	}

	public long getItemId(int position)
	{
		return -1;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view	= convertView != null
					? convertView
					: inflater.inflate(R.layout.place_view, parent, false);
		
		Place place = places.get(position);
		CheckBox checkBox = (CheckBox) view.findViewById(
				R.id.place_view_check_box);
		TextView nameTextView = (TextView) view.findViewById(
				R.id.place_view_name_text_view);
		TextView coordinatesTextView = (TextView) view.findViewById(
				R.id.place_view_coordinates_text_view);
		
		checkBox.setOnCheckedChangeListener(null);
		checkBox.setChecked(place.isChecked());
		checkBox.setOnCheckedChangeListener(place);
		nameTextView.setText(place.getName());
		coordinatesTextView.setText(
				Coordinates.longitudeToString(place.getLongitude())
				+ " " +
				Coordinates.latitudeToString(place.getLatitude())
		);
		
		return view;
	}
	
	/*
	 * Custom methods
	 */
	
	public Place get(int position)
	{
		return displayedPlaces.get(position);
	}
	
	public void add(Place place)
	{
		places.add(place);
	}
	
	public void remove(Place place)
	{
		places.remove(place);
	}
	
	public void filter(String constraint)
	{
		if ((constraint == null || constraint.length() == 0)
				&& displayedPlaces != places)
		{
			this.constraint = null;
			displayedPlaces = places;
			super.notifyDataSetChanged();
		}
		else
		{
			this.constraint = constraint.toLowerCase();
			filter.filter(this.constraint);
		}
	}
	
	public void refilter()
	{
		filter(constraint);
	}
	/*
	public void notifyDataSetChanged()
	{
		if (constraint == null)
			super.notifyDataSetChanged();
		else
			refilter();
	}*/
	
	/*
	 * A filter class that chooses only those places which names contain
	 * given string.
	 */
	private class PlacesFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence constraint)
		{
			List<Place> filteredPlaces = new LinkedList<Place>();
			for (Place place : places)
				if (place.getName().toLowerCase().contains(constraint))
					filteredPlaces.add(place);
			FilterResults results = new FilterResults();
			results.values = filteredPlaces;
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results)
		{
			displayedPlaces = (List<Place>) results.values;
			PlacesAdapter.super.notifyDataSetChanged();
		}
	}
}
