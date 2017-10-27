package com.hover.tester;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.OperatorAction;

public class WakeUpService extends Service {
	public final static String TAG = "WakeUpService";
	private final IBinder mBinder = new WakeUpService.LocalBinder();

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Log.e(TAG, "Service running");

		startTransaction();

		WakeUpReceiver.completeWakefulIntent(i);
		return START_STICKY;
	}

	private void startTransaction() {
		final Intent i = new Intent(this, ActionDetailActivity.class);
		i.putExtra(OperatorAction.ID, 1);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	@Override
	public IBinder onBind(Intent intent) { return mBinder; }
	public class LocalBinder extends Binder {
		public WakeUpService getService() { return WakeUpService.this; }
	}
}
