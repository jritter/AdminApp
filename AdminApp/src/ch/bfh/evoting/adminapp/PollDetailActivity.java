package ch.bfh.evoting.adminapp;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import ch.bfh.evoting.votinglib.PollDbHelper;
import ch.bfh.evoting.votinglib.entities.DatabaseException;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Poll;

public class PollDetailActivity extends Activity implements OnClickListener {
	
	private ListView lv;
	private PollOptionAdapter adapter;
	ArrayList<Option> options;
	
	private ImageButton btnAddOption;
	private EditText etOption;
	private EditText etQuestion;
	
	private Poll poll;
	
	private PollDbHelper pollDbHelper;
	
	private boolean updatePoll = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poll_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		pollDbHelper = PollDbHelper.getInstance(this);
		
		
		if (getIntent().getIntExtra("pollid", -1) == -1){
			// we didn't get a poll id, so let's create a new poll.
			poll = new Poll();
			options = new ArrayList<Option>();
			poll.setOptions(options);
			updatePoll = false;
		}
		else {
			poll = pollDbHelper.getPoll(getIntent().getIntExtra("pollid", -1));
			options = (ArrayList<Option>) poll.getOptions();
			updatePoll = true;
		}
		
		lv = (ListView) findViewById(R.id.listview_pollquestions);
		btnAddOption = (ImageButton) findViewById(R.id.button_addoption);
		etOption = (EditText) findViewById(R.id.edittext_option);
		etQuestion = (EditText) findViewById(R.id.edittext_question);
		
		etQuestion.setText(poll.getQuestion());
		
		btnAddOption.setOnClickListener(this);
		
		adapter = new PollOptionAdapter(this, R.id.listview_pollquestions, poll);
		
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
			if (updatePoll){
				updatePoll();
				Toast.makeText(this, R.string.toast_poll_updated, Toast.LENGTH_SHORT).show();
			}
			else {
				savePoll();
				Toast.makeText(this, R.string.toast_poll_saved, Toast.LENGTH_SHORT).show();
			}
			
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
				poll.setOptions(options);
				adapter.notifyDataSetChanged();
				etOption.setText("");
			}
		}
	}
	
	private void savePoll() {
		poll.setQuestion(etQuestion.getText().toString());
		poll.setOptions(options);
		try {
			pollDbHelper.savePoll(poll);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	private void updatePoll() {
		poll.setQuestion(etQuestion.getText().toString());
		poll.setOptions(options);
		pollDbHelper.updatePoll(poll.getId(), poll);
	}
}
