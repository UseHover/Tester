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

	public int id;
	public String mName, mSlug, mCountryIso, mCurrencyIso;
	public List<OperatorAction> mActions;

	public OperatorService(Intent data, Context c) {
		id = data.getIntExtra("serviceId", -1);
		mSlug = data.getStringExtra("opSlug");
		mName = data.getStringExtra("opSlug");
		mCountryIso = data.getStringExtra("countryName");
		mCurrencyIso = data.getStringExtra("currency");
		mActions = getActionsFromSdk(c);
		save(c);
	}

	public OperatorService(Context c) {
		SharedPreferences db = Utils.getSharedPrefs(c);
		id = db.getInt(ID, -1);
		mName = db.getString(NAME, "");
		mSlug = db.getString(SLUG, "");
		mCountryIso = db.getString(COUNTRY, "");
		mCurrencyIso = db.getString(CURRENCY, "");
//		getSavedActions(c);
		mActions = getActionsFromSdk(c);
		if (mActions.size() > 0 && mActions.size() != getSavedActions(c).size())
			saveActions(null, c);
	}

	private List<OperatorAction> getActionsFromSdk(Context c) {
		JSONArray jsonActions = HoverIntegrationActivity.getActionsList(id, c);
		List<OperatorAction> actions = new ArrayList<>(jsonActions.length());
		Log.e(TAG, "Adding actions from list. json: " + jsonActions);
		try {
			for (int a = 0; a < jsonActions.length(); a++)
				actions.add(new OperatorAction(jsonActions.getJSONObject(a), mSlug));
		} catch (JSONException e) { Log.e(TAG, "Exception processing actions from json", e); }
		return actions;
	}

	private List<OperatorAction> getSavedActions(Context c) {
		JSONArray jsonActions = getActionList(c);
		List<OperatorAction> actions = new ArrayList<>(jsonActions.length());
		try {
			for (int a = 0; a < jsonActions.length(); a++)
				actions.add(new OperatorAction(jsonActions.getString(a), mSlug, c));
		} catch (JSONException e) { Log.e(TAG, "Exception processing actions from shared prefs", e); }
		return actions;
	}

	private void save(Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putInt(ID, id);
		editor.putString(SLUG, mSlug);
		editor.putString(NAME, mName);
		editor.putString(COUNTRY, mCountryIso);
		editor.putString(CURRENCY, mCurrencyIso);
		saveActions(editor, c);
		editor.commit();
	}

	private void saveActions(SharedPreferences.Editor editor, Context c) {
		if (editor == null) editor = Utils.getSharedPrefs(c).edit();
		for (OperatorAction opAction: mActions) {
			addAction(opAction.mSlug, c, editor);
			opAction.save(c);
		}
		editor.commit();
	}

	public static boolean savedServiceExists(Context c) {
		return Utils.getSharedPrefs(c).getInt(ID, -1) != -1;
	}

	public JSONArray getActionList(Context context) {
		try {
			return new JSONArray(Utils.getSharedPrefs(context).getString(ACTION_LIST, "[]"));
		} catch (Exception e) { return new JSONArray(); }
	}
	public void addAction(final String value, Context context, SharedPreferences.Editor editor) {
		try {
			JSONArray new_actions = getActionList(context);
			new_actions.put(value);
			editor.putString(ACTION_LIST, new_actions.toString());
		} catch (Exception e) {}
	}
}
