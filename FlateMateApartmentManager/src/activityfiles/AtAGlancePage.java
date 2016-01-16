package activityfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class AtAGlancePage extends BaseNav {

	private RequestQueue mRequestQueue;

	private String flatID;

	private List<String> listActivity, flatActivity, chatActivity, expensesActivity;

	private String[] categories = new String[]{"Flat Activity", "List Activity", "Chat Activity", "Expenses"};
	private List[] activities;

	Handler mHandler = new Handler();

	int activityCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_at_aglance_page);

		super.onCreate("activity_at_aglance_page");

		final AndroidApplication app = ((AndroidApplication)getApplication());
		flatID = app.flatID;

		activityCount = -1;

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

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
								params.put("FlatID", flatID);

								JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getRecentActivity.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) {
										try 
										{
											JSONArray jsonArray = response.getJSONArray("list"); 
											if (jsonArray != null)
											{ 
												int len = jsonArray.length();
												flatActivity = new ArrayList<String>();
												listActivity = new ArrayList<String>();
												chatActivity = new ArrayList<String>();
												expensesActivity = new ArrayList<String>();

												for (int i=0; i<len; i++)
												{ 
													JSONObject object = jsonArray.getJSONObject(i);
													switch(object.get("Category").toString())
													{
													case "Flat":
														flatActivity.add(object.get("Activity").toString());
														break;

													case "List":
														listActivity.add(object.get("Activity").toString());
														break;

													case "Chat":
														chatActivity.add(object.get("Activity").toString());
														break;

													case "Expenses":
														expensesActivity.add(object.get("Activity").toString());
														break;
													}
												} 

												if(activityCount != flatActivity.size() + listActivity.size() + chatActivity.size() + expensesActivity.size())
												{
													activityCount = flatActivity.size() + listActivity.size() + chatActivity.size() + expensesActivity.size();
													
													flatActivity = flatActivity.subList(Math.max(0, flatActivity.size() - 10), flatActivity.size());
													listActivity = listActivity.subList(Math.max(0, listActivity.size() - 10), listActivity.size());
													chatActivity = chatActivity.subList(Math.max(0, chatActivity.size() - 10), chatActivity.size());
													expensesActivity = expensesActivity.subList(Math.max(0, expensesActivity.size() - 10), expensesActivity.size());

													Collections.reverse(flatActivity);
													Collections.reverse(listActivity);
													Collections.reverse(chatActivity);
													Collections.reverse(expensesActivity);

													activities = new List[]{flatActivity, listActivity, chatActivity, expensesActivity};

													generateRecentTable();
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
									public void onErrorResponse(VolleyError error) {
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
	}

	private void generateRecentTable()
	{
		TableLayout table = (TableLayout) findViewById(R.id.recentActivityTableAtAGlance);

		table.removeAllViews();

		for(int i = 0; i < categories.length; i++)
		{
			//Add the flat title category text view
			TableRow tableRow = new TableRow(this);
			table.addView(tableRow);
			final TextView flatTitle = new TextView(this);
			flatTitle.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));
			flatTitle.setText(categories[i]);
			flatTitle.setTextSize(30);
			flatTitle.setTextColor(Color.BLUE);
			tableRow.addView(flatTitle);

			List<String> categoryList = activities[i];

			if(categoryList.size() == 0)
			{
				TableRow empty = new TableRow(this);
				table.addView(empty);
				final TextView name = new TextView(this);
				name.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,//width
						TableRow.LayoutParams.MATCH_PARENT,//height
						1.0f //scaling weight
						));
				name.setText("You don't have any recent " + categories[i]);

				empty.addView(name);
			}
			for(int j = 0; j < categoryList.size(); j++)
			{
				//Add in all of the recent activity for the category
				tableRow = new TableRow(this);
				table.addView(tableRow);

				final TextView flatActivityItem = new TextView(this);
				flatActivityItem.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,//width
						TableRow.LayoutParams.MATCH_PARENT,//height
						1.0f //scaling weight
						));
				flatActivityItem.setText(categoryList.get(j));
				tableRow.addView(flatActivityItem);
			}
			//Add in a buffer
			tableRow = new TableRow(this);
			table.addView(tableRow);
			final TextView buffer = new TextView(this);
			buffer.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));
			buffer.setTextSize(30);
			tableRow.addView(buffer);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.at_aglance_page, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		//moveTaskToBack(true);
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
