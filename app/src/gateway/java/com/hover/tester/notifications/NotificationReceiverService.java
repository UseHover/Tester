package com.hover.tester.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hover.tester.wake.WakeUpHelper;
import com.hover.tester.wake.WakeUpReceiver;
import com.hover.tester.actions.HoverAction;
import com.hover.tester.utils.Utils;

import java.util.Map;

public class NotificationReceiverService extends FirebaseMessagingService {
	public static final String TAG = "NotificationReceiver", WEBHOOK = "webhook", IS_SLACK_WEBHOOK = "is_slack_webhook";

	public NotificationReceiverService() { }

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		if (remoteMessage.getData().size() > 0) {
			Log.i(TAG, "Received notification. Message data payload: " + remoteMessage.getData());
			if (remoteMessage.getData().containsKey(WEBHOOK))
				setWebhook(remoteMessage.getData().get(WEBHOOK), remoteMessage.getData().containsKey(IS_SLACK_WEBHOOK));
			if (remoteMessage.getData().containsKey(HoverAction.ID))
				sendFcmTriggeredWake(this, remoteMessage.getData());
		} else {
			startService(new Intent(this, DeviceInfoService.class));
		}
	}

	public static void sendFcmTriggeredWake(Context c, Map<String, String> data) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (entry.getKey().equals(HoverAction.ID))
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
}
