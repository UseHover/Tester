package com.hover.tester.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.hover.tester.R;
import com.hover.tester.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HoverIntegratonListService extends NetworkService {
	public final static String TAG = "HoverIntListService",
			SIM_IDS = "sim_ids", ID = "id", NAME = "name", ACTIONS = "saved_actions";

	public HoverIntegratonListService() {
		super(TAG);
		netOps = new NetworkOps(this);
	}

	@Override
	protected void onHandleIntent(Intent i) {
		try {
			String actions = netOps.download(getString(R.string.action_list_endpoint));
			saveActions(actions);
		} catch (Exception e) {
			Crashlytics.logException(e);
			Log.d("HoverListService", "download failed", e);
		}
	}

	public void saveActions(String actionsResponse) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(this).edit();
		editor.putString(ACTIONS, actionsResponse);
		editor.apply();
	}

	public static CharSequence[] getActionsList(Context c) {
		try {
			JSONArray json = new JSONArray(Utils.getSharedPrefs(c).getString(ACTIONS, null));
			CharSequence[] list = new CharSequence[json.length()];
			for (int j = 0; j < json.length(); j++)
				list[j] = json.getJSONObject(j).getString(ID) + ". " + json.getJSONObject(j).getString(NAME);
			return list;
		} catch (Exception e) {
			Crashlytics.logException(e);
			return new CharSequence[0];
		}
	}

	private static JSONObject getActionJson(int id, Context c) throws JSONException {
		JSONArray json = new JSONArray(Utils.getSharedPrefs(c).getString(ACTIONS, null));
		CharSequence[] list = new CharSequence[json.length()];
		for (int j = 0; j < json.length(); j++) {
			if (json.getJSONObject(j).getInt(ID) == id)
				return json.getJSONObject(j);
		}
		throw new JSONException("Could not find action in json");
	}

//	private List<OperatorAction> getActionsFromSdk(int serviceId, Context c) {
//		JSONArray jsonActions = HoverHelper.getActionsList(serviceId, c);
//		List<OperatorAction> actions = new ArrayList<>(jsonActions.length());
//		try {
//			for (int a = 0; a < jsonActions.length(); a++)
//				actions.add(new OperatorAction(jsonActions.getJSONObject(a), serviceId));
//		} catch (JSONException e) {
//			Crashlytics.logException(e);
//			Log.d("HIntegratonListService", "Exception processing actions from SDK", e);
//		}
//		return actions;
//	}

	public static int getActionId(int index, Context c) {
		try {
			return new JSONArray(Utils.getSharedPrefs(c).getString(ACTIONS, null)).getJSONObject(index).getInt(ID);
		} catch (JSONException e) {
			Crashlytics.logException(e);
			return -1;
		}
	}

	public static JSONObject getAction(int index, Context c) {
		try {
			return new JSONArray(Utils.getSharedPrefs(c).getString(ACTIONS, null)).getJSONObject(index);
		} catch (JSONException e) { Crashlytics.logException(e); }
		return new JSONObject();
	}
	public static int getActionListSize(Context c) {
		try {
			return new JSONArray(Utils.getSharedPrefs(c).getString(ACTIONS, null)).length();
		} catch (JSONException e) {
			Log.e(TAG, "Could not get actions list size");
			return 0;
		}
	}
}