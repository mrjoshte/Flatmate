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
import com.android.volley.toolbox.Volley;
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

public class LandlordAddUnits extends Activity {

	Button createButton;

	EditText unitName;
	EditText address;
	EditText unitId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landlord_add_units);

		createButton=(Button)findViewById(R.id.createButtonAddUnit);

		unitName = (EditText)findViewById(R.id.unitIdAddUnitInput);
		address = (EditText)findViewById(R.id.addressAddUnitInput);
		unitId = (EditText)findViewById(R.id.unitNameAddUnitInput) ;

		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			//TODO; add intake from the app form
			public void onClick(View v) {
				AndroidApplication app = ((AndroidApplication)getApplication());
				Map<String, String> params = new HashMap<String, String>();
				params.put("AdminID", app.userName);
				params.put("Address", app.landlordAddress + " Unit " + address.getText().toString());
				params.put("FlatID", unitId.getText().toString());
				params.put("ApartmentID", app.apartmentId);
				params.put("UnitName", unitName.getText().toString());

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
						try 
						{
							Toast.makeText(getApplicationContext(), response.get("a") + "",Toast.LENGTH_LONG).show();

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						startActivity(new Intent(LandlordAddUnits.this, LandlordHomePage.class));
					}
				},
						new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});

				mRequestQueue.add(jsObjRequest);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landlord_add_units, menu);
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
