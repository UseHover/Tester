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
		Log.e(TAG, "Transaction received. Op: " + i.getStringExtra(Utils.OPERATOR) + ", Action: " + i.getStringExtra(Utils.ACTION));
		OperatorAction.savePositiveResult(i, context);
	}
}
