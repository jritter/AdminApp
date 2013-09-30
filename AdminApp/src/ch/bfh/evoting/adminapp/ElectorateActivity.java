package ch.bfh.evoting.adminapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.bfh.evoting.adminapp.adapters.NetworkParticipantListAdapter;
import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ElectorateActivity extends ListActivity {

	private Poll poll;
	private List<Participant> participants;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_electorate);
		setupActionBar();

		//if extra is present, it has priority
		Intent intent = getIntent();
		Poll serializedPoll = (Poll)intent.getSerializableExtra("poll");
		if(serializedPoll!=null){
			poll = serializedPoll;
		}

		participants = AndroidApplication.getInstance().getNetworkInterface().getConversationParticipants();
		NetworkParticipantListAdapter npa = new NetworkParticipantListAdapter(this, R.layout.list_item_participant_network, participants);
		setListAdapter(npa);

		//TODO register bc receiver to update view

		//TODO send list

		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				//TODO send updated data

			}
		});

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
		case R.id.action_next:
			List<Participant> finalParticipants = new ArrayList<Participant>();
			for(Participant p: participants){
				if(p.isSelected()){
					finalParticipants.add(p);
				}
			}
			poll.setParticipants(finalParticipants);
			//TODO send poll to other participants
			Intent intent = new Intent(this, ReviewPollActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			startActivity(intent);
			return true;

		}
		return super.onOptionsItemSelected(item); 
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("poll", poll);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		poll = (Poll)savedInstanceState.getSerializable("poll");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.electorate, menu);
		return true;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

}
