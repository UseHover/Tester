package com.hover.tester;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

public class NotificationReceiverService extends FirebaseMessagingService {
	public static final String TAG = "NotificationReceiver";

	public NotificationReceiverService() { }

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// [START_EXCLUDE]
		// There are two types of messages data messages and notification messages. Data messages are handled
		// here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
		// traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
		// is in the foreground. When the app is in the background an automatically generated notification is displayed.
		// When the user taps on the notification they are returned to the app. Messages containing both notification
		// and data payloads are treated as notification messages. The Firebase console always sends notification
		// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
		// [END_EXCLUDE]

		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.e(TAG, "From: " + remoteMessage.getMessageType());

		WakeUpHelper.sendWakeIntent(this);

		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			Log.e(TAG, "Message data payload: " + remoteMessage.getData());


			if (/* Check if data needs to be processed by long running job */ true) {
				// For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
				scheduleJob();
			} else {
				// Handle message within 10 seconds
				handleNow();
			}

		}
	}

	/**
	 * Schedule a job using FirebaseJobDispatcher.
	 */
	private void scheduleJob() {
		// [START dispatch_job]
//		FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//		Job myJob = dispatcher.newJobBuilder()
//				.setService(MyJobService.class)
//				.setTag("my-job-tag")
//				.build();
//		dispatcher.schedule(myJob);
		// [END dispatch_job]
	}

	/**
	 * Handle time allotted to BroadcastReceivers.
	 */
	private void handleNow() {
		Log.d(TAG, "Short lived task is done.");
	}

	/**
	 * Create and show a simple notification containing the received FCM message.
	 *
	 * @param messageBody FCM message body received.
	 */
	private void sendNotification(String messageBody) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

//		String channelId = getString(R.string.default_notification_channel_id);
//		Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		NotificationCompat.Builder notificationBuilder =
//				new NotificationCompat.Builder(this, channelId)
//						.setSmallIcon(R.drawable.ic_stat_ic_notification)
//						.setContentTitle("FCM Message")
//						.setContentText(messageBody)
//						.setAutoCancel(true)
//						.setSound(defaultSoundUri)
//						.setContentIntent(pendingIntent);
//
//		NotificationManager notificationManager =
//				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//		notificationManager.notify(0, notificationBuilder.build());
	}
}
