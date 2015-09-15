package com.adarshhasija.ahelp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MonthYearPickerActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            if (name != null) {
                setTitle(name);
            }
        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		ArrayList<String> list = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
	/*	if(		c.getActualMaximum(Calendar.DAY_OF_MONTH) == c.get(Calendar.MONTH) &&
				c.get(Calendar.HOUR_OF_DAY) == 23 && 
				c.get(Calendar.MINUTE) > 45) {
			c.add(Calendar.MINUTE, 15);  //We are going to set it to the next day
		}	*/
		String monthString= "";
		int year = 0;
        Calendar tempC = Calendar.getInstance();
        int selectedIndex = 0;
		for(int i=0; i < 12/*6*/; i++) {
			monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
			year = c.get(Calendar.YEAR);
			list.add(monthString + " " + year);
            if (c.get(Calendar.YEAR) == tempC.get(Calendar.YEAR) &&
                    c.get(Calendar.MONTH) == tempC.get(Calendar.MONTH)) {
                selectedIndex = i;
            }
			c.add(Calendar.MONTH, 1);
		}
		
		LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(this, 0, list);
        setListAdapter(adapter);
        getListView().setSelection(selectedIndex);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Calendar c = Calendar.getInstance();
		//c.add(Calendar.MONTH, position);
        c.add(Calendar.MONTH, position - 6);
		Bundle bundle = new Bundle();
        bundle.putInt("month", c.get(Calendar.MONTH));
        bundle.putInt("year", c.get(Calendar.YEAR));

        //Intent intent = new Intent(this,DatePickerActivity.class);
        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK) {
			setResult(Activity.RESULT_OK, data);
	        finish();
		}
	}
	

}
