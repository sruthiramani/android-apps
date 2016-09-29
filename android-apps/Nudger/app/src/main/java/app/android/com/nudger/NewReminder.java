package app.android.com.nudger;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewReminder extends AppCompatActivity  {

    public static final String SELECT_A_USER = "Select a  user...";
    private int priorityValue;
    private String reminderAssigner;
    private static String dateString, timeString, failureString;
    private static TextView dateDisplay, timeDisplay;
    private static EditText title, description;
    public static String userName;
    private int currentPosition;
    private Spinner assigner;
    private List<String> categories;
    ArrayAdapter<String> dataAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        Button add, cancel, dateChooser;
        currentPosition = -1;
        title = (EditText)findViewById(R.id.newTitle);
        description = (EditText)findViewById(R.id.newDescription);
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        setupPrioritySpinner();
        setupAssignerSpinner();

        dateString = "";
        timeString = "";

        add = (Button) findViewById(R.id.addButton);
        cancel = (Button)findViewById(R.id.cancelButton);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(false == validateInputData()) {
                    Toast.makeText(getBaseContext(), failureString, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent resultIntent = fillResultIntent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelIntent = new Intent();
                setResult(RESULT_CANCELED, cancelIntent);
                finish();
            }
        });

        final Button datePickerButton = (Button) findViewById(R.id.datePicker);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        final Button timerPickerButton = (Button) findViewById(R.id.timePicker);
        timerPickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


    }

    @NonNull
    private Intent fillResultIntent() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("Title",title.getText().toString() );
        resultIntent.putExtra("Desc", description.getText().toString());
        resultIntent.putExtra("Priority", priorityValue);
        resultIntent.putExtra("Assigner", reminderAssigner);
        String dueDate = new String(dateString+" "+timeString);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dueDate);
            long dateInMS = date.getTime();
           // resultIntent.putExtra("date", dateString+'T'+timeString);
            resultIntent.putExtra("date", dateInMS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return resultIntent;
    }

    private boolean validateInputData() {
        if(title.getText().toString().isEmpty()) {
            failureString = " Please enter a title for the reminder";
            return  false;
        }
        if(description.getText().toString().isEmpty()) {
            failureString = " Please enter a description for the reminder";
            return  false;
        }
        if(dateString.isEmpty()) {
            failureString = " Please choose a due date for the reminder";
            return  false;
        }
        if(timeString.isEmpty()) {
            failureString = " Please choose a time for the reminder";
            return  false;
        }
        return true;
    }


    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    private void setupAssignerSpinner() {
       assigner= (Spinner) findViewById(R.id.assignerName);
        categories= new ArrayList<String>();
        // Creating adapter for spinner
        dataAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Spinner Drop down elements

        categories.add("Self");
        categories.add(SELECT_A_USER);
        assigner.setAdapter(dataAdapter);

        // attaching data adapter to spinner
        assigner.setAdapter(dataAdapter);
        assigner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              if(i == 0)
                  reminderAssigner = "Self";
              else {
                 String user = (String) assigner.getItemAtPosition(i);
                  if(user.equals(SELECT_A_USER)) {
                      FragmentManager manager = getFragmentManager();
                      SelectUserDialog selectUserDialog = new SelectUserDialog();
                      selectUserDialog.show(manager, "Assign to a user");

                  } else {
                      reminderAssigner = user;
                  }
              }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





    }

    private void setupPrioritySpinner() {
        Spinner priority = (Spinner) findViewById(R.id.choosePriority);
        priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                priorityValue = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Spinner Drop down elements
        List<String> priorityList = new ArrayList<String>();

        priorityList.add("Low");
        priorityList.add("Medium");
        priorityList.add("High");
        priorityList.add("None");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priorityList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        priority.setAdapter(dataAdapter);
    }

    public void onDialogResponse(boolean userNameAvailable, String userName) {
        Log.d("Username avail?", ""+SelectUserDialog.getUserNameAvailable());
        if(SelectUserDialog.getUserNameAvailable()) {
            reminderAssigner = SelectUserDialog.getUserName();
            Log.d("User", reminderAssigner);
            categories.add(1,reminderAssigner);
            dataAdapter.notifyDataSetChanged();
            assigner.setAdapter(dataAdapter);
            assigner.setSelection(1);

        } else {
            assigner.setSelection(0);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);



            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            setDateString(year, monthOfYear, dayOfMonth);

            dateDisplay.setText(dateString);
        }
        private static void setDateString(int year, int monthOfYear, int dayOfMonth) {

            // Increment monthOfYear for Calendar/Date -> Time Format setting
            monthOfYear++;
            String mon = "" + monthOfYear;
            String day = "" + dayOfMonth;

            if (monthOfYear < 10)
                mon = "0" + monthOfYear;
            if (dayOfMonth < 10)
                day = "0" + dayOfMonth;

            dateString = year + "-" + mon + "-" + day;
        }




    }
    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTimeString(hourOfDay, minute, 0);
            timeDisplay.setText(timeString);
        }
        private static void setTimeString(int hourOfDay, int minute, int mili) {
            String hour = "" + hourOfDay;
            String min = "" + minute;

            if (hourOfDay < 10)
                hour = "0" + hourOfDay;
            if (minute < 10)
                min = "0" + minute;

            timeString = hour + ":" + min+":"+"00";
        }

    }



}

