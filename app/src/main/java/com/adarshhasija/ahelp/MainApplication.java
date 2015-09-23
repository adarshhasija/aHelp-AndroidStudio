package com.adarshhasija.ahelp;

import android.app.Application;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.parse.Parse;
import com.parse.ParseACL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainApplication extends Application{

	/*
	 * Helper functions
	 * 
	 * 
	 */
	public HashMap<String, String> getUpdatedDeviceContactsList() {
		HashMap<String, String> localContacts=new HashMap<String, String>();
		
		Cursor data = getContentResolver()
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}, null, null,  Phone.DISPLAY_NAME + " ASC");
		
		int i=0;
		data.moveToFirst();
		while (data.isAfterLast() == false) 
		{
			//String number = data.getString(data.getColumnIndex(Phone.NUMBER));
			int numberIndex = data.getColumnIndex(Phone.NUMBER);
			int nameIndex = data.getColumnIndex(Phone.DISPLAY_NAME);
			String number = data.getString(numberIndex).replaceAll("\\s+","");
			String name = data.getString(nameIndex);
		    if(!localContacts.containsKey(number)) {  
		    	localContacts.put(number, name);
		    }
		    i++;
		    data.moveToNext();
		}
		data.close();
		
		return localContacts;
	}
	
	public List getUpdatedDeviceContactsListAsArray() {
		List<Contact> localContacts=new ArrayList<Contact>();
		
		Cursor data = getContentResolver()
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}, null, null,  Phone.DISPLAY_NAME + " ASC");
		
		int i=0;
		data.moveToFirst();
		while (data.isAfterLast() == false) 
		{
            Contact contact = new Contact();
            int idIndex = data.getColumnIndex(Phone._ID);
			int numberIndex = data.getColumnIndex(Phone.NUMBER);
			int nameIndex = data.getColumnIndex(Phone.DISPLAY_NAME);
            String id = data.getString(idIndex);
			String number = data.getString(numberIndex).replaceAll("\\s+","");
			String name = data.getString(nameIndex);
            contact.setId(id);
            contact.setName(name);
            contact.setNumber(number);
            localContacts.add(contact);
		    i++;
		    data.moveToNext();
		}
		data.close();
		
		return localContacts;
	}
		
	
/*	
	static boolean validEmail(String email) {
	    // editing to make requirements listed
	    // return email.matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}");
	    return email.matches("[A-Z0-9._%+-][A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{3}");
	}
*/
	

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.enableLocalDatastore(getApplicationContext());
		Parse.initialize(this, "9f3p730Ynsj9hLbeEuGxGC9Nifwmh5Co0NCAsbi5", "MogP0et66o0SlSgS2XXFNJhVqnlvICy3L3don29q");
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);

		//PushService.setDefaultPushCallback(this, RecordListActivity.class);
	}
	
}
