package com.hover.tester.report;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hover.tester.actions.HoverAction;
import com.hover.tester.database.Contract;
import com.hover.tester.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StatusReport {
	public final static String TAG = "StatusReport", STATUS = "status", TRANSACTION = "transaction";
	public final static int PENDING = 0, FAILURE = 1, SUCCESS = 2;

	private int mId, mStatus;
	private String mActionId, mSrc, mSessionMsg, mConfirmMsg, mFailureMsg, mTransaction;
	private Map<String, String> mExtras;
	private long mStartTime, mEndTime;

	public StatusReport(Intent i) {
		mActionId = i.getStringExtra(HoverAction.ID);
		mStartTime = System.currentTimeMillis();
		mStatus = PENDING;
		mExtras = getExtras(i);
	}

	public StatusReport(int id, Context c) {
		Cursor cursor = c.getContentResolver().query(Contract.StatusReportEntry.CONTENT_URI, Contract.REPORT_PROJECTION, Contract.StatusReportEntry.COLUMN_ENTRY_ID + " = " + id, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			mId = cursor.getInt(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_ENTRY_ID));
			mActionId = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_ACTION_ID));
			mStatus = cursor.getInt(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_STATUS));
			mStartTime = cursor.getLong(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_START_TIMESTAMP));
			mEndTime = cursor.getLong(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_FINISH_TIMESTAMP));
			mTransaction = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_TRANSACTION));
			mFailureMsg = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_FAILURE_MESSAGE));
			mSessionMsg = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_FINAL_SESSION_MSG));
			mConfirmMsg = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE));
			try {
				String s = cursor.getString(cursor.getColumnIndex(Contract.StatusReportEntry.COLUMN_EXTRAS));
				if (s != null) mExtras = getExtras(new JSONObject(s));
			} catch (NullPointerException | JSONException e) { }
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

	public void update(String sessionMsg, String failMsg) {
		mSessionMsg = sessionMsg;
		mFailureMsg = failMsg;
	}

	public void update(int status, String confirmMsg, String failMsg, String transaction_info, final Context c) {
		mStatus = status;
		mConfirmMsg = confirmMsg;
		mTransaction = transaction_info;
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

	public JSONObject getJson(Context c) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("action_id", mActionId);
		json.put("status", mStatus == SUCCESS ? "success" : "failure");
		json.put("started_timestamp", mStartTime);
		json.put("finished_timestamp", mEndTime);
		json.put("final_session_message", mSessionMsg);
		json.put("confirmation_message", mConfirmMsg);
		json.put("failure_message", mFailureMsg);
		json.put("transaction", mTransaction);
		json.put("hover_device_id", Utils.getDeviceId(c));
		json.put("firebase_token", FirebaseInstanceId.getInstance().getToken());
		if (mExtras != null && mExtras.size() > 0)
			json.put("input_extras", new JSONObject(mExtras));
		return json;
	}

	private ContentValues getStartContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.StatusReportEntry.COLUMN_ACTION_ID, mActionId);
		cv.put(Contract.StatusReportEntry.COLUMN_START_TIMESTAMP, mStartTime);
		cv.put(Contract.StatusReportEntry.COLUMN_STATUS, mStatus);
		if (mExtras != null && mExtras.size() > 0)
			cv.put(Contract.StatusReportEntry.COLUMN_EXTRAS, new JSONObject(mExtras).toString());
		return cv;
	}

	private ContentValues getUpdateContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.StatusReportEntry.COLUMN_FINISH_TIMESTAMP, mEndTime);
		cv.put(Contract.StatusReportEntry.COLUMN_STATUS, mStatus);
		cv.put(Contract.StatusReportEntry.COLUMN_TRANSACTION, mTransaction);
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

	private HashMap<String, String> getExtras(JSONObject extras) throws JSONException {
		HashMap<String, String> map = new HashMap<>(extras.length());
		Iterator keys = extras.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			map.put(key, extras.getString(key));
		}
		return map.size() > 0 ? map : null;
	}
}
