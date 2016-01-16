package activityfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddRoommates extends Activity {

	Button inviteButton;
	EditText rm1, rm2, rm3, rm4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_roommates);

		inviteButton=(Button)findViewById(R.id.inviteButtonInAddRoommates);

		rm1=(EditText)findViewById(R.id.roommateOneEmail);
		rm2=(EditText)findViewById(R.id.roommateTwoEmail);
		rm3=(EditText)findViewById(R.id.roommateThreeEmail);
		rm4=(EditText)findViewById(R.id.roommateFourEmail);

		inviteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<EditText> roommates = new ArrayList<EditText>();
				roommates.add(rm1);
				roommates.add(rm2);
				roommates.add(rm3);
				roommates.add(rm4);

				AndroidApplication app = ((AndroidApplication)getApplication());

				for(EditText user : roommates)
				{
					if(!user.getText().toString().equals(""))
					{
						Map<String, String> params = new HashMap<String, String>();
						params.put("to", user.getText().toString());
						params.put("code", app.flatID);

						RequestQueue mRequestQueue;

						// Instantiate the cache
						Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

						// Set up the network to use HttpURLConnection as the HTTP client.
						Network network = new BasicNetwork(new HurlStack());

						// Instantiate the RequestQueue with the cache and network.
						mRequestQueue = new RequestQueue(cache, network);

						// Start the queue
						mRequestQueue.start();

						//Apartment is created, now email the roommates
						JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/inviteRoommates.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								AndroidApplication app = (AndroidApplication) getApplication();
								//We do get a response, just don't know what to do about it
								if(app.isLandlord == true) {
									startActivity(new Intent(AddRoommates.this, ManageTenantsActivity.class));
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
				
				Toast.makeText(getApplicationContext(), "Roommates have been emailed the code", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(AddRoommates.this, AtAGlancePage.class));
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_roommates, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		AndroidApplication app = (AndroidApplication) getApplication();
		if(app.isLandlord) {
			startActivity(new Intent(AddRoommates.this, ManageTenantsActivity.class));
		}
		else {
			startActivity(new Intent(AddRoommates.this, AtAGlancePage.class));
		}
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
