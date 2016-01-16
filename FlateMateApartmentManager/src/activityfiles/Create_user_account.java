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

//public class Create_user_account extends Activity {
public class Create_user_account extends Activity {

	EditText createFirstName, createLastName, createEmail, createRepeatEmail, createUsername, createPassword, createRepeatPassword, createDOB;

	Button createButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user_account);

		createFirstName=(EditText)findViewById(R.id.createFirstName);
		createLastName=(EditText)findViewById(R.id.createLastName);
		createEmail=(EditText)findViewById(R.id.createEmail);
		createRepeatEmail=(EditText)findViewById(R.id.createRepeatEmail);
		createUsername=(EditText)findViewById(R.id.createUsername);
		createPassword=(EditText)findViewById(R.id.createPassword);
		createRepeatPassword=(EditText)findViewById(R.id.createRepeatPassword);
		createDOB=(EditText)findViewById(R.id.createDOB);
		createButton=(Button)findViewById(R.id.createUserAccountContinueButton);

		//Go to create activity
		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("FirstName", createFirstName.getText().toString());
				params.put("LastName", createLastName.getText().toString());
				params.put("Password", createPassword.getText().toString());
				params.put("UserId", createUsername.getText().toString());
				params.put("Email", createEmail.getText().toString());
				params.put("DOB", createDOB.getText().toString());
				
				boolean doServer = true;
				String firstName = createFirstName.getText().toString();
				String username = createUsername.getText().toString();
				String lastName = createLastName.getText().toString();
				String dob = createDOB.getText().toString();
				String password = createPassword.getText().toString();
				String repeatPassword = createRepeatPassword.getText().toString();
				String email = createEmail.getText().toString();
				String repeatEmail = createRepeatEmail.getText().toString();
				
				//first and last name length
				if(firstName.length() < 1 || lastName.length() < 1)
				{
					Toast.makeText(getApplicationContext(), "Your name must be longer than 0 characters", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				
				//first name has valid characters
				if (doServer == true)
				{
					for(int i = 0; i < firstName.length(); i++)
					{
						if(!(firstName.charAt(i) >= 'a' && firstName.charAt(i) <= 'z'))
						{
							if(!(firstName.charAt(i) >= 'A' && firstName.charAt(i) <= 'Z'))
							{
								if(!(firstName.charAt(i) == '-'))
								{
									Toast.makeText(getApplicationContext(), "First name contains invalid characters", Toast.LENGTH_LONG).show();
									doServer = false;
								}
							}
						}
					}
				}
				
				//last name has valid characters
				if (doServer == true)
				{
					for(int i = 0; i < lastName.length(); i++)
					{
						if(!(lastName.charAt(i) >= 'a' && lastName.charAt(i) <= 'z'))
						{
							if(!(lastName.charAt(i) >= 'A' && lastName.charAt(i) <= 'Z'))
							{
								if(!(lastName.charAt(i) == '-'))
								{
									Toast.makeText(getApplicationContext(), "Last name contains invalid characters", Toast.LENGTH_LONG).show();
									doServer = false;
								}
							}
						}
					}
				}
				
				//email length
				if(email.length() < 1) {
					Toast.makeText(getApplicationContext(), "Your email must be longer than 0 characters", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				//email contains @ and .
				if(!email.contains("@") || !email.contains(".")) {
					Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				
				//email contains valid characters
				if (doServer == true)
				{
					for(int i = 0; i < email.length(); i++)
					{
						if(!(email.charAt(i) >= 'a' && email.charAt(i) <= 'z'))
						{
							if(!(email.charAt(i) >= 'A' && email.charAt(i) <= 'Z'))
							{
								if(!(email.charAt(i) == '-' || email.charAt(i) == '.' || email.charAt(i) == '@'
										|| email.charAt(i) == '_'))
								{
									if(!(email.charAt(i) >= '0' && email.charAt(i) <= '9'))
									{
										Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_LONG).show();
										doServer = false;
									}
								}
							}
						}
					}
				}
				if(email.compareTo(repeatEmail) != 0) {
					Toast.makeText(getApplicationContext(), "Ensure both email fields are equal", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				
				//username length
				if(username.length() < 1) {
					Toast.makeText(getApplicationContext(), "Your username must be longer than 0 characters", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				//username contains valid characters
				if (doServer == true)
				{
					for(int i = 0; i < username.length(); i++)
					{
						if(!(username.charAt(i) >= 'a' && username.charAt(i) <= 'z'))
						{
							if(!(username.charAt(i) >= 'A' && username.charAt(i) <= 'Z'))
							{
								if(!(username.charAt(i) >= '0' && username.charAt(i) <= '9'))
								{
									Toast.makeText(getApplicationContext(), "Username may only contain letters and numbers", Toast.LENGTH_LONG).show();
									doServer = false;
								}
							}
						}
					}
				}
				
				//password length
				if(password.length() < 1) {
					Toast.makeText(getApplicationContext(), "Your password must be longer than 0 characters", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				//password matches repeat
				if(password.compareTo(repeatPassword) != 0) {
					Toast.makeText(getApplicationContext(), "Ensure both password fields are equal", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				//password contains valid characters
				if (doServer == true)
				{
					for(int i = 0; i < password.length(); i++)
					{
						if(!(password.charAt(i) >= 'a' && password.charAt(i) <= 'z'))
						{
							if(!(password.charAt(i) >= 'A' && password.charAt(i) <= 'Z'))
							{
								if(!(password.charAt(i) >= '0' && password.charAt(i) <= '9'))
								{
									Toast.makeText(getApplicationContext(), "Password may only contain letters and numbers", Toast.LENGTH_LONG).show();
									doServer = false;
								}
							}
						}
					}
				}
				
				if(dob.length() != 10) {
					Toast.makeText(getApplicationContext(), "Ensure Date of Birth has the format: yyyy-mm-dd", Toast.LENGTH_LONG).show();
					doServer = false;
				}
				else {
					for(int i = 0; i < 10; i++)
					{
						if(i == 4 || i == 7)
						{
							if(dob.charAt(i) != '-')
							{
								Toast.makeText(getApplicationContext(), "Ensure Date of Birth has the format: yyyy-mm-dd", Toast.LENGTH_LONG).show();
								doServer = false;
								break;
							}
						}
						else if(dob.charAt(i) < '0' || dob.charAt(i) > '9' )
						{
							Toast.makeText(getApplicationContext(), "Ensure Date of Birth has the format: yyyy-mm-dd", Toast.LENGTH_LONG).show();
							doServer = false;
							break;
						}
					}
				}
				
				if(doServer == true)
				{
					RequestQueue mRequestQueue;
	
					// Instantiate the cache
					Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
	
					// Set up the network to use HttpURLConnection as the HTTP client.
					Network network = new BasicNetwork(new HurlStack());
	
					// Instantiate the RequestQueue with the cache and network.
					mRequestQueue = new RequestQueue(cache, network);
	
					// Start the queue
					mRequestQueue.start();
	
					JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, 
							"http://192.168.0.111/flatmateadd.php", new JSONObject(params),  new Response.Listener<JSONObject>() {
	
						@Override
						public void onResponse(JSONObject response) {
							try {
								if((response.get("Success").toString()).equals("New record created successfully")) {
									Toast.makeText(getApplicationContext(), "Account Successfully Created. Log in again.", Toast.LENGTH_SHORT).show();
									startActivity(new Intent(Create_user_account.this, LoginActivity.class));
								}
								else
									Toast.makeText(getApplicationContext(), response.get("Success").toString(), Toast.LENGTH_LONG).show();
							} 
							catch (JSONException e) {
								e.printStackTrace();
							}
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(getApplicationContext(), "An Error Occured", Toast.LENGTH_LONG).show();
						}
					});
					mRequestQueue.add(jsObjRequest);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_user_account, menu);
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