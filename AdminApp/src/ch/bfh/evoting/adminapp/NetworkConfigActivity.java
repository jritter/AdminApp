package ch.bfh.evoting.adminapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class NetworkConfigActivity extends Activity implements OnItemClickListener, ConnectNetworkDialogFragment.NoticeDialogListener {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private NetworkArrayAdapter adapter;
	//private AdhocWifiManager adhoc;

	private ArrayList<HashMap<String, Object>> arraylist = new ArrayList<HashMap<String, Object>>();
	private HashMap<String, Object> lastItem = new HashMap<String, Object>();

	private ListView lv;

	private SharedPreferences preferences;
	private List<ScanResult> results;
	private List<WifiConfiguration> configuredNetworks;

	private ScanResult selectedResult;

	private EditText txtIdentification;

	private WifiManager wifi;
	private AdhocWifiManager adhoc;

	private BroadcastReceiver wifibroadcastreceiver;
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter nfcIntentFilter;
	private IntentFilter[] intentFiltersArray;
	private boolean nfcAvailable;
	private Parcelable[] rawMsgs;
	private int selectedNetId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_config);
		// Show the Up button in the action bar.
		setupActionBar();
		
		lv = (ListView) findViewById(R.id.listview_networks);
		// Handling the WiFi
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifi.startScan();
		adhoc = new AdhocWifiManager(wifi);
		
		lastItem.put("SSID", "Create new network...");
		
		adapter = new NetworkArrayAdapter(this, R.layout.list_item_network,
				arraylist);
		adapter.add(lastItem);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);

		// defining what happens as soon as scan results arrive
		wifibroadcastreceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context c, Intent intent) {
				results = wifi.getScanResults();
				configuredNetworks = wifi.getConfiguredNetworks();
				arraylist.clear();

				for (ScanResult result : results) {
					HashMap<String, Object> item = new HashMap<String, Object>();

					item.put("known", false);

					// check whether the network is already known, i.e. the
					// password is already stored in the device
					for (WifiConfiguration configuredNetwork : configuredNetworks) {
						if (configuredNetwork.SSID.equals("\"".concat(
								result.SSID).concat("\""))) {
							item.put("known", true);
							item.put("netid", configuredNetwork.networkId);
							break;
						}
					}

					if (result.capabilities.contains("WPA")
							|| result.capabilities.contains("WEP")) {
						item.put("secure", true);
					} else {
						item.put("secure", false);
					}
					item.put("SSID", result.SSID);
					item.put("capabilities", result.capabilities);
					item.put("object", result);
					arraylist.add(item);
					Log.d(TAG, result.SSID + " known: " + item.get("known")
							+ " netid " + item.get("netid"));
				}
				arraylist.add(lastItem);
				adapter.notifyDataSetChanged();

			}
		};

		// register the receiver, subscribing for the SCAN_RESULTS_AVAILABLE
		// action
		registerReceiver(wifibroadcastreceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
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
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	public void onItemClick(AdapterView<?> listview, View view, int position,
			long id) {

		if (listview.getAdapter().getCount() - 1 == position) {
			// handling the last item in the list, which is the "Create network"
			// item
			//Intent intent = new Intent(this, CreateNetworkActivity.class);
			//startActivity(intent);
		} else {
			// extract the Hashmap assigned to the position which has been
			// clicked
			HashMap<String, Object> hash = (HashMap<String, Object>) listview
					.getAdapter().getItem(position);

			selectedResult = (ScanResult) hash.get("object");
			selectedNetId = -1;

			// going through the different connection scenarios
			DialogFragment dialogFragment;
			if ((Boolean) hash.get("secure") && !((Boolean) hash.get("known"))) {
				dialogFragment = new ConnectNetworkDialogFragment(true);
			} else if ((Boolean) hash.get("known")) {
				selectedNetId = (Integer) hash.get("netid");
				dialogFragment = new ConnectNetworkDialogFragment(false);
			} else {
				dialogFragment = new ConnectNetworkDialogFragment(false);
			}

			dialogFragment.show(getFragmentManager(), TAG);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bfh.instacircle.ConnectNetworkDialogFragment.NoticeDialogListener#
	 * onDialogNegativeClick(android.app.DialogFragment)
	 */
	public void onDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bfh.instacircle.ConnectNetworkDialogFragment.NoticeDialogListener#
	 * onDialogPositiveClick(android.app.DialogFragment)
	 */
	public void onDialogPositiveClick(DialogFragment dialog) {

		if (selectedNetId != -1) {
			adhoc.connectToNetwork(selectedNetId, this);
		} else {
			adhoc.connectToNetwork(selectedResult.SSID,
					((ConnectNetworkDialogFragment) dialog).getNetworkKey(),
					this);
		}

//		SharedPreferences.Editor editor = preferences.edit();
//		editor.putString("SSID", selectedResult.SSID);
//		editor.putString("password",
//				((ConnectNetworkDialogFragment) dialog).getPassword());
//		editor.commit();

		dialog.dismiss();
	}

}
