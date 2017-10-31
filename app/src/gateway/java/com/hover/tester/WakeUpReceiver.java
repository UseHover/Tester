package com.hover.tester;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class WakeUpReceiver extends WakefulBroadcastReceiver {
	public final static String TAG = "WakeUpReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Received broadcast");

		Intent i = new Intent(context, WakeUpService.class);
		i.putExtras(intent.getExtras());
		i.putExtra(WakeUpHelper.CMD, WakeUpHelper.START);
		startWakefulService(context, i);
	}
}