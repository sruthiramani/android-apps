package app.android.com.nudger;

/**
 * Created by Sruthi on 8/31/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReminderDBStore extends  SQLiteOpenHelper{

    public static final String DATABASE_NAME = "Nudger.db";
    public static final String TABLE_NAME = "Reminders";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DUE_DATE_IN_MS = "duedate";
    public static final String COLUMN_ASSIGNER = "assigner";
    public static final String COLUMN_ALARM_ID = "alarmid";

    public ReminderDBStore(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " +TABLE_NAME+
                        "("+COLUMN_ID+" integer primary key,"+COLUMN_TITLE+ " text," + COLUMN_DESCRIPTION+" text,"+COLUMN_PRIORITY+" integer,"+COLUMN_STATUS+" integer,"+COLUMN_ASSIGNER+" text,"+COLUMN_DUE_DATE_IN_MS+" text,"+COLUMN_ALARM_ID+" text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertReminderEntry(String title, String desc, int priority, boolean status, String assigner, long dueDateInMS, int alarmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_DESCRIPTION, desc);
        contentValues.put(COLUMN_PRIORITY, priority);
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_ASSIGNER, assigner);
        contentValues.put(COLUMN_DUE_DATE_IN_MS,dueDateInMS);
        contentValues.put(COLUMN_ALARM_ID,alarmId);
        long id = db.insert(TABLE_NAME, null, contentValues);
        return id;
    }

    public ArrayList<ReminderEntry> getReminderEntries(int type) {
        ArrayList<ReminderEntry> arrayList = new ArrayList<ReminderEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        if(type == Base.UPCOMING_REMINDERS) {
            res = db.rawQuery("select * from " + TABLE_NAME + " where status = 0 and "+COLUMN_ASSIGNER+" == 'Self'", null);
        } else if(type == Base.COMPLETED_REMINDERS) {
            res = db.rawQuery("select * from " + TABLE_NAME + " where status = 1", null);
        } else {
           res = db.rawQuery("select * from " + TABLE_NAME + " where "+COLUMN_ASSIGNER+" != 'Self'", null);
        }
        res.moveToFirst();
        while (res.isAfterLast()==false) {
            ReminderEntry entry = getReminderEntryFromCursor(res);
            arrayList.add(entry);
            res.moveToNext();
        }
        return arrayList;
    }

    @NonNull
    private ReminderEntry getReminderEntryFromCursor(Cursor res) {
        long id = Long.parseLong(res.getString(res.getColumnIndex(COLUMN_ID)));
        Log.d(Base.APP_TAG,"Reminder ID"+id);
        String title = res.getString(res.getColumnIndex(COLUMN_TITLE));
        String desc = res.getString(res.getColumnIndex(COLUMN_DESCRIPTION));
        int priority = Integer.parseInt(res.getString(res.getColumnIndex(COLUMN_PRIORITY)));
        int st = Integer.parseInt(res.getString(res.getColumnIndex(COLUMN_STATUS)));
        int alarmId = Integer.parseInt(res.getString(res.getColumnIndex(COLUMN_ALARM_ID)));
        Log.d(Base.APP_TAG,"ID:DBtitle"+ id+":"+title);
        boolean status = false;
        if(st == 1) status = true;
        String assigner = res.getString(res.getColumnIndex(COLUMN_ASSIGNER));
        long dateInMS = Long.parseLong(res.getString(res.getColumnIndex(COLUMN_DUE_DATE_IN_MS)));
        return new ReminderEntry(id,title,desc,status,priority,assigner,dateInMS,alarmId);
    }

    public int updateReminderEntryAsDone(ReminderEntry rowItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, 1);
        Log.d(Base.APP_TAG,"Row ID:"+ rowItem.getId());
        // updating row
        return db.update(TABLE_NAME, values, COLUMN_ID + " ="+rowItem.getId(),null);

    }

    public ReminderEntry lookupTitleAndDescWithAlarmId(int alarmId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where "+COLUMN_ALARM_ID+"="+alarmId, null);
        Log.d(Base.APP_TAG,"AlarmID" +alarmId);
        res.moveToFirst();
        return getReminderEntryFromCursor(res);

    }
}
