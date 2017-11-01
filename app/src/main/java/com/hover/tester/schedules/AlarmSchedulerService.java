package com.hover.tester.schedules;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hover.tester.WakeUpHelper;
import com.hover.tester.WakeUpReceiver;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;
import com.hover.tester.database.DbHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmSchedulerService extends IntentService {
	public final static String TAG = "AlarmSchedulerService", INTERVAL = "interval";
	private ArrayList<Scheduler> mHourly, mDaily, mWeekly;
	private Scheduler mActionSchedule;

	public AlarmSchedulerService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Performing scheduling");

		if (intent.hasExtra(OperatorAction.ID)) {
			mActionSchedule = Scheduler.load(intent.getIntExtra(OperatorAction.ID, -1), this);
			if (mActionSchedule != null) {
				switch (mActionSchedule.getType()) {
					case 2:
						setAlarm(mActionSchedule.getId(), WakeUpHelper.getScheduleTime(mActionSchedule), AlarmManager.INTERVAL_DAY * 7);
						break;
					case 1:
						setAlarm(mActionSchedule.getId(), WakeUpHelper.getScheduleTime(mActionSchedule), AlarmManager.INTERVAL_DAY);
						break;
					default:
						setAlarm(mActionSchedule.getId(), WakeUpHelper.getPlusHour(), AlarmManager.INTERVAL_HOUR);
				}
			}
		} else {
			getScheduled();
			if (mHourly.size() > 6 || mDaily.size() > 24)
				Log.e(TAG, "Too many scheduled!");
			setAlarms();
		}
	}

	private void setAlarms() {
		for (Scheduler s : mWeekly)
			setAlarm(s.getId(), WakeUpHelper.getScheduleTime(s), AlarmManager.INTERVAL_DAY * 7);
		for (Scheduler c : mDaily)
			setAlarm(c.getId(), WakeUpHelper.getScheduleTime(c), AlarmManager.INTERVAL_DAY);
		int i = 0;
		for (Scheduler h : mHourly) {
			setAlarm(h.getId(), WakeUpHelper.getScheduleTime(i, mHourly.size()), AlarmManager.INTERVAL_HOUR);
			i++;
		}
	}

	public void setAlarm(int actionId, long when, long interval) {
		Intent wake = createScheduledIntent(this, actionId);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			wake.putExtra(INTERVAL, interval);
			alarm.setExact(AlarmManager.RTC_WAKEUP, when, PendingIntent.getBroadcast(this, actionId, wake, PendingIntent.FLAG_UPDATE_CURRENT));
		} else
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, when, interval, PendingIntent.getBroadcast(this, actionId, wake, PendingIntent.FLAG_UPDATE_CURRENT));
	}

	private Intent createScheduledIntent(Context c, int actionId) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		wake.putExtra(OperatorAction.ID, actionId);
		wake.putExtra(WakeUpHelper.SOURCE, WakeUpHelper.TIMER);
		addActionVariableValues(actionId, wake);
		return wake;
	}

	private void addActionVariableValues(int actionId, Intent wake) {
		Cursor cursor = getContentResolver().query(Contract.ActionVariableEntry.CONTENT_URI, Contract.VARIABLE_PROJECTION,
				Contract.ActionVariableEntry.COLUMN_ACTION_ID + " = " + actionId, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			wake.putExtra(cursor.getString(cursor.getColumnIndex(Contract.ActionVariableEntry.COLUMN_NAME)),
					cursor.getString(cursor.getColumnIndex(Contract.ActionVariableEntry.COLUMN_VALUE)));
		}
		cursor.close();
	}

	private void getScheduled() {
		mWeekly = getAllOfType(Scheduler.WEEKLY);
		mDaily = getAllOfType(Scheduler.DAILY);
		mHourly = getAllOfType(Scheduler.HOURLY);
	}

	private ArrayList<Scheduler> getAllOfType(int type) {
		ArrayList<Scheduler> schedules = new ArrayList<>();
		SQLiteDatabase database = new DbHelper(this).getReadableDatabase();
		Cursor cursor = database.query(Contract.ActionScheduleEntry.TABLE_NAME, Contract.SCHEDULE_PROJECTION, Contract.ActionScheduleEntry.COLUMN_TYPE + " = " + type,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			schedules.add(new Scheduler(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		database.close();
		return schedules;
	}
}
