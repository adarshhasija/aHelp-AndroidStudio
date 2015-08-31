package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class SelectScribeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_scribe);

        setTitle("Select Scribe");

        ListView listView = (ListView) findViewById(R.id.listView);
        MainApplication mainApplication = (MainApplication) getBaseContext().getApplicationContext();
        List<Contact> localContacts = mainApplication.getUpdatedDeviceContactsListAsArray();
        final MainContactsListAdapter adapter = new MainContactsListAdapter(this, 0, localContacts);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_scribe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }
}
