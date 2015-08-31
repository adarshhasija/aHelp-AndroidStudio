package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EnterLocationStringActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_location_string);

        setTitle("Enter location");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_location_string, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_accept) {
            EditText editTextLocation = (EditText) findViewById(R.id.editTextLocation);
            String locationString = editTextLocation.getText().toString();

            if (locationString.isEmpty()) {
                Toast.makeText(EnterLocationStringActivity.this, "You have not eneted a location", Toast.LENGTH_SHORT).show();
                return false;
            }

            Intent returnIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("locationString", locationString);
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
