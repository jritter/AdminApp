package ch.bfh.evoting.adminapp;

import java.util.ArrayList;
import java.util.HashMap;

import ch.bfh.evoting.votinglib.VoteOptionListAdapter;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Poll;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class PollDetailActivity extends Activity implements OnClickListener {
	
	private ListView lv;
	private VoteOptionListAdapter adapter;
	ArrayList<Option> options;
	
	private ImageButton btnAddOption;
	private EditText etOption;
	private EditText etQuestion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poll_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		options = new ArrayList<Option>();
		
		lv = (ListView) findViewById(R.id.listview_pollquestions);
		btnAddOption = (ImageButton) findViewById(R.id.button_addoption);
		etOption = (EditText) findViewById(R.id.edittext_option);
		etQuestion = (EditText) findViewById(R.id.edittext_question);
		
		btnAddOption.setOnClickListener(this);
		
		adapter = new VoteOptionListAdapter(this, R.id.listview_pollquestions, options);
		
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
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.poll_detail_actions, menu);
	    return super.onCreateOptionsMenu(menu);
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
		case R.id.action_save:
			Poll poll = new Poll();
			poll.setQuestion(etQuestion.getText().toString());
			//poll.setOptions(options);
			return true;
		}
		return super.onOptionsItemSelected(item); 
	}

	@Override
	public void onClick(View view) {	
		if (view == btnAddOption){ 
			if (!etOption.getText().toString().equals("")){
				Option option = new Option();
				option.setText(etOption.getText().toString());
				options.add(option);
				adapter.notifyDataSetChanged();
				etOption.setText("");
			}
		}
	}
}
