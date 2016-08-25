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
		Log.d(TAG, "Op: " + i.getStringExtra(Utils.OPERATOR));
		Utils.saveActionResult(i.getStringExtra(Utils.OPERATOR), i.getStringExtra(Utils.ACTION), true, context);

		Intent intent = new Intent();
		intent.putExtra("sdk_action", i.getStringExtra(Utils.ACTION));
		intent.setClass(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
}
