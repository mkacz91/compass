package com.mkacz.compass;
import com.mkacz.compass.R;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;


public class ListItemHoldPopupListener implements OnClickListener
{
	private final MyListAdapter	adapter;
	private final int			position;
	private final PopupWindow	popup;
	
	ListItemHoldPopupListener(MyListAdapter adapter, int itemPosition, PopupWindow popup)
	{
		this.adapter	= adapter;
		this.position	= itemPosition;
		this.popup		= popup;
	}
	
	public void onClick(View view)
	{
		ListItem item = adapter.getItem(position);
		
		switch (view.getId())
		{
		case R.id.item_edit_button:
			MainActivity main = (MainActivity) view.getContext();
			Intent intent = new Intent(main, ItemEditActivity.class);
			intent.putExtra("requestCode", MainActivity.REQUEST_EDIT);
			intent.putExtra("position", position);
			intent.putExtra("caption", item.getCaption());
			intent.putExtra("description", item.getDescription());
			main.startActivityForResult(intent, MainActivity.REQUEST_EDIT);
			break;
			
		case R.id.item_delete_button:
			adapter.deleteItem(item);
			adapter.notifyDataSetChanged();
			break;
		}
		
		popup.dismiss();
	}
}
