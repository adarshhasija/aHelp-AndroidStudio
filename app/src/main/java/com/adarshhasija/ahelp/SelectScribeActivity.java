package com.adarshhasija.ahelp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

public class SelectScribeActivity extends Activity {


    private ListView listView;

    private MenuItem searchButton;


    /*
	 * Action bar buttons
	 * Private functions
	 *
	 */
    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newText) {
            ContactsListAdapter adapter = (ContactsListAdapter) listView.getAdapter();
            if(adapter != null) {
                adapter.getFilter().filter(newText);
            }
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            // TODO Auto-generated method stub
            return false;
        }


    };

    private void populateList() {
        MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
        List<Contact> localContacts = mainApplication.getUpdatedDeviceContactsListAsArray();
        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        //remove the student from the list as the student cannot be their own scribe
        for (int i=0; i < localContacts.size(); i++) {
            Contact c = localContacts.get(i);
            if (c.getId().equals(id)) {
                localContacts.remove(i);
            }
        }
        final ContactsListAdapter adapter = new ContactsListAdapter(this, 0, localContacts);
        listView.setAdapter(adapter);
    }


    /*
	 * Action bar item functions
	 *
	 *
	 *
	 */
    private void addPersonPressed() {
        //Intent intent = new Intent(this, RecordEditActivity.class);
        //int index = 50000; //random very large integer to show insert
        //startActivityForResult(intent, index);
        Intent i = new Intent(Intent.ACTION_INSERT);
        i.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (Integer.valueOf(Build.VERSION.SDK) > 14)
            i.putExtra("finishActivityOnSaveCompleted", true); // Fix for 4.0.3 +
        startActivityForResult(i, 50000);
    };

    private void refreshPressed() {
        populateList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_scribe);

        setTitle("Select Scribe");

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ContactsListAdapter adapter = (ContactsListAdapter) listView.getAdapter();
                Contact contact = adapter.getItem(position);
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("scribeId", contact.getId());
                bundle.putString("scribeName", contact.getName());
                bundle.putString("scribeNumber", contact.getNumber());
                returnIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        populateList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();

        if (requestCode == 50000 && resultCode == Activity.RESULT_OK) {
            populateList();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_scribe, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        searchButton = menu.findItem(R.id.search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                return true;
            case R.id.add_person:
                addPersonPressed();
                return true;
            case R.id.refresh:
                refreshPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
