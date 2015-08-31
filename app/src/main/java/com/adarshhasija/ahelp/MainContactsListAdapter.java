package com.adarshhasija.ahelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarshhasija on 8/14/15.
 */
public class MainContactsListAdapter extends ArrayAdapter<Contact> implements Filterable {

    private final Context context;
    private List<Contact> values;
    private final List<Contact> backupList; //used when filtering is happening

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
            if(values.size() < backupList.size()) {
                restoreOriginalList();
            }
            FilterResults filterResults = new FilterResults();
            ArrayList<Contact> tempList=new ArrayList<Contact>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(constraint != null && values !=null) {
                String constraintString = constraint.toString().toLowerCase();
                int length=values.size();
                int i=0;
                while(i<length){
                    Contact contact=values.get(i);
                    String contactName = contact.getName();
                    if(contactName.toLowerCase().contains(constraint)) {
                        tempList.add(contact);
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
            values = (ArrayList<Contact>) results.values;
            if (values.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

    };

    @Override
    public Filter getFilter() {
        return filter;
    }

    private void restoreOriginalList() {
        values.clear();
        values.addAll(backupList);
    }

    public MainContactsListAdapter(Context context, int resource, List<Contact> values) {
        super(context, resource, values);
        this.context = context;
        this.values = values;
        this.backupList = new ArrayList<Contact>(values);
    }

    @Override
    public int getCount() {
        return values != null?values.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_large_height, parent, false);
        TextView labelView = (TextView) rowView.findViewById(R.id.label);

        Contact contact = values.get(position);

        labelView.setText(contact.getName());
        labelView.setContentDescription(contact.getName());
        rowView.setTag(contact);
        return rowView;
    }

}
