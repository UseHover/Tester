package com.hover.tester;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.hover.sdk.onboarding.HoverIntegrationActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class OperatorService {
	public static final String TAG = "OperatorService", ID = "service_id", SLUG = "service_slug",
			NAME = "service_name", COUNTRY = "country", CURRENCY = "currency", ACTION_LIST = "service_actions";

	public int mId;
	public String mName, mSlug, mCountryIso, mCurrencyIso;
	public List<OperatorAction> mActions;

	public OperatorService(Intent data, Context c) {
		mId = data.getIntExtra("serviceId", -1);
		mSlug = data.getStringExtra("opSlug");
		mName = data.getStringExtra("opSlug");
		mCountryIso = data.getStringExtra("countryName");
		mCurrencyIso = data.getStringExtra("currency");
		mActions = getActionsFromSdk(c);
		save(c);
	}

	public OperatorService(Context c) {
		SharedPreferences db = Utils.getSharedPrefs(c);
		mId = db.getInt(ID, -1);
		mName = db.getString(NAME, "");
		mSlug = db.getString(SLUG, "");
		mCountryIso = db.getString(COUNTRY, "");
		mCurrencyIso = db.getString(CURRENCY, "");
		mActions = getActionsFromSdk(c);
	}

	private List<OperatorAction> getActionsFromSdk(Context c) {
		JSONArray jsonActions = HoverIntegrationActivity.getActionsList(mId, c);
		List<OperatorAction> actions = new ArrayList<>(jsonActions.length());
		Log.e(TAG, "Adding actions from json: " + jsonActions);
		try {
			for (int a = 0; a < jsonActions.length(); a++)
				actions.add(new OperatorAction(jsonActions.getJSONObject(a), mId));
		} catch (JSONException e) { Log.e(TAG, "Exception processing actions from json", e); }
		return actions;
	}

	public OperatorService save(Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putInt(ID, mId);
		editor.putString(SLUG, mSlug);
		editor.putString(NAME, mName);
		editor.putString(COUNTRY, mCountryIso);
		editor.putString(CURRENCY, mCurrencyIso);
		editor.commit();
		saveActions(c);
		return this;
	}

	public void saveActions(Context c) {
		Log.e(TAG, "Saving Actions");
		for (OperatorAction opAction: mActions)
			opAction.save(c);
	}

	public static int getLastUsedId(Context c) {
		return Utils.getSharedPrefs(c).getInt(ID, -1);
	}
}
