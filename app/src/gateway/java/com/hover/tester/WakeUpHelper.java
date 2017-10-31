package com.hover.tester;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hover.tester.actions.OperatorAction;

import java.util.Calendar;
import java.util.Map;

public class WakeUpHelper {
	public final static String TAG = "WakeUpHelper", CMD = "cmd", START = "start", DONE = "done",
			SOURCE = "source", FCM = "fcm", TIMER = "timer";

	public static void sendFcmTriggeredWake(Context c, Map<String, String> data) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (entry.getKey().equals(OperatorAction.ID))
				wake.putExtra(entry.getKey(), (int) Integer.valueOf(entry.getValue()));
			else
				wake.putExtra(entry.getKey(), entry.getValue());
		}
		wake.putExtra(SOURCE, FCM);
		setAlarm(wake, now(), c);
	}

	public static Intent createScheduledIntent(Context c, int actionId) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		wake.putExtra(OperatorAction.ID, actionId);
		wake.putExtra(SOURCE, TIMER);
		return wake;
	}

	public static void setAlarm(Intent wake, long time, Context c) {
		AlarmManager alarm = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 19)
			alarm.setExact(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(c, 0, wake, PendingIntent.FLAG_UPDATE_CURRENT));
		else
			alarm.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(c, 0, wake, PendingIntent.FLAG_UPDATE_CURRENT));
	}

	private static long now() {
		Calendar when = Calendar.getInstance();
		when.setTimeInMillis(System.currentTimeMillis());
		when.add(Calendar.SECOND, 15);
		return when.getTimeInMillis();
	}
}
