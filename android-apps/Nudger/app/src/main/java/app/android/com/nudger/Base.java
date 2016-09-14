package app.android.com.nudger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Base extends AppCompatActivity {

    public static final int UPCOMING_REMINDERS = 1;
    public static final int COMPLETED_REMINDERS = 2;
    public static final int PENDING_REMINDERS = 3;
    public static final int CREATE_NEW_REMINDER = 1;
    public static final String APP_TAG = "Nudger";
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    static ReminderDBStore mRemDB;
    static AlarmManager manager;
    static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setOffscreenPageLimit(3);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newReminderIntent = new Intent(Base.this, NewReminder.class);
                startActivityForResult(newReminderIntent, CREATE_NEW_REMINDER);

            }
        });

        mRemDB = new ReminderDBStore(this);
        context = Base.this;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CREATE_NEW_REMINDER:
                switch (resultCode) {
                    case RESULT_OK:
                        /* Create new reminder entry from entered data */
                        PlaceholderFragment.createNewReminder(mViewPager, data);
                        break;
                    case RESULT_CANCELED:
                    default:
                        break;
                }
                break;
            default:
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void moveReminderToCompleted(ReminderEntry rowItem) {
        //TODO update record in DB
        PlaceholderFragment.upcomingListAdapter.remove(rowItem);
        PlaceholderFragment.upcomingListAdapter.notifyDataSetChanged();
        PlaceholderFragment.completedListAdapter.add(rowItem);
        PlaceholderFragment.completedListAdapter.notifyDataSetChanged();
        int status = mRemDB.updateReminderEntryAsDone(rowItem);
        AlarmCreator alarmCreator = new AlarmCreator(context);
        alarmCreator.cancelAlarm(rowItem.getAlarmId());
        Log.d("Update", ""+rowItem.getId() );

    }

/*
    private static int getAlarmId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int alarmId = preferences.getInt("ALARM", 1);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ALARM", alarmId + 1).apply();
        return alarmId;
    }
*/
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static ListView mUpcomingList, mPendingList, mCompletedList;
        public static ArrayList<ReminderEntry> upcoming_reminders, pending_reminders, completed_reminders;
        public static CustomListAdapter upcomingListAdapter, pendingListAdapter, completedListAdapter;
        private boolean initUpcoming, initPending, initCompleted;


        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_base, container, false);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case UPCOMING_REMINDERS:
                    /*  create a listview, inflate from an xml, add array adaptors */
                    populateUpcomingReminders(rootView);
                    break;
                case COMPLETED_REMINDERS:
                    populateCompletedReminders(rootView);
                    break;
                case PENDING_REMINDERS:
                    populatePendingReminders(rootView);
                    break;
                default:

            }
            return rootView;
        }

        private void populateCompletedReminders(View rootView) {
            if (initCompleted == false) {

                mCompletedList = (mCompletedList == null) ? (ListView) rootView.findViewById(R.id.list) : mCompletedList;
                completed_reminders = (completed_reminders == null) ? new ArrayList<ReminderEntry>() : completed_reminders;
                completedListAdapter = (completedListAdapter == null) ? new CustomListAdapter(getContext(), R.layout.reminder_entry, completed_reminders, PENDING_REMINDERS) : completedListAdapter;
                ArrayList<ReminderEntry> entries = Base.mRemDB.getReminderEntries(Base.COMPLETED_REMINDERS);
                for (ReminderEntry entry : entries) {
                    Log.d(APP_TAG, "Added to Completed Reminders");
                    completedListAdapter.add(entry);
                }
                mCompletedList.setAdapter(completedListAdapter);
                initCompleted = true;
                Log.d(APP_TAG, "Init Completed reminders");
            }
        }

        private void populatePendingReminders(View rootView) {
            if (initPending == false) {
                mPendingList = (mPendingList == null) ? (ListView) rootView.findViewById(R.id.list) : mPendingList;
                pending_reminders = (pending_reminders == null) ? new ArrayList<ReminderEntry>() : pending_reminders;
                pendingListAdapter = (pendingListAdapter == null) ? new CustomListAdapter(getContext(), R.layout.reminder_entry, pending_reminders, PENDING_REMINDERS) : pendingListAdapter;
                ArrayList<ReminderEntry> entries = Base.mRemDB.getReminderEntries(Base.PENDING_REMINDERS);
                for (ReminderEntry entry : entries) {
                    Log.d(APP_TAG, "Reminder Entry Added to pending");
                    pendingListAdapter.add(entry);
                }
                mPendingList.setAdapter(pendingListAdapter);
                initPending = true;
                Log.d(APP_TAG, "Init Pending reminders");
            }


        }


        private void populateUpcomingReminders(View rootView) {

            /* set up */
            if (initUpcoming == false) {
                mUpcomingList = (ListView) rootView.findViewById(R.id.list);
                upcoming_reminders = new ArrayList<ReminderEntry>();
                upcomingListAdapter = new CustomListAdapter(getContext(), R.layout.reminder_entry, upcoming_reminders, UPCOMING_REMINDERS);
                ArrayList<ReminderEntry> entries = Base.mRemDB.getReminderEntries(Base.UPCOMING_REMINDERS);
                for (ReminderEntry entry : entries) {
                    Log.d(APP_TAG, "Upcoming Reminder Entry Added");
                    upcomingListAdapter.add(entry);
                }
                mUpcomingList.setAdapter(upcomingListAdapter);
                initUpcoming = true;
                Log.d(APP_TAG, "Init Upcoming reminders");
            }

            /* read from database and display */


        }

        public static void createNewReminder(ViewPager viewPager, Intent data) {
            AlarmCreator alarmCreator = new AlarmCreator(context);

            String title = data.getStringExtra("Title");
            String desc = data.getStringExtra("Desc");
            int priority = data.getIntExtra("Priority", 0);
            String assigner = data.getStringExtra("Assigner");
            long dateInMilliSec = data.getLongExtra("date", 0);

            Log.d(APP_TAG, "Millisec  " + dateInMilliSec);
            Log.d(APP_TAG, "ReminderEntry" + title + " " + desc + " " + priority + " " + assigner + " ");

            int alarmId = alarmCreator.getUniqueAlarmId();
            long id = Base.mRemDB.insertReminderEntry(title, desc, priority, ReminderEntry.NOT_DONE, assigner, dateInMilliSec, alarmId);
            Log.d(APP_TAG, "ReminderEntry ID:" + id);

            ReminderEntry reminder = new ReminderEntry(id, title, desc, ReminderEntry.NOT_DONE, priority, assigner, dateInMilliSec, alarmId);

            if (assigner.equalsIgnoreCase("Self")) {
                viewPager.setCurrentItem(0);
                upcoming_reminders.add(reminder);
                upcomingListAdapter.notifyDataSetChanged();
                alarmCreator.startAlarm(dateInMilliSec, reminder);
            } else {
                viewPager.setCurrentItem(2);
                pending_reminders.add(reminder);
                pendingListAdapter.notifyDataSetChanged();
            }
        }

     /*
        public static void startAlarm(long dInMS, ReminderEntry entry) {
            Calendar cal_now = Calendar.getInstance();
            cal_now.setTimeInMillis(dInMS);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("alarmid", entry.getAlarmId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, entry.getAlarmId(), alarmIntent, 0);
            alarmIntent.setAction(new String("" + entry.getAlarmId()));
            manager.setExact(AlarmManager.RTC,cal_now.getTimeInMillis(),pendingIntent);
            Log.d(APP_TAG, "Alarm Set for" + cal_now.getTime().toString());
        }
        */
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Upcoming Reminders";
                case 1:
                    return "Completed";
                case 2:
                    return "Pending ";
            }
            return null;
        }
    }
}
