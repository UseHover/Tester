package com.hover.tester.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.hover.tester.R;
import com.hover.tester.Utils;

import org.json.JSONArray;
import org.json.JSONException;

public class HoverIntegratonListService extends NetworkService {
	public final static String TAG = "HoverIntegratonListService", SERVICES = "saved_services",
		ID = "id", NAME = "name";

	public HoverIntegratonListService() {
		super(TAG);
		netOps = new NetworkOps(this);
	}

	@Override
	protected void onHandleIntent(Intent i) {
		try {
			String services = netOps.download(getString(R.string.service_list_endpoint));
			saveServices(services);
		} catch (Exception e) {
			Log.d("HoverListService", "download failed", e);
		}
	}

	public void saveServices(String servicesResponse) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(this).edit();
		editor.putString(SERVICES, servicesResponse);
		editor.apply();
	}

	public static CharSequence[] getServices(Context c) {
		try {
			JSONArray json = new JSONArray(Utils.getSharedPrefs(c).getString(SERVICES, null));
			CharSequence[] list = new CharSequence[json.length()];
			for (int j = 0; j < json.length(); j++)
				list[j] = json.getJSONObject(j).getInt(ID) + ": " + json.getJSONObject(j).getString(NAME);
			return list;
		} catch (Exception e) {
			return new CharSequence[0];
		}
	}

	public static int getServiceId(int index, Context c) {
		try {
			return new JSONArray(Utils.getSharedPrefs(c).getString(SERVICES, null)).getJSONObject(index).getInt(ID);
		} catch (JSONException e) {
			return -1;
		}
	}
}
