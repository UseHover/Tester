package com.hover.tester;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.database.Contract;

import java.util.Map;

public class GatewayManagerService extends Service {
	public final static String TAG = "GatewayManagerService", CMD = "cmd", START = "start", UPDATE = "update", DONE = "done";

	public static final int TIMER_LENGTH = 180000;
	private CountDownTimer cdt = null;
	private StatusReport mReport;

	private final IBinder mBinder = new LocalBinder();
	public GatewayManagerService() { }

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Log.e(TAG, "Gateway manager CMD: " + i.getStringExtra(GatewayManagerService.CMD));
		if (i.hasExtra(GatewayManagerService.CMD) && i.getStringExtra(GatewayManagerService.CMD).equals(GatewayManagerService.START))
			start(i);
		else if (mReport != null && i.hasExtra(GatewayManagerService.CMD) && i.getStringExtra(GatewayManagerService.CMD).equals(GatewayManagerService.UPDATE)) {
			mReport.update(i.getIntExtra(Contract.StatusReportEntry.COLUMN_TRANSACTION_ID, -1),
					i.getStringExtra(Contract.StatusReportEntry.COLUMN_FINAL_SESSION_MSG), i.getStringExtra(Contract.StatusReportEntry.COLUMN_FAILURE_MESSAGE));
		} else if (mReport != null)
			end(i, false);
		else
			stopSelf();
		return START_STICKY;
	}

	private void start(Intent i) {
		WakeUpHelper.lockAlarms(this);
		startTimer();
		mReport = new StatusReport(i).save(this);
		startTransaction(i);
	}

	private void end(Intent i, boolean timeout) {
		if (!timeout) {
			cancelTimer();
			updateReport(i);
		} else
			updateReport(StatusReport.FAILURE, null, "Timeout Reached");
		WakeUpHelper.releaseAlarms(this);
		releaseWakeLock();
		stopSelf();
	}

	private void startTransaction(Intent intent) {
		final Intent i = new Intent(this, ActionDetailActivity.class);
		i.putExtras(intent.getExtras());
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	private void updateReport(Intent i) {
		updateReport(i.getIntExtra(StatusReport.STATUS, StatusReport.FAILURE),
				i.getStringExtra(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE), i.getStringExtra(Contract.StatusReportEntry.COLUMN_FAILURE_MESSAGE));
	}
	private void updateReport(int status, String confirmMsg, String failMsg) {
		mReport.update(status, confirmMsg, failMsg, this);
		mReport.upload(this);
	}

	void releaseWakeLock() {
		Intent i = new Intent(this, WakeUpService.class);
		i.putExtra(GatewayManagerService.CMD, GatewayManagerService.DONE);
		startService(i);
	}

	public void startTimer() {
		Log.d(TAG, "starting timer");
		cancelTimer();
		cdt = new CountDownTimer(TIMER_LENGTH, TIMER_LENGTH/6) {
			public void onTick(long millisUntilFinished) { Log.d(TAG, "Time left: " + millisUntilFinished); }
			public void onFinish() { Log.e(TAG, "Expired. Notifying"); end(null, true); }
		}.start();
	}

	public void cancelTimer() {
		Log.d(TAG, "Canceling");
		if (cdt != null) {
			cdt.cancel();
			cdt = null;
		}
	}

	public class LocalBinder extends Binder {
		public GatewayManagerService getService() { return GatewayManagerService.this; }
	}
	@Override
	public IBinder onBind(Intent intent) { return mBinder; }
}
