package com.adarshhasija.ahelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CountryPickerActivity extends Activity {


    private Map<String, String> isoMap = new HashMap<String, String>();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_picker);

        setTitle("Select Country");

        listView = (ListView) findViewById(R.id.list);
        String[] isoCountries = Locale.getISOCountries();
        final ArrayList<String> countries = new ArrayList<String>();
        for(String countryISO : isoCountries) {
            Locale locale = new Locale("en", countryISO);
            String name = locale.getDisplayCountry();
            countries.add(name);
            isoMap.put(name, countryISO);
        }
        Collections.sort(countries);
        LargeHeightSimpleArrayAdapter adapter = new LargeHeightSimpleArrayAdapter(this, 0, countries);
        listView.setAdapter(adapter);

        Locale current = getResources().getConfiguration().locale;
        listView.setSelection(countries.indexOf(current.getDisplayCountry()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String country = countries.get(position);
                String countryISO = isoMap.get(country);
                String countryCode = Iso2Phone.getPhone(countryISO);

                Intent intent = new Intent(getApplicationContext(), PhoneNumberActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("countryCode", countryCode);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_country_picker, menu);
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
