package com.hover.tester;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class OperatorAction {
	public static final String TAG = "OperatorAction", NAME = "action_name", SLUG = "action_slug",
		STATUS = "action_status", TIMESTAMP = "action_timestamp",
			VARIABLE = "action_variable", VARIABLES = "action_variables";
	public static final int STATUS_UNTESTED = -1, STATUS_FAILED = 0, STATUS_SUCCEEDED = 1;

	public String mName, mSlug, mOpSlug;
	public HashMap<String, String> mVariables;
	public Long mLastRunTime = 0L;
	public int mStatus = STATUS_UNTESTED;

	public OperatorAction(JSONObject jsonAct, String opSlug) throws JSONException {
		mOpSlug = opSlug;
		mSlug = jsonAct.getString("slug");
		mName = jsonAct.getString("name");
		JSONArray variables = jsonAct.getJSONArray("variables");
		mVariables = new HashMap<>(variables.length());
		for (int v = 0; v < variables.length(); v++)
			mVariables.put(variables.getString(v), "");
	}

	public OperatorAction(String slug, String opSlug, Context c) throws JSONException {
		mOpSlug = opSlug;
		mSlug = slug;
		mName = Utils.getSharedPrefs(c).getString(getPrefix() + NAME, "");
		mStatus = Utils.getSharedPrefs(c).getInt(getPrefix() + STATUS, -1);
		mLastRunTime = Utils.getSharedPrefs(c).getLong(getPrefix() + TIMESTAMP, 0L);

		JSONArray variableNames = getVariableNames(c);
		mVariables = new HashMap<>(variableNames.length());
		for (int v = 0; v < variableNames.length(); v++)
			mVariables.put(getVariable(c, variableNames.getString(v)), "");
	}

	public void save(Context c) {
		SharedPreferences.Editor editor = Utils.getSharedPrefs(c).edit();
		editor.putString(getPrefix() + NAME, mName);
		editor.putString(getPrefix() + SLUG, mSlug);
		editor.putInt(getPrefix() + STATUS, mStatus);
		editor.putLong(getPrefix() + TIMESTAMP, mLastRunTime);
		for (HashMap.Entry<String, String> variable : mVariables.entrySet())
			saveVariable(c, variable.getKey(), variable.getValue());
		editor.commit();
	}

	public String getVariable(Context context, String name) {
		return Utils.getSharedPrefs(context).getString(getPrefix() + VARIABLE + name, "");
	}
	public void saveVariable(Context context, String name, String value){
		SharedPreferences.Editor editor = Utils.getSharedPrefs(context).edit();
		editor.putString(getPrefix() + VARIABLE + name, value);
		addVariableName(name, context, editor);
		editor.commit();
	}
	public JSONArray getVariableNames(Context context) {
		try {
			return new JSONArray(Utils.getSharedPrefs(context).getString(getPrefix() + VARIABLES, "[]"));
		} catch (Exception e) { return new JSONArray(); }
	}
	public void addVariableName(final String value, Context context, SharedPreferences.Editor editor) {
		try {
			JSONArray new_variables = getVariableNames(context);
			new_variables.put(value);
			editor.putString(VARIABLES, new_variables.toString());
		} catch (Exception e) {}
	}

	public boolean exists(String opName, String slug) {
		return false;
	}

	private String getPrefix() {
		return mOpSlug + "_" + mSlug + "_";
	}
}
