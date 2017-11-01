package com.hover.tester.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
	public static final String TAG = "Utils", ACTION = "action";

	public static final String[] parsableValues = new String[]{ "code", "currency", "balance", "amount", "who" };

	public static SharedPreferences getSharedPrefs(Context context) {
		return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
	}

	public static long now() {
		return new Date().getTime();
	}
	public static String convertTimestampToISO(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date(timestamp)); // *1000L ?
	}

	public static String shortDateFormatTimestamp(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date(timestamp)); // *1000L ?
	}

	public static int[] getIdListFromString(String fromDb) {
		if (fromDb == null) return new int[0];
		String[] s = fromDb.split(",");
		int[] numbers = new int[s.length];
		for (int curr = 0; curr < s.length; curr++)
			numbers[curr] = Integer.parseInt(s[curr]);
		return numbers;
	}

	public static String[] getListFromString(String fromDb) {
		if (fromDb == null) return null;
		return fromDb.split(",");
	}

	public static void storeParsedValues(Bundle extras, Context c) {
//		String operatorName = getOperator(c);
//		String actionName = extras.getString(ACTION);
//		String prefix = operatorName + "_" + actionName + "_";
//
//		for (int i = 0; i < parsableValues.length; i++) {
//			String val = extras.getString(parsableValues[i]);
//			SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
//			editor.putString(prefix + parsableValues[i], val);
//			editor.commit();
//			Log.d(TAG, "Stored " + prefix + parsableValues[i] + ": " + val);
//		}

	}
}