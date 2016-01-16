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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class ManageTenantsActivity extends BaseNav {

	protected ArrayList<String> listOfTenantsInFlatID;
	static ArrayList<String> listOfUserIDs;
	private Button addTenants, resetButton;
	
	//ArrayList<String> listOfRoommates, listOfUserIds;

	RequestQueue mRequestQueue;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_tenants);
		
		super.onCreate("activity_manage_tenants");
		//Verify log in
		addTenants=(Button)findViewById(R.id.AddTenantsButton);
		resetButton=(Button)findViewById(R.id.ResetTenantsButton);
		
		addTenants.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ManageTenantsActivity.this, AddRoommates.class));
			}
		});
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetTenants();
			}
		});

		getTenants();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_tenants, menu);
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
	private void getTenants(){

		AndroidApplication app = (AndroidApplication)getApplication();
		//get list of tenants
		//calls populateTenantButtons() after clearing out the current table somehow
		//populateButtons();

		Map<String, String> paramsForTenants = new HashMap<String, String>();
		paramsForTenants.put("FlatID", app.flatID);


		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

		JsonObjectRequest jsObjRequestTenants = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getTenants.php", new JSONObject(paramsForTenants),  new Response.Listener<JSONObject>() {	
			@Override
			public void onResponse(JSONObject response) {
				try {	
					listOfTenantsInFlatID = new ArrayList<String>();
					listOfUserIDs = new ArrayList<String>();

					JSONArray jsonArray = response.getJSONArray("list"); 
					if (jsonArray != null)
					{ 
						int len = jsonArray.length();
						for (int i=0; i<len; i++)
						{ 
							JSONObject object = jsonArray.getJSONObject(i);
							listOfTenantsInFlatID.add(""+object.get("FirstName").toString()+" "+object.get("LastName").toString());
							listOfUserIDs.add(""+object.get("UserID").toString());
						} 
					}
					populateButtons();
				}
				catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Tenants Not Found", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		},
		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "An Error Occured Finding Tenants", Toast.LENGTH_SHORT).show();
			}
		});

		mRequestQueue.add(jsObjRequestTenants);

	}
	private void populateButtons() 
	{
		TableLayout table = (TableLayout) findViewById(R.id.tenantbuttons);
		table.removeAllViews();
		for (int row = 0; row < listOfTenantsInFlatID.size() ; row++)
		{
			final int ROWFINAL = row;

			TableRow tableRow = new TableRow(this);
			//			tableRow.setLayoutParams(new TableLayout.LayoutParams(
			//					TableLayout.LayoutParams.MATCH_PARENT,//width
			//					TableLayout.LayoutParams.MATCH_PARENT,//height
			//					1.0f //scaling weight (yeah I'm not really sure what this means-JJW)
			//					));
			table.addView(tableRow);
			//TODO: eventually add delete button in another column next to the main button for each flat
			//TODO: add the color changing feature for determining if a flat needs attention(and on what level)(this is mentioned at about minute 19 in the video used.
			Button button = new Button(this);
			button.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight (yeah I'm not really sure what this means-JJW)
					));
			button.setText("" + listOfTenantsInFlatID.get(row));
			//sets what each button does
			button.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					tenantButtonClicked(listOfUserIDs.get(ROWFINAL));					
				}
			});

			tableRow.addView(button);
		}

	}
	//what each tenant button does when clicked
	private void tenantButtonClicked(final String tenant) {

		final AndroidApplication app = (AndroidApplication)getApplication();
		
		new AlertDialog.Builder(ManageTenantsActivity.this)
		.setMessage("Are you sure you want to remove " + tenant + " from the Flat?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("UserID", tenant);
				params.put("landlordID", app.userName);
				params.put("FlatID", app.flatID);

				JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/removeRoommate.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try 
						{
							if(((String)response.get("Success")).equals("Success"))
							{
								Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
								getTenants();
							}
							else
								Toast.makeText(getApplicationContext(), "An error occured removing the User", Toast.LENGTH_SHORT).show();

						}	 
						catch (JSONException e) 
						{
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
		})
		.setNegativeButton("No", null)
		.show();
	}

	private void resetTenants() {
		
		AndroidApplication app = (AndroidApplication) getApplication();
		
		RequestQueue mRequestQueue;

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();
		
		for(int i=0; i<this.listOfTenantsInFlatID.size(); i++) {
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("UserID", listOfUserIDs.get(i));
			params.put("landlordID", app.userName);
			params.put("FlatID", app.flatID);

			JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/removeRoommate.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					try 
					{
						if(((String)response.get("Success")).equals("Success"))
						{
							Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
						}
					}	 
					catch (JSONException e) 
					{
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
		
	}
	
}
