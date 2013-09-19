/*
 *  UniCrypt Cryptographic Library
 *  Copyright (c) 2013 Berner Fachhochschule, Biel, Switzerland.
 *  All rights reserved.
 *
 *  Distributable under GPL license.
 *  See terms of license at gnu.org.
 *  
 */

package ch.bfh.instavoteadmin;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Implements an ArrayAdapter which maps the values of the Hashmaps stored in
 * the ArrayList to the views in the layout displaying all the available WLAN
 * networks
 * 
 * @author Juerg Ritter (rittj1@bfh.ch)
 */
public class PollQuestionArrayAdapter extends ArrayAdapter<HashMap<String, String>> {

	private ArrayList<HashMap<String, String>> items;
	private Context context;
	private String capabilities;

	/**
	 * @param context
	 *            The context from which it has been created
	 * @param textViewResourceId
	 *            The id of the item layout
	 * @param items
	 *            The ArrayList with the values
	 */
	public PollQuestionArrayAdapter(Context context, int textViewResourceId,
			ArrayList<HashMap<String, String>> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item_pollquestion, null);
		}

		// extract the Hashmap at the according postition of the ArrayList
		HashMap<String, String> item = items.get(position);
		if (item != null) {
			// extract the views in the layout
			TextView content = (TextView) view.findViewById(R.id.content);
			TextView description = (TextView) view
					.findViewById(R.id.description);
			ImageView icon = (ImageView) view.findViewById(R.id.icon);
			content.setText((String) item.get("question"));
		}
		return view;
	}
}
