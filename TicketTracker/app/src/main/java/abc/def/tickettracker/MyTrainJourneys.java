package abc.def.tickettracker;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class MyTrainJourneys extends ListActivity {
	String[] values;
	private static final int TRAIN_JOURNEYS = 1;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String[] values;
		String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		Log.d("TJ", currDate);
		Cursor c = TicketTracker.db.rawQuery(
				"SELECT * FROM train_tkts  ASC WHERE Date(DOJ) >= Date('"
						+ currDate + "') ORDER BY DOJ ", null);
		if (c.getCount() == 0) {
			// showMessage("Error", "No records found");
			values = new String[] { "Upcoming Journeys..." + "\n"
					+ "No upcoming journeys :(" };
		} else {
			int i = 1;
			values = new String[c.getCount() * 2 + 1];

			values[0] = "Upcoming Journeys... :)";
			while (c.moveToNext()) {

				values[i++] = c.getString(2) + " @ " + c.getString(3);
				values[i] = "PNR: " + c.getString(0) + " TRAIN no: "
						+ c.getString(1) + "\n" + "From: " + c.getString(4)
						+ " To:" + c.getString(5);
				i++;
			}

		}

		ArrayAdapter<String> adapter = new MyArrayAdapter(this, TRAIN_JOURNEYS,
				values);
		setListAdapter(adapter);
	}
}
