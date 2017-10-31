package com.hover.tester.notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hover.tester.WakeUpHelper;

public class NotificationReceiverService extends FirebaseMessagingService {
	public static final String TAG = "NotificationReceiver";

	public NotificationReceiverService() { }

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		if (remoteMessage.getData().size() > 0) {
			Log.e(TAG, "Message data payload: " + remoteMessage.getData());
			WakeUpHelper.sendFcmTriggeredWake(this, remoteMessage.getData());
		}
	}
}
