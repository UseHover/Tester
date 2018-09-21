package com.hover.tester.wake;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.hover.tester.actions.HoverAction;
import com.hover.tester.schedules.AlarmSchedulerService;

import java.util.Calendar;

public class WakeUpReceiver extends WakefulBroadcastReceiver {
	public final static String TAG = "WakeUpReceiver";
	private static int RESCHEDULE_ID = -1;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Received broadcast. Source: " + intent.getStringExtra(WakeUpHelper.SOURCE));

		if (!WakeUpHelper.alarmsLocked(context))
			wakeService(context, intent);
		else
			delayAlarm(context, intent);
	}

	private void wakeService(Context c, Intent intent) {
		Intent i = new Intent(c, WakeUpService.class);
		i.putExtras(intent.getExtras());
		i.putExtra(GatewayManagerService.CMD, GatewayManagerService.START);
		startWakefulService(c, i);

		if (i.hasExtra(AlarmSchedulerService.INTERVAL))
			scheduleNextAlarm(i, c);
	}

	private void scheduleNextAlarm(Intent i, Context c) {
		Intent intent = new Intent(c, AlarmSchedulerService.class);
		intent.putExtra(HoverAction.ID, i.getStringExtra(HoverAction.ID));
		c.startService(intent);
	}

	private void delayAlarm(Context c, Intent intent) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		wake.putExtras(intent.getExtras());
		WakeUpHelper.setExactAlarm(wake, getPlusFive(), RESCHEDULE_ID, c);
		RESCHEDULE_ID -= 1;
	}

	private long getPlusFive() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.MINUTE, 5);
		return cal.getTimeInMillis();
	}
}