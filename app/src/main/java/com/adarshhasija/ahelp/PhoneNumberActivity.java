package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class PhoneNumberActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        setTitle("Enter phone number");

        Bundle extras = getIntent().getExtras();
        String countryCode = extras.getString("countryCode");

        EditText editTextCountryCode = (EditText) findViewById(R.id.editTextCountryCode);
        editTextCountryCode.setText(countryCode);
        editTextCountryCode.setFocusable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_number, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_accept) {
            Bundle extras = getIntent().getExtras();
            String countryCode = extras.getString("countryCode");
            EditText editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
            String phoneNumber = editTextPhoneNumber.getText().toString();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(PhoneNumberActivity.this, "You have not entered a phone number", Toast.LENGTH_SHORT).show();
                return false;
            }

            Bundle bundle = new Bundle();
            bundle.putString("countryCode", countryCode);
            phoneNumber = phoneNumber.replaceAll("[^\\d+]", "");
            bundle.putString("phoneNumber", phoneNumber);
            Intent returnIntent = new Intent();
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
