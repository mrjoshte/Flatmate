package activityfiles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Chat extends BaseNav {

	EditText chatMessage;
	Button chatSendMessage;
	ScrollView scroll;

	ArrayList<String> messages, names, time;

	RequestQueue mRequestQueue;

	AndroidApplication app;

	Handler mHandler = new Handler();

	int chatCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		super.onCreate("activity_chat");

		chatCount = -1;

		app = ((AndroidApplication)getApplication());

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

		chatMessage = (EditText)findViewById(R.id.MessageField);
		chatSendMessage = (Button)findViewById(R.id.sendMessageButton);
		scroll = (ScrollView)findViewById(R.id.scrollViewChatPage);

		chatMessage.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scroll.post(new Runnable() {

						@Override
						public void run() {
							scroll.fullScroll(ScrollView.FOCUS_DOWN);
						}
					});
				}
			}
		});

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true) 
				{
					try 
					{
						mHandler.post(new Runnable() 
						{
							@Override
							public void run() 
							{
								Map<String, String> params = new HashMap<String, String>();
								params.put("FlatID", app.flatID);

								JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getChatMessages.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) 
									{
										try 
										{
											JSONArray jsonArray = response.getJSONArray("list"); 
											if (jsonArray != null)
											{ 
												int len = jsonArray.length();

												messages = new ArrayList<String>();
												names = new ArrayList<String>();
												time = new ArrayList<String>();

												for (int i = 0; i < len; i++)
												{ 
													JSONObject object = jsonArray.getJSONObject(i);
													messages.add(object.get("Message").toString());
													names.add(object.get("FirstName").toString() + " " + object.get("LastName").toString().toCharArray()[0]);
													time.add(object.get("timeSent").toString());
												} 

												if(chatCount < messages.size())
												{
													chatCount = messages.size();
													generateTable();

													scroll.post(new Runnable() {

														@Override
														public void run() {
															scroll.fullScroll(ScrollView.FOCUS_DOWN);
														}
													});
												}
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
									public void onErrorResponse(VolleyError error) 
									{
										Toast.makeText(getApplicationContext(), "An Error Occured", Toast.LENGTH_SHORT).show();
									}
								});

								mRequestQueue.add(jsObjRequest);
							}
						});
						Thread.sleep(1000);
					} 
					catch (Exception e)
					{
						// TODO: handle exception
					}
				}
			}
		}).start();

		chatSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(!chatMessage.getText().toString().equals(""))
				{
					int messageLength = chatMessage.getText().toString().length();
					if (messageLength > 160)
					{
						Toast.makeText(getApplicationContext(), "Your message is " + messageLength + " characters. Limit message to 160 characters.", Toast.LENGTH_SHORT).show();
						return;
					}

					Map<String, String> params = new HashMap<String, String>();
					params.put("UserID", app.userName);
					params.put("FlatID", app.flatID);
					params.put("FirstName", app.firstName);
					params.put("LastName", app.lastName);
					params.put("Message", chatMessage.getText().toString());

					JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/addMessageToChat.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {

							try {
								if(response.get("Success").toString().equals("Success"))
								{
									messages.add(chatMessage.getText().toString());
									names.add(app.firstName + " " + app.lastName.toCharArray()[0]);
									time.add(new SimpleDateFormat("HH:mmaa").format(Calendar.getInstance().getTime()).toString().toLowerCase());

									generateTable();
									chatMessage.setText("");

									scroll.post(new Runnable() {

										@Override
										public void run() {
											scroll.fullScroll(ScrollView.FOCUS_DOWN);
										}
									});
								}
								else
									Toast.makeText(getApplicationContext(), "Error ssending message", Toast.LENGTH_SHORT).show();
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
			}
		});
	}


	//TODO THis doesnt work

	private void generateTable()
	{
		TableLayout table = (TableLayout) findViewById(R.id.chatTable);

		table.removeAllViews();

		for(int i = 0; i < messages.size(); i++)
		{
			//Add the name
			TableRow nameRow = new TableRow(this);
			table.addView(nameRow);

			final TextView name = new TextView(this);
			name.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));
			name.setText(names.get(i) + " " + time.get(i));
			name.setTextColor(Color.rgb(120, 120, 120));
			name.setTextSize(11);

			//Add timestamp
			final TextView timeStamp = new TextView(this);
			timeStamp.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));
			timeStamp.setText(time.get(i));
			timeStamp.setTextColor(Color.GRAY);
			timeStamp.setTextSize(11);

			//Add message
			TableRow messageRow = new TableRow(this);
			table.addView(messageRow);

			final TextView message = new TextView(this);
			message.setText(messages.get(i));
			message.setTextSize(14);
			message.setPadding(0, 0, 0, 10);
			message.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));

			if((app.firstName + " " + app.lastName.toCharArray()[0]).equals(names.get(i)))
			{
				message.setPadding(0, 0, 5, 10);
				message.setGravity(Gravity.RIGHT);
				name.setText(time.get(i) + " You");
				name.setPadding(0, 0, 5, 0);
				name.setTextColor(Color.GREEN);
				name.setGravity(Gravity.RIGHT);
			}

			nameRow.addView(name);
			messageRow.addView(message);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
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
