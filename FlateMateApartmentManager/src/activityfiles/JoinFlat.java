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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinFlat extends Activity {

	Button continueButton;
	EditText UserId, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_apartment);

		continueButton=(Button)findViewById(R.id.ContinueButtonJoinPage);
		password=(EditText)findViewById(R.id.EnterApartPassJoinPage);
		
		//UserId.setText(app.userName);

		//Go to at a glance page
		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AndroidApplication app = ((AndroidApplication)getApplication());

				Map<String, String> params = new HashMap<String, String>();
				params.put("UserID", app.userName);
				params.put("Password", password.getText().toString());
				params.put("Email", app.email);
				params.put("FirstName", app.firstName);
				params.put("LastName", app.lastName);
				
				RequestQueue mRequestQueue;

				// Instantiate the cache
				Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

				// Set up the network to use HttpURLConnection as the HTTP client.
				Network network = new BasicNetwork(new HurlStack());

				// Instantiate the RequestQueue with the cache and network.
				mRequestQueue = new RequestQueue(cache, network);

				// Start the queue
				mRequestQueue.start();

				JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/joinFlat.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							if(response.get("Success").toString().equals("success"))
							{
								app.role = "Tenant";
								app.flatID = password.getText().toString();
								startActivity(new Intent(JoinFlat.this, AtAGlancePage.class));
							}
							else if(response.get("Success").toString().equals("Success, you are Admin!")){
								app.role = "Admin";
								app.flatID = password.getText().toString();
								startActivity(new Intent(JoinFlat.this, AtAGlancePage.class));
							}
							else
								Toast.makeText(getApplicationContext(), "You were not invited to this apartment", Toast.LENGTH_SHORT).show();
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
		});
	}
}
