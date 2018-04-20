package com.hover.tester.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseDeviceIDService extends FirebaseInstanceIdService {
	public static final String TAG = "FirebaseDeviceIDService";

	@Override
	public void onTokenRefresh() {
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.i(TAG, "Refreshed token: " + refreshedToken);
		FirebaseMessaging.getInstance().subscribeToTopic("global");
//		sendRegistrationToServer(refreshedToken);
	}
}
