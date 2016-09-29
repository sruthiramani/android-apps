package app.android.com.nudger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Sruthi on 8/28/2016.
 */
public class CustomListAdapter extends ArrayAdapter<ReminderEntry> {
    public static final int LOW = 0, MEDIUM = 1, HIGH = 2, NONE = 3;
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
        TextView remStatusLabel;
        TextView remAssignerLabel;

        public  static final int SELF = 0, USER_NAME = 1;
      }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final ReminderEntry rowItem = getItem(position);


        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.reminder_entry, null);
            convertView.setBackgroundColor(getAlternateColor(position));
            holder = new ViewHolder();
            holder.remTitle = (TextView) convertView.findViewById(R.id.titleView);
            holder.remStatusLabel = (TextView) convertView.findViewById(R.id.StatusLabel);
            holder.remStatus = (CheckBox) convertView.findViewById(R.id.statusCheckBox);
            holder.remPriority = (TextView) convertView.findViewById(R.id.priorityView);
            holder.remDescription = (TextView) convertView.findViewById(R.id.descriptionView);
            holder.remAssignerLabel = (TextView) convertView.findViewById(R.id.AssignLabel);
            holder.remAssigner = (TextView) convertView.findViewById(R.id.assignView);
            holder.remDueDate = (TextView)convertView.findViewById(R.id.dateView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.remTitle.setText(rowItem.getTitle());
        holder.remDescription.setText(rowItem.getDescription());

        if(type == Base.UPCOMING_REMINDERS) {
            if (rowItem.getReminderStatus() == true) {
                holder.remStatus.setChecked(true);
            } else {
                holder.remStatus.setChecked(false);
            }
        } else {
            if(type == Base.COMPLETED_REMINDERS) {
                holder.remAssignerLabel.setVisibility(View.INVISIBLE);
                holder.remAssigner.setVisibility(View.INVISIBLE);
                holder.remStatusLabel.setVisibility(View.INVISIBLE);
                holder.remStatus.setVisibility(View.INVISIBLE);
            } else {
                holder.remStatusLabel.setVisibility(View.INVISIBLE);
                holder.remStatus.setVisibility(View.INVISIBLE);
                holder.remAssignerLabel.setText("Assigned To:");
            }

        }
       // holder.remStatus.setClickable(false);
        final ViewHolder finalHolder = holder;
        final View finalConvertView = convertView;
        holder.remStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context,"Implementation pending...", Toast.LENGTH_SHORT).show();
                /* move this item to completed list */
                Base.moveReminderToCompleted(rowItem);
            }
        });

        switch (rowItem.getReminderPriority()) {
            case LOW:
                holder.remPriority.setText("Low");
                break;
            case MEDIUM:
                holder.remPriority.setText("Medium");
                break;
            case HIGH:
                holder.remPriority.setText("High");
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
        return new String(cal.get(Calendar.YEAR) +"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+ cal.get(Calendar.SECOND));
    }

    public int getAlternateColor(int position) {
        if(position%2 == 0) {
            return Color.rgb(100,149,237);
        } else {
            return Color.rgb(135,206,250);
        }
    }
}
