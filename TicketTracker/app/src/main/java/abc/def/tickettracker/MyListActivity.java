package abc.def.tickettracker;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class MyListActivity  extends ListActivity {
	private static final int TRAVEL_PLAN = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		String[] values;
		Cursor c = TicketTracker.db.rawQuery(
				"SELECT * FROM travel_plan ORDER BY TICKET_DATE;", null);
		Log.d("Cursor", ""+c.getCount());
		int count = c.getCount();
		if(count != 0) {
			int i  = 1;
			 values = new String[count+1];
			 values[0] = "Travel Date \t Ticket Booking Date \t";
				while (c.moveToNext())
				{      
				
				values[i] = c.getString(0) +  "\t\t" + c.getString(1);
				i++;
			
				}
		}
		else {
			 values = new String[2];
			 values[0] = "Travel Plans...\n";
			 values[1] = "No travel plans available.";
		}
		
	    // use your custom layout
	    ArrayAdapter<String> adapter = new MyArrayAdapter(this, TRAVEL_PLAN, values);
	    setListAdapter(adapter);
	}

}
