package com.adarshhasija.ahelp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocationActivity extends ListActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

	//MenuItems
	private MenuItem progressButton;
	private MenuItem searchButton;
	private MenuItem addButton;
	
	/*
	 * Parse callbacks
	 * 
	 */
	private FindCallback<ParseObject> populateListCallbackLocal = new FindCallback<ParseObject>() {

		@Override
		public void done(List<ParseObject> list, ParseException e) {
			if (e == null) {
				LocationListAdapter locationAdapter = new LocationListAdapter(SelectLocationActivity.this, 0, list);
	            setListAdapter(locationAdapter);
	            
	        } else {
	            Log.d("SelectLocationActivity", "Error: " + e.getMessage());
	        }
		}
		
	};
	
	private FindCallback<ParseObject> populateListCallbackCloud = new FindCallback<ParseObject>() {

		@Override
		public void done(final List<ParseObject> locations, ParseException e) {
			
			if(e == null) {
		        ParseObject.unpinAllInBackground("Location", new DeleteCallback() {

					@Override
					public void done(ParseException e) {
						if(e == null) {
							ParseObject.pinAllInBackground("Location", locations);
						}
						else {
							Log.d("SelectLocationActivity", "Error: " + e.getMessage());
						}
						
					}
		        	
		        });
		        
		        LocationListAdapter locationAdapter = new LocationListAdapter(SelectLocationActivity.this, 0, locations);
		        setListAdapter(locationAdapter);
			}
			else {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	
	/*
	 * Action bar buttons
	 * Private functions
	 * 
	 */
	private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

		@Override
		public boolean onQueryTextChange(String newText) {
			LocationListAdapter adapter = (LocationListAdapter) getListAdapter();
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
	
	private void addPressed() {
		Intent intent = new Intent(this, LocationEditActivity.class);
		startActivityForResult(intent, 50000);
	}
	
	
	/*
	 * private functions
	 * 
	 */
	private void populateListLocal() {
		ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Location");
		localQuery.fromLocalDatastore();
		//localQuery.orderByDescending("updatedAt");
		localQuery.orderByAscending("title");
		localQuery.findInBackground(populateListCallbackLocal);
	}
	
	private void populateListCloud() {
		ParseQuery<ParseObject> cloudQuery = ParseQuery.getQuery("Location");
		//cloudQuery.orderByDescending("updatedAt");
		cloudQuery.orderByAscending("title");
		cloudQuery.findInBackground(populateListCallbackCloud);
	}
	
	private void populateList() {
		populateListLocal();
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) {
			populateListCloud();
		}
	}
	
	private Bundle prepareBundle(ParseObject parseObject) {
		Bundle bundle = new Bundle();
		bundle.putString("parseId", parseObject.getObjectId());
		bundle.putString("uuid", parseObject.getString("uuid"));
		bundle.putString("title", parseObject.getString("title"));
		
		return bundle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		  //Check for Google Play Services
		 /* int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
			if(status == ConnectionResult.SUCCESS) {
				mGoogleApiClient = new GoogleApiClient
						.Builder(this)
						.addApi(LocationServices.API)
						.addApi(Places.GEO_DATA_API)
						.addApi(Places.PLACE_DETECTION_API)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.build();

				mResolvingError = savedInstanceState != null
						&& savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

				int PLACE_PICKER_REQUEST = 1;
				PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

				Context context = getApplicationContext();
				try {
					startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
				} catch (GooglePlayServicesRepairableException e) {
					e.printStackTrace();
				} catch (GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				}
			}	*/


		//populateList();

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	}


	@Override
	protected void onStart() {
        super.onStart();

		TextView emptyView = new TextView(this);
		((ViewGroup) getListView().getParent()).addView(emptyView);
		emptyView.setText("There are currently no locations to list.");
		emptyView.setGravity(Gravity.CENTER);
		getListView().setEmptyView(emptyView);


	/*	getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				ParseObject parseObject = (ParseObject) getListView().getAdapter().getItem(position);
				Bundle bundle = prepareBundle(parseObject);
				Intent intent = new Intent(SelectLocationActivity.this, LocationEditActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, position);
				
				return true;
			}
		});	*/
		//registerForContextMenu(getListView());
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
	}


	@Override
	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject selected = (ParseObject) getListAdapter().getItem(position);
		String parseId = selected.getObjectId();
		String uuid = selected.getString("uuid");
		String selectedString = selected.getString("title");
		
		Bundle bundle = new Bundle();
		bundle.putString("parseId", parseId);
		bundle.putString("uuid", uuid);
		bundle.putString("title", selectedString);

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
		
		final LocationListAdapter adapter = (LocationListAdapter) getListAdapter();
		
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode != 50000 && data != null) {  //modify
				Bundle extras = data.getExtras();
				ParseObject parseObject = adapter.getItem(requestCode);
				adapter.remove(adapter.getItem(requestCode));
				parseObject.put("title", extras.getString("title"));
				adapter.insert(parseObject, requestCode);
				adapter.notifyDataSetChanged();
			}
			else if(requestCode != 50000 && data == null) {	//delete
				ParseObject parseObject = adapter.getItem(requestCode);
				adapter.remove(parseObject);
				adapter.notifyDataSetChanged();
			}
			else if(data != null) { //insert new
				Bundle extras = data.getExtras();
				String uuid = extras.getString("uuid");
				ParseQuery<ParseObject> locationQuery = ParseQuery.getQuery("Location");
				locationQuery.whereEqualTo("uuid", uuid);
				locationQuery.fromLocalDatastore();
				locationQuery.findInBackground(new FindCallback<ParseObject> () {

					@Override
					public void done(List<ParseObject> list, ParseException e) {
						if(list.size() > 0) {
							ParseObject obj = list.get(0);
							adapter.insert(obj, 0);
							adapter.notifyDataSetChanged();
						}
					}
					
				});
			}
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.search:
	            return true;
	        case R.id.add:
	            addPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.select_location, menu);
		
		progressButton = (MenuItem)menu.findItem(R.id.progress);
		progressButton.setVisible(false);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(onQueryTextListener);
	    searchButton = menu.findItem(R.id.search);
	    searchButton.setVisible(false);
	    
	    addButton = menu.findItem(R.id.add);
	    addButton.setVisible(false);
		
		return super.onCreateOptionsMenu(menu);
	}


    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(getBaseContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //Toast.makeText(getBaseContext(), mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(),
                        // In this sample, get just a single address.
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                Log.e("WOW", "ERROR MESSAGE", ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                Log.e("WOW", "ERROR. " +
                        "Latitude = " + mLastLocation.getLatitude() +
                        ", Longitude = " +
                        mLastLocation.getLongitude(), illegalArgumentException);
            }
            if (addresses == null || addresses.size()  == 0) {
                Toast.makeText(getBaseContext(), "NO ADDRESSES FOUND", Toast.LENGTH_SHORT).show();
            }
            else {
                Address address = addresses.get(0);

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                //Toast.makeText(getBaseContext(), address.getLocality(), Toast.LENGTH_SHORT).show();

             /*   // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);
                String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=cruise&key=AIzaSyBH9XbLZPkLy71w1EKXJVCs-Wfq4Q0zUH8";

                // Request a string response from the provided URL.
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Display the first 500 characters of the response string.
                                Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "THAT DIDNT WORK", Toast.LENGTH_SHORT).show();
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);   */
                /* PendingResult result =
                        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query,
                                mBounds, mAutocompleteFilter);	*/
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {

        }
    }
}

	

