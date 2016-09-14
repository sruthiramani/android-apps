package abc.def.tickettracker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
		preHoneyComb();
		} else {
			fragmentStyle(savedInstanceState);
		}
	}

	private void fragmentStyle(Bundle savedInstanceState) {
	FragmentTransaction transaction = getFragmentManager().beginTransaction();
	Fragment fragment = new MyPrefFragments();
	transaction.replace(android.R.id.content, fragment);
	transaction.commit();
		
	}

	@SuppressWarnings("deprecation")
	private void preHoneyComb() {
		addPreferencesFromResource(R.xml.ticket_tracker_prefs);
	}
	
	public static class MyPrefFragments extends PreferenceFragment {
		@Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			this.addPreferencesFromResource(R.xml.ticket_tracker_prefs);
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		
	}
}
