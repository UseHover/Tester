package com.hover.tester.services;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hover.tester.gateway.KeyStoreHelper;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;
import com.hover.tester.database.DbHelper;
import com.hover.tester.network.HoverIntegratonListService;

import org.json.JSONException;

import java.util.List;

public class OperatorService {
	public static final String TAG = "OperatorService", PIN = "pin";

	public int mId;
	public String mName, mOpSlug, mCountryIso, mCurrencyIso, mEncryptedPin;

	public OperatorService(Intent data, Context c) {
		if (data.hasExtra("serviceId")) {
			mId = data.getIntExtra("serviceId", -1);
			mName = data.getStringExtra("serviceName");
			mOpSlug = data.getStringExtra("operator");
			mCountryIso = data.getStringExtra("country");
			mCurrencyIso = data.getStringExtra("currency");
		}
	}

	public OperatorService(Cursor cursor, Context c) {
		mId = cursor.getInt(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_SERVICE_ID));
		mName = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_NAME));
		mOpSlug = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_OP_SLUG));
		mCountryIso = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_COUNTRY));
		mCurrencyIso = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_CURRENCY));
	}

	public OperatorService save(Context c) {
		mId = (int) ContentUris.parseId(c.getContentResolver().insert(Contract.OperatorServiceEntry.CONTENT_URI, getBasicContentValues()));
		return this;
	}

	public void saveAllActions(Context c) {
		try {
			for (int i = 0; i < HoverIntegratonListService.getActionListSize(mId, c); i++) {
				OperatorAction newAction = new OperatorAction(HoverIntegratonListService.getAction(mId, i, c), mId);
				newAction.save(c);
			}
		} catch (JSONException e) {}
	}

	public static int count(Context c) {
		Cursor countCursor = c.getContentResolver().query(Contract.OperatorServiceEntry.CONTENT_URI, new String[] {"count(*) AS count"}, null, null, null);
		if (countCursor != null) {
			countCursor.moveToFirst();
			int count = countCursor.getInt(0);
			countCursor.close();
			return count;
		}
		return 0;
	}

	private ContentValues getBasicContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.OperatorServiceEntry.COLUMN_SERVICE_ID, mId);
		cv.put(Contract.OperatorServiceEntry.COLUMN_NAME, mName);
		cv.put(Contract.OperatorServiceEntry.COLUMN_OP_SLUG, mOpSlug);
		cv.put(Contract.OperatorServiceEntry.COLUMN_COUNTRY, mCountryIso);
		cv.put(Contract.OperatorServiceEntry.COLUMN_CURRENCY, mCurrencyIso);
		cv.put(Contract.OperatorServiceEntry.COLUMN_PIN, mEncryptedPin);
		return cv;
	}

	public static int getId(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_SERVICE_ID));
	}
	public String getPin(Context c) {
		String encryptedpin = null;
		SQLiteDatabase database = new DbHelper(c).getReadableDatabase();
		Cursor cursor = database.query(Contract.OperatorServiceEntry.TABLE_NAME, Contract.SERVICE_PIN_PROJECTION,
				Contract.OperatorServiceEntry.COLUMN_SERVICE_ID + " = " + mId,
				null, null, null, null);
		if (cursor.moveToFirst())
			encryptedpin = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_PIN));
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

	public static OperatorService load(int id, Context c) {
		OperatorService service = null;
		SQLiteDatabase database = new DbHelper(c).getReadableDatabase();
		Cursor cursor = database.query(Contract.OperatorServiceEntry.TABLE_NAME, Contract.SERVICE_PROJECTION,
				Contract.OperatorServiceEntry.COLUMN_SERVICE_ID + " = " + id,
				null, null, null, null);
		if (cursor.moveToFirst())
			service = new OperatorService(cursor, c);
		cursor.close();
		database.close();
		return service;
	}
}
