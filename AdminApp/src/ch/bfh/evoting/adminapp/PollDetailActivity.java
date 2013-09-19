package ch.bfh.evoting.adminapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class PollDetailActivity extends Activity implements OnClickListener {
	
	private ListView lv;
	private PollOptionArrayAdapter adapter;
	ArrayList<HashMap<String, String>> items;
	
	private ImageButton btnAddOption;
	private EditText txtOption;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poll_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		items = new ArrayList<HashMap<String, String>>();
		
		lv = (ListView) findViewById(R.id.listview_pollquestions);
		btnAddOption = (ImageButton) findViewById(R.id.button_addoption);
		txtOption = (EditText) findViewById(R.id.edittext_option);
		
		btnAddOption.setOnClickListener(this);
		
		adapter = new PollOptionArrayAdapter(this, R.layout.list_item_pollquestion,
				items);
		
		lv.setAdapter(adapter);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.poll_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item); 
	}

	@Override
	public void onClick(View view) {	
		if (view == btnAddOption){ 
			if (!txtOption.getText().toString().equals("")){
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("option", txtOption.getText().toString());
				items.add(item);
				adapter.notifyDataSetChanged();
				
				txtOption.setText("");
			}
		}
	}
}
