package app.android.com.nudger;

/**
 * Created by Sruthi on 8/31/2016.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectUserDialog extends DialogFragment {
    private static String userName;
    private static boolean userNameAvailable;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        userNameAvailable = false;
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        return new AlertDialog.Builder(getActivity())
                // set dialog icon
                .setIcon(android.R.drawable.stat_notify_error)
                // set Dialog Title
                .setTitle("Choose a user...")
                // Set Dialog Message
                .setMessage("Select a friend to assign the reminder to...")
                .setView(input)

                // positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userName = input.getText().toString();
                        userNameAvailable = true;
                        NewReminder callingActivity = (NewReminder) getActivity();
                        callingActivity.onDialogResponse(userNameAvailable, userName);
                    }
                })
                // negative button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userNameAvailable = false;
                        NewReminder callingActivity = (NewReminder) getActivity();
                        callingActivity.onDialogResponse(userNameAvailable, userName);

                    }
                }).create();
    }

    public static String getUserName() {
        return userName;
    }

    public static boolean getUserNameAvailable() {
        return userNameAvailable;
    }
}