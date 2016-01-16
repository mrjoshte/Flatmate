package activityfiles;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.flatemateapartmentmanager.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateLandlordApartment extends Activity {

	Button createButton;
	EditText address, city, areaCode, password, confirmPassword;
	Spinner state;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_landlord_apartment);
		
		address=(EditText)findViewById(R.id.CreateLandlordApartmentStreetAddress);
		city=(EditText)findViewById(R.id.CreateLandlordApartmentCity);
		areaCode=(EditText)findViewById(R.id.CreateLandlordApartmentAreaCode);
		password=(EditText)findViewById(R.id.CreateLandlordApartmentPassword);
		confirmPassword=(EditText)findViewById(R.id.CreateLandlordApartmentConfirmPassword);
		createButton=(Button)findViewById(R.id.CreateLandlordApartmentContinueButton);
		state=(Spinner)findViewById(R.id.CreateLandlordApartmentStateDropDownMenu);
		
		String[] items = new String[]{"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
		state.setAdapter(adapter);
		
		//Go to add units page
		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AndroidApplication app = ((AndroidApplication)getApplication());
				if(!address.toString().equals("") && !city.toString().equals("") && !areaCode.toString().equals("") && !password.toString().equals("") && !confirmPassword.toString().equals(""))
				{		
					app.landlordAddress = address.getText().toString() + " " + city.getText().toString() + " " + state.getSelectedItem().toString() + " " + areaCode.getText().toString();
					Map<String, String> params = new HashMap<String, String>();
					params.put("LandLordID", app.userName);
					params.put("Address", address.getText().toString());
					params.put("ApartmentID", password.getText().toString());

					app.landlordPassword = password.getText().toString();
					
					RequestQueue mRequestQueue;

					// Instantiate the cache
					Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

					// Set up the network to use HttpURLConnection as the HTTP client.
					Network network = new BasicNetwork(new HurlStack());

					// Instantiate the RequestQueue with the cache and network.
					mRequestQueue = new RequestQueue(cache, network);

					// Start the queue
					mRequestQueue.start();

					JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://69.5.140.151/createApartment.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							try {
								if((response.get("a").toString()).equals("New record created successfully"))
								{
									Toast.makeText(getApplicationContext(), "Apartment Created. Please Log In.", Toast.LENGTH_SHORT).show();
									startActivity(new Intent(CreateLandlordApartment.this, LoginActivity.class));
								}
								else
								{
									Toast.makeText(getApplicationContext(), "An Error Occured creating the apartment", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					},
							new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(getApplicationContext(), "An Error Occured", Toast.LENGTH_SHORT).show();
						}
					});
					mRequestQueue.add(jsObjRequest);
				}
				else
					Toast.makeText(getApplicationContext(), "You must fill in all fields!", Toast.LENGTH_LONG).show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_landlord_apartment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
