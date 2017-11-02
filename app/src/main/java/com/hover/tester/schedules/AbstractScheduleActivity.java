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
		DialogFragment newFragment = AddScheduleDialogFragment.newInstance(AddScheduleDialogFragment.ADD_SCHEDULE_STEP, actionId);
		newFragment.show(getSupportFragmentManager(), AddScheduleDialogFragment.TAG);
	}

	@Override
	public void setType(int type) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.setType(type);
		if (type == 0)
			saveSchedule();
	}

	@Override
	public void chooseTime(int day) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		if (day != -1)
			mScheduler.setDay(day);
		DialogFragment newFragment = AddScheduleDialogFragment.newInstance(AddScheduleDialogFragment.TIME_PICKER_STEP, mScheduler.getId());
		newFragment.show(getSupportFragmentManager(), AddScheduleDialogFragment.TAG);
	}

	@Override
	public void setTime(int hour, int min) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.setTime(hour, min);
		saveSchedule();
	}

	@Override
	public void saveSchedule() {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.save(this);
	}
}
