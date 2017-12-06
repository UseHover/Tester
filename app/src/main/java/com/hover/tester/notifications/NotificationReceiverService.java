package com.hover.tester.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hover.tester.R;
import com.hover.tester.WakeUpHelper;
import com.hover.tester.WakeUpReceiver;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.network.VolleySingleton;
import com.hover.tester.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class NotificationReceiverService extends FirebaseMessagingService {
	public static final String TAG = "NotificationReceiver", WEBHOOK = "webhook", IS_SLACK_WEBHOOK = "is_slack_webhook";

	public NotificationReceiverService() { }

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		if (remoteMessage.getData().size() > 0) {
			Log.i(TAG, "Received notification. Message data payload: " + remoteMessage.getData());
			if (remoteMessage.getData().containsKey(WEBHOOK))
				setWebhook(remoteMessage.getData().get(WEBHOOK), remoteMessage.getData().containsKey(IS_SLACK_WEBHOOK));
			if (remoteMessage.getData().containsKey(OperatorAction.ID))
				sendFcmTriggeredWake(this, remoteMessage.getData());
		} else {
			sendDeviceInfo();
		}
	}

	public static void sendFcmTriggeredWake(Context c, Map<String, String> data) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (entry.getKey().equals(OperatorAction.ID))
				wake.putExtra(entry.getKey(), (int) Integer.valueOf(entry.getValue()));
			else
				wake.putExtra(entry.getKey(), entry.getValue());
		}
		wake.putExtra(WakeUpHelper.SOURCE, WakeUpHelper.FCM);
		WakeUpHelper.setExactAlarm(wake, WakeUpHelper.now(), 0, c);
	}

	private void setWebhook(String webhookString, boolean isSlackWebhook) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(this).edit();
		editor.putString(WEBHOOK, webhookString);
		if (isSlackWebhook) editor.putBoolean(IS_SLACK_WEBHOOK, isSlackWebhook);
		editor.apply();
	}

	private void sendDeviceInfo() {
		try {
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
