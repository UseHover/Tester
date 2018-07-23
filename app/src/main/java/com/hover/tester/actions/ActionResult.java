package com.hover.tester.actions;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.hover.tester.utils.Utils;
import com.hover.tester.database.Contract;
import com.hover.tester.database.DbHelper;

import java.util.Arrays;

public class ActionResult {
	public static final String TAG = "ActionResult", UUID = "uuid",
			RESULT = "response_message", NEG_RESULT = "error", RESULT_TIMESTAMP = "response_timestamp",
			SORT_ORDER = Contract.ActionResultEntry.COLUMN_TIMESTAMP + " DESC";
	public static final int STATUS_UNTESTED = -1, STATUS_FAILED = 0, STATUS_SUCCEEDED = 1, STATUS_UNKNOWN = 2;
	public int mId, mStatus;
	public long mTimeStamp;
	public String mActionId, mSdkUuid, mText, mDetails;

	public ActionResult(String actionId, int resultCode, Intent data) {
		mSdkUuid = data.hasExtra(UUID) ? data.getStringExtra(UUID) : "none";
		mActionId = actionId;
		if (resultCode == Activity.RESULT_OK) {
			mStatus = STATUS_UNKNOWN;
			mText = data.getStringExtra(RESULT);
			mDetails = getDetails(data);
		} else {
			mStatus = STATUS_FAILED;
			mText = data.getStringExtra(NEG_RESULT);
		}
		if (data.hasExtra(RESULT_TIMESTAMP))
			mTimeStamp = data.getLongExtra(RESULT_TIMESTAMP, 0L);
		else
			mTimeStamp = Utils.now();
	}

	public ActionResult(Cursor c) {
		mId = c.getInt(c.getColumnIndex(Contract.ActionResultEntry.COLUMN_ENTRY_ID));
		mSdkUuid = c.getString(c.getColumnIndex(Contract.ActionResultEntry.COLUMN_SDK_UUID));
		mActionId = c.getString(c.getColumnIndex(Contract.ActionResultEntry.COLUMN_ACTION_ID));
		mText = c.getString(c.getColumnIndex(Contract.ActionResultEntry.COLUMN_TEXT));
		mDetails = c.getString(c.getColumnIndex(Contract.ActionResultEntry.COLUMN_RETURN_VALUES));
		mStatus = c.getInt(c.getColumnIndex(Contract.ActionResultEntry.COLUMN_STATUS));
		mTimeStamp = c.getLong(c.getColumnIndex(Contract.ActionResultEntry.COLUMN_TIMESTAMP));
	}

	String getDetails(Intent data) {
		Bundle bundle = data.getExtras();
		String deets = "";
		if (bundle != null) {
			for (String key : bundle.keySet())
				if (key.equals("ussd_messages")) {
					String[] msgs = bundle.getStringArray(key);
					String ussd = "";
					for (String msg: msgs)
						ussd += msg;
					deets += key + ": " + ussd;
				} else if (bundle.get(key) != null) deets += key + ": " + bundle.get(key).toString() + ", ";
		}
		return deets;
	}

	public static ActionResult getByUuid(String uuid, Context context) {
		ActionResult ar = null;
		Cursor cursor = new DbHelper(context).getReadableDatabase()
				.query(Contract.ActionResultEntry.TABLE_NAME, Contract.RESULT_PROJECTION,
						Contract.ActionResultEntry.COLUMN_SDK_UUID + " = '" + uuid + "'",
						null, null, null, null);
		if (cursor.moveToFirst())
			ar = new ActionResult(cursor);
		cursor.close();
		return ar;
	}

	public static ActionResult getLatest(String actionId, Context context) {
		ActionResult ar = null;
		Cursor cursor = new DbHelper(context).getReadableDatabase()
				.query(Contract.ActionResultEntry.TABLE_NAME, Contract.RESULT_PROJECTION,
						Contract.ActionResultEntry.COLUMN_ACTION_ID + " = '" + actionId +"'",
						null, null, null, SORT_ORDER);
		if (cursor.moveToFirst())
			 ar = new ActionResult(cursor);
		cursor.close();
		return ar;
	}

	public void save(final Context c) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				c.getContentResolver().insert(Contract.ActionResultEntry.CONTENT_URI, getContentValues());
			}
		}).start();
	}

	private ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.ActionResultEntry.COLUMN_SDK_UUID, mSdkUuid);
		cv.put(Contract.ActionResultEntry.COLUMN_ACTION_ID, mActionId);
		cv.put(Contract.ActionResultEntry.COLUMN_STATUS, mStatus);
		cv.put(Contract.ActionResultEntry.COLUMN_TIMESTAMP, mTimeStamp);
		cv.put(Contract.ActionResultEntry.COLUMN_TEXT, mText);
		cv.put(Contract.ActionResultEntry.COLUMN_RETURN_VALUES, mDetails);
		return cv;
	}
}
