package com.hover.tester;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StatusReport {
	public final static String TAG = "StatusReport", STATUS = "status";
	public final static int PENDING = 0, FAILURE = 1, SUCCESS = 2;

	private int mId, mActionId, mStatus, mTransactionId;
	private String mSrc, mSessionMsg, mConfirmMsg, mFailureMsg;
	private Map<String, String> mExtras;
	private long mStartTime, mEndTime;

	public StatusReport(Intent i) {
		mActionId = i.getIntExtra(OperatorAction.ID, -1);
		mStartTime = System.currentTimeMillis();
		mStatus = PENDING;
		mExtras = getExtras(i);
	}

	public StatusReport(int id, Context c) {
		Cursor cursor = c.getContentResolver().query(Contract.StatusReportEntry.CONTENT_URI, Contract.REPORT_PROJECTION, Contract.StatusReportEntry.COLUMN_ENTRY_ID + " = " + id, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			mId = cursor.getInt(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_ENTRY_ID));
			mActionId = cursor.getInt(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_ACTION_ID));
			mStatus = cursor.getInt(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_STATUS));
			mStartTime = cursor.getLong(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_START_TIMESTAMP));
			mEndTime = cursor.getLong(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_FINISH_TIMESTAMP));
			mFailureMsg = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_FAILURE_MESSAGE));
			mSessionMsg = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_FINAL_SESSION_MSG));
			mConfirmMsg = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE));
		} else {
			Crashlytics.log("Failed to get cursor for Status Report");
			Log.d(TAG, "didn't get cursor...");
		}
		cursor.close();
	}

	public StatusReport save(final Context c) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mId = (int) ContentUris.parseId(c.getContentResolver().insert(Contract.StatusReportEntry.CONTENT_URI, getStartContentValues()));
			}
		}).start();
		return this;
	}

	public void update(int transactionId, String sessionMsg, String failMsg) {
		mTransactionId = transactionId;
		mSessionMsg = sessionMsg;
		mFailureMsg = failMsg;
	}

	public void update(int status, String confirmMsg, String failMsg, final Context c) {
		mStatus = status;
		mConfirmMsg = confirmMsg;
		if (mFailureMsg == null) mFailureMsg = failMsg;
		mEndTime = System.currentTimeMillis();
		c.getContentResolver().update(
				Contract.StatusReportEntry.CONTENT_URI, getUpdateContentValues(), Contract.StatusReportEntry.COLUMN_ENTRY_ID + " = " + mId, null);
	}

	public void upload(Context c) {
		Intent i = new Intent(c, ReportUpService.class);
		i.putExtra(Contract.StatusReportEntry.COLUMN_ENTRY_ID, mId);
		c.startService(i);
	}

	public JSONObject getJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", mId);
		json.put("action_id", mActionId);
		json.put("status", mStatus == SUCCESS ? "success" : "failure");
		json.put("started_timestamp", mStartTime);
		json.put("finished_timestamp", mEndTime);
		json.put("final_session_message", mSessionMsg);
		json.put("confirmation_message", mConfirmMsg);
		json.put("failure_message", mFailureMsg);
		return json;
	}

	private ContentValues getStartContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.StatusReportEntry.COLUMN_ACTION_ID, mActionId);
		cv.put(Contract.StatusReportEntry.COLUMN_START_TIMESTAMP, mStartTime);
		cv.put(Contract.StatusReportEntry.COLUMN_STATUS, mStatus);
//		cv.put(Contract.StatusReportEntry.COLUMN_INPUT_EXTRAS, mExtras);
		return cv;
	}

	private ContentValues getUpdateContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.StatusReportEntry.COLUMN_FINISH_TIMESTAMP, mEndTime);
		cv.put(Contract.StatusReportEntry.COLUMN_STATUS, mStatus);
		cv.put(Contract.StatusReportEntry.COLUMN_TRANSACTION_ID, mTransactionId);
		cv.put(Contract.StatusReportEntry.COLUMN_FAILURE_MESSAGE, mFailureMsg);
		cv.put(Contract.StatusReportEntry.COLUMN_FINAL_SESSION_MSG, mSessionMsg);
		cv.put(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE, mConfirmMsg);
		return cv;
	}

	private Map<String, String> getExtras(Intent i) {
		Map<String, String> extras = new HashMap<>();
		for (String key : i.getExtras().keySet()) {
			if (i.getExtras().get(key) != null) extras.put(key, i.getExtras().get(key).toString());
			else Crashlytics.log("Status Report extra " + key + " was null. Action Id: " + mActionId);
		}
		return extras;
	}
}
