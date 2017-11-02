package com.hover.tester;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.hover.tester.database.Contract;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.network.NetworkService;
import com.hover.tester.network.VolleySingleton;
import com.hover.tester.notifications.NotificationReceiverService;
import com.hover.tester.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class ReportUpService extends NetworkService {
	public final static String TAG = "ReportUpService";

	public ReportUpService() {
		super(TAG);
		netOps = new NetworkOps(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.getIntExtra(Contract.StatusReportEntry.COLUMN_ENTRY_ID, -1) != -1) {
			try {
				JSONObject report = new StatusReport(intent.getIntExtra(Contract.StatusReportEntry.COLUMN_ENTRY_ID, -1), this).getJson();
				Log.i(TAG, "Uploading! " + report);
				uploadToHover(report);
				uploadToWebhook(report);
			} catch (JSONException e) { Crashlytics.logException(e); }
		}
	}

	private void uploadToHover(JSONObject report) {
		try {
			String webhook = "https://hooks.slack.com/services/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";
			String hoverResponse = VolleySingleton.uploadJsonNowAbsolute(this, Request.Method.POST, webhook, createSlackJson(report));
//				VolleySingleton.uploadJsonNow(this, Request.Method.POST, getString(R.string.hover_status_endpoint), report);
		} catch (NullPointerException | JSONException | InterruptedException | TimeoutException | ExecutionException e) {
			Log.d(TAG, "Failed to upload to hover", e);
			Crashlytics.logException(e);
		}
	}

	private void uploadToWebhook(JSONObject report) {
		try {
			String webhook = Utils.getSharedPrefs(this).getString(NotificationReceiverService.WEBHOOK, null);
			if (webhook != null) {
				String hoverResponse = VolleySingleton.uploadJsonNowAbsolute(this, Request.Method.POST, webhook, report);
				Log.d(TAG, "Webhook upload response: " + hoverResponse);
			}
		} catch (NullPointerException | InterruptedException | TimeoutException | ExecutionException e) {
			Log.e(TAG, "Failed to upload to webhook", e);
			Crashlytics.logException(e);
		}
	}

	private JSONObject createSlackJson(JSONObject reportJson) throws JSONException {
		JSONObject root = new JSONObject();
		JSONArray attachments = new JSONArray();
		JSONObject attachOne = new JSONObject();
		attachOne.put("fallback", "Status Report from Hover Gateway App");
		attachOne.put("title", "Status Report from Hover Gateway App");
		attachOne.put("color", reportJson.get("status").equals("success") ? "#008000" : "#CD2328");
		JSONArray fields = new JSONArray();

		Iterator<String> iter = reportJson.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			JSONObject field = new JSONObject();
			field.put("title", key);
			field.put("value", reportJson.get(key));
			field.put("short", (!key.equals("confirmation_message") && !key.equals("final_session_message")));
			fields.put(field);
		}

		attachOne.put("fields", fields);
		attachments.put(attachOne);
		root.put("attachments", attachments);
		return root;
	}
}
