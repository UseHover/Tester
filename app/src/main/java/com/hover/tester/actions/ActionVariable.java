package com.hover.tester.actions;

import android.content.ContentValues;
import android.content.Context;

import com.hover.tester.database.Contract;

public class ActionVariable {
	public static final String TAG = "ActionVariable";

	public String mName, mValue, mActionId;

	public ActionVariable(String actionId, String name) {
		mActionId = actionId;
		mName = name;
	}

	public ActionVariable(String name, String value, String actionId) {
		mName = name;
		mValue = value;
		mActionId = actionId;
	}

	public void save(final Context c) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				c.getContentResolver().insert(Contract.ActionVariableEntry.CONTENT_URI, getContentValues());
			}
		}).start();
	}

	private ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.ActionVariableEntry.COLUMN_NAME, mName);
		if (mValue != null) cv.put(Contract.ActionVariableEntry.COLUMN_VALUE, mValue);
		cv.put(Contract.ActionVariableEntry.COLUMN_ACTION_ID, mActionId);
		return cv;
	}
}
