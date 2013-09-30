package ch.bfh.evoting.adminapp;

import java.io.Serializable;

import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.VoteActivity;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

//TODO put as much as possible in a fragement in lib

public class ReviewPollActivity extends Activity {

	private Poll poll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_poll);
		setupActionBar();
		
		Intent intent = getIntent();
		poll = (Poll)intent.getSerializableExtra("poll");
		
		ListView lv = (ListView)findViewById(android.R.id.list);
		LayoutInflater inflater = this.getLayoutInflater();

		View header = inflater.inflate(R.layout.review_header, null, false);
		lv.addHeaderView(header);
		View footer = inflater.inflate(R.layout.review_footer, null, false);
		lv.addFooterView(footer);

		String[] array = {};
		int[] toViews = {android.R.id.text1};
		lv.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, array, toViews, 0));

		TextView tv_question = (TextView) header.findViewById(R.id.textview_poll_question);
		tv_question.setText(poll.getQuestion());

		//Create options table
		TableLayout optionsTable = (TableLayout)header.findViewById(R.id.layout_options);

		for(Option op : poll.getOptions()){
			TableRow tableRow= new TableRow(this);
			tableRow.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			View vItemOption = inflater.inflate(R.layout.list_item_option_poll, null);
			TextView tv_option = (TextView)vItemOption.findViewById(R.id.textview_poll_option_review);
			tv_option.setText(op.getText());

			tableRow.addView(vItemOption);
			tableRow.setBackgroundResource(R.drawable.borders);

			optionsTable.addView(tableRow);
		}

		//Create participants table
		TableLayout participantsTable = (TableLayout)footer.findViewById(R.id.layout_participants);

		for(Participant part : poll.getParticipants()){
			TableRow tableRow= new TableRow(this);
			tableRow.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			View vItemParticipant = inflater.inflate(R.layout.list_item_participant_poll, null);
			TextView tv_option = (TextView)vItemParticipant.findViewById(R.id.textview_participant_identification);
			tv_option.setText(part.getIdentification());

			tableRow.addView(vItemParticipant);
			tableRow.setBackgroundResource(R.drawable.borders);

			participantsTable.addView(tableRow);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.review_poll, menu);
		return true;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		case R.id.action_start_vote:

			//TODO send start
			poll.setStartTime(System.currentTimeMillis());
			if(isContainedInParticipants(AndroidApplication.getInstance().getNetworkInterface().getMyIpAddress())){
				Intent intent = new Intent(this, VoteActivity.class);
				intent.putExtra("poll", (Serializable)poll);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, AdminWaitForVotesActivity.class);
				intent.putExtra("poll", (Serializable)poll);
				startActivity(intent);
			}

			return true;
		
		}
		return super.onOptionsItemSelected(item); 
	}
	
	private boolean isContainedInParticipants(String ipAddress){
		for(Participant p : poll.getParticipants()){
			if(p.getIpAddress().equals(ipAddress)){
				return true;
			}
		}
		return false;
	}

}
