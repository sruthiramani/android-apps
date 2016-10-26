package app.android.com.nudger;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sruthi on 8/28/2016.
 */
public class ReminderEntry {
    public static final boolean DONE = true, NOT_DONE = false;
    long id;
    private String title;
    private String description;
    private boolean reminderStatus;
    private int reminderPriority;
    private String reminderAssigner;
    private long reminderDueDateInMilliSec;
    private int alarmId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

    ReminderEntry(long id,
                  String title,
                  String description,
                  boolean reminderStatus,
                  int reminderPriority,
                  String reminderAssigner,
                  long reminderDueDateInMilliSec,
                  int alarmId,
                  int type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.reminderStatus = reminderStatus;
        this.reminderPriority = reminderPriority;
        this.reminderAssigner = reminderAssigner;
        this.reminderDueDateInMilliSec = reminderDueDateInMilliSec;
        this.alarmId = alarmId;
        this.type = type;
    }

    public String getTitle() {
        return  title;
    }
    public boolean getReminderStatus() {
        return  reminderStatus;
    }

    public int getReminderPriority() {
        return reminderPriority;
    }
    public String getDescription() {
        return description;
    }
    public  String getReminderAssigner() {
        return reminderAssigner;
    }


    public  long getReminderDueDateInMilliSec() { return  reminderDueDateInMilliSec;}

    public long getId() {
        return id;
    }

    public int getAlarmId() {
        return alarmId;
    }
}
