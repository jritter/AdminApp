package ch.bfh.evoting.adminapp;

import java.io.Serializable;
import java.util.List;

import ch.bfh.evoting.votinglib.DisplayResultActivity;
import ch.bfh.evoting.votinglib.adapters.WaitParticipantListAdapter;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

//TODO put as much as possible in a fragement in lib

/**
 * Activity show when the participant has already submitted her vote but other voters are still voting
 * @author Philémon von Bergen
 *
 */
public class AdminWaitForVotesActivity extends ListActivity {

	private int progressBarMaxValue = 0;
	private Poll poll;
	private List<Participant> participants;
	private ProgressBar pb;
	private int numberOfVotes;
	private WaitParticipantListAdapter wpAdapter;

	private  TextView tvCastVotes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_wait_for_votes);

		//Get data in the intent
		Intent intent = this.getIntent();
		poll = (Poll)intent.getSerializableExtra("poll");
		participants = poll.getParticipants();

		//Create the adapter for the ListView
		wpAdapter = new WaitParticipantListAdapter(this, R.layout.list_item_participant_wait, participants);
		this.setListAdapter(wpAdapter);

		//Initialize the progress bar 
		numberOfVotes = 0;
		pb=(ProgressBar)findViewById(R.id.progress_bar_votes);
		progressBarMaxValue = pb.getMax();

		tvCastVotes = (TextView)findViewById(R.id.textview_cast_votes);
		tvCastVotes.setText(getString(R.string.cast_votes, 0, participants.size()));


		//Register a BroadcastReceiver on participantStateUpdate events
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				//TODO compute new votes only
				numberOfVotes++;
				updateStatus(numberOfVotes, false);
			}

		}, new IntentFilter(BroadcastIntentTypes.participantStateUpdate));

		simulate();
	}

	@Override
	public void onBackPressed() {
		//do nothing because we don't want that people access to an anterior activity
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wait_for_votes, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.action_stop:
			this.updateStatus(numberOfVotes, true);
			return true;
		}
		return super.onOptionsItemSelected(item); 
	}
	
	/**
	 * Update the state of the progress bar, change the image of the participants when they have voted
	 * and start the activity which displays the results
	 * @param progress
	 */
	private void updateStatus(int numberOfReceivedVotes, boolean stopOrder){
		//update progress bar and participants list
		int progress = numberOfReceivedVotes*progressBarMaxValue/poll.getParticipants().size();

		pb.setProgress(progress);
		wpAdapter.notifyDataSetChanged();
		tvCastVotes.setText(getString(R.string.cast_votes, numberOfReceivedVotes, participants.size()));


		if(progress>=100 || stopOrder){
			//TODO get through compute result and set result
			List<Option> options = poll.getOptions();
			for(Option option : options){
				option.setVotes(3);
				option.setPercentage(33.3);
			}

			poll.setTerminated(true);

			//start to result activity
			Intent intent = new Intent(this, DisplayResultActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			intent.putExtra("saveToDb", true);
			startActivity(intent);
		}
	}

	//TODO remove, only for simulation
	private void simulate(){
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... arg0) {
				int pos = 0;
				while(numberOfVotes<participants.size()){
					SystemClock.sleep(2000);
					if(pos<poll.getParticipants().size()){
						poll.getParticipants().get(pos).setHasVoted(true);
						pos++;
						LocalBroadcastManager.getInstance(AdminWaitForVotesActivity.this).sendBroadcast(new Intent(BroadcastIntentTypes.participantStateUpdate));
					}


				}
				return null;
			}
		}.execute();
	}
}
