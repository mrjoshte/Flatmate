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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	Button loginButton,createButton;
	EditText userName,password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//Find elements on xml
		loginButton=(Button)findViewById(R.id.loginButton);
		userName=(EditText)findViewById(R.id.userIdTextField);
		password=(EditText)findViewById(R.id.passwordTextField);
		createButton=(Button)findViewById(R.id.createButtonAddUnitID);


		//Verify log in
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("UserId", userName.getText().toString());
				params.put("Password", password.getText().toString());

				RequestQueue mRequestQueue;

				// Instantiate the cache
				Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

				// Set up the network to use HttpURLConnection as the HTTP client.
				Network network = new BasicNetwork(new HurlStack());

				// Instantiate the RequestQueue with the cache and network.
				mRequestQueue = new RequestQueue(cache, network);

				// Start the queue
				mRequestQueue.start();

				JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/login.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						System.out.println("Stop");
						try {
							if((response.get("Success").toString()).equals("true"))
							{
								//store username for use in future activities
								AndroidApplication app = ((AndroidApplication)getApplication());
								app.userName = userName.getText().toString();
								app.email = response.get("email").toString();
								app.flatID = response.get("flatID").toString();
								app.firstName = response.get("firstName").toString();
								app.lastName = response.get("lastName").toString();
								app.role = response.get("Role").toString();
								app.isLandlord = false;

								//Go to a specific page when logging in
								if(app.role != null)
									switch(app.role)
									{
									case "Tenant":
										startActivity(new Intent(LoginActivity.this, AtAGlancePage.class));
										break;
									case "Admin":
										startActivity(new Intent(LoginActivity.this, AtAGlancePage.class));
										break;
									case "Landlord":
										app.isLandlord = true;
										startActivity(new Intent(LoginActivity.this, LandlordHomePage.class));
										break;
									default:
										startActivity(new Intent(LoginActivity.this, CreateOptions.class));
									}

							}
							else
								Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				},
						new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "An Error Occured At Login", Toast.LENGTH_SHORT).show();
					}
				});

				mRequestQueue.add(jsObjRequest);
			}
		});

		//Go to create activity
		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, Create_user_account.class));
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
	    moveTaskToBack(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

