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

public class CreateFlat extends Activity {

	EditText address, unit, city, areaCode, password, confirmPassword, apartmentName;
	Button continueButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_flat);

		address=(EditText)findViewById(R.id.StreetAddress);
		unit=(EditText)findViewById(R.id.unitNumber);
		city=(EditText)findViewById(R.id.city);
		areaCode=(EditText)findViewById(R.id.areaCode);
		password=(EditText)findViewById(R.id.passwordInCreateApartment);
		confirmPassword=(EditText)findViewById(R.id.confirmPasswordInCreateApartment);
		apartmentName=(EditText)findViewById(R.id.apartmentNameInCreateApartment);

		continueButton=(Button)findViewById(R.id.continueButtonInCreateApartment);

		final Spinner dropdown = (Spinner)findViewById(R.id.stateDropDownMenu);
		String[] items = new String[]{"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
		dropdown.setAdapter(adapter);

		//Go to add roommates page
		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AndroidApplication app = ((AndroidApplication)getApplication());
				if(!address.toString().equals("") && !unit.toString().equals("") && !city.toString().equals("") && !areaCode.toString().equals("") && !password.toString().equals("") && !confirmPassword.toString().equals(""))
				{		
					Map<String, String> params = new HashMap<String, String>();
					params.put("AdminID", app.userName);
					params.put("Address", address.getText().toString() + " " + unit.getText().toString() + " " + city.getText().toString() + " " + dropdown.getSelectedItem().toString() + " " + areaCode.getText().toString());
					params.put("FlatID", password.getText().toString());
					params.put("ApartmentID", "");
					params.put("UnitName", apartmentName.getText().toString());

					app.flatID = password.getText().toString();

					RequestQueue mRequestQueue;

					// Instantiate the cache
					Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

					// Set up the network to use HttpURLConnection as the HTTP client.
					Network network = new BasicNetwork(new HurlStack());

					// Instantiate the RequestQueue with the cache and network.
					mRequestQueue = new RequestQueue(cache, network);

					// Start the queue
					mRequestQueue.start();

					JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/createFlat.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							try {
								if((response.get("a").toString()).equals("New record created successfully"))
								{
									app.role = "Admin";
									startActivity(new Intent(CreateFlat.this, AddRoommates.class));
								}
								else if((response.get("a").toString().equals("FlatID already exists")))
								{
									Toast.makeText(getApplicationContext(), "Your Flat password already exists, choose another", Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.create_apartent, menu);
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
