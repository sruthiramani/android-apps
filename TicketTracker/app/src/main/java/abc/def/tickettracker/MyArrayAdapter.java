package abc.def.tickettracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class MyArrayAdapter extends ArrayAdapter<String> {
	private static final int TRAIN_JOURNEYS = 1;
	private static final int TRAVEL_PLAN = 2;
	private final Context context;
	private final String[] values;
	int schemeno;

	public MyArrayAdapter(Context context, int schemeno, String[] values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
		this.schemeno = schemeno;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(values[position]);
		colorScheme(position, textView, schemeno);
		return rowView;
	}

	public void colorScheme(int position, TextView textView, int schemeno) {
		if (schemeno == TRAIN_JOURNEYS) {

			if (position == 0) {
				textView.setTextColor(0xFFFFFFFF);
				textView.setBackgroundColor(0xFF5F04B4);
				textView.setTextSize(25);

			} else if (position % 2 == 0) {
				textView.setBackgroundColor(0xFFFFFFFF);
			} else {
				textView.setBackgroundColor(0xFFBE81F7);
			}

		} else if (schemeno == TRAVEL_PLAN) {

			if (position == 0) {
				textView.setTextColor(0xFFFFFFFF);
				textView.setBackgroundColor(0xFFF781F3);

			} else {
				textView.setBackgroundColor(0xFFF6CEF5);
			}

		}
	}

}
