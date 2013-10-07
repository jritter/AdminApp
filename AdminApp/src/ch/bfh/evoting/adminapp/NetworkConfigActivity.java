package ch.bfh.evoting.adminapp;

import ch.bfh.evoting.votinglib.NetworkInformationsActivity;
import ch.bfh.evoting.votinglib.util.HelpDialogFragment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Class displaying the activity that shows the list of network that can be used.
 *
 */
public class NetworkConfigActivity extends Activity implements TextWatcher, OnClickListener {

	private static final String PREFS_NAME = "network_preferences";
	private SharedPreferences preferences;
	private EditText etIdentification;
	
	private Button btnRescanWifi;
	
	private WifiManager wifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_config);
		// Show the Up button in the action bar.
		setupActionBar();	

		// reading the identification from the preferences, if it is not there
		// it will try to read the name of the device owner
		preferences = getSharedPreferences(PREFS_NAME, 0);
		String identification = preferences.getString("identification",	"");

		if(identification.equals("")){
			identification = readOwnerName();
			// saving the identification field
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("identification", identification);
			editor.commit();
		}

		etIdentification = (EditText) findViewById(R.id.edittext_identification);
		etIdentification.setText(identification);
		
		btnRescanWifi = (Button) findViewById(R.id.button_rescan_wifi);
		btnRescanWifi.setOnClickListener(this);

		etIdentification.addTextChangedListener(this);
		
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		LocalBroadcastManager.getInstance(this).registerReceiver(serviceStartedListener, new IntentFilter("NetworkServiceStarted"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	public void afterTextChanged(Editable s) {
		// saving the identification field
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("identification", etIdentification.getText()
				.toString());
		editor.commit();
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
		getMenuInflater().inflate(R.menu.network_config, menu);
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
		case R.id.network_info:
			Intent i = new Intent(this, NetworkInformationsActivity.class);
			startActivity(i);
			return true;
		case R.id.help:
			HelpDialogFragment hdf = HelpDialogFragment.newInstance( getString(R.string.help_title_network_config), getString(R.string.help_text_network_config) );
	        hdf.show( getFragmentManager( ), "help" );
	        return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	/**
	 * This method is used to extract the name of the device owner
	 * 
	 * @return the name of the device owner
	 */
	private String readOwnerName() {

		Cursor c = getContentResolver().query(
				ContactsContract.Profile.CONTENT_URI, null, null, null, null);
		if (c.getCount() == 0) {
			return "";
		}
		c.moveToFirst();
		String displayName = c.getString(c.getColumnIndex("display_name"));
		c.close();

		return displayName;

	}

	/**
	 * this broadcast receiver listens for incoming instacircle broadcast notifying that network service was started
	 */
	private BroadcastReceiver serviceStartedListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent i = new Intent(NetworkConfigActivity.this, NetworkInformationsActivity.class);
			i.putExtra("goToMain", true);
			startActivity(i);
		}
	};

	@Override
	public void onClick(View view) {
		if (view == btnRescanWifi){
			wifi.startScan();
			Toast.makeText(this, "Rescan initiated", Toast.LENGTH_SHORT).show();
		}
	}

}
