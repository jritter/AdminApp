
package ch.bfh.evoting.adminapp;

import java.util.List;

import ch.bfh.evoting.votinglib.entities.Option;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * List adapter showing a list of the options with the corresponding result
 * @author von Bergen Philémon
 */
public class PollOptionAdapter extends ArrayAdapter<Option> {

	private Context context;
	private List<Option> values;

	/**
	 * Create an adapter object
	 * @param context android context
	 * @param textViewResourceId id of the layout that must be inflated
	 * @param objects list of options that have to be listed
	 */
	public PollOptionAdapter(Context context, int textViewResourceId, List<Option> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.values = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View view;
		if (null == convertView) {
			view =  inflater.inflate(R.layout.list_item_polloption, parent, false);
		} else {
			view = convertView;
		}
		
		TextView tvContent = (TextView)view.findViewById(R.id.textview_content);
		tvContent.setText(this.values.get(position).getText());

		return view;
	}


}
