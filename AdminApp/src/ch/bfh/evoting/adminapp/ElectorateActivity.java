package ch.bfh.evoting.adminapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.bfh.evoting.adminapp.adapters.NetworkParticipantListAdapter;
import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.IPAddressComparator;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Class displaying the activity that allows the administrator to select which participants to include in the electorate
 * @author Philémon von Bergen
 *
 */
public class ElectorateActivity extends ListActivity {

	private Poll poll;
	private Map<String,Participant> participants;
	private NetworkParticipantListAdapter npa;

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
		npa = new NetworkParticipantListAdapter(this, R.layout.list_item_participant_network, new ArrayList<Participant>(participants.values()));
		setListAdapter(npa);

		// Subscribing to the participantStateUpdate events
		LocalBroadcastManager.getInstance(this).registerReceiver(participantsDiscoverer, new IntentFilter(BroadcastIntentTypes.participantStateUpdate));
		
		//Send the list of participants in the network over the network
		VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_ELECTORATE, (Serializable)participants);
		AndroidApplication.getInstance().getNetworkInterface().sendMessage(vm);

		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				//Send the updated list of participants in the network over the network
				VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_ELECTORATE, (Serializable)participants);
				AndroidApplication.getInstance().getNetworkInterface().sendMessage(vm);

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
			Map<String,Participant> finalParticipants = new TreeMap<String,Participant>(new IPAddressComparator());
			for(Participant p: participants.values()){
				if(p.isSelected()){
					finalParticipants.put(p.getIpAddress(),p);
				}
			}
			poll.setParticipants(finalParticipants);

			//Send poll to other participants
			VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_POLL_TO_REVIEW, (Serializable)poll);
			AndroidApplication.getInstance().getNetworkInterface().sendMessage(vm);

			Intent intent = new Intent(this, ReviewPollActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			startActivity(intent);
			LocalBroadcastManager.getInstance(this).unregisterReceiver(participantsDiscoverer);
			return true;

		}
		return super.onOptionsItemSelected(item); 
	}
	
	/**
	 * this broadcast receiver listens for incoming instacircle broadcast notifying set of participants has changed
	 */
	private BroadcastReceiver participantsDiscoverer = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Map<String,Participant> newReceivedMapOfParticipants = AndroidApplication.getInstance().getNetworkInterface().getConversationParticipants();
			for(String ip : newReceivedMapOfParticipants.keySet()){
				if(!participants.containsKey(ip)){
					//Participant is not already know
					//we add it
					participants.put(ip, newReceivedMapOfParticipants.get(ip));
				} else if (!participants.get(ip).getIdentification().equals(newReceivedMapOfParticipants.get(ip).getIdentification())) {
					//There is already a participant registered with this ip,
					//but the identification in the new set is not the same
					//so we delete the old and put the new
					participants.remove(ip);
					participants.put(ip, newReceivedMapOfParticipants.get(ip));
				}
			}
			
			List<String> toRemove = new ArrayList<String>();
			for(String ip : participants.keySet()){
				if(!newReceivedMapOfParticipants.containsKey(ip)){
					//participant is no more in the new set
					//we delete it
					toRemove.add(ip);
				}
			}
			for(String ip : toRemove){
				participants.remove(ip);
			}
			
			npa.clear();
			npa.addAll(participants.values());
			npa.notifyDataSetChanged();
			
			//Send the updated list of participants in the network over the network
			VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_ELECTORATE, (Serializable)participants);
			AndroidApplication.getInstance().getNetworkInterface().sendMessage(vm);
		}
	};

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
