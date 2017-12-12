package com.hover.tester.notifications;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.network.NetworkService;
import com.hover.tester.network.VolleySingleton;
import com.hover.tester.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class DeviceInfoService extends NetworkService {
	public final static String TAG = "DeviceInfoService", UPLOAD_DEVICE_INFO = "uploadFirebaseToken";

	public DeviceInfoService() {
		super(TAG);
		netOps = new NetworkOps(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			Log.i(TAG, "Uploading device info");
			String webhook = "https://hooks.slack.com/services/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";
			String hoverResponse = VolleySingleton.uploadJsonNowAbsolute(this, Request.Method.POST, webhook, createSlackJson());
		} catch (NullPointerException | InterruptedException | TimeoutException | ExecutionException | JSONException e) {
			Log.d(TAG, "Failed to upload to slack webhook", e);
			Crashlytics.logException(e);
		}
	}

	private JSONObject createSlackJson() throws JSONException {
		JSONObject root = new JSONObject();
		JSONArray attachments = new JSONArray();
		JSONObject attachOne = new JSONObject();
		attachOne.put("fallback", "Device info from Hover Gateway App");
		attachOne.put("title", "Device info from Hover Gateway App");
		JSONArray fields = new JSONArray();


		JSONObject field = new JSONObject();
		field.put("title", "hover_device_id");
		field.put("value", Utils.getDeviceId(this));
		fields.put(field);

		JSONObject field2 = new JSONObject();
		field2.put("title", "firebase_token");
		field2.put("value", FirebaseInstanceId.getInstance().getToken());
		fields.put(field2);

		attachOne.put("fields", fields);
		attachments.put(attachOne);
		root.put("attachments", attachments);
		return root;
	}
}
