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
import android.widget.TextView;
import android.widget.Toast;

public class TransferAdmin extends BaseNav {

	RequestQueue mRequestQueue;

	ArrayList<String> listOfRoommates, listOfUserIds;

	AndroidApplication app;
	String adminIDOfFlat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_admin);

		super.onCreate("activity_transfer_admin");
		
		app = ((AndroidApplication)getApplication());

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

		Map<String, String> params = new HashMap<String, String>();
		params.put("FlatID", app.flatID);

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getFlatAdmin.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try 
				{
					adminIDOfFlat = response.get("AdminID").toString();
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
		
		
		params = new HashMap<String, String>();
		params.put("FlatID", app.flatID);
		params.put("UserID", app.userName);

		jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getRoommates.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try 
				{
					listOfRoommates = new ArrayList<String>();
					listOfUserIds = new ArrayList<String>();

					JSONArray jsonArray = response.getJSONArray("list"); 
					if (jsonArray != null)
					{ 
						int len = jsonArray.length();
						for (int i = 0; i < len; i++)
						{ 
							JSONObject object = jsonArray.getJSONObject(i);
							listOfRoommates.add(object.get("FirstName").toString() + " " + object.get("LastName").toString());
							listOfUserIds.add(object.get("UserId").toString());
						} 
					} 	
					populateItems(listOfRoommates, listOfUserIds);
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


	private void populateItems(final ArrayList<String> userNames, final ArrayList<String> userIds)
	{
		TableLayout table = (TableLayout) findViewById(R.id.transferAdminTableInTransferAdmin);
 
		table.removeAllViews();

		if(userNames.size() == 0)
		{
			TableRow tableRow = new TableRow(this);
			table.addView(tableRow);
			final TextView name = new TextView(this);
			name.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));
			name.setText("You don't have any roommates");

			tableRow.addView(name);
			
			return;
		}
		
		for (int row = 0; row < userNames.size(); row++)
		{
			final int ROWFINAL = row;

			TableRow tableRow = new TableRow(this);
			table.addView(tableRow);

			//eventually add delete button in another column next to the main button for each item

			final TextView name = new TextView(this);
			name.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));
			name.setText(userNames.get(row));

			tableRow.addView(name);

			Button deleteButton = new Button(this);
			deleteButton.setText("Make Admin");
			deleteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(TransferAdmin.this)
					.setMessage("You will no longer be an Admin after transferring privilege. Are you sure you want to give Admin privileges to " + userNames.get(ROWFINAL) + "? ")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Map<String, String> params = new HashMap<String, String>();
							params.put("UserID", userIds.get(ROWFINAL));
							params.put("AdminID", adminIDOfFlat);
							params.put("FlatID", app.flatID);

							JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/changeAdminOwner.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try 
									{
										if(((String)response.get("success")).equals("Success"))
										{
											startActivity(new Intent(TransferAdmin.this, LoginActivity.class));
											Toast.makeText(getApplicationContext(), "Admin Privileges Tranferred. Please log in again.", Toast.LENGTH_SHORT).show();
										}
										else
											Toast.makeText(getApplicationContext(), "An error occured transferring Admin Privileges", Toast.LENGTH_SHORT).show();

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
			});

			tableRow.addView(deleteButton);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfer_admin, menu);
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
}
