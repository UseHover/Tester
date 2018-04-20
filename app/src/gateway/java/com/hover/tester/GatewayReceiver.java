package com.hover.tester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;
import com.hover.tester.report.StatusReport;
import com.hover.tester.wake.GatewayManagerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GatewayReceiver extends BroadcastReceiver {
	public final static String TAG = "GatewayReceiver", ACTION = "TRANSACTION_UPDATED";
	public GatewayReceiver() { }

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, GatewayManagerService.class);
		i.putExtra(GatewayManagerService.CMD, intent.getStringExtra(GatewayManagerService.CMD));
		i.putExtra(OperatorAction.ID, intent.getIntExtra(OperatorAction.ID, -1));
		i.putExtra(StatusReport.STATUS, intent.getStringExtra(StatusReport.STATUS));
		i.putExtra(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE, intent.getStringExtra("response_message"));
		i.putExtra(StatusReport.TRANSACTION, TransactionReceiver.convertTinfoToJsonString(intent.getExtras(), new JSONObject()));
		context.startService(i);
	}
}
