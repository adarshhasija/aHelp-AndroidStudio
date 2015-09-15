package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ContactRecordsActivity extends Activity {

    private String mContactId;
    private String mContactName;
    private String mContactNumber;

    private Button buttonMonthYear;
    private ListView listView;
    private Button buttonNewRecord;
    private TextView textViewNoRecords;


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
                buttonNewRecord.setVisibility(View.GONE);
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
                buttonNewRecord.setVisibility(View.GONE);
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
        buttonMonthYear = (Button) findViewById(R.id.buttonMonthYear);
        Calendar c = Calendar.getInstance();
        buttonMonthYear.setText(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + c.get(Calendar.YEAR));
        buttonMonthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactRecordsActivity.this, MonthYearPickerActivity.class);
                startActivityForResult(intent, 5000);
            }
        });

        listView = (ListView) findViewById(R.id.list);
        buttonNewRecord = (Button) findViewById(R.id.buttonNewRecord);
        listView.setVisibility(View.GONE);
        buttonNewRecord.setVisibility(View.VISIBLE);
        buttonNewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactRecordsActivity.this, RecordEditActivity.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", extras.getString("id"));
                    bundle.putString("number", extras.getString("number"));
                    bundle.putString("name", extras.getString("name"));
                    bundle.putInt("month", extras.getInt("month"));
                    bundle.putInt("year", extras.getInt("year"));
                    intent.putExtras(bundle);
                }
                startActivityForResult(intent, 0);
            }
        });

        //populateList();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 5000 && data != null) {
            Bundle extras = data.getExtras();
            int month = extras.getInt("month");
            int year = extras.getInt("year");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, month);
            c.set(Calendar.YEAR, year);
            buttonMonthYear.setText(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + c.get(Calendar.YEAR));
        }
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
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", extras.getString("id"));
                bundle.putString("number", extras.getString("number"));
                bundle.putString("name", extras.getString("name"));
                bundle.putInt("month", extras.getInt("month"));
                bundle.putInt("year", extras.getInt("year"));
                intent.putExtras(bundle);
            }
            startActivityForResult(intent, 0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
