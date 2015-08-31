package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactRecordsActivity extends Activity {

    private String mContactId;
    private String mContactName;
    private String mContactNumber;

    private ListView listView;
    private TextView textViewEmpty;


    /*
	 * Parse callbacks
	 *
	 */
    private FindCallback<ParseObject> populateListCallbackLocal = new FindCallback<ParseObject>() {

        @Override
        public void done(final List<ParseObject> list, ParseException e) {
            if (e == null) {
                if(list.size() == 0) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                    if(cm.getActiveNetworkInfo() != null) {
                        populateListCloud();
                    }
                    return;
                }
                Collections.sort(list, new Comparator<ParseObject>() {
                    public int compare(ParseObject o1, ParseObject o2) {
                        if (o2.getUpdatedAt() == null)
                            if (o1.getUpdatedAt() == null)
                                return 0; //equal
                            else
                                return -1; // null is before other strings
                        else // this.member != null
                            if (o1.getUpdatedAt() == null)
                                return 1;  // all other strings are after null
                            else
                                return o2.getUpdatedAt().compareTo(o1.getUpdatedAt()); //descending
                    }
                });
                RecordAdapter recordAdapter = new RecordAdapter(ContactRecordsActivity.this, 0, list);
                listView.setAdapter(recordAdapter);
                listView.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
            } else {
                Log.d("ContactRecordsActivity", "Error: " + e.getMessage());
            }
        }

    };

    private FindCallback<ParseObject> populateListCallbackCloud = new FindCallback<ParseObject>() {

        @Override
        public void done(List<ParseObject> list, ParseException e) {
            if (e == null) {
                Collections.sort(list, new Comparator<ParseObject>() {
                    public int compare(ParseObject o1, ParseObject o2) {
                        return o2.getUpdatedAt().compareTo(o1.getUpdatedAt()); //descending
                    }
                });
                ParseObject.pinAllInBackground("Record", list);

                RecordAdapter recordAdapter = new RecordAdapter(ContactRecordsActivity.this, 0, list);
                listView.setAdapter(recordAdapter);
                listView.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
            } else {
                Log.d("ContactRecordsActivity", "Error: " + e.getMessage());
            }
        }

    };


    /*
	 * Private functions
	 *
	 *
	 */
    private void populateList() {
		populateListLocal();
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {
			//populateListCloud();
		}
    }

    private void populateListLocal() {
        ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Record");
        localQuery.fromLocalDatastore();
        localQuery.findInBackground(populateListCallbackLocal);
    }

    private void populateListCloud() {
        ParseQuery<ParseObject> cloudQuery = ParseQuery.getQuery("Record");
        cloudQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        cloudQuery.findInBackground(populateListCallbackCloud);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_records);

        Bundle extras = getIntent().getExtras();
        mContactId = extras.getString("id");
        mContactName = extras.getString("name");
        mContactNumber = extras.getString("number");
        setTitle(mContactName);

        listView = (ListView) findViewById(R.id.list);
        textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);
        listView.setVisibility(View.GONE);
        textViewEmpty.setVisibility(View.VISIBLE);

        populateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            Intent intent = new Intent(this, RecordEditActivity.class);
            startActivityForResult(intent, 0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
