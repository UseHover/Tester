package com.hover.tester.wake;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hover.tester.schedules.Scheduler;
import com.hover.tester.utils.Utils;

import java.util.Calendar;

public class WakeUpHelper {
	public final static String TAG = "WakeUpHelper",
			SOURCE = "source", FCM = "fcm", TIMER = "timer",
			ALARM_IN_USE = "alarm_in_use";

	public static void setExactAlarm(Intent wake, long time, int id, Context c) {
		AlarmManager alarm = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= 19)
			alarm.setExact(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(c, id, wake, PendingIntent.FLAG_UPDATE_CURRENT));
		else
			alarm.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(c, id, wake, PendingIntent.FLAG_UPDATE_CURRENT));
	}

	public static long now() {
		Calendar when = Calendar.getInstance();
		when.setTimeInMillis(System.currentTimeMillis());
		when.add(Calendar.SECOND, 5);
		return when.getTimeInMillis();
	}
	public static long getScheduleTime(Scheduler s) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		if (s.getType() == Scheduler.WEEKLY) {
			cal.add(Calendar.WEEK_OF_MONTH, 1);
			cal.set(Calendar.DAY_OF_WEEK, s.getDay());
		} else
			cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, s.getHour());
		cal.set(Calendar.MINUTE, s.getMin());
		return cal.getTimeInMillis();
	}
	public static long getScheduleTime(int hourlyPos, int numHourly) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
//		cal.add(Calendar.SECOND, 10);
		cal.add(Calendar.HOUR, 1);
		cal.set(Calendar.MINUTE, hourlyPos * 60/numHourly);
		return cal.getTimeInMillis();
	}
	public static long getPlusHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.HOUR, 1);
		return cal.getTimeInMillis();
	}
	public static long getPlusTen() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.MINUTE, 10);
		return cal.getTimeInMillis();
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
