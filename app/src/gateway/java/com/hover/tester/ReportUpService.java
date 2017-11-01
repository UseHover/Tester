package com.hover.tester;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.hover.tester.database.Contract;
import com.hover.tester.network.VolleySingleton;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class ReportUpService extends IntentService {
	public final static String TAG = "ReportUpService";

	public ReportUpService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			if (intent.getIntExtra(Contract.StatusReportEntry.COLUMN_ENTRY_ID, -1) != -1) {
				StatusReport report = new StatusReport(intent.getIntExtra(Contract.StatusReportEntry.COLUMN_ENTRY_ID, -1), this);
				VolleySingleton.uploadJsonNow(this, Request.Method.POST, getString(R.string.base_url) + getString(R.string.hover_status_endpoint), report.getJson());
			}
		} catch (NullPointerException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
		}
	}
}
