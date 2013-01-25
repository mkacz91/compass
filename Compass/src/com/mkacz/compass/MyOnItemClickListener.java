package com.mkacz.compass;
import com.mkacz.compass.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class MyOnItemClickListener implements OnItemClickListener
{
	private final View	itemHoldContentView;
	
	public MyOnItemClickListener (Context context)
	{
		LayoutInflater inflater		= (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.itemHoldContentView	= inflater.inflate(R.layout.list_item_click_popup,
				null, false);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		MyListAdapter adapter = (MyListAdapter) parent.getAdapter();
		PopupWindow itemHoldPopup = new PopupWindow(itemHoldContentView,
				view.getWidth() / 2, view.getHeight(), true);
		Button editButton = (Button) itemHoldContentView
				.findViewById(R.id.item_edit_button);
		Button deleteButton	= (Button) itemHoldContentView
				.findViewById(R.id.item_delete_button);
		ListItemHoldPopupListener listener = new ListItemHoldPopupListener(
				adapter, position, itemHoldPopup);
		int[] location = new int[2];
		view.getLocationInWindow(location);
		
		editButton.setOnClickListener(listener);
		deleteButton.setOnClickListener(listener);
		itemHoldPopup.setBackgroundDrawable(new BitmapDrawable(
				Resources.getSystem()));
		itemHoldPopup.showAtLocation(view, Gravity.NO_GRAVITY,
				location[0] + (int) (view.getWidth() / 2), location[1]);
	}
}
