package app.android.com.nudger;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager mManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Base.APP_TAG, "Alarm caught");
        int alarmId = intent.getIntExtra("alarmid",0);
        Log.d(Base.APP_TAG, "Alarm ID:"+alarmId);
        if(alarmId == 0) {
            Toast.makeText(context, "Nudger: Cannot find matching alarmId",Toast.LENGTH_LONG).show();
            return;
        }

        Intent resultIntent = new Intent(context, Base.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        alarmId,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        ReminderDBStore mRemDB = null;
        if(Base.mRemDB  == null) {
           mRemDB = new ReminderDBStore(context);
        } else {
            mRemDB = Base.mRemDB;
        }
        ReminderEntry entry = mRemDB.lookupTitleAndDescWithAlarmId(alarmId);
        String title = entry.getTitle();
        String desc = entry.getDescription();
        Log.d(Base.APP_TAG,"Title+Desc"+title+""+desc);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(desc);
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = alarmId;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(alarmId, mBuilder.build());



    }

}