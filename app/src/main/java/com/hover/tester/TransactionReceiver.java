package com.hover.tester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TransactionReceiver extends BroadcastReceiver {
	public final static String TAG = "TransactionReceiver";
	public TransactionReceiver() { }

	@Override
	public void onReceive(Context context, Intent i) {
		Log.e(TAG, "Transaction received. trans id: " + i.getLongExtra("_id", -1) + ", Action: " + i.getStringExtra(Utils.ACTION));
		ActionResult ar = ActionResult.getBySdkId((int) i.getLongExtra("_id", -1), context);
		if (ar != null) {
			ar.mStatus = ActionResult.STATUS_SUCCEEDED;
			ar.mTimeStamp = i.getLongExtra("response_timestamp", 0L);
			ar.mText = i.getStringExtra("response_message");
			ar.save(context);
		}
	}
}
