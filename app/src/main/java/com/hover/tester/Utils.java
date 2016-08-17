package com.hover.tester;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
	public static final String TAG = "Utils", OPERATOR = "operator_slug", ACTION = "action", ACTIVE = "active",
						COUNTRY = "country", CURRENCY = "currency",
						AMOUNT = "amount", PHONE = "phone", MERCHANT = "merchant",
						PAYBILL = "paybill", PAYBILL_ACCT = "paybill_acct";

	public static SharedPreferences getSharedPrefs(Context context) {
		return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
	}

	public static boolean hasActionResult(String opSlug, String actionName, Context c) {
		return Utils.getSharedPrefs(c).contains(opSlug + actionName + "_result");
	}
	public static void saveActionResult(String opSlug, String actionName, boolean value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putBoolean(opSlug + actionName + "_result", value);
		editor.putLong(opSlug + actionName + "_time", now());
		editor.commit();
	}
	public static boolean actionResultPositive(String opSlug, String actionName, Context c) {
		return Utils.getSharedPrefs(c).getBoolean(opSlug + actionName + "_result", false);
	}
	public static String getActionResultTime(String opSlug, String actionName, Context c) {
		return convertTimestampToISO(Utils.getSharedPrefs(c).getLong(opSlug + actionName + "_time", 0));
	}

	public static long now() {
		return new Date().getTime();
	}
	public static String convertTimestampToISO(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date(timestamp)); // *1000L ?
	}

	public static void setActive(boolean value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putBoolean(ACTIVE, value);
		editor.commit();
	}
	public static Boolean isActive(Context c) { return Utils.getSharedPrefs(c).getBoolean(ACTIVE, false); }

	public static void setOperator(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(OPERATOR, value);
		editor.commit();
	}
	public static String getOperator(Context c) { return Utils.getSharedPrefs(c).getString(OPERATOR, ""); }

	public static void setCountry(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(COUNTRY, value);
		editor.commit();
	}
	public static String getCountry(Context c) { return Utils.getSharedPrefs(c).getString(COUNTRY, ""); }

	public static void setCurrency(String value, Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(CURRENCY, value);
		editor.commit();
	}
	public static String getCurrency(Context c) { return Utils.getSharedPrefs(c).getString(CURRENCY, ""); }

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
}
