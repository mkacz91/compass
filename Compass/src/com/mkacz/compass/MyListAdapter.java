package com.mkacz.compass;

import java.util.LinkedList;
import java.util.List;

import com.mkacz.compass.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter
{
	private final LayoutInflater	inflater;
	private final List<ListItem>	baseItems;
	private List<ListItem>			items;
	private MyFilter				filter = new MyFilter();
	private String					constraint = null;
	
	public MyListAdapter(Context context, List<ListItem> items)
	{
		this.inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.baseItems = items;
		this.items = items;
	}
	
	public int getCount()
	{
		return items.size();
	}
	
	public long getItemId(int position)
	{
		return -1;
	}
	
	public ListItem getItem(int position)
	{
		return items.get(position);
	}
	
	public void addItem(ListItem item)
	{
		baseItems.add(item);
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view	= convertView != null
					? convertView
					: inflater.inflate(R.layout.list_item, parent, false);
		
		ListItem item = items.get(position);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_check_box);
		TextView caption = (TextView) view.findViewById(R.id.item_caption);
		TextView description = (TextView) view.findViewById(
				R.id.item_description);
		
		checkBox.setOnCheckedChangeListener(null);
		checkBox.setChecked(item.isChecked());
		checkBox.setOnCheckedChangeListener(item);
		caption.setText(item.getCaption());
		description.setText(item.getDescription());
		
		return view;
	}
	
	public void deleteItem(ListItem item)
	{
		baseItems.remove(item);
	}
	
	public void filter(String constraint)
	{
		if (constraint == null || constraint.length() == 0
				&& items != baseItems)
		{
			this.constraint = null;
			items = baseItems;
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
	
	public void notifyDataSetChanged()
	{
		if (items == baseItems)
			super.notifyDataSetChanged();
		refilter();
	}
	
	public void notifyDataSetInvalidated()
	{
		if (items == baseItems)
			super.notifyDataSetInvalidated();
		refilter();
	}
	
	private class MyFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence constraint)
		{
			List<ListItem> filteredItems = new LinkedList<ListItem>();
			for (ListItem item : baseItems)
				if (item.getCaption().toLowerCase().contains(constraint))
					filteredItems.add(item);
			FilterResults results = new FilterResults();
			results.values = filteredItems;
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results)
		{
			items = (List<ListItem>) results.values;
			MyListAdapter.super.notifyDataSetChanged();
		}
	}
}
