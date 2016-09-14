package app.android.com.nudger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Sruthi on 9/6/2016.
 */
public class AlarmCreator {
    AlarmManager alarmManager;
    Context context;

    public AlarmCreator(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
    }

    public void startAlarm(long dInMS, ReminderEntry entry) {
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTimeInMillis(dInMS);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("alarmid", entry.getAlarmId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, entry.getAlarmId(), alarmIntent, 0);
        alarmIntent.setAction(new String("" + entry.getAlarmId()));
        alarmManager.setExact(AlarmManager.RTC,cal_now.getTimeInMillis(),pendingIntent);
        Log.d(Base.APP_TAG, "Alarm Set for" + cal_now.getTime().toString());
    }
    public int getUniqueAlarmId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int alarmId = preferences.getInt("ALARM", 1);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ALARM", alarmId + 1).apply();
        return alarmId;
    }
    public  void cancelAlarm(int alarmId) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent cancelIntent = PendingIntent.getBroadcast(context, alarmId, alarmIntent, 0 );
        cancelIntent.cancel();
       // Toast.makeText(context, "Cancelling alarmId: "+alarmId, Toast.LENGTH_SHORT).show();
    }
}
