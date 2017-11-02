package com.hover.tester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.ActionResult;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;
import com.hover.tester.utils.Utils;

public class TransactionReceiver extends BroadcastReceiver {
	public final static String TAG = "TransactionReceiver";
	public TransactionReceiver() { }

	@Override
	public void onReceive(Context context, Intent i) {
		Log.i(TAG, "Transaction received. Trans id: " + i.getLongExtra("_id", -1) + ", Action: " + i.getStringExtra(Utils.ACTION));
		ActionResult ar = ActionResult.getBySdkId((int) i.getLongExtra("_id", -1), context);
		if (ar != null) {
			ar.mStatus = ActionResult.STATUS_SUCCEEDED;
			ar.mTimeStamp = i.getLongExtra("response_timestamp", 0L);
			ar.mText = i.getStringExtra("response_message");
			ar.save(context);
		}
		notifyGatewayManager(context, i);
		openActivity(context, i);
	}

	private void notifyGatewayManager(Context c, Intent intent) {
		Intent i = new Intent(c, GatewayManagerService.class);
		i.putExtra(GatewayManagerService.CMD, GatewayManagerService.DONE);
		i.putExtra(OperatorAction.ID, intent.getIntExtra(OperatorAction.ID, -1));
		i.putExtra(StatusReport.STATUS, StatusReport.SUCCESS);
		i.putExtra(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE, intent.getStringExtra("response_message"));
		c.startService(i);
	}

	private void openActivity(Context c, Intent i) {
		i = new Intent(i);
		i.setClass(c, ActionDetailActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		i.putExtra(OperatorAction.ID, i.getIntExtra("action_id", -1));
		c.startActivity(i);
	}
}
