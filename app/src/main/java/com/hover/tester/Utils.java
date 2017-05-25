package com.hover.tester;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
	public static final String TAG = "Utils", OPERATOR = "operator_slug", ACTION = "action", ACTIVE = "active",
						COUNTRY = "country", CURRENCY = "currency", SERVICE = "service_id",
						AMOUNT = "amount", PHONE = "phone", MERCHANT = "merchant",
						PAYBILL = "paybill", PAYBILL_ACCT = "paybill_acct",
						RECIPIENT_NRC = "recipient_nrc", WITHDRAWAL_CODE = "withdrawal_code";

	public static final String[] parsableValues = new String[]{ "code", "currency", "balance",
			"amount", "who" };

	public static SharedPreferences getSharedPrefs(Context context) {
		return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
	}

	public static boolean hasActionResult(Integer serviceId, String actionName, Context c) {
		return Utils.getSharedPrefs(c).contains(serviceId + actionName + "_result");
	}
	public static void saveActionResult(Integer serviceId, String actionName, boolean value, Context c) {
		Log.d(TAG, "saveActionResult: " + serviceId + actionName + "_time");
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putBoolean(serviceId + actionName + "_result", value);
		editor.putLong(serviceId + actionName + "_time", now());
		editor.commit();
	}
	public static boolean actionResultPositive(Integer serviceId, String actionName, Context c) {
		return Utils.getSharedPrefs(c).getBoolean(serviceId + actionName + "_result", false);
	}
	public static String getActionResultTime(Integer serviceId, String actionName, Context c) {
		return shortDateFormatTimestamp(Utils.getSharedPrefs(c).getLong(serviceId + actionName + "_time", 0));
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

	public static void setActive(boolean value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putBoolean(ACTIVE, value);
		editor.commit();
	}
	public static Boolean isActive(Context c) { return Utils.getSharedPrefs(c).getBoolean(ACTIVE, false); }



	public static void setAmount(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(AMOUNT, value);
		editor.commit();
	}
	public static String getAmount(Context c) { return Utils.getSharedPrefs(c).getString(AMOUNT, ""); }

	public static void setPhone(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(PHONE, value);
		editor.commit();
	}
	public static String getPhone(Context c) { return Utils.getSharedPrefs(c).getString(PHONE, ""); }

	public static void setMerchant(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(MERCHANT, value);
		editor.commit();
	}
	public static String getMerchant(Context c) { return Utils.getSharedPrefs(c).getString(MERCHANT, ""); }

	public static void setPaybill(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(PAYBILL, value);
		editor.commit();
	}
	public static String getPaybill(Context c) { return Utils.getSharedPrefs(c).getString(PAYBILL, ""); }

	public static void setPaybillAcct(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(PAYBILL_ACCT, value);
		editor.commit();
	}
	public static String getPaybillAcct(Context c) { return Utils.getSharedPrefs(c).getString(PAYBILL_ACCT, ""); }

	public static void setRecipientNRC(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(RECIPIENT_NRC, value);
		editor.commit();
	}
	public static String getRecipientNRC(Context c) { return Utils.getSharedPrefs(c).getString(RECIPIENT_NRC, ""); }

	public static void setWithdrawalCode(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(WITHDRAWAL_CODE, value);
		editor.commit();
	}
	public static String getWithdrawalCode(Context c) { return Utils.getSharedPrefs(c).getString(WITHDRAWAL_CODE, ""); }

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