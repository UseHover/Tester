package com.hover.tester.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hover.tester.WakeUpHelper;
import com.hover.tester.WakeUpReceiver;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.utils.Utils;

import java.util.Map;

public class NotificationReceiverService extends FirebaseMessagingService {
	public static final String TAG = "NotificationReceiver", WEBHOOK = "webhook";

	public NotificationReceiverService() { }

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		if (remoteMessage.getData().size() > 0) {
			Log.i(TAG, "Received notification. Message data payload: " + remoteMessage.getData());
			if (remoteMessage.getData().containsKey(WEBHOOK))
				setWebhook(remoteMessage.getData().get(WEBHOOK));
			if (remoteMessage.getData().containsKey(OperatorAction.ID))
				sendFcmTriggeredWake(this, remoteMessage.getData());
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

	private void setWebhook(String webhookString) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(this).edit();
		editor.putString(WEBHOOK, webhookString);
		editor.apply();
	}
}
