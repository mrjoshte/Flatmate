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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Lists extends BaseNav {

	Spinner dropdown;
	Button addItemButton, deleteButton;

	TextView listStatus;

	ArrayList<String> listOfItems ;
	ArrayList<String> lists;
	ArrayList<Boolean> listOfEnabled ;

	String dialogInput, flatID;

	RequestQueue mRequestQueue;

	Handler mHandler = new Handler();

	int listsCount, itemsCount;
	
	String listName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists);

		super.onCreate("activity_lists");

		final AndroidApplication app = ((AndroidApplication)getApplication());
		flatID = app.flatID;

		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

		listsCount = -1;
		itemsCount = -1;

		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());

		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);

		// Start the queue
		mRequestQueue.start();

		dropdown = (Spinner)findViewById(R.id.selectListSpinner);
		deleteButton = (Button)findViewById(R.id.deleteListButton);
		addItemButton = (Button)findViewById(R.id.addItemToListButton);
		listStatus = (TextView)findViewById(R.id.listData);

		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dropdown.getSelectedItem().toString().equals("-"))
				{
					Toast.makeText(getApplicationContext(), "This list can't be deleted", Toast.LENGTH_SHORT).show();
				}
				else
				{
					new AlertDialog.Builder(Lists.this)
					.setMessage("Are you sure you want to delete " + dropdown.getSelectedItem().toString() + "?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Map<String, String> params = new HashMap<String, String>();
							params.put("ListName", dropdown.getSelectedItem().toString());
							params.put("FlatID", flatID);
							JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/deleteList.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try 
									{
										if(((String)response.get("Success")).equals("Success"))
										{
											lists.remove(dropdown.getSelectedItem().toString());
											ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lists.this, android.R.layout.simple_spinner_dropdown_item, lists);
											dropdown.setAdapter(adapter);

											TableLayout table = (TableLayout) findViewById(R.id.itemListTable);
											table.removeAllViews();

											listStatus.setText("The list was deleted");

											Toast.makeText(getApplicationContext(), "The list has been deleted", Toast.LENGTH_SHORT).show();
										}
										else
											Toast.makeText(getApplicationContext(), "An Error Occured deleting the list", Toast.LENGTH_SHORT).show();

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
			}
		});

		dropdown.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(lists.get(arg2).equals("Create New List"))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(Lists.this);
					builder.setTitle("Enter New List Name:");

					final EditText input = new EditText(Lists.this);
					input.setInputType(InputType.TYPE_CLASS_TEXT);
					builder.setView(input);

					builder.setPositiveButton("Add List",  new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							dialogInput = input.getText().toString();

							Map<String, String> params = new HashMap<String, String>();
							params.put("OwnedBy", app.firstName + " " + app.lastName);
							params.put("ListName", dialogInput);
							params.put("FlatID", flatID);

							JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/createList.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try 
									{
										if(((String)response.get("Success")).equals("Success"))
										{
											Toast.makeText(getApplicationContext(), dialogInput + " list was created.", Toast.LENGTH_SHORT).show();
											lists.add(dialogInput);
											ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lists.this, android.R.layout.simple_spinner_dropdown_item, lists);
											dropdown.setAdapter(adapter);
										}
										else
											Toast.makeText(getApplicationContext(), "An Error Occured creating the list", Toast.LENGTH_SHORT).show();
									} 
									catch (JSONException e) 
									{
										e.printStackTrace();
									}

									lists.remove("Create New List");
									lists.add("Create New List");

									ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lists.this, android.R.layout.simple_spinner_dropdown_item, lists);
									dropdown.setAdapter(adapter);
									
									int index = 0;
									if(lists.indexOf(dialogInput) < 0)
										index = 0;
									else
										index = lists.indexOf(dialogInput);
									
									dropdown.setSelection(index);
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
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							dropdown.setSelection(lists.indexOf("-"));
						}
					});
					AlertDialog dialog = builder.create();

					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

					dialog.show();
				}

				//Default list of nothing, don't do anything
				else if(lists.get(arg2).equals("-"))
				{
					listStatus.setText("Select or Create a list!");
					TableLayout table = (TableLayout) findViewById(R.id.itemListTable);
					table.removeAllViews();
				}
				//else get all of the items from the list and display them
				else
				{
					itemsCount = -1;
					listName = lists.get(arg2);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//Do nothing!
			}
		});

		addItemButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dropdown.getSelectedItem().toString().equals("-"))
				{
					Toast.makeText(getApplicationContext(), "You can't add an item to this list", Toast.LENGTH_SHORT).show();
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(Lists.this);
					builder.setTitle("Enter new Item to your list:");

					final EditText input = new EditText(Lists.this);
					input.setInputType(InputType.TYPE_CLASS_TEXT);
					builder.setView(input);

					builder.setPositiveButton("Add",  new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							dialogInput = input.getText().toString();

							Map<String, String> params = new HashMap<String, String>();
							params.put("itemName", dialogInput);
							params.put("ListName", dropdown.getSelectedItem().toString());
							params.put("Selected", "0");
							params.put("FlatID", flatID);

							JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/addItemToList.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									try 
									{
										if(((String)response.get("Success")).equals("Success"))
										{
											listStatus.setText("Modify the list!");
											listOfItems.add(dialogInput);
											listOfEnabled.add(false);

											populateItems(listOfItems, listOfEnabled);

											Toast.makeText(getApplicationContext(), dialogInput + " was added to the list.", Toast.LENGTH_SHORT).show();
										}
										else
											//Toast.makeText(getApplicationContext(), "An Error Occured adding the item", Toast.LENGTH_SHORT).show();
											Toast.makeText(getApplicationContext(), response.get("Success").toString(), Toast.LENGTH_SHORT).show();
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
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
						}
					});

					AlertDialog dialog = builder.create();

					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

					dialog.show();
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
								listName = "";

								if(dropdown.getSelectedItem() == null)
									listName = "-";
								else
									listName = dropdown.getSelectedItem().toString();
								
								Map<String, String> params = new HashMap<String, String>();
								params.put("FlatID", flatID);

								JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getLists.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) {
										try 
										{
											JSONArray jsonArray = response.getJSONArray("list"); 
											if (jsonArray != null)
											{ 
												int len = jsonArray.length();
												lists = new ArrayList<String>();
												lists.add("-");
												for (int i=0; i<len; i++)
												{ 
													JSONObject object = jsonArray.getJSONObject(i);
													lists.add(object.get("ListName").toString());
												} 
												if(len == 0)
												{
													lists.remove("-");
													lists.add("-");
												}
											} 				
										} 
										catch (JSONException e) 
										{
											e.printStackTrace();
										}
										lists.remove("Create New List");
										lists.add("Create New List");

										if(listsCount != lists.size())
										{
											listsCount = lists.size();
													
											ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lists.this, android.R.layout.simple_spinner_dropdown_item, lists);
											dropdown.setAdapter(adapter);
											
											int index = lists.indexOf(listName);
											if(index < 0)
												index = 0;
											dropdown.setSelection(index);
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
								params.put("ListName", listName);
								params.put("FlatID", flatID);

								jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/getItemsFromList.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) {
										try 
										{
											listOfItems = new ArrayList<String>();
											listOfEnabled = new ArrayList<Boolean>();

											JSONArray jsonArray = response.getJSONArray("items"); 
											if (jsonArray != null)
											{ 
												int len = jsonArray.length();
												for (int i=0; i<len; i++)
												{ 
													JSONObject object = jsonArray.getJSONObject(i);
													listOfItems.add(object.get("Item").toString());

													if(object.get("Selected").toString().equals("0"))
														listOfEnabled.add(false);
													else
														listOfEnabled.add(true);
												} 
												if(len == 0)
												{
													listStatus.setText("This list is empty");
												}
												else
													listStatus.setText("Modify the list!");
											} 	
											populateItems(listOfItems, listOfEnabled);
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

	private void populateItems(final ArrayList<String> list, final ArrayList<Boolean> enabled)
	{
		TableLayout table = (TableLayout) findViewById(R.id.itemListTable);

		table.removeAllViews();

		for (int row = 0; row < list.size(); row++)
		{
			final int ROWFINAL = row;

			TableRow tableRow = new TableRow(this);
			table.addView(tableRow);

			//eventually add delete button in another column next to the main button for each item

			final CheckBox checkBox = new CheckBox(this);
			checkBox.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,//width
					TableRow.LayoutParams.MATCH_PARENT,//height
					1.0f //scaling weight
					));
			checkBox.setText(list.get(row));
			checkBox.setChecked(enabled.get(row));

			//sets what each button does
			checkBox.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, String> params = new HashMap<String, String>();
					params.put("itemName", list.get(ROWFINAL));
					params.put("ListName", dropdown.getSelectedItem().toString());
					params.put("FlatID", flatID);
					params.put("Selected", checkBox.isChecked() ? "1" : "0");

					JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/changeItemSelectedState.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							try 
							{
								if(!((String)response.get("Success")).equals("Success"))
									Toast.makeText(getApplicationContext(), "An error occured deleting the item.", Toast.LENGTH_SHORT).show();
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

			tableRow.addView(checkBox);

			Button deleteButton = new Button(this);
			deleteButton.setText("Delete");
			deleteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, String> params = new HashMap<String, String>();
					params.put("itemName", list.get(ROWFINAL));
					params.put("ListName", dropdown.getSelectedItem().toString());
					params.put("FlatID", flatID);

					JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://proj-309-40.cs.iastate.edu/deleteItemFromList.php", new JSONObject(params),  new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							try 
							{
								if(((String)response.get("Success")).equals("Success"))
								{
									listOfEnabled.remove(listOfItems.indexOf(list.get(ROWFINAL)));
									listOfItems.remove(list.get(ROWFINAL));

									populateItems(listOfItems, listOfEnabled);

									if(listOfItems.size() == 0)
										listStatus.setText("This list is empty");

									Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
								}
								else
									Toast.makeText(getApplicationContext(), "An error occured deleting the item.", Toast.LENGTH_SHORT).show();

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

			tableRow.addView(deleteButton);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lists, menu);
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
