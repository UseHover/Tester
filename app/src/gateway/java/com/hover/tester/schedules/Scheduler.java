package com.hover.tester.schedules;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hover.tester.R;
import com.hover.tester.database.Contract;
import com.hover.tester.database.DbHelper;

public class Scheduler {
	public static String TAG = "Scheduler";
	public static int HOURLY = 0, DAILY = 1, WEEKLY = 2;
	private static Scheduler instance;

	private int mActionId, mType, mDay, mHour, mMin;

	public Scheduler() {}

	public Scheduler(Cursor cursor) {
		Log.i(TAG, "Creating schedule from cursor");
		mActionId = cursor.getInt(cursor.getColumnIndex(Contract.ActionScheduleEntry.COLUMN_ACTION_ID));
		mType = cursor.getInt(cursor.getColumnIndex(Contract.ActionScheduleEntry.COLUMN_TYPE));
		if (mType != HOURLY) {
			if (mType != DAILY)
				mDay = cursor.getInt(cursor.getColumnIndex(Contract.ActionScheduleEntry.COLUMN_DAY));
			mHour = cursor.getInt(cursor.getColumnIndex(Contract.ActionScheduleEntry.COLUMN_HOUR));
			mMin = cursor.getInt(cursor.getColumnIndex(Contract.ActionScheduleEntry.COLUMN_MIN));
		}
	}

	public static Scheduler getInstance() {
		if (instance == null) instance = new Scheduler();
		return instance;
	}

	public void setId(int actionId) { mActionId = actionId; }
	public int getId() {
		return mActionId;
	}

	public void setType(int schedule) {
		mType = schedule;
	}
	public int getType() { return mType; }

	public void setDay(int day) {
		mDay = day;
	}
	public int getDay() { return mDay; }

	public void setTime(int hour, int min) {
		mHour = hour;
		mMin = min;
	}
	public int getHour() { return mHour; }
	public int getMin() { return mMin; }

	public void reset() {
		instance = null;
	}

	public void save(final Context c) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int id = (int) ContentUris.parseId(c.getContentResolver().insert(Contract.ActionScheduleEntry.CONTENT_URI, getContentValues()));
				c.startService(new Intent(c, AlarmSchedulerService.class));
			}
		}).start();
	}

	public static Scheduler load(int actionId, Context c) {
		Scheduler schedule = null;
		SQLiteDatabase database = new DbHelper(c).getReadableDatabase();
		Cursor cursor = database.query(Contract.ActionScheduleEntry.TABLE_NAME, Contract.SCHEDULE_PROJECTION, Contract.ActionScheduleEntry.COLUMN_ACTION_ID + " = " + actionId,
				null, null, null, null);
		if (cursor.moveToFirst())
			schedule = new Scheduler(cursor);
		else
			Log.d(TAG, "schedule cursor was null");
		cursor.close();
		database.close();
		return schedule;
	}

	private ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.ActionScheduleEntry.COLUMN_ACTION_ID, mActionId);
		cv.put(Contract.ActionScheduleEntry.COLUMN_TYPE, mType);
		if (mType != HOURLY) {
			if (mType != DAILY)
				cv.put(Contract.ActionScheduleEntry.COLUMN_DAY, mDay);
			cv.put(Contract.ActionScheduleEntry.COLUMN_HOUR, mHour);
			cv.put(Contract.ActionScheduleEntry.COLUMN_MIN, mMin);
		}
		return cv;
	}

	public String getString(Context c) {
		if (mType == DAILY)
			return c.getString(R.string.daily, getTimeText());
		else if (mType == WEEKLY)
			return c.getString(R.string.weekly, c.getResources().getStringArray(R.array.day_choices)[mDay], getTimeText());
		else
			return c.getString(R.string.hourly);
	}
	private String getTimeText() {
		return mHour + ":" + (mMin < 10 ? "0" + mMin : mMin);
	}
}
