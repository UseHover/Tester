package com.hover.tester.actions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hover.tester.database.DbHelper;
import com.hover.tester.gateway.KeyStoreHelper;
import com.hover.tester.database.Contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HoverAction {
	public static final String TAG = "HoverAction", ID = "action_id";

	public String mId, mName, mNetworkName, mEncryptedPin;
	public JSONArray mSimHniList;
	public ArrayList<ActionVariable> mVariables = new ArrayList<>(0);
	public ActionResult mLastResult;

	public HoverAction(JSONObject jsonAct) {
		try {
			mId = jsonAct.getString("id");
			mName = jsonAct.getString("name");
			mNetworkName = jsonAct.getString("network_name");
			mSimHniList = jsonAct.getJSONArray("hni_list");
			JSONArray variables = jsonAct.getJSONArray("custom_steps");
			mVariables = new ArrayList<>(0);
			for (int v = 0; v < variables.length(); v++) {
				if (variables.getJSONObject(v).getBoolean("is_param"))
					mVariables.add(new ActionVariable(mId, variables.getJSONObject(v).getString("value")));
			}
		} catch (JSONException e) { }
	}

	public HoverAction(Cursor c, Context context) {
		mId = c.getString(c.getColumnIndex(Contract.HoverActionEntry.COLUMN_ENTRY_ID));
		mName = c.getString(c.getColumnIndex(Contract.HoverActionEntry.COLUMN_NAME));
		try {
			mSimHniList = new JSONArray(c.getString(c.getColumnIndex(Contract.HoverActionEntry.COLUMN_SIM_ID)));
		} catch (JSONException | NullPointerException ignored) { }
		mNetworkName = c.getString(c.getColumnIndex(Contract.HoverActionEntry.COLUMN_NETWORK_NAME));
//		mVariableIds = Utils.getIdListFromString(c.getString(c.getColumnIndex(Contract.HoverActionEntry.COLUMN_VARIABLES)));

		mLastResult = ActionResult.getLatest(mId, context);
	}

	public void save(final Context c) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				c.getContentResolver().insert(Contract.HoverActionEntry.CONTENT_URI, getBasicContentValues());
				for (int v = 0; v < mVariables.size(); v++)
					mVariables.get(v).save(c);
				Log.d(TAG, "Saved action " + mName);
			}
		}).start();
	}

	private ContentValues getBasicContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.HoverActionEntry.COLUMN_ENTRY_ID, mId);
		cv.put(Contract.HoverActionEntry.COLUMN_NAME, mName);
		cv.put(Contract.HoverActionEntry.COLUMN_SIM_ID, mSimHniList.toString());
		cv.put(Contract.HoverActionEntry.COLUMN_NETWORK_NAME, mNetworkName);
		return cv;
	}

	public static HoverAction load(String id, Context c) {
		HoverAction action = null;
		SQLiteDatabase database = new DbHelper(c).getReadableDatabase();
		Cursor cursor = database.query(Contract.HoverActionEntry.TABLE_NAME, Contract.ACTION_PROJECTION,
				Contract.HoverActionEntry.COLUMN_ENTRY_ID + " = '" + id + "'",
				null, null, null, null);
		if (cursor.moveToFirst())
			action = new HoverAction(cursor, c);
		cursor.close();
		database.close();
		return action;
	}

	public String getPin(Context c) {
		String encryptedpin = null;
		SQLiteDatabase database = new DbHelper(c).getReadableDatabase();
		Cursor cursor = database.query(Contract.HoverActionEntry.TABLE_NAME, Contract.ACTION_PIN_PROJECTION,
				Contract.HoverActionEntry.COLUMN_ENTRY_ID + " = '" + mId + "'",
				null, null, null, null);
		if (cursor.moveToFirst())
			encryptedpin = cursor.getString(cursor.getColumnIndex(Contract.HoverActionEntry.COLUMN_PIN));
		cursor.close();
		database.close();
		if (encryptedpin != null)
			return KeyStoreHelper.decrypt(encryptedpin, mId, c);
		else
			return null;
	}
	public void setPin(String value) {
		mEncryptedPin = value;
	}
}
