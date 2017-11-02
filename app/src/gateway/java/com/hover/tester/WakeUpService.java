package com.hover.tester;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class WakeUpService extends Service {
	public final static String TAG = "WakeUpService";
	public static PowerManager.WakeLock mWakeLock;
	private final IBinder mBinder = new WakeUpService.LocalBinder();

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Log.i(TAG, "Service running. CMD: " + i.getStringExtra(GatewayManagerService.CMD));

		if (i.hasExtra(GatewayManagerService.CMD) && i.getStringExtra(GatewayManagerService.CMD).equals(GatewayManagerService.DONE))
			releaseWakeLock();
		else {
			getWakeLock();
			startGatewayManager(i);
			WakeUpReceiver.completeWakefulIntent(i);
		}
		return START_NOT_STICKY;
	}

	private void startGatewayManager(Intent intent) {
		final Intent i = new Intent(this, GatewayManagerService.class);
		i.putExtras(intent.getExtras());
		startService(i);
	}

	private void getWakeLock() {
		boolean isReady;
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 20)
			isReady = powerManager.isInteractive();
		else
			isReady = powerManager.isScreenOn();

		if (!isReady) {
			disableKeyguard();
			mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
			mWakeLock.acquire();
		}
	}

	private void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
		stopSelf();
	}

	private void disableKeyguard() {
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock(TAG);
		keyguardLock.disableKeyguard();
	}

	@Override
	public IBinder onBind(Intent intent) { return mBinder; }
	public class LocalBinder extends Binder {
		public WakeUpService getService() { return WakeUpService.this; }
	}
}
