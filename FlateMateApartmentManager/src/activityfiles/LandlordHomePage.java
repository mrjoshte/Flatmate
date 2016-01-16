package activityfiles;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;


public class LandlordHomePage extends BaseNav implements AdapterView.OnItemSelectedListener{

	private Button addNewUnit;
	private Spinner apartmentIdSpinner;

	private ArrayList<String> listOfApartmentIds, listOfApartmentAddresses;
	private ArrayList<String> listOfFlatIdsInApartment;

	AndroidApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		app = ((AndroidApplication)getApplication());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landlord_home_page);
		
		super.onCreate("activity_landlord_home_page");

		addNewUnit=(Button)findViewById(R.id.addNewUnitButton);
		//Verify log in
		addNewUnit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LandlordHomePage.this, LandlordAddUnits.class));
			}
		});

		Map<String, String> params = new HashMap<String, String>();
		params.put("LandlordId", app.userName);

		RequestQueue mRequestQueue;

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getApartmentIds.php", new JSONObject(params),  new Response.Listener<JSONObject>() {	
			@Override
			public void onResponse(JSONObject response) {
				try 
				{
					listOfApartmentIds = new ArrayList<String>();
					listOfApartmentAddresses = new ArrayList<String>();
					
					String aptId = "";

					JSONArray jsonArray = response.getJSONArray("list"); 
					if (jsonArray != null)
					{ 
						int len = jsonArray.length();
						for (int i=0; i<len; i++)
						{ 
							JSONObject object = jsonArray.getJSONObject(i);
							listOfApartmentIds.add(object.get("ApartmentId").toString());
							listOfApartmentAddresses.add(object.get("Address").toString());
							
							if(aptId.equals("")) {
								aptId = listOfApartmentIds.get(0);
							}
						} 
						if(listOfApartmentAddresses.size() > 0)
							app.landlordAddress = listOfApartmentAddresses.get(0);
					} 	
					app.apartmentId = aptId;
					listOfApartmentIds.add("Add Apartment");
					makeSpinner();
					getFlats();
				}
				catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Apartments Not Found", Toast.LENGTH_SHORT).show();
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

	private void populateButtons() 
	{
		TableLayout table = (TableLayout) findViewById(R.id.flatButtonsLHP);
		table.removeAllViews();
		for (int row = 0; row < listOfFlatIdsInApartment.size() ; row++)
		{
			final int ROWFINAL = row;

			TableRow tableRow = new TableRow(this);
			table.addView(tableRow);

			Button button = new Button(this);
			button.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight (yeah I'm not really sure what this means-JJW)
					));
			button.setText("Flat Name: " + listOfFlatIdsInApartment.get(row));
			//sets what each button does
			button.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					flatButtonClicked(listOfFlatIdsInApartment.get(ROWFINAL));					
				}
			});

			tableRow.addView(button);
		}

	}


	//what each flat button does when clicked
	private void flatButtonClicked(String flatID) {
		/*this needs to go to the individual flat management page which will have a function
		that pulls the relevant data to fill the page in the onCreate.*/
		app.flatID = flatID;

		startActivity(new Intent(LandlordHomePage.this, ManageTenantsActivity.class));
	}

	//TODO:
	@SuppressWarnings("unused")
	private void getApartmentIdOptions(String LandlordId){
		//call getFlats(app.apartmentId); after all other work is done here
	}

	private void getFlats(){

		Map<String, String> paramsForFlats = new HashMap<String, String>();
		paramsForFlats.put("ApartmentId", app.apartmentId);

		RequestQueue mRequestQueue;

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

		JsonObjectRequest jsObjRequestFlats = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getListOfFlats.php", new JSONObject(paramsForFlats),  new Response.Listener<JSONObject>() {	
			@Override
			public void onResponse(JSONObject response) {
				try {	
					listOfFlatIdsInApartment = new ArrayList<String>();

					JSONArray jsonArray = response.getJSONArray("list"); 
					if (jsonArray != null)
					{ 
						int len = jsonArray.length();
						for (int i=0; i<len; i++)
						{ 
							JSONObject object = jsonArray.getJSONObject(i);
							listOfFlatIdsInApartment.add(object.get("FlatID").toString());
						} 
					}
					populateButtons();
				}
				catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Apartments Not Found", Toast.LENGTH_SHORT).show();
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

		mRequestQueue.add(jsObjRequestFlats);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landlord_home_page, menu);
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
		
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		app.apartmentId = listOfApartmentIds.get(position);
		app.landlordAddress = listOfApartmentAddresses.get(position);
		if(app.apartmentId == "Add Apartment") {
			startActivity(new Intent(LandlordHomePage.this, CreateLandlordApartment.class));
		}
		else {
			getFlats();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

		Toast.makeText(this, "Already Selected", Toast.LENGTH_LONG).show();

	}
	public void makeSpinner() {
		//Create an ArrayAdapter using the string array and a default spinner layout
		apartmentIdSpinner = (Spinner) findViewById(R.id.apartment_id_spinner);
		ArrayAdapter<String> aptIdAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfApartmentIds);
		aptIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		apartmentIdSpinner.setAdapter(aptIdAdapter);
		apartmentIdSpinner.setOnItemSelectedListener(this);
	}
}
