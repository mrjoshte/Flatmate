package activityfiles;

import com.example.flatemateapartmentmanager.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BaseNav extends ActionBarActivity{

	private ListView mDrawerList;
	private ArrayAdapter<String> mAdapter;

	ActionBarDrawerToggle mDrawerToggle;
	DrawerLayout mDrawerLayout;
	String mActivityTitle;

	String[] osArray = null;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	public void onCreate(String page)
	{
		mDrawerList = (ListView)findViewById(R.id.navList);
		addDrawerItems();

		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch(osArray[position])
				{
				case "At a Glance":
					startActivity(new Intent(BaseNav.this, AtAGlancePage.class));
					break;
				case "Chat":
					startActivity(new Intent(BaseNav.this, Chat.class));
					break;
				case "Expenses":
					startActivity(new Intent(BaseNav.this, Expenses.class));
					break;
				case "Lists":
					startActivity(new Intent(BaseNav.this, Lists.class));
					break;
				case "Calendar":
					startActivity(new Intent(BaseNav.this, AtAGlancePage.class));
					break;
				case "Add Roommates":
					startActivity(new Intent(BaseNav.this, AddRoommates.class));
					break;
				case "Remove Roommates":
					startActivity(new Intent(BaseNav.this, RemoveUser.class));
					break;
				case "Change Admin":
					startActivity(new Intent(BaseNav.this, TransferAdmin.class));
					break;
				case "Logout":
					startActivity(new Intent(BaseNav.this, LoginActivity.class));
					break;
				case "Homepage":
					startActivity(new Intent(BaseNav.this, LandlordHomePage.class));
					break;
				}
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if(page == "activity_create_user_account") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_create_user_account);
		else if(page == "activity_chat") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_chat);
		else if(page == "activity_at_aglance_page") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_at_aglance_page);
		else if(page == "activity_lists") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_lists);
		else if(page == "activity_remove_user") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_remove_user);
		else if(page == "activity_transfer_admin") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_transfer_admin);
		else if(page == "activity_expenses") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_expenses);

		else if(page == "activity_landlord_home_page") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_landlord_home_page);
		else if(page == "activity_manage_tenants") mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_manage_tenants);

		mActivityTitle = getTitle().toString();

		setupDrawer();
	}

	private void addDrawerItems() {
		AndroidApplication app = ((AndroidApplication)getApplication());
		if(app.role != null)
			switch(app.role)
			{
			case "Admin":
			{
				osArray = new String[]{ "At a Glance", "Chat", "Lists", "Expenses", "Add Roommates", "Remove Roommates", "Change Admin", "Logout"};
				break;
			}
			case "Tenant":
			{
				osArray = new String[]{ "At a Glance", "Chat", "Lists", "Expenses", "Logout" };
				break;
			}
			case "Landlord":
			{
				osArray = new String[]{ "Homepage", "Logout" };
				break;
			}
			}
		else
			osArray = new String[]{ "At a Glance", "Chat", "Lists", "Expenses", "Logout" };

		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
		mDrawerList.setAdapter(mAdapter);
	}

	private void setupDrawer() {

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle("Navigation!");
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mActivityTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

}
