package com.adarshhasija.ahelp;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.adarshhasija.ahelp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class PushNotificationHandler extends BroadcastReceiver {
	private Context context=null;
	private JSONObject json=null;
	
	//This is the callback after a successful save to the local datastore
	//Called from GetCallback
	private SaveCallback saveCallback = new SaveCallback() {

		@Override
		public void done(ParseException e) {
			if(e == null) {
				generateNotification();
			} else {
				e.printStackTrace();
			}
		}
		
	};
	
	
	
	private GetCallback getCallback2 = new GetCallback() {

		@Override
		public void done(ParseObject arg0, ParseException arg1) {
			Log.d("WOW", "**********IN HERE********");
			if(arg1 == null) {
				Log.d("WOW", "************NO ERROR**********");
			}
			else {
				Log.d("WOW", "********EXCEPTION*********"+arg1.getMessage());
			}
			
		}
		
	};
	
	
	
	
	//This callback gets the record for saving in the local datastore
	private GetCallback getCallback = new GetCallback() {

		@Override
		public void done(ParseObject object, ParseException e) {
			Log.d("WOW", "**************GET CALLBACK************");
			if(e == null) {
				Log.d("WOW", "************REACHES HERE***************");
				try {
					Log.d("WOW", "***************"+object.getParseObject("location").getObjectId());
					object.getParseObject("location").fetchIfNeeded();
					//ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRequest");
					//query.getInBackground(object.getParseObject("location").getObjectId(), getCallback2);
					Log.d("WOW", "**********TITLE******"+object.getParseObject("location").getString("title"));
					object.getParseObject("subject").fetch();
					try {
						List<ParseObject> actionList = object.getList("actions");
		            	for(int i = 0; i < actionList.size(); i++) {
		            		((ParseObject) object.getList("actions").get(i)).fetch();
		            	}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					object.pinInBackground("ScribeRequest", saveCallback);
				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					Log.d("WOW", "********EXCEPTION****"+e2.getMessage()+"**************");
					e2.printStackTrace();
				}
			} else {
				e.printStackTrace();
			}
		}
		
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("WOW", "************ON RECEIVE**************");
		ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null) {
			try {
				json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
				this.context = context;
				generateNotification();
			
				//UNCOMMENT THIS IF YOU WANT OFFLINE STORAGE
				saveRecord(); 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private void saveRecord() {
		try {
			String objectId=null;
			String uuid = null;
			if(json.getString("objectId") != null) {
				objectId = json.getString("objectId");
			}
			else if(json.getString("uuid") != null) {
				uuid = json.getString("uuid");
			}
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ScribeRequest");
			if(objectId != null) {
				Log.d("WOW", "**********OBJECTID REQUEST*************");
				query.getInBackground(objectId, getCallback);
			}
			else {
				Log.d("WOW", "**************UUID REQUEST**************");
				query.whereEqualTo("uuid", uuid);
				query.getFirstInBackground(getCallback);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void generateNotification() {
		Intent intent = new Intent(context, RecordListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addParentStack(InboxActivity.class);
        stackBuilder.addNextIntent(intent);
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent contentIntent =
        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		        
		        NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("aScribe")
		        //.setLargeIcon(R.drawable.ic_launcher)
		        .setAutoCancel(true)
		        .setContentText("You have notifications");
		
		        mBuilder.setContentIntent(contentIntent);
		        int defaults = Notification.DEFAULT_SOUND;
		        //defaults |= Notification.FLAG_AUTO_CANCEL;
		        defaults |= Notification.DEFAULT_VIBRATE;
		        defaults |= Notification.FLAG_SHOW_LIGHTS;
		        defaults |= Notification.DEFAULT_LIGHTS;
		        mBuilder.setDefaults(defaults);
		
		        mNotifM.notify(0, mBuilder.build());

      }

}
