package com.adarshhasija.ahelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends ArrayAdapter<ParseObject> implements Filterable {

	private final Context context;
	private List<ParseObject> recordList;
	private final List<ParseObject> backupList; //used when filtering is happening
	static class ViewHolderRecord {
	    TextView userView;
        TextView locationView;
	    TextView subjectView;
        TextView studentView;
        TextView scribeView;
	    TextView dateTimeView;
	    TextView lastActionView;
	    TextView updatedAtDateView;
	    ImageView iconView;
	}
	
	/*
	 * my custom Filter
	 * 
	 * 
	 */
	Filter filter = new Filter() {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			//If recordList size is less, filtering has happened
			//restore original before continuing
			if(recordList.size() < backupList.size()) {
				restoreOriginalList();
			}
			FilterResults filterResults = new FilterResults();   
	         ArrayList<ParseObject> tempList=new ArrayList<ParseObject>();
	         //constraint is the result from text you want to filter against. 
	         //objects is your data set you will filter from
	         if(constraint != null && recordList !=null) {
	             int length=recordList.size();
	             int i=0;
	                while(i<length){
	                    ParseObject record=recordList.get(i);
	                    ParseUser creator = record.getParseUser("createdBy");
	                    try {
							creator.fetchFromLocalDatastore();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    String firstName = creator.getString("firstName").toLowerCase();
	                    String lastName = creator.getString("lastName").toLowerCase();
	                    String student = firstName + " " + lastName;
	                    if(student.contains(constraint)) {
	                    	tempList.add(record);
	                    }
	                    i++;
	                }
	                //following two lines is very important
	                //as publish result can only take FilterResults objects
	                filterResults.values = tempList;
	                filterResults.count = tempList.size();
	          }
	          return filterResults;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			recordList = (ArrayList<ParseObject>) results.values;
	          if (recordList.size() > 0) {
	           notifyDataSetChanged();
	          } else {
	              notifyDataSetInvalidated();
	          }
			
		}
		
	};
	
	
	/*
	 * private function to controller what is and isnt visible
	 * 
	 */
	private void visibilitySettings(ViewHolderRecord viewHolder)
	{
		viewHolder.subjectView.setVisibility(View.GONE);
		viewHolder.updatedAtDateView.setVisibility(View.GONE);
	}
	
	/*
	 * Private function to handle logic for setting dateTime value as string
	 * 
	 */
	private String getDateValueAsString(Date dateTime) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		int record_date = c.get(Calendar.DATE);
		String monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);

		String final_date;
	/*	if(cur_date == record_date) {
			final_date = "TODAY";
		}
		else if(record_date == cur_date - 1) {
			final_date = "YESTERDAY";
		}
		else if(record_date == cur_date + 1) {
			final_date = "TOMORROW";
		}
		else {
			final_date = monthString + " " + Integer.toString(record_date);
		}	*/
		final_date = monthString + " " + Integer.toString(record_date);
		
		return final_date;
	}
	
	private String examUpdateFormatted(ParseUser fromUser) {
		try {
			fromUser.fetchFromLocalDatastore();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(fromUser.equals(ParseUser.getCurrentUser())) {
			return "You changed the exam details";
		}
		
		return fromUser.getString("firstName") + " changed the exam details";
	}
	
	private String getLastActionFormatted(ParseObject action) {
		try {
			ParseUser currentUser = ParseUser.getCurrentUser();
			ParseUser fromUser = action.getParseUser("from");
			ParseUser toUser = action.getParseUser("to");
			String type = action.getString("type");
			String statusString = action.getString("statusString");
			if(type.equals("examUpdate")) {
				return examUpdateFormatted(fromUser);
			}
			fromUser.fetchFromLocalDatastore();
			toUser.fetchFromLocalDatastore();
			
			String sender = fromUser.getString("firstName");
			String receiver = toUser.getString("firstName");
			
			if(fromUser.equals(currentUser)) {
				sender = "You";
			}
			if(toUser.equals(currentUser)) {
				receiver = "you";
			}
			
			String finalResult = sender + " requested " + receiver;
			if(statusString != null) {
				if(statusString.equals("accepted")) {
					finalResult = sender + " " + statusString + " the request"; 
				}
			}
			
			return finalResult;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private int getStatusIcon(ParseObject record, ViewHolderRecord viewHolder)
	{
		int imageResource;
		
		if(record.getBoolean("status") == false) {
			imageResource = R.drawable.ic_action_event;
		}
		else if(record.getBoolean("status") == true) {
			imageResource = R.drawable.ic_action_accept;
		}
		else {
			imageResource = R.drawable.ic_action_event;
		}
	/*	if(record.getString("status").equals("accepted")) {
			imageResource = R.drawable.ic_action_accept;
		}
		else if(record.getString("status").equals("rejected")) {
			imageResource = R.drawable.ic_action_cancel;
		}
		else {
			imageResource = R.drawable.ic_action_event;
		}	*/
		return imageResource;
	}
	
	public RecordAdapter(Context context, int resource, List<ParseObject> values) {
		super(context, resource, values);
		this.context = context;
		this.recordList = values;
		this.backupList = new ArrayList<ParseObject>(values);
	}

	@Override
	public int getCount() {
		return recordList != null?recordList.size() : 0;
	}

	@Override
	public ParseObject getItem(int position) {
		return recordList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolderRecord viewHolder;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.record_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.userView = (TextView) convertView.findViewById(R.id.user);
            viewHolder.locationView = (TextView) convertView.findViewById(R.id.location);
			viewHolder.subjectView = (TextView) convertView.findViewById(R.id.subject);
            viewHolder.studentView = (TextView) convertView.findViewById(R.id.student);
            viewHolder.scribeView = (TextView) convertView.findViewById(R.id.scribe);
			viewHolder.lastActionView = (TextView) convertView.findViewById(R.id.lastAction);
			viewHolder.dateTimeView = (TextView) convertView.findViewById(R.id.recordDateTime);
			viewHolder.updatedAtDateView = (TextView) convertView.findViewById(R.id.updatedAtDate);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolderRecord) convertView.getTag();
		}
		
		visibilitySettings(viewHolder);
		
	    ParseObject record = recordList.get(position);
		if(record != null) {
			String recordDateTime = getDateValueAsString(record.getDate("dateTime"));
            String placeName = record.getString("placeName");
            String subject = record.getString("subject");
            String studentName = record.getString("studentName");
            String scribeName = record.getString("scribeName");
			viewHolder.dateTimeView.setText(recordDateTime);
            viewHolder.locationView.setText(placeName);
            viewHolder.subjectView.setText(subject);
            viewHolder.studentView.setText("Student: " + studentName);
			viewHolder.scribeView.setText("Scribe: " + scribeName);

			
			if(record.getObjectId() != null) {
				String updatedAt = getDateValueAsString(record.getUpdatedAt());
				viewHolder.updatedAtDateView.setText(updatedAt);
				viewHolder.updatedAtDateView.setContentDescription("Last modified: "+updatedAt);
			}

			convertView.setContentDescription("Exam on " + recordDateTime + " at " + placeName + ". Tap for more details");

			//viewHolder.categoryView.setTag(record);
		}

		return convertView;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}
	
	private void restoreOriginalList() {
		recordList.clear();
		recordList.addAll(backupList);
	}

	
}
