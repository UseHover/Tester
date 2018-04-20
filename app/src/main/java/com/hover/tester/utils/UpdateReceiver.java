package com.hover.tester.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hover.tester.main.MainActivity;

public class UpdateReceiver extends BroadcastReceiver {
	public final static String TAG = "UpdateReceiver", ACTION = "CONFIG_UPDATED";
	public UpdateReceiver() { }

	@Override
	public void onReceive(Context context, Intent i) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(ACTION);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}