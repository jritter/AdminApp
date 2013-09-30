package ch.bfh.evoting.adminapp.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import ch.bfh.evoting.adminapp.R;
import ch.bfh.evoting.votinglib.entities.Participant;

/**
 * Adapter listing the participants that are present in the network and if they are included or not in the electorate
 * This class is used in the Android ListView
 * @author Philémon von Bergen
 *
 */
public class NetworkParticipantListAdapter extends ArrayAdapter<Participant> {

	private Context context;
	private List<Participant> values;
	
	/**
	 * Create an adapter object
	 * @param context android context
	 * @param textViewResourceId id of the layout that must be inflated
	 * @param objects list of participants that have to be displayed
	 */
	public NetworkParticipantListAdapter(Context context, int textViewResourceId, List<Participant> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.values=objects;
	}
	
	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View view;
		if (null == convertView) {
			//when view is created
			view =  inflater.inflate(R.layout.list_item_participant_network, parent, false);
			CheckedTextView ctv = (CheckedTextView)view.findViewById(R.id.checked_textview_network_participant);
			ctv.setTag(position);
			ctv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckedTextView tv = (CheckedTextView)v;
					tv.toggle();
					if(tv.isChecked()){
						values.get((Integer)v.getTag()).setSelected(true);
					} else {
						values.get((Integer)v.getTag()).setSelected(false);
					}
				}
			});
			if(values.get(position).isSelected()){
				ctv.setChecked(true);
			} else {
				ctv.setChecked(false);
			}
		} else {
			view = convertView;
		}
		
		
		//set the participant identification
		CheckedTextView ctvParticipant =  (CheckedTextView)view.findViewById(R.id.checked_textview_network_participant);
		ctvParticipant.setText(this.values.get(position).getIdentification());
		
		return view;
	}
	
	@Override
	public Participant getItem (int position)
	{
		return super.getItem (position);
	}

}