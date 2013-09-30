package ch.bfh.evoting.adminapp;

import ch.bfh.evoting.votinglib.ListTerminatedPollsActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button btnSetupNetwork;
	private Button btnPolls;
	private Button btnPollArchive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnSetupNetwork = (Button) findViewById(R.id.button_setupnetwork);
		btnPolls = (Button) findViewById(R.id.button_polls);
		btnPollArchive = (Button) findViewById(R.id.button_archive);
		
		btnSetupNetwork.setOnClickListener(this);
		btnPolls.setOnClickListener(this);
		btnPollArchive.setOnClickListener(this);
		
	}

	

	@Override
	public void onClick(View view) {
		if (view == btnSetupNetwork) {
			Intent intent = new Intent(this, NetworkConfigActivity.class);
	        startActivity(intent);
		}
		else if (view == btnPolls){
			Intent intent = new Intent(this, PollActivity.class);
	        startActivity(intent);
		}
		else if (view == btnPollArchive){
			Intent intent = new Intent(this, ListTerminatedPollsActivity.class);
	        startActivity(intent);
		}
		
	}

}
