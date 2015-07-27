package com.adarshhasija.ahelp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class MainActivity extends Activity {
	
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ParseObject.unpinAllInBackground("Location");
		//ParseObject.unpinAllInBackground("Subject");
		//ParseObject.unpinAllInBackground("Action");
		//ParseObject.unpinAllInBackground("Exam");
		//ParseObject.unpinAllInBackground("Event");
		//ParseObject.unpinAllInBackground("ScribeRequestSubject");
		//ParseObject.unpinAllInBackground("ScribeRequestLocation");
		//ParseObject.unpinAllInBackground("ScribeRequest");
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null) {
			Intent intent = new Intent(this, CountryPickerActivity.class);
			startActivity(intent);
			finish();						 	 
		}
		else {
			Intent loginIntent = new Intent(this, Login.class);
			startActivity(loginIntent);
			finish();
		}	
	}

	
}
