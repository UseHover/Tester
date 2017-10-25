package com.hover.tester.actions;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.hover.tester.utils.Utils;
import com.hover.tester.database.Contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OperatorAction {
	public static final String TAG = "OperatorAction", ID = "action_id";

	public String mName, mSlug;
	public int[] mVariableIds;
	public int mId, mOpId;
	public ActionVariable[] mVariables;
	public ActionResult mLastResult;

	public OperatorAction(JSONObject jsonAct, int opId) throws JSONException {
		mOpId = opId;
		mSlug = jsonAct.getString("slug");
		mName = jsonAct.getString("name");
		JSONArray variables = jsonAct.getJSONArray("variables");
		Log.d(TAG, "variable length: " + variables.length());
		mVariables = new ActionVariable[variables.length()];
		for (int v = 0; v < variables.length(); v++)
			mVariables[v] = new ActionVariable(variables.getString(v));
	}

	public OperatorAction(Cursor c, Context context) {
		mId = c.getInt(c.getColumnIndex(Contract.OperatorActionEntry.COLUMN_ENTRY_ID));
		mOpId = c.getInt(c.getColumnIndex(Contract.OperatorActionEntry.COLUMN_SERVICE_ID));
		mSlug = c.getString(c.getColumnIndex(Contract.OperatorActionEntry.COLUMN_SLUG));
		mName = c.getString(c.getColumnIndex(Contract.OperatorActionEntry.COLUMN_NAME));
		mVariableIds = Utils.getIdListFromString(c.getString(c.getColumnIndex(Contract.OperatorActionEntry.COLUMN_VARIABLES)));

		mLastResult = ActionResult.getLatest(mId, context);
	}

	public void save(final Context c) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mId = (int) ContentUris.parseId(c.getContentResolver().insert(Contract.OperatorActionEntry.CONTENT_URI, getBasicContentValues()));
				for (ActionVariable variable: mVariables) {
					variable.mActionId = mId;
					variable.save(c);
				}
			}
		}).start();
	}
	private ContentValues getBasicContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.OperatorActionEntry.COLUMN_NAME, mName);
		cv.put(Contract.OperatorActionEntry.COLUMN_SLUG, mSlug);
		cv.put(Contract.OperatorActionEntry.COLUMN_SERVICE_ID, mOpId);
		return cv;
	}
}
