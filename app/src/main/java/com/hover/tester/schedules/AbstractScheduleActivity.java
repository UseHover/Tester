package com.hover.tester.schedules;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class AbstractScheduleActivity extends AppCompatActivity implements SchedulerInterface {
	public final static String TAG = "AScheduleActivity";
	private Scheduler mScheduler;

	@Override
	public void addSchedule(int actionId) {
		mScheduler = Scheduler.getInstance();
		mScheduler.setId(actionId);
		Log.e(TAG, "Schedule Action Id set to " + actionId);
		DialogFragment newFragment = AddScheduleDialogFragment.newInstance(AddScheduleDialogFragment.ADD_SCHEDULE_STEP, actionId);
		newFragment.show(getSupportFragmentManager(), AddScheduleDialogFragment.TAG);
	}

	@Override
	public void setType(int type) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.setType(type);
		Log.e(TAG, "set type, Schedule Action Id is " + mScheduler.getId());
		if (type == 0)
			saveSchedule();
	}

	@Override
	public void chooseTime(int day) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		if (day != -1)
			mScheduler.setDay(day);
		Log.e(TAG, "set day, Schedule Action Id is " + mScheduler.getId());
		DialogFragment newFragment = AddScheduleDialogFragment.newInstance(AddScheduleDialogFragment.TIME_PICKER_STEP, mScheduler.getId());
		newFragment.show(getSupportFragmentManager(), AddScheduleDialogFragment.TAG);
	}

	@Override
	public void setTime(int hour, int min) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.setTime(hour, min);
		Log.e(TAG, "set time, Schedule Action Id is " + mScheduler.getId());
		saveSchedule();
	}

	@Override
	public void saveSchedule() {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.save(this);
		Log.e(TAG, "saved. Schedule Action Id is " + mScheduler.getId());

	}
}
