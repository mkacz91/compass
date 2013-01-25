package com.mkacz.compass;

import java.util.LinkedList;
import java.util.List;

import com.mkacz.compass.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity
{
	final static int	REQUEST_EDIT	= 1;
	final static int	REQUEST_ADD		= 2;
	
	private SharedPreferences preferences;
	private final List<ListItem> items = new LinkedList<ListItem>();
	private MyListAdapter adapter;
	private EditText filterEditText;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filterEditText = (EditText) findViewById(R.id.filter_edit_text);
        adapter = new MyListAdapter(this, items);
        ListView listView = (ListView) findViewById(R.id.list_view);
        MyOnItemClickListener listener = new MyOnItemClickListener(this);
        
        filterEditText.addTextChangedListener(filterTextWatcher);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        
        
        // Restore application state
        preferences = this.getPreferences(MODE_PRIVATE);
        String serializedItems = preferences.getString("items", "");
        String[] tokens = serializedItems.split(String.valueOf((char) 30));
        int i = 0;
        while (i < tokens.length - 2)
        {
        	boolean checked = tokens[i++].equals("t") ? true : false;
        	adapter.addItem(new ListItem(checked, tokens[i++], tokens[i++]));
        }
        adapter.notifyDataSetChanged();
        
        listView.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onPause()
    {
    	// Save application state
    	StringBuffer serializedItems = new StringBuffer();
    	char sep = (char) 30;
    	for (ListItem item : items)
    	{
    		char checked = item.isChecked() ? 't' : 'f';
    		serializedItems.append(checked).append(sep);
    		serializedItems.append(item.getCaption()).append(sep);
    		serializedItems.append(item.getDescription()).append(sep);
    	}
    	Editor editor = preferences.edit();
    	editor.putString("items", serializedItems.toString());
    	editor.commit();
    	super.onDestroy();
    }
    
    public void clearFilter(View view)
    {
    	filterEditText.setText(null);
    }
    
    
    private TextWatcher filterTextWatcher = new TextWatcher()
    {
    	public void afterTextChanged(Editable s) {}
    	public void beforeTextChanged(CharSequence s, int start, int count,
    			int after) {}
    	
    	// This is called every time the filter text changes.
    	public void onTextChanged(CharSequence s, int start, int count,
    			int after)
    	{
    		adapter.filter(s.toString());
    	}
    };
    
    // This handles Edit and Add information provided by ItemEditActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if (resultCode != RESULT_OK) return;
    	
    	switch (requestCode)
    	{
    	case REQUEST_EDIT:
    		ListItem item = adapter.getItem(data.getIntExtra("position", -1));
    		item.setCaption(data.getStringExtra("caption"));
    		item.setDescription(data.getStringExtra("description"));
    		adapter.refilter();
    		break;
    		
    	case REQUEST_ADD:
    		adapter.addItem(new ListItem(data.getStringExtra("caption"),
    				data.getStringExtra("description")));
    		adapter.notifyDataSetChanged();
    		break;
    	}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{	
    	case R.id.menu_new_item:
    		Intent intent = new Intent(this, ItemEditActivity.class);
    		intent.putExtra("requestCode", REQUEST_ADD);
    		startActivityForResult(intent, REQUEST_ADD);
    		return true;
    		
    	case R.id.menu_populate:
    		populateList();
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    // Called on "Populate" menu item click. For testing purposes.
    private void populateList()
    {
    	adapter.addItem(new ListItem("Mieszkam w Legnicy", "Mieœcie wielu " +
    			"ludzi"));
    	adapter.addItem(new ListItem("Justin Bieber", "Baby"));
    	adapter.addItem(new ListItem("Ala", "Ma kota i jest super"));
    	adapter.addItem(new ListItem("Ser za 5 z³", "Taniej ju¿ nie bêdzie"));
    	adapter.addItem(new ListItem("Poemat", "Dzisiaj jest coœ fajnego" +
    			"\nJutro nie bêdzie" +
        		"\nA gdy wzejdzie s³oñce" +
        		"\nCztery"));
    	adapter.addItem(new ListItem("Wojtek", "Ma psa i jest super"));
    	adapter.addItem(new ListItem("Java", "Jest nawet w porz¹dku"));
    	adapter.addItem(new ListItem("Paluszki rybne", "Najlepsze paluszki " +
    			"rybne robi lisner"));
    	adapter.addItem(new ListItem("D³ugi tekst", "Jakiœ d³ugi tekst, " +
    			"który teoretycznie powinien siê zawin¹æ, bo nie zmieœci siê " +
    			"na ekranie. Jeœli nie z³ama³ siê do tej pory, to na pewno " +
    			"teraz siê z³amie."));
    	adapter.addItem(new ListItem("Ostatni wpis", "To jest ostatni wpis " +
    			"ze wszystkich"));
    	adapter.notifyDataSetChanged();
    }
}
