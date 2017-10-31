package com.hover.tester;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseDeviceIDService extends FirebaseInstanceIdService {
	public static final String TAG = "FirebaseDeviceIDService";

	@Override
	public void onTokenRefresh() {
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.e(TAG, "Refreshed token: " + refreshedToken);

//		sendRegistrationToServer(refreshedToken);
	}
}
