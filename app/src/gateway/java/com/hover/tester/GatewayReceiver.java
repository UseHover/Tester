package com.hover.tester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hover.tester.actions.HoverAction;
import com.hover.tester.database.Contract;
import com.hover.tester.report.StatusReport;
import com.hover.tester.wake.GatewayManagerService;

import org.json.JSONObject;


public class GatewayReceiver extends BroadcastReceiver {
	public final static String TAG = "GatewayReceiver", ACTION = "TRANSACTION_UPDATED";
	public GatewayReceiver() { }

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, GatewayManagerService.class);
		i.putExtra(GatewayManagerService.CMD, intent.getStringExtra(GatewayManagerService.CMD));
		i.putExtra(HoverAction.ID, intent.getStringExtra(HoverAction.ID));
		i.putExtra(StatusReport.STATUS, intent.getStringExtra(StatusReport.STATUS));
		i.putExtra(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE, intent.getStringExtra("response_message"));
		i.putExtra(StatusReport.TRANSACTION, TransactionReceiver.convertTinfoToJsonString(intent.getExtras(), new JSONObject()));
		context.startService(i);
	}
}
