package com.hover.tester.notifications;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hover.tester.WakeUpHelper;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.utils.Utils;

public class NotificationReceiverService extends FirebaseMessagingService {
	public static final String TAG = "NotificationReceiver", WEBHOOK = "webhook";

	public NotificationReceiverService() { }

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		if (remoteMessage.getData().size() > 0) {
			Log.e(TAG, "Message data payload: " + remoteMessage.getData());
			if (remoteMessage.getData().containsKey(WEBHOOK))
				setWebhook(remoteMessage.getData().get(WEBHOOK));
			if (remoteMessage.getData().containsKey(OperatorAction.ID))
				WakeUpHelper.sendFcmTriggeredWake(this, remoteMessage.getData());
		}
	}

	private void setWebhook(String webhookString) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(this).edit();
		editor.putString(WEBHOOK, webhookString);
		editor.apply();
	}
}
