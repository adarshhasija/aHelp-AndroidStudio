package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactRecordsActivity extends Activity {

    private String mContactId;
    private String mContactName;
    private String mContactNumber;

    private Calendar mCalendar = Calendar.getInstance();

    private Button buttonMonthYear;
    private ListView listView;
    private Button buttonNewRecord;
    private TextView textViewNoRecords;

    MenuItem addMenuItem;


    /*
	 * Parse callbacks
	 *
	 */
    private FindCallback<ParseObject> populateListCallbackLocal = new FindCallback<ParseObject>() {

        @Override
        public void done(final List<ParseObject> list, ParseException e) {
            if (e == null) {
             /*   Collections.sort(list, new Comparator<ParseObject>() {
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
                }); */
                RecordAdapter recordAdapter = new RecordAdapter(ContactRecordsActivity.this, 0, list);
                listView.setAdapter(recordAdapter);

                if(list.size() == 0) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                    if(cm.getActiveNetworkInfo() != null) {
                        populateListCloud();
                    }
                    listView.setVisibility(View.GONE);
                    buttonNewRecord.setVisibility(View.VISIBLE);
                }
            } else {
                Log.d("ContactRecordsActivity", "Error: " + e.getMessage());
            }
        }

    };

    private FindCallback<ParseObject> populateListCallbackCloud = new FindCallback<ParseObject>() {

        @Override
        public void done(List<ParseObject> list, ParseException e) {
            if (e == null) {
                if (list.size() > 0) {
                  /*  Collections.sort(list, new Comparator<ParseObject>() {
                        public int compare(ParseObject o1, ParseObject o2) {
                            return o2.getUpdatedAt().compareTo(o1.getUpdatedAt()); //descending
                        }
                    }); */
                    ParseObject.pinAllInBackground("ScribeRecord", list);

                    RecordAdapter recordAdapter = new RecordAdapter(ContactRecordsActivity.this, 0, list);
                    listView.setAdapter(recordAdapter);
                    listView.setVisibility(View.VISIBLE);
                    buttonNewRecord.setVisibility(View.GONE);
                }
                else {
                    listView.setVisibility(View.GONE);
                    buttonNewRecord.setVisibility(View.VISIBLE);
                }
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
        Bundle extras = getIntent().getExtras();

        //ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRecord");
        //query.fromLocalDatastore();
        //query.addAscendingOrder("dateTime");

        ParseQuery<ParseObject> queryStudent = ParseQuery.getQuery("ScribeRecord");
        queryStudent.whereEqualTo("studentId", extras.getString("id"));

        ParseQuery<ParseObject> queryScribe = ParseQuery.getQuery("ScribeRecord");
        queryScribe.whereEqualTo("scribeId", extras.getString("id"));

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(queryStudent);
        queries.add(queryScribe);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.fromLocalDatastore();
        mainQuery.addAscendingOrder("dateTime");

        mainQuery.findInBackground(populateListCallbackLocal);
    }

    private void populateListCloud() {
        Bundle extras = getIntent().getExtras();

        //ParseQuery<ParseObject> cloudQuery = ParseQuery.getQuery("ScribeRecord");
        //cloudQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        //cloudQuery.whereEqualTo("studentId", extras.getString("id"));
        //cloudQuery.addAscendingOrder("dateTime");

        ParseQuery<ParseObject> queryStudent = ParseQuery.getQuery("ScribeRecord");
        queryStudent.whereEqualTo("studentId", extras.getString("id"));

        ParseQuery<ParseObject> queryScribe = ParseQuery.getQuery("ScribeRecord");
        queryScribe.whereEqualTo("scribeId", extras.getString("id"));

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(queryStudent);
        queries.add(queryScribe);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.fromLocalDatastore();
        mainQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        mainQuery.addAscendingOrder("dateTime");

        mainQuery.findInBackground(populateListCallbackCloud);
    }


    private int getInsertIndex(Date newDate) {
        RecordAdapter adapter = (RecordAdapter) listView.getAdapter();
        int size = adapter.getCount();
        int start = 0;
        int end = size - 1;
        while (start <= end) {
            int mid = (start + end)/2;
            ParseObject tmp = adapter.getItem(mid);
            if (0 == newDate.compareTo(tmp.getDate("dateTime"))) {
                return mid;
            }
            else if (-1 == newDate.compareTo(tmp.getDate("dateTime"))) {
                end = mid -1;
            }
            else {
                start = mid + 1;
            }
        }
        return start;
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
        buttonMonthYear.setText(mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + mCalendar.get(Calendar.YEAR));
        buttonMonthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactRecordsActivity.this, MonthYearPickerActivity.class);
                startActivityForResult(intent, 5000);
            }
        });

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordAdapter adapter = (RecordAdapter) listView.getAdapter();
                ParseObject record = adapter.getItem(position);
                Intent intent = new Intent(ContactRecordsActivity.this, RecordEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("parseId", record.getObjectId());
                bundle.putString("uuid", record.getString("uuid"));
                intent.putExtras(bundle);
                startActivityForResult(intent, position);
            }
        });
        buttonNewRecord = (Button) findViewById(R.id.buttonNewRecord);
        textViewNoRecords = (TextView) findViewById(R.id.textViewNoRecords);
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
                    bundle.putInt("month", mCalendar.get(Calendar.MONTH));
                    bundle.putInt("year", mCalendar.get(Calendar.YEAR));
                    intent.putExtras(bundle);
                }
                startActivityForResult(intent, 1000);
            }
        });

        populateList();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
Log.d("HELLO", "**************"+requestCode+"**********"+resultCode+"*********"+data);
        if (resultCode == Activity.RESULT_OK && requestCode == 5000 && data != null) {
            //Month/year changed
            Bundle extras = data.getExtras();
            int month = extras.getInt("month");
            int year = extras.getInt("year");
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.YEAR, year);
            buttonMonthYear.setText(mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + mCalendar.get(Calendar.YEAR));
            populateList();
        }

        else if (resultCode == Activity.RESULT_OK && requestCode == 1000 && data != null) {
            //new record added
            Log.d("HELLO", "**********ONE***********");
            Bundle extras = data.getExtras();
            String uuid = extras.getString("uuid");
            Log.d("HELLO", "***************"+uuid+"****************");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRecord");
            query.fromLocalDatastore();
            query.whereEqualTo("uuid", uuid);
            try {
                ParseObject record = query.getFirst();
                Log.d("HELLO", "**********TWO*************");
                int index = getInsertIndex(record.getDate("dateTime"));
                Log.d("HELLO", "*************"+index+"**************");
                RecordAdapter adapter = (RecordAdapter) listView.getAdapter();
                adapter.insert(record, index);
                adapter.notifyDataSetChanged();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        else if (resultCode == Activity.RESULT_OK && data != null) {
            //existing record modified
            Bundle extras = data.getExtras();
            String uuid = extras.getString("uuid");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRequest");
            query.fromLocalDatastore();
            query.whereEqualTo("uuid", uuid);
            try {
                ParseObject record = query.getFirst();
                RecordAdapter adapter = (RecordAdapter) listView.getAdapter();
                adapter.remove(adapter.getItem(requestCode));
                adapter.insert(record, requestCode);
                adapter.notifyDataSetChanged();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode > 0 && resultCode == Activity.RESULT_OK) {
            //Activity result is ok but data == null...this means record has been deleted
            RecordAdapter adapter = (RecordAdapter) listView.getAdapter();
            adapter.remove(adapter.getItem(requestCode));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_records, menu);
        addMenuItem = (MenuItem) menu.findItem(R.id.action_new);

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
                bundle.putInt("month", mCalendar.get(Calendar.MONTH));
                bundle.putInt("year", mCalendar.get(Calendar.YEAR));
                intent.putExtras(bundle);
            }
            startActivityForResult(intent, 1000);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
