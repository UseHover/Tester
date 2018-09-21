package com.hover.tester.schedules;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;
import com.hover.tester.R;


public class AddScheduleDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {
	public final static String TAG = "AddScheduleDialogFragment", STEP = "step", ID = "id";
	public static int ADD_SCHEDULE_STEP = 0, TIME_PICKER_STEP = 1;
	private String mId;
	private int mStep, mDay = -1, mSchedule;
	private SchedulerInterface mListener;

	public AddScheduleDialogFragment() { }

	public static AddScheduleDialogFragment newInstance(int step, String id) {
		AddScheduleDialogFragment frag = new AddScheduleDialogFragment();
		Bundle args = new Bundle();
		args.putInt(STEP, step);
		args.putString(ID, id);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onAttach(Context c) {
		super.onAttach(c);
		try { mListener = (SchedulerInterface) c;
		} catch (ClassCastException e) { Crashlytics.logException(e); }
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStep = getArguments().getInt(STEP);
		mId = getArguments().getString(ID);
		if (mStep == TIME_PICKER_STEP)
			return timeDialog();
		else // if (mStep == ADD_SCHEDULE_STEP)
			return chooseScheduleDialog();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mStep == ADD_SCHEDULE_STEP && getDialog() != null && getDialog().findViewById(R.id.schedule_choice) != null)
			scheduleView();
	}

	private AlertDialog chooseScheduleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.add_schedule_q)
				.setView(R.layout.schedule)
				.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (mListener != null) {
							mListener.setType(mSchedule);
							if (mSchedule == Scheduler.DAILY || (mSchedule == Scheduler.WEEKLY && mDay != -1))
								mListener.chooseTime(mDay);
						}
					}
				})
				.setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface d) {
				((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		});
		return dialog;
	}

	private TimePickerDialog timeDialog() {
		return new TimePickerDialog(getActivity(), this, 12, 0, DateFormat.is24HourFormat(getActivity()));
	}

	private void scheduleView() {
		createSpinner(R.id.schedule_choice, R.array.schedule_choices).setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		mSchedule = pos;
		if (pos == Scheduler.TEN_MIN || pos == Scheduler.HOURLY || pos == Scheduler.DAILY || pos == Scheduler.WEEKLY)
			((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

		if (pos == Scheduler.HOURLY || pos == Scheduler.TEN_MIN)
			((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.done);
		else if (pos == Scheduler.DAILY)
			((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.next);

		if (pos == Scheduler.WEEKLY)
			showDayChoices();
		else
			hideDayChoices();
	}
	@Override public void onNothingSelected(AdapterView<?> parent) { }

	private void showDayChoices() {
		getDialog().findViewById(R.id.day_choice).setVisibility(View.VISIBLE);
		createSpinner(R.id.day_choice, R.array.day_choices).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				mDay = i;
				((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.next);
			}
			@Override public void onNothingSelected(AdapterView<?> adapterView) { }
		});
	}

	private void hideDayChoices() {
		getDialog().findViewById(R.id.day_choice).setVisibility(View.GONE);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (mListener != null) mListener.setTime(hourOfDay, minute);
	}

	private Spinner createSpinner(int viewId, int arrayId) {
		Spinner spinner = (Spinner) getDialog().findViewById(viewId);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), arrayId, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		return spinner;
	}
}
