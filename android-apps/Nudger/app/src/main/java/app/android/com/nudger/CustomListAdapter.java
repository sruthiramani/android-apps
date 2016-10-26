package app.android.com.nudger;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Sruthi on 8/28/2016.
 */
public class CustomListAdapter extends ArrayAdapter<ReminderEntry> {
    public static final int LOW = 0, MEDIUM = 1, HIGH = 2, NONE = 3;
    public static final int LOW_COLOR = 0xFFB7950B;
    private static final int MEDIUM_COLOR = 0xFFF99C05;
    private static final int HIGH_COLOR = 0xFFF90505;
    Context context;
    int type;
    public CustomListAdapter(Context context, int resourceId, List<ReminderEntry> objects, int type) {
        super(context, resourceId, objects);
       this.context = context;
        this.type = type;
    }
    public class ViewHolder {
        TextView remTitle;
        CheckBox remStatus;
        TextView remPriority;
        TextView remDescription;
        TextView remAssigner;
        TextView remDueDate;
        public  static final int SELF = 0, USER_NAME = 1;
      }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final ReminderEntry rowItem = getItem(position);


        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.reminder_entry, null);
            //convertView.setBackgroundColor(Color.rgb(215, 189, 226  ));
            holder = new ViewHolder();
            holder.remTitle = (TextView) convertView.findViewById(R.id.titleView);
            holder.remStatus = (CheckBox) convertView.findViewById(R.id.statusCheckBox);
            holder.remPriority = (TextView) convertView.findViewById(R.id.priorityView);
            holder.remDescription = (TextView) convertView.findViewById(R.id.descriptionView);
            holder.remAssigner = (TextView) convertView.findViewById(R.id.assignView);
            holder.remDueDate = (TextView)convertView.findViewById(R.id.dateView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.remTitle.setText(rowItem.getTitle());
        holder.remTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Base.deleteReminderEntry(rowItem);
                return true;
            }
        });
        holder.remDescription.setText(rowItem.getDescription());

        if(type == Base.UPCOMING_REMINDERS) {
            if (rowItem.getReminderStatus() == true) {
                holder.remStatus.setChecked(true);
            } else {
                holder.remStatus.setChecked(false);
            }

        } else {
            if(type == Base.COMPLETED_REMINDERS) {
                holder.remAssigner.setVisibility(View.INVISIBLE);
                holder.remStatus.setVisibility(View.INVISIBLE);
            } else {
                holder.remStatus.setVisibility(View.INVISIBLE);
            }

        }

        final ViewHolder finalHolder = holder;
        final View finalConvertView = convertView;
        holder.remStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* move this item to completed list */
                Base.moveReminderToCompleted(rowItem);

            }
        });

        switch (rowItem.getReminderPriority()) {
            case LOW:
                holder.remPriority.setText("Low ");
                holder.remPriority.setTextColor(LOW_COLOR);
                break;
            case MEDIUM:
                holder.remPriority.setText("Medium ");
                holder.remPriority.setTextColor(MEDIUM_COLOR);
                break;
            case HIGH:
                holder.remPriority.setText("High ");
                holder.remPriority.setTextColor(HIGH_COLOR);
                break;
            default:
                holder.remPriority.setText("None ");

        }

       holder.remAssigner.setText(rowItem.getReminderAssigner());
        holder.remDueDate.setText(new String(getDateFromMilliSec(rowItem.getReminderDueDateInMilliSec())));

        return convertView;
    }

    private String getDateFromMilliSec(long reminderDueDateInMilliSec) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(reminderDueDateInMilliSec);
        String readableDate = convertToReadableDate(cal);
        return readableDate;
    }

    private String convertToReadableDate(Calendar cal) {
        String months[] = {"","Jan", "Feb", "Mar", "April", "May", "Jun", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String textDate = months[cal.get(Calendar.MONTH)+1] +" "+cal.get(Calendar.DAY_OF_MONTH)+", "+cal.get(Calendar.YEAR);
        textDate += " " + cal.get(Calendar.HOUR_OF_DAY) +":"+cal.get(Calendar.MINUTE);
        if(cal.get(Calendar.HOUR_OF_DAY) >= 12) {
            textDate += " p.m";
        } else {
            textDate += " a.m";
        }
        return textDate;
    }


}
