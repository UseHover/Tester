package com.hover.tester;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.hover.tester.actions.OperatorAction;
import com.hover.tester.utils.Utils;

import java.util.Calendar;
import java.util.Map;

public class WakeUpHelper {
	public final static String TAG = "WakeUpHelper",
			SOURCE = "source", FCM = "fcm", TIMER = "timer",
			ALARM_IN_USE = "alarm_in_use";

	public static void sendFcmTriggeredWake(Context c, Map<String, String> data) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (entry.getKey().equals(OperatorAction.ID))
				wake.putExtra(entry.getKey(), (int) Integer.valueOf(entry.getValue()));
			else
				wake.putExtra(entry.getKey(), entry.getValue());
		}
		wake.putExtra(SOURCE, FCM);
		setExactAlarm(wake, now(), 0, c);
	}

	public static Intent createScheduledIntent(Context c, int actionId) {
		Intent wake = new Intent(c, WakeUpReceiver.class);
		wake.putExtra(OperatorAction.ID, actionId);
		wake.putExtra(SOURCE, TIMER);
		return wake;
	}

	public static void setExactAlarm(Intent wake, long time, int id, Context c) {
		AlarmManager alarm = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 19)
			alarm.setExact(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(c, id, wake, PendingIntent.FLAG_UPDATE_CURRENT));
		else
			alarm.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(c, id, wake, PendingIntent.FLAG_UPDATE_CURRENT));
	}

	private static long now() {
		Calendar when = Calendar.getInstance();
		when.setTimeInMillis(System.currentTimeMillis());
		when.add(Calendar.SECOND, 5);
		return when.getTimeInMillis();
	}

	public static void lockAlarms(Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putBoolean(ALARM_IN_USE, true);
		editor.commit();
	}
	public static boolean alarmsLocked(Context c) {
		return Utils.getSharedPrefs(c).getBoolean(ALARM_IN_USE, false);
	}
	public static void releaseAlarms(Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putBoolean(ALARM_IN_USE, false);
		editor.commit();
	}
}
