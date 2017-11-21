package com.hover.tester.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.tester.R;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HoverIntegratonListService extends NetworkService {
	public final static String TAG = "HoverIntegratonListService", SERVICES = "saved_services",
			SIM_IDS = "sim_ids", ID = "id", NAME = "name", ACTIONS = "actions";

	public HoverIntegratonListService() {
		super(TAG);
		netOps = new NetworkOps(this);
	}

	@Override
	protected void onHandleIntent(Intent i) {
		try {
			String services = netOps.download(getString(R.string.service_list_endpoint) + (i.hasExtra(SIM_IDS) ? "?sims[]" + i.getStringExtra(SIM_IDS) : ""));
			saveServices(services);
		} catch (Exception e) {
			Crashlytics.logException(e);
			Log.d("HoverListService", "download failed", e);
		}
	}

	public void saveServices(String servicesResponse) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(this).edit();
		editor.putString(SERVICES, servicesResponse);
		editor.apply();
	}

	public static CharSequence[] getServicesList(Context c) {
		try {
			JSONArray json = new JSONArray(Utils.getSharedPrefs(c).getString(SERVICES, null));
			CharSequence[] list = new CharSequence[json.length()];
			for (int j = 0; j < json.length(); j++)
				list[j] = json.getJSONObject(j).getInt(ID) + ": " + json.getJSONObject(j).getString(NAME);
			return list;
		} catch (Exception e) {
			Crashlytics.logException(e);
			return new CharSequence[0];
		}
	}

	public static CharSequence[] getActionsList(int serviceId, Context c) {
		try {
			JSONArray json = getServiceJson(serviceId, c).getJSONArray(ACTIONS);
			CharSequence[] list = new CharSequence[json.length()];
			for (int j = 0; j < json.length(); j++)
				list[j] = json.getJSONObject(j).getString(ID) + ". " + json.getJSONObject(j).getString(NAME);
			return list;
		} catch (Exception e) {
			Crashlytics.logException(e);
			return new CharSequence[0];
		}
	}

	private static JSONObject getServiceJson(int id, Context c) throws JSONException {
		JSONArray json = new JSONArray(Utils.getSharedPrefs(c).getString(SERVICES, null));
		CharSequence[] list = new CharSequence[json.length()];
		for (int j = 0; j < json.length(); j++) {
			if (json.getJSONObject(j).getInt(ID) == id)
				return json.getJSONObject(j);
		}
		throw new JSONException("Could not find service in json");
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

	public static int getServiceId(int index, Context c) {
		try {
			return new JSONArray(Utils.getSharedPrefs(c).getString(SERVICES, null)).getJSONObject(index).getInt(ID);
		} catch (JSONException e) {
			Crashlytics.logException(e);
			return -1;
		}
	}

	public static JSONObject getAction(int serviceId, int index, Context c) throws JSONException {
		return getServiceJson(serviceId, c).getJSONArray(ACTIONS).getJSONObject(index);
	}
}