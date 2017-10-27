package com.hover.tester;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class WakeUpHelper {

	public static void sendWakeIntent(Context c) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		AlarmManager alarm = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 19)
			alarm.setExact(AlarmManager.RTC_WAKEUP, now(), PendingIntent.getBroadcast(c, 0, wake, 0));
		else
			alarm.set(AlarmManager.RTC_WAKEUP, now(), PendingIntent.getBroadcast(c, 0, wake, 0));
	}

	private static long now() {
		Calendar when = Calendar.getInstance();
		when.setTimeInMillis(System.currentTimeMillis());
		when.add(Calendar.MINUTE, 1);
		return when.getTimeInMillis();
	}
}
