package com.mkacz.compass;

import com.mkacz.compass.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ItemEditActivity extends Activity
{	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        
        final Intent intent = getIntent();
        final int requestCode = intent.getIntExtra("requestCode", -1);
        final int position = intent.getIntExtra("position", -1);
        final EditText captionEditText = (EditText) findViewById(
        		R.id.caption_edit_text);
        final EditText descriptionEditText = (EditText) findViewById(
        		R.id.description_edit_text);
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        Button confirmButton = (Button) findViewById(R.id.confirm_button);
        
        switch (requestCode)
        {
        case MainActivity.REQUEST_EDIT:
        	titleTextView.setText(R.string.title_edit_item);
        	confirmButton.setText(R.string.update);
        	break;
        	
        case MainActivity.REQUEST_ADD:
        	titleTextView.setText(R.string.title_add_item);
        	confirmButton.setText(R.string.add);
        	break;
        }
        
        OnClickListener	listener = new OnClickListener()
        {
        	public void onClick(View view)
        	{

        		switch (view.getId())
        		{
        		case R.id.cancel_button:
        			setResult(Activity.RESULT_CANCELED);
        			break;
        		
        		case R.id.confirm_button:
        			String caption = captionEditText.getText().toString();
	        		String description = descriptionEditText.getText()
	        				.toString();
		            if (caption == null || caption.length() == 0
		            		|| description == null || description.length() == 0)
		            {
		            	Toast toast = Toast.makeText(getApplicationContext(),
		            			R.string.empty_fields_alert,
		            			Toast.LENGTH_SHORT);
		            	toast.show();
		            	return;
		            }
        			Intent result = new Intent();
        			if (requestCode == MainActivity.REQUEST_EDIT)
        				result.putExtra("position", position);
        			result.putExtra("caption", caption);
        			result.putExtra("description", description);
        			setResult(Activity.RESULT_OK, result);
        			break;
        		}
        		
        		finish();
        	}
        };
        
        captionEditText.setText(intent.getStringExtra("caption"));
        descriptionEditText.setText(intent.getStringExtra("description"));
        cancelButton.setOnClickListener(listener);
        confirmButton.setOnClickListener(listener);
    }
}
