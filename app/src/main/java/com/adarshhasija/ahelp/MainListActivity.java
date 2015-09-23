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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainListActivity extends Activity {

	
	private ArrayList<ParseObject> recordsList = new ArrayList<ParseObject>();

	private ListView listView;
	private Button buttonNewContact;

	private MenuItem progressButton;
	private MenuItem searchButton;
	private MenuItem addPersonButton;
	private MenuItem contactsButton;
	private MenuItem coordinatorButton;
	private MenuItem refreshButton;
	private MenuItem logoutButton;
	private boolean refreshing=false;
	
	
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
		Toast.makeText(getBaseContext(), "Refresh completed", Toast.LENGTH_SHORT).show();
	}
	
	private void contactsPressed() {
		Intent intent = new Intent(this, ContactsListActivity.class);
		startActivity(intent);
	};
	
	private void coordinatorPressed() {
		Intent intent = new Intent(this, CoordinatorListActivity.class);
		startActivity(intent);
	};
	
	private void logoutPressed() {
		ParseUser.logOut();
		Intent loginIntent = new Intent(this, Login.class);
		startActivity(loginIntent);
		finish();
	};
	
	
	
	
	/*
	 * Private functions
	 * 
	 * 
	 */	
	private void populateList() {
        MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
        List<Contact> localContacts = mainApplication.getUpdatedDeviceContactsListAsArray();
		ContactsListAdapter adapter = new ContactsListAdapter(this, 0, localContacts);
		listView.setAdapter(adapter);

		if (localContacts.size() == 0) {
			listView.setVisibility(View.GONE);
			buttonNewContact.setVisibility(View.VISIBLE);
		}
		else if (localContacts.size() > 0) {
			listView.setVisibility(View.VISIBLE);
			buttonNewContact.setVisibility(View.GONE);
		}
	}
	
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_main_list);

		buttonNewContact = (Button) findViewById(R.id.buttonNewContact);
		buttonNewContact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_INSERT);
				i.setType(ContactsContract.Contacts.CONTENT_TYPE);
				if (Integer.valueOf(Build.VERSION.SDK) > 14)
					i.putExtra("finishActivityOnSaveCompleted", true); // Fix for 4.0.3 +
				startActivityForResult(i, 50000);
			}
		});
		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Intent intent = new Intent(this, MainDetailActivity.class);
				ParseAnalytics.trackEvent("contactTapped");
				Contact contact = (Contact) listView.getAdapter().getItem(position);
				Intent intent = new Intent(MainListActivity.this, ContactRecordsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("id", contact.getId());
				bundle.putString("number", contact.getNumber());
				bundle.putString("name", contact.getName());
				intent.putExtras(bundle);
				startActivity(intent);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.search:
	            return true;
	        case R.id.add_person:
	            addPersonPressed();
	            return true;
	    /*    case R.id.contacts:
	        	contactsPressed();
	        	return true;
	        case R.id.coordinator:
	        	coordinatorPressed();
	        	return true;	*/
	        case R.id.refresh:
	        	refreshPressed();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.records_list_activity, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(onQueryTextListener);
	    searchButton = menu.findItem(R.id.search);
	    
	    //uncomment this to set search to go to a new results page
	    //searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
	    
		
		addPersonButton = (MenuItem)menu.findItem(R.id.add_person);
		//contactsButton = (MenuItem)menu.findItem(R.id.contacts);
		//coordinatorButton = (MenuItem)menu.findItem(R.id.coordinator);
		refreshButton = (MenuItem)menu.findItem(R.id.refresh);
		//logoutButton = (MenuItem)menu.findItem(R.id.logout);
		
		return super.onCreateOptionsMenu(menu);
	}
}
