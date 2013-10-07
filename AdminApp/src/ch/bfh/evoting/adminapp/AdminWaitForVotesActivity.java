package ch.bfh.evoting.adminapp;

import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.HelpDialogFragment;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity show when the participant has already submitted her vote but other voters are still voting
 * @author Philémon von Bergen
 *
 */
public class AdminWaitForVotesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_wait_for_votes);
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

		case R.id.help:
			HelpDialogFragment hdf = HelpDialogFragment.newInstance( getString(R.string.help_title_wait), getString(R.string.help_text_wait) );
			hdf.show( getFragmentManager( ), "help" );
			return true;
		case R.id.action_stop:
			Intent i = new Intent(BroadcastIntentTypes.stopVote);
			LocalBroadcastManager.getInstance(this).sendBroadcast(i);

			//Send stop signal over the network
			VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_STOP_POLL, null);
			AndroidApplication.getInstance().getNetworkInterface().sendMessage(vm);

			return true;

		}
		return super.onOptionsItemSelected(item); 
	}
}
