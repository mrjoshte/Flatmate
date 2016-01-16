package activityfiles;

import android.app.Application;
import android.widget.Toast;

public class AndroidApplication extends Application {
	

	String userName;
	String apartmentId;
	String flatID;
	String email;
	String landlordAddress;
	String landlordPassword; //Apartment ID
	String role;
	String firstName, lastName;

	boolean isLandlord;
	
	String selectedFlatInApartment;
	
	String selectedTenantInFlat;
		
	String expenseName, expenseDescription, assignedTo, cost, expenseType;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Toast.makeText(this, "Welcome to FlatMate", Toast.LENGTH_SHORT).show();
	}
}
