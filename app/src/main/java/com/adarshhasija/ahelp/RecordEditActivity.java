package com.adarshhasija.ahelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RecordEditActivity extends Activity { //extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	private ParseObject scribeRecord=null;

	private ListView listView;
	private Button buttonCallScribe;

	private String studentId=null;
	private String studentName = null;
	private String studentNumber = null;
	private Calendar dateTime = Calendar.getInstance();
	//This is the reference to the currently selected location
	private String placeId=null;
	private String placeName=null;
	private String placeAddress=null;
	private String placePhoneNumber=null;
	private Double latitude=0.0;
	private Double longitude=0.0;
	//This is a reference to the currently selected subject
    private String subjectString=null;
    //This is a reference to the selected scribe
    private String scribeId=null;
    private String scribeName=null;
    private String scribeNumber=null;
	
	//MenuItems
	private MenuItem discardButton;
	private MenuItem saveButton;
	
	/*
	 * Parse callbacks
	 * 
	 */
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
			/*	JSONObject jsonObj=new JSONObject();
	        	try {
					jsonObj.put("action", "com.adarshhasija.ahelp.intent.RECEIVE");
					jsonObj.put("objectId", scribeRequest.getObjectId());
					jsonObj.put("uuid", scribeRequest.getString("uuid"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	HashMap<String, Object> params = new HashMap<String, Object>();
	        	List<ParseObject> phoneNumbers = scribeRequest.getList("phoneNumbers");
				params.put("recipientPhoneNumber", phoneNumbers.get(0));
				params.put("data", jsonObj);
				ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
				   public void done(String success, ParseException e) {
				       if (e == null) {
				    	   toggleProgressButton();
				    	   Bundle bundle = new Bundle();
							bundle.putString("parseId", scribeRequest.getObjectId());
							bundle.putString("uuid", scribeRequest.getString("uuid"));
							Intent returnIntent = new Intent();
							returnIntent.putExtras(bundle);
							setResult(Activity.RESULT_OK, returnIntent);
							finish();
				       }
				       else {
				    	   Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				       }
				   }
				});	    */
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("uuid", scribeRecord.getString("uuid"));
				returnIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
				
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
      
    /*
     * Action bar button functions
     *   
     */
    private void savePressed() throws ParseException {
    	if(!validate()) {
    		return;
    	}
    /*	ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
    		if(cm.getActiveNetworkInfo() != null) {

			} */

        if (scribeRecord == null) {
            scribeRecord = new ParseObject("ScribeRecord");
            scribeRecord.put("uuid", UUID.randomUUID().toString());
            ParseACL recordAcl = new ParseACL();
            recordAcl.setReadAccess(ParseUser.getCurrentUser(), true);
            recordAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
            scribeRecord.setACL(recordAcl);
			scribeRecord.put("user", ParseUser.getCurrentUser());

			scribeRecord.put("studentId", studentId);
			scribeRecord.put("studentName", studentName);
			scribeRecord.put("studentNumber", studentNumber);
        }

        //long dateTimeMillis = dateTime.getTimeInMillis();
        //Calendar dateTime = Calendar.getInstance();
        //dateTime.setTimeInMillis(dateTimeMillis);
        scribeRecord.put("dateTime", dateTime.getTime());
        scribeRecord.put("placeId", placeId);
        scribeRecord.put("placeName", placeName);
        scribeRecord.put("placeAddress", placeAddress);
        scribeRecord.put("placePhoneNumber", placePhoneNumber);
        scribeRecord.put("latitude", latitude);
        scribeRecord.put("longitude", longitude);
		scribeRecord.put("subject", subjectString);
        scribeRecord.put("scribeId", scribeId);
        scribeRecord.put("scribeName", scribeName);
        scribeRecord.put("scribeNumber", scribeNumber);
        scribeRecord.saveEventually();
        scribeRecord.pinInBackground("ScribeRecord", saveCallback);
    }

	private void discardPressed() {
		new AlertDialog.Builder(RecordEditActivity.this)
				.setTitle("Are you sure?")
				.setMessage("This cannot be undone")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (scribeRecord != null) {
							scribeRecord.deleteEventually();
							scribeRecord.unpinInBackground("ScribeRecord", new DeleteCallback() {
								@Override
								public void done(ParseException e) {
									setResult(Activity.RESULT_OK, null);
									finish();
								}
							});
						}
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						//do nothing
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}
    
    /*
     * Private functions
     * 
     */
    private boolean validate()
    {
		Calendar c = Calendar.getInstance();
	/*	if(dateTime.getTime().compareTo(c.getTime()) < 0) {
			Toast.makeText(getBaseContext(), "You must enter a date and time that is in the future", Toast.LENGTH_SHORT).show();
			return false;
		}	*/

		
		if(placeName == null) {
			Toast.makeText(getBaseContext(), "You have not selected a location", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(subjectString == null) {
			Toast.makeText(getBaseContext(), "You have not selected a subject", Toast.LENGTH_SHORT).show();
			return false;
		}

        if(scribeName == null) {
            Toast.makeText(getBaseContext(), "You have not selected a scribe", Toast.LENGTH_SHORT).show();
            return false;
        }
    	
    	return true;
    }

    
    private void setupPrivateVariables(ParseObject input)
    {
		studentId = null;
		studentName = null;
		studentNumber = null;
    	dateTime = null;
        placeId=null;
        placeName=null;
        placeAddress=null;
        placePhoneNumber=null;
        latitude=0.0;
        longitude=0.0;
        subjectString=null;
        scribeId=null;
        scribeName=null;
        scribeNumber=null;

		Date date = input.getDate("dateTime");
		dateTime = Calendar.getInstance();
		dateTime.setTime(date);
		placeName = input.getString("placeName");
        placeId = input.getString("placeId");
        placeAddress = input.getString("placeAddress");
		placePhoneNumber = input.getString("placePhoneNumber");
        latitude = input.getDouble("latitude");
        longitude = input.getDouble("longitude");
		subjectString = input.getString("subject");
		scribeId = input.getString("scribeId");
		scribeName = input.getString("scribeName");
		scribeNumber = input.getString("scribeNumber");
    }
    
    private String dateTimeFormatted(Date input)
    {
    	String result = null;
    	
    	Calendar inputDateTime = Calendar.getInstance();
    	if(input != null) {
    		inputDateTime.setTime(input);
    	}
    	else {
    		if(inputDateTime.get(Calendar.HOUR_OF_DAY) < 7) {
    			inputDateTime.set(Calendar.HOUR_OF_DAY, 7);
    			inputDateTime.set(Calendar.MINUTE, 0);
    		}
    		else {
    			int mod = inputDateTime.get(Calendar.MINUTE) % 15;
				inputDateTime.add(Calendar.MINUTE, 15-mod);
    		}
    	}
    	
    	int dayOfMonth = inputDateTime.get(Calendar.DAY_OF_MONTH);
		String monthString = inputDateTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		int year = inputDateTime.get(Calendar.YEAR);
		int hourOfDay = inputDateTime.get(Calendar.HOUR_OF_DAY);
		if(hourOfDay > 12) hourOfDay = hourOfDay - 12;
        else if(hourOfDay == 0) hourOfDay = 12;
		int minute = inputDateTime.get(Calendar.MINUTE);
        String minuteString = "";
        if(minute == 0) minuteString = "00";
        else minuteString = Integer.toString(minute);
        String am_pm = inputDateTime.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
		result = dayOfMonth + " " + monthString; // + " " + year + "  " + hourOfDay + ":" + minuteString + " " + am_pm;
    	
    	return result;
    }
    
    private List<String> setupUIContent(ParseObject input)
    {
    	String timeString=null;
        String locationString = "No location selected";
        String subjectString = "No subject selected";
        String scribeString = "No scribe selected";
    	if(input != null) {
    		timeString = dateTimeFormatted(input.getDate("dateTime"));
            locationString = input.getString("placeName");
            subjectString = input.getString("subject");
            if (input.getString("scribeName") != null) {
                scribeString = input.getString("scribeName");
            }
    	}
    	else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int month = extras.getInt("month");
                int year = extras.getInt("year");
                Calendar c = Calendar.getInstance();
                if (c.get(Calendar.MONTH) == month) {
                    //current month therefore pass in null
                    timeString = dateTimeFormatted(null);
                }
                else {
                    //It is a future month, set the date to 1
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.YEAR, year);
                    timeString = dateTimeFormatted(c.getTime());
                }
            }
    	}


        List<String> content = new ArrayList<String>();
        content.add(timeString);
        content.add(locationString);
        content.add(subjectString);
        content.add(scribeString);
	/*	String[] content = new String[] { 	timeString,
				locationString, 
				subjectString, 
				scribeString};  */
		
		return content;
    }
	
	
      

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit_activity);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			studentId = extras.getString("id");
			studentName = extras.getString("name");
			studentNumber = extras.getString("number");
			//set according to month and year that have been passed in
			int month = extras.getInt("month");
			int year = extras.getInt("year");
			dateTime = Calendar.getInstance();
			dateTime.set(Calendar.MONTH, month);
			dateTime.set(Calendar.YEAR, year);
		}

		String parseId=null;
		String uuid=null;

		if(extras != null) {
			parseId = extras.getString("parseId");
			uuid = extras.getString("uuid");
		}
		ParseQuery query = ParseQuery.getQuery("ScribeRecord");
		query.fromLocalDatastore();
		if(parseId != null) {
			try {
				scribeRecord = query.get(parseId);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(uuid != null) {
			query.whereEqualTo("uuid", uuid);
			try {
				scribeRecord = query.getFirst();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(scribeRecord != null) {
			setTitle("Edit details");
            setupPrivateVariables(scribeRecord);
		}

		listView = (ListView) findViewById(R.id.list);
        List<String> content = new ArrayList<String>(setupUIContent(scribeRecord));
	    RecordEditAdapter editAdapter = new RecordEditAdapter(this, 0, content);
	    listView.setAdapter(editAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent;
				Bundle bundle;

				switch (position) {
					case 0:
						//intent = new Intent(RecordEditActivity.this, MonthYearPickerActivity.class);
						intent = new Intent(RecordEditActivity.this, DatePickerActivity.class);
						bundle = new Bundle();
						bundle.putInt("month", dateTime.get(Calendar.MONTH));
						bundle.putInt("year", dateTime.get(Calendar.YEAR));
						intent.putExtras(bundle);
						startActivityForResult(intent, position);
						return;
					case 1:
						//intent = new Intent(RecordEditActivity.this, SelectLocationActivity.class);
						intent = new Intent(RecordEditActivity.this, LocationAutocompleteActivity.class);
						startActivityForResult(intent, position);
						return;
					case 2:
						intent = new Intent(RecordEditActivity.this, SelectSubjectActivity.class);
						startActivityForResult(intent, position);
						return;
					case 3:
						//intent = new Intent(RecordEditActivity.this, RepresenteeEditActivity.class);
						intent = new Intent(RecordEditActivity.this, SelectScribeActivity.class);
						bundle = new Bundle();
						bundle.putString("id", studentId);
						bundle.putString("name", studentName);
						bundle.putString("number", studentNumber);
						intent.putExtras(bundle);
						startActivityForResult(intent, position);
						return;
					default:
						return;
				}
			}
		});

		buttonCallScribe = (Button) findViewById(R.id.button_call_scribe);
		if (scribeId != null) {
			buttonCallScribe.setVisibility(View.VISIBLE);
		}
		else {
			buttonCallScribe.setVisibility(View.GONE);
		}
		buttonCallScribe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = "tel:" + scribeNumber.trim() ;
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse(uri));
				startActivity(intent);
			}
		});

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == Activity.RESULT_OK) {
			RecordEditAdapter adapter = (RecordEditAdapter) listView.getAdapter();
			if(data == null) {
				return;
			}
			Bundle extras = data.getExtras();
			
			switch(requestCode) {
				case 0:
					dateTime.set(Calendar.YEAR, extras.getInt("year"));
					dateTime.set(Calendar.MONTH, extras.getInt("month"));
					dateTime.set(Calendar.DAY_OF_MONTH, extras.getInt("dayOfMonth"));
					//dateTime.set(Calendar.HOUR_OF_DAY, extras.getInt("hourOfDay"));
					//dateTime.set(Calendar.MINUTE, extras.getInt("minute"));
					
				/*	String monthString = dateTime.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
					int hourOfDay = dateTime.get(Calendar.HOUR_OF_DAY);
					if(hourOfDay > 12) hourOfDay = hourOfDay - 12;
			        else if(hourOfDay == 0) hourOfDay = 12;
					
					int minute = dateTime.get(Calendar.MINUTE);
			        String minuteString = "";
			        if(minute == 0) minuteString = "00";
			        else minuteString = Integer.toString(minute);
			        
					String am_pm = dateTime.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
					String dateTimeString = dateTime.get(Calendar.DAY_OF_MONTH) + " " + monthString + " " + dateTime.get(Calendar.YEAR) +
											"  " + hourOfDay + ":" + minuteString + " " + am_pm;    */
					
					adapter.remove(adapter.getItem(0));
					String dateTimeString = dateTimeFormatted(dateTime.getTime());
					adapter.insert(dateTimeString, 0);
					adapter.notifyDataSetChanged();
					Toast.makeText(getBaseContext(), "Date selected: "+dateTimeString, Toast.LENGTH_SHORT).show();
					return;
				case 1:
					//locationUuid = extras.getString("uuid");
					//locationParseId = extras.getString("parseId");
                    //locationString = extras.getString("locationString");
                    placeId = extras.getString("placeId");
                    placeName = extras.getString("placeName");
                    placeAddress = extras.getString("placeAddress");
                    placePhoneNumber = extras.getString("placePhoneNumber");
                    longitude = extras.getDouble("longitude");
                    latitude = extras.getDouble("latitude");
					adapter.remove(adapter.getItem(1));
					//adapter.insert(extras.getString("title"), 1);
                    adapter.insert(placeName, 1);
					adapter.notifyDataSetChanged();
					Toast.makeText(getBaseContext(), "Location selected: "+placeName, Toast.LENGTH_SHORT).show();
					return;
				case 2:
					//subjectUuid = extras.getString("uuid");
					//subjectParseId = extras.getString("parseId");
                    subjectString = extras.getString("subjectString");
					adapter.remove(adapter.getItem(2));
					adapter.insert(subjectString, 2);
					adapter.notifyDataSetChanged();
					Toast.makeText(getBaseContext(), "Subject selected: "+subjectString, Toast.LENGTH_SHORT).show();
					return;
				case 3:
					adapter.remove(adapter.getItem(3));
					
					String notes;
                    scribeId = extras.getString("scribeId");
                    scribeName = extras.getString("scribeName");
                    scribeNumber = extras.getString("scribeNumber");
                    notes = scribeName;
                    adapter.insert(notes, 3);
					adapter.notifyDataSetChanged();
					Toast.makeText(getBaseContext(), "Scribe selected: "+scribeName, Toast.LENGTH_SHORT).show();
					if (scribeId != null) {
						buttonCallScribe.setVisibility(View.VISIBLE);
					}
					return;
				case 50000: //This means we are returning from SelectContactActivity and new event has been successfully created
					setResult(Activity.RESULT_OK, data);
					finish();
					return;
				default:
					return;
			}	
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.save:
	        	try {
	        		savePressed();
	        	} catch (ParseException e) {
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	        	}
	        	return true;
			case R.id.discard:
				discardPressed();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.record_edit_activity, menu);
		
		//progressButton = (MenuItem)menu.findItem(R.id.progress);
		//progressButton.setVisible(false);

		saveButton = (MenuItem)menu.findItem(R.id.save);
		discardButton = (MenuItem)menu.findItem(R.id.discard);

		if(scribeRecord != null) {
			discardButton.setVisible(true);
		}
		else {
			discardButton.setVisible(false);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

}
