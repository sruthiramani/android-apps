package abc.def.tickettracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class TicketTracker extends Activity implements OnClickListener,
		OnSharedPreferenceChangeListener {
	public static class TicketTrackerData {

		private static final int BOOKING_DATE_OFFSET = -120;
		public Button updatePlan;
		public EditText addDates;
		public int datesUpdated;
		Context context;
		

		public TicketTrackerData(Context context) {
			this.context = context;
		}

		private void addToPlan(boolean autoReminder, int reminderT) {
			/*
			 * validate the input in EditText on valid input: *handle single
			 * input case *handle multiple input case on invalid input: * ask
			 * user to correct and retry
			 */
			String inputDates = addDates.getText().toString();

			if (inputDates.isEmpty()) {
				Toast.makeText(context, "No dates updated.", Toast.LENGTH_SHORT)
						.show();
				showUI();
				return;
			} else {

				if (inputDates.contains(",")) {
					/* more than one date */
					for (String input : inputDates.split(",")) {
						validateAndInsert(input.replaceAll("\\s+", ""),
								autoReminder, reminderT);

					}

				} else {
					/* only one date */
					validateAndInsert(inputDates.replaceAll("\\s+", ""),
							autoReminder, reminderT);

				}
				if (datesUpdated > 0)
					Toast.makeText(context, "Updated dates:" + datesUpdated,
							Toast.LENGTH_SHORT).show();
				datesUpdated = 0;
				showUI();
			}

		}

		private void validateAndInsert(String inputDates, boolean autoReminder, int reminderT) {
			if (true == validateDate(inputDates)) {
				String ticket_date = computeTrainTicketDate(inputDates);
				if (!ticket_date.equalsIgnoreCase("NULL")) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date iDate = sdf.parse(inputDates);
						Log.d("DBBefore", inputDates);
						inputDates = sdf.format(iDate);
						Log.d("DBAfter", inputDates);
						Cursor resultSet = db.rawQuery(
								"Select * from travel_plan WHERE HOLIDAY_DATE=Date('"
										+ inputDates + "');", null);

						Log.d("DBupd", "" + resultSet.getCount());
						if (resultSet.getCount() == 0) {
							db.execSQL("INSERT INTO travel_plan VALUES('"
									+ inputDates + "','" + ticket_date + "');");
							datesUpdated++;

							if (autoReminder == true) {
								createEventAndReminder(inputDates, ticket_date,
										sdf, reminderT);
							}

						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}

		@SuppressLint("InlinedApi")
		private void createEventAndReminder(String travel_date,
				String ticket_date, SimpleDateFormat sdf, int reminderT) throws ParseException {

			/* set reminder */

			String eventUriString;
			if (Build.VERSION.SDK_INT > 7)
				eventUriString = "content://com.android.calendar/events";
			else
				eventUriString = "content://calendar/events";
			ContentValues event = new ContentValues();
			event.put("calendar_id", 1);
			event.put("title", "Ticket tracker");
			event.put("description", "Book Ticket for " + travel_date);
			event.put("eventLocation", "Home");
			/* Create event for 15mins from 7:45 AM to 8:00 AM */
			long startTime = (sdf.parse(ticket_date).getTime() + 27900 * 1000);
			long endTime = startTime + 1000 * 60 * 15;

			event.put("dtstart", startTime);
			event.put("dtend", endTime);
			event.put("eventStatus", 1);
			event.put("allDay", 0);
			event.put("hasAlarm", 1);
			if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
				event.put(Events.EVENT_TIMEZONE, "India");
			}
			Uri eventsUri = Uri.parse(eventUriString);
			Uri eventUri = context.getContentResolver()
					.insert(eventsUri, event);
			long eventID = Long.parseLong(eventUri.getLastPathSegment());

			// String to access default google calendar of device for reminder
			// setting.
			String reminderUriString = "content://com.android.calendar/reminders";
			ContentValues reminderValues = new ContentValues();
			reminderValues.put("event_id", eventID);
			switch(reminderT) {
			case 0:
			case 1:
				reminderValues.put("minutes", 1);
				break;
			case 2:
				reminderValues.put("minutes", 5);
				break;
			case 3:
				reminderValues.put("minutes", 10);
				break;
			case 4:
				reminderValues.put("minutes", 15);
				break;
			case 5:
				reminderValues.put("minutes", 30);
				break;
			case 6:
				reminderValues.put("minutes",60);
				break;
			case 7:
				reminderValues.put("minutes", 120);
				break;
			case 8:
				reminderValues.put("minutes", 180);
				break;
			case 9:
				reminderValues.put("minutes", 240);
				break;
			case 10:
				reminderValues.put("minutes", 480);
				break;
			case 11:
				reminderValues.put("minutes", 720);
				break;
			case 12:
				reminderValues.put("minutes", 1440);
				break;
				default:
					reminderValues.put("minutes", 5);
			}
			
			reminderValues.put("method", 1);

			// Setting reminder in calendar on Event.
			context.getContentResolver().insert(Uri.parse(reminderUriString),
					reminderValues);
		}

		@SuppressLint("SimpleDateFormat")
		private String computeTrainTicketDate(String inputDate) {
			Calendar cal = Calendar.getInstance();
			/*
			 * cal.set(Calendar.DATE, Integer.parseInt(strings[2]));
			 * cal.set(Calendar.MONTH, Integer.parseInt(strings[1]));
			 * cal.set(Calendar.YEAR, Integer.parseInt(strings[0]));
			 * cal.add(Calendar.DAY_OF_YEAR, -30);
			 */
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1;
			try {
				date1 = sdf.parse(inputDate);
				cal.setTime(date1);
				cal.add(Calendar.DAY_OF_MONTH, BOOKING_DATE_OFFSET);
				Date today60 = cal.getTime();
				// today60 = cal.getTime();
				String finalDate = new SimpleDateFormat("yyyy-MM-dd")
						.format(today60);
				Log.e("Date", finalDate);
				return finalDate;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "NULL";
			}

		}

		private boolean validateDate(String inputDates) {
			if (inputDates.contains("-")) {

				String currDate = new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date());
				try {

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date1 = sdf.parse(inputDates);
					Date date2 = sdf.parse(currDate);
					if (date1.after(date2) || date1.equals(date2)) {
						return true;
					} else {
						return false;
					}

				} catch (Exception e) {
					return false;

				}
			} else {
				return false;
			}
		}

		public void showPlan() {
			/*
			 * Hide everything and add all ticket booking details with a Back
			 * button
			 */
			Intent local = new Intent(context, MyListActivity.class);
			context.startActivity(local);

		}

		private void showUI() {
			int addVisibility = addDates.getVisibility();
			int viewVisibility = updatePlan.getVisibility();
			if (addVisibility == EditText.GONE) {
				addDates.setText("");
				addDates.setVisibility(EditText.VISIBLE);
			} else if (addVisibility == EditText.VISIBLE) {
				addDates.setVisibility(EditText.GONE);
			}

			if (viewVisibility == Button.GONE) {
				updatePlan.setVisibility(Button.VISIBLE);
			} else if (viewVisibility == Button.VISIBLE) {
				updatePlan.setVisibility(Button.GONE);
			}
		}
	}

	static SQLiteDatabase db;
	private TicketTrackerData data = new TicketTrackerData(this);
	private static final String create_travel_plan_query = "CREATE TABLE IF NOT EXISTS travel_plan(HOLIDAY_DATE VARCHAR PRIMARY KEY NOT NULL, TICKET_DATE VARCHAR);";

	private static final String create_train_tkts_query = "CREATE TABLE IF NOT EXISTS train_tkts(PNR VARCHAR PRIMARY KEY NOT NULL, TRAIN VARCHAR, DOJ DATE, DEP TIME, SRC VARCHAR, DST VARCHAR);";

	private static final String TAG = "DB";
	public static final String KEY_LIST_PREFERENCE = "listPref";

	Button extInfo, txtInfo;

	EditText dispText;
	String msgData;
	private String pnrText;
	private String trainText;
	private String dojText;
	private String[] stations;

	private String depTime;
	private Button dispInfo;
	LinearLayout base;

	private SharedPreferences sharedPref, mPrefs;
	//private ListPreference mListPreference;

	private Long stored_date;

	private int noOfSmsRead;
	private boolean autoUpdate, autoReminder;
	private int reminderT;

	private Button planTravel;
	private Button addTxt;
	private Button viewPlan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_tracker);
		initialize();
		if (autoUpdate) {
			boolean success = readSMS();
			extractTrainTickets(success);
		}
	}

	private void initialize() {
		initUIs();

		registerOnClickListeners();

		pnrText = new String("");
		trainText = new String("");
		dojText = new String("");
		msgData = new String("");
		stations = new String[2];
		reminderT = 2;

		sharedPref = getApplicationContext().getSharedPreferences(
				getString(R.string.last_updated_date), Context.MODE_PRIVATE);

		mPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		//mListPreference = (ListPreference) getApplicationContext().getSharedPreferences(KEY_LIST_PREFERENCE, Context.MODE_PRIVATE);
		
		sharedPref.registerOnSharedPreferenceChangeListener(this);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		//mListPreference.getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		onSharedPreferenceChanged(null, null);

		db = openOrCreateDatabase("Tickets", Context.MODE_PRIVATE, null);
		db.execSQL(create_train_tkts_query);
		db.execSQL(create_travel_plan_query);
	}

	private void registerOnClickListeners() {

		data.updatePlan.setOnClickListener(this);
		extInfo.setOnClickListener(this);
		viewPlan.setOnClickListener(this);
		txtInfo.setOnClickListener(this);
		addTxt.setOnClickListener(this);
		dispInfo.setOnClickListener(this);
		planTravel.setOnClickListener(this);
	}

	private void initUIs() {
		extInfo = (Button) findViewById(R.id.extInfo);
		txtInfo = (Button) findViewById(R.id.tktInfo);
		dispInfo = (Button) findViewById(R.id.dispInfo);
		planTravel = (Button) findViewById(R.id.planTravel);
		base = (LinearLayout) findViewById(R.id.trainDisp);
		dispText = (EditText) findViewById(R.id.display);
		addTxt = (Button) findViewById(R.id.addTkt);

		viewPlan = (Button) findViewById(R.id.viewPlan);
		data.updatePlan = (Button) findViewById(R.id.updatePlan);
		data.addDates = (EditText) findViewById(R.id.addDates);
	}

	@Override
	public void onClick(View view) {
		if (view == extInfo) {
			boolean success = readSMS();
			extractTrainTickets(success);
		} else if (view == dispInfo) {
			displayTrainTickets();
		} else if (view == planTravel) {
			showPlanYourTravel();
		} else if (view == txtInfo) {
			toggleSmsUI();
		} else if (view == addTxt) {
			addTrainTickets();
		} else if (view == viewPlan) {
			data.showPlan();
		} else if (view == data.updatePlan) {
			data.addToPlan(autoReminder, reminderT);
		}

	}

	private void addTrainTickets() {
		String ticketInfo = dispText.getText().toString();
		if (!ticketInfo.equals("")) {
			msgData = ticketInfo;
			extractTrainTickets(true);
		} else {
			Toast.makeText(getApplicationContext(), "No tickets updated",
					Toast.LENGTH_SHORT).show();
		}
		/* clear text before toggling the UI */
		dispText.setText("");
		toggleSmsUI();
	}

	private void toggleSmsUI() {

		/* Hide unwanted UI */

		data.addDates.setVisibility(EditText.GONE);
		data.updatePlan.setVisibility(Button.GONE);

		/* Toggle visibility for addTxt and display */
		int addVisibility = addTxt.getVisibility();
		int viewVisibility = dispText.getVisibility();
		if (addVisibility == Button.GONE) {
			addTxt.setVisibility(Button.VISIBLE);
		} else if (addVisibility == Button.VISIBLE) {
			addTxt.setVisibility(Button.GONE);
		}

		if (viewVisibility == Button.GONE) {
			dispText.setVisibility(Button.VISIBLE);
		} else if (viewVisibility == Button.VISIBLE) {
			dispText.setVisibility(Button.GONE);
		}

	}

	private void showPlanYourTravel() {

		Log.d(TAG, "Plan your travel");
		//db.execSQL(create_travel_plan_query);
		/*
		 * Intent local = new Intent(this, PlanYourTravel.class);
		 * startActivity(local);
		 */
		/* Toggle the visibility of addPlan and viewPlan */
		togglePytUI();

	}

	private void togglePytUI() {
		/* Hide unwanted SMS UI */
		addTxt.setVisibility(Button.GONE);
		dispText.setVisibility(Button.GONE);

		data.showUI();
	}

	private void displayTrainTickets() {
		Log.d(TAG, "Display Train Tickets.");
		Intent local = new Intent(this, MyTrainJourneys.class);
		startActivity(local);
	}

	private void extractTrainTickets(boolean success) {
		if (success) {
			int recordsUpdated = 0;
			if (!msgData.equals("")) {

				boolean filledPNR = false, filledTrain = false, filledDoj = false, filledDep = false, filledStations = false;

				for (String line : msgData.split(",")) {
					if (!line.equals("")) {
						if (line.contains(":")) {
							String[] tag_value = line.split(":");
							int length = tag_value.length;
							Log.d(TAG, line);

							if (length == 2) {
								if (tag_value[0].replaceAll("\\s+", "")
										.equalsIgnoreCase("PNR")) {
									pnrText = tag_value[1];
									filledPNR = true;

								} else if (tag_value[0].replaceAll("\\s+", "")
										.equalsIgnoreCase("TRAIN")) {
									trainText = tag_value[1];
									filledTrain = true;

								} else if (tag_value[0].replaceAll("\\s+", "")
										.equalsIgnoreCase("DOJ")) {
									// convert to YYYY-MM-DD format to help in
									// ordering
									dojText = convertToDateFormat(tag_value[1]);
									// dojText = tag_value[1];
									filledDoj = true;

								} else if (tag_value[0].replaceAll("\\s+", "")
										.equalsIgnoreCase("Dep")) {
									// handle N.A case in tickets
									depTime = tag_value[1];
									filledDep = true;
								}

							} else if (length >= 3) {
								if (tag_value[0].replaceAll("\\s+", "")
										.equalsIgnoreCase("Dep")) {
									depTime = tag_value[1] + ":" + tag_value[2];
									filledDep = true;
								}
							}
						} else if (line.contains("-")) {
							stations = line.split("-");
							filledStations = true;
						} else {
							/*
							 * if (line.length() == 2) { Log.d("nolen-1", line);
							 * coach = line; filledCoach = true; }
							 */
						}

					}

					if (filledPNR && filledTrain && filledDoj && filledDep
							&& filledStations) {
						filledPNR = filledTrain = filledDoj = filledDep = filledStations = false;

						// check if PNR is already there
						Cursor resultSet = db.rawQuery(
								"Select * from train_tkts WHERE PNR=" + pnrText
										+ ";", null);

						Log.d("DBupd", "" + resultSet.getCount() + "PNR"
								+ pnrText);

						if (resultSet.getCount() == 0) {
							/*
							 * if (filledCoach == true) { filledCoach = false;
							 * db.execSQL("INSERT INTO train_tkts VALUES('" +
							 * pnrText + "','" + trainText + "','" + dojText +
							 * "','" + depTime + "','" + stations[0] + "','" +
							 * stations[1] + "','" + coach + "');");
							 * 
							 * } else
							 */
							{
								db.execSQL("INSERT INTO train_tkts VALUES('"
										+ pnrText + "','" + trainText + "','"
										+ dojText + "','" + depTime + "','"
										+ stations[0] + "','" + stations[1]
										+ "');");
							}

							recordsUpdated++;
						}
						resultSet.close();
						pnrText = trainText = dojText = depTime = "";
					}

				}
			}
			if (recordsUpdated > 0) {
				SharedPreferences.Editor editor = sharedPref.edit();
				Date d = new Date();
				editor.putLong(getString(R.string.last_updated_date),
						d.getTime());

				editor.commit();
				Toast.makeText(getApplicationContext(),
						"Updated " + recordsUpdated + " tickets",
						Toast.LENGTH_SHORT).show();
			} else {

				Toast.makeText(getApplicationContext(),
						"No new tickets since " + millisToDate(stored_date),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private String convertToDateFormat(String string) {

		String[] contents = new String[3];
		String result = new String("");
		contents = string.split("-");
		result += "20" + contents[2] + "-" + contents[1] + "-" + contents[0];
		return result;
	}

	public void showMessage(String title, String message) {
		Builder builder = new Builder(this);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}

	public static String millisToDate(long currentTime) {
		String finalDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		Date date = calendar.getTime();
		finalDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		return finalDate;
	}

	public boolean readSMS() {
		String[] projection = new String[] { "_id", "address", "body", "date" };
		/*
		 * stored_date = sharedPref.getLong(
		 * getResources().getString(R.string.last_updated_date), 0);
		 */
		noOfSmsRead = 0;
		msgData = "";
		/* reads only SMS from AM-IRCTCi */
		Cursor cursor = getContentResolver().query(
				Uri.parse("content://sms/inbox"), projection,
				"address" + " like ?", new String[] { "%" + "IRCTC" + "%" },
				"date desc");
		try {
			cursor.moveToFirst();

			do {
				/*
				 * if current sms's date is less than last stored date, no need
				 * to process it
				 */
				Long sms_date = (Long.parseLong(cursor.getString(cursor
						.getColumnIndex("date"))));
				Log.d("sms_date", "" + millisToDate(sms_date));
				Log.d("sms_date_stored", "" + millisToDate(stored_date));
				if (sms_date < stored_date) {
					Log.d("sms_date_stored", "Not processing sms.");
					break;
				} else {

					int add_idx = cursor.getColumnIndex("address");
					{
						if (cursor.getString(add_idx).contains("IRCTC")) {
							int indx = cursor.getColumnIndex("body");
							if (indx >= 0) {
								msgData += cursor.getString(indx) + "," + "\n";
							}
						}
					}
				}
				noOfSmsRead++;
			} while (cursor.moveToNext());
			cursor.close();
			Log.d("sms_read", "" + noOfSmsRead);
			return true;
		} catch (Exception e) {
			Log.d(TAG, "No records found!", e);
			Toast.makeText(getApplicationContext(), "No tickets found!",
					Toast.LENGTH_SHORT).show();
			return false;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {

		autoUpdate = mPrefs.getBoolean("auto-scan", true);
		autoReminder = mPrefs.getBoolean("auto-reminder", true);
		stored_date = sharedPref.getLong(
				getResources().getString(R.string.last_updated_date), 0);
		 if (arg1 != null && arg1.equals(KEY_LIST_PREFERENCE)) {
			Log.d("Pref", "Im here");
			String listV = mPrefs.getString(KEY_LIST_PREFERENCE, "2");	
			
			Log.d("Pref", listV);
			try {
			reminderT = Integer.parseInt(listV);
			}
			catch(Exception e) {
				
			}
	        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
