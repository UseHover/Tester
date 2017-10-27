package com.hover.tester;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class WakeUpReceiver extends WakefulBroadcastReceiver {
	public final static String TAG = "WakeUpReceiver";
	public static PowerManager.WakeLock mWakeLock;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received broadcast");
		boolean isReady;
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 20)
			isReady = powerManager.isInteractive();
		else
			isReady = powerManager.isScreenOn();

		if (!isReady) {
			mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
			mWakeLock.acquire();

			KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			KeyguardManager.KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock(TAG);
			keyguardLock.disableKeyguard();

			Intent service = new Intent(context, WakeUpService.class);
			startWakefulService(context, service);
		}
	}
}