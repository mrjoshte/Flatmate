package activityfiles;

import java.util.ArrayList;
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Expenses extends BaseNav {

	private RequestQueue mRequestQueue;

	private String flatID;

	private Button createExpenseButton;

	private List<Expense> requiredExpenses, optionalExpenses;

	private EditText message;

	String[] roommates;

	AndroidApplication app;

	Handler mHandler = new Handler();

	int expenseCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expenses);

		super.onCreate("activity_expenses");

		expenseCount = -1;

		app = ((AndroidApplication)getApplication());
		flatID = app.flatID;

		createExpenseButton = (Button)findViewById(R.id.createNewExpenseButton);

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

		Map<String, String> params = new HashMap<String, String>();
		params.put("FlatID", flatID);
		params.put("UserID", "");
		JsonObjectRequest jsObjRequestFlats = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getRoommates.php", new JSONObject(params),  new Response.Listener<JSONObject>() {	
			@Override
			public void onResponse(JSONObject response) {
				try {	
					JSONArray jsonArray = response.getJSONArray("list"); 
					if (jsonArray != null)
					{ 
						int len = jsonArray.length();
						roommates = new String[len];
						for (int i=0; i<len; i++)
						{ 
							JSONObject object = jsonArray.getJSONObject(i);
							roommates[i] = object.get("FirstName").toString() + " " + object.get("LastName").toString();
						} 
					}
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

		createExpenseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());

				alert.setTitle("Create New Expense");

				// Set an EditText view to get user input 
				final EditText expenseNameField = new EditText(Expenses.this);
				expenseNameField.setHint("Expense Name");
				final EditText expenseDescriptionField = new EditText(Expenses.this);
				expenseDescriptionField.setHint("Enter Expense Description");
				final Button assignedToField = new Button(Expenses.this);
				assignedToField.setHint("Assign To");
				assignedToField.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final ArrayList<String> mSelectedItems = new ArrayList<String>();  // Where we track the selected items
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						// Set the dialog title

						builder.setTitle("Select Roommates")
						// Specify the list array, the items to be selected by default (null for none),
						// and the listener through which to receive callbacks when items are selected
						.setMultiChoiceItems(roommates, null,
								new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which,
									boolean isChecked) {
								if (isChecked) {
									// If the user checked the item, add it to the selected items
									mSelectedItems.add(roommates[which]);
								} else if (mSelectedItems.contains(which)) {
									// Else, if the item is already in the array, remove it 
									mSelectedItems.remove(which);
								}
							}
						})
						// Set the action buttons
						.setPositiveButton("Select", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								String buttonName = "";
								for(String name : mSelectedItems)
									buttonName += name + ", ";
								if(!buttonName.equals(""))
									buttonName = "Assigned To: " + buttonName.substring(0, buttonName.length() - 2);
								else
									buttonName = "Assigned To: Nobody";
								assignedToField.setText(buttonName);
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});
						builder.show();
					}
				});
				final EditText costField = new EditText(Expenses.this);
				costField.setHint("Enter Cost");
				costField.setKeyListener(new DigitsKeyListener());
				final Spinner typeField = new Spinner(Expenses.this);
				String[] items = new String[]{"Required", "Optional"};
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(Expenses.this, android.R.layout.simple_spinner_dropdown_item, items);
				typeField.setAdapter(adapter);

				LinearLayout layout = new LinearLayout(getApplicationContext());
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.addView(typeField);
				layout.addView(expenseNameField);
				layout.addView(expenseDescriptionField);
				layout.addView(costField);
				layout.addView(assignedToField);
				alert.setView(layout);

				alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						final String name = expenseNameField.getText().toString();
						final String description = expenseDescriptionField.getText().toString();
						final String assignedTo = assignedToField.getText().toString();
						final String cost = costField.getText().toString();
						final String type = typeField.getSelectedItem().toString();

						Map<String, String> params = new HashMap<String, String>();
						params.put("FlatID", flatID);
						params.put("ExpenseName", name);
						params.put("ExpenseDescription", description);
						params.put("AssignedTo", assignedTo);
						params.put("Cost", cost);
						params.put("ExpenseType", type);

						JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/createExpense.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try 
								{
									if(response.get("Success").toString().equals("Success"))
									{
										switch(type)
										{
										case "Required":
											requiredExpenses.add(
													new Expense(
															name, 
															description,
															assignedTo,
															Double.parseDouble(cost)
															));
											break;

										case "Optional":
											optionalExpenses.add(
													new Expense(
															name, 
															description,
															assignedTo,
															Double.parseDouble(cost)
															));
											break;
										}
										generateRecentTable();

										Toast.makeText(getApplicationContext(), "Expense Created", Toast.LENGTH_SHORT).show();
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

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
				alert.show();
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
								params.put("FlatID", flatID);

								JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getExpenses.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) {
										try 
										{
											JSONArray jsonArray = response.getJSONArray("list"); 
											if (jsonArray != null)
											{ 
												int len = jsonArray.length();
												requiredExpenses = new ArrayList<Expense>();
												optionalExpenses = new ArrayList<Expense>();

												for (int i=0; i<len; i++)
												{ 
													JSONObject object = jsonArray.getJSONObject(i);
													switch(object.get("ExpenseType").toString())
													{
													case "Required":
														requiredExpenses.add(
																new Expense(
																		object.get("ExpenseName").toString(), 
																		object.get("ExpenseDescription").toString(),
																		object.get("AssignedTo").toString(),
																		Double.parseDouble(object.get("Cost").toString())
																		));
														break;

													case "Optional":
														optionalExpenses.add(
																new Expense(
																		object.get("ExpenseName").toString(), 
																		object.get("ExpenseDescription").toString(),
																		object.get("AssignedTo").toString(),
																		Double.parseDouble(object.get("Cost").toString())
																		));
														break;
													}
												} 

												if(expenseCount < optionalExpenses.size() + requiredExpenses.size())
												{
													expenseCount = optionalExpenses.size() + requiredExpenses.size();
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
		TableLayout table = (TableLayout) findViewById(R.id.expensesTable);

		table.removeAllViews();

		final String[] categories = new String[]{"Required", "Optional"};

		List[] expenses = new List[]{requiredExpenses, optionalExpenses};

		for(int i = 0; i < categories.length; i++)
		{
			final int finalRowI = i;

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

			final List<Expense> categoryList = expenses[i];

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
				name.setText("You don't have any recent " + categories[i] + " expenses");

				empty.addView(name);
			}

			for(int j = 0; j < categoryList.size(); j++)
			{
				final int finalRow = j;

				//Add in all of the recent activity for the category
				tableRow = new TableRow(this);
				table.addView(tableRow);

				final Button expense = new Button(this);
				expense.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,//width
						TableRow.LayoutParams.MATCH_PARENT,//height
						1.0f //scaling weight
						));
				expense.setText(categoryList.get(j).ExpenseName + " - $" + categoryList.get(j).cost);
				expense.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						// 1. Instantiate an AlertDialog.Builder with its constructor
						AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());

						alert.setTitle(categoryList.get(finalRow).ExpenseName);

						alert.setMessage("Expense Type: " + categories[finalRowI] + "\n\n" 
								+ "Description: " + categoryList.get(finalRow).ExpenseDescription + "\n\n" 
								+ "Cost: $" + categoryList.get(finalRow).cost + "\n\n" 
								+ categoryList.get(finalRow).AssignedTo);

						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								//Do nothing. This code is required
							}
						});

						if(app.role.equals("Admin"))
							alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									Map<String, String> params = new HashMap<String, String>();
									params.put("FlatID", flatID);
									params.put("ExpenseName", categoryList.get(finalRow).ExpenseName);
									JsonObjectRequest jsObjRequestFlats = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/deleteExpense.php", new JSONObject(params),  new Response.Listener<JSONObject>() {	
										@Override
										public void onResponse(JSONObject response) {
											try {
												if(response.get("Success").toString().equals("Success"))
												{
													for(Expense e : requiredExpenses)
													{
														if(e.ExpenseName.equals(categoryList.get(finalRow).ExpenseName))
														{
															categoryList.remove(e);
															break;
														}
													}
													for(Expense e : optionalExpenses)
													{
														if(e.ExpenseName.equals(categoryList.get(finalRow).ExpenseName))
														{
															optionalExpenses.remove(e);
															break;
														}
													}
													generateRecentTable();
													Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
												}
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
							});
						alert.show();
					}
				});

				tableRow.addView(expense);
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


	public class Expense
	{
		String ExpenseName, ExpenseDescription, AssignedTo;
		double cost;

		public Expense(String ExpenseName, String ExpenseDescription, String AssignedTo, double cost)
		{
			this.ExpenseName = ExpenseName;
			this.ExpenseDescription = ExpenseDescription;
			this.AssignedTo = AssignedTo;
			this.cost = cost;
		}
	}
}
