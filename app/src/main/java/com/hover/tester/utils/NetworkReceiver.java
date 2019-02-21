package com.hover.tester.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.hover.tester.main.MainActivity;
import com.hover.tester.network.NetworkOps;

import static android.net.ConnectivityManager.EXTRA_NO_CONNECTIVITY;

public class NetworkReceiver extends BroadcastReceiver {
	public static final String TAG = "NetworkReceiver", ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public NetworkReceiver() { }

	@Override
	public void onReceive(Context c, Intent intent) {
		if (NetworkOps.isConnected(c)) {
			Intent i = new Intent(c, MainActivity.class);
			i.setAction(ACTION);
			i.putExtra(EXTRA_NO_CONNECTIVITY, intent.getBooleanExtra(EXTRA_NO_CONNECTIVITY, false));
			c.startActivity(i);
		}
	}

	public static IntentFilter buildIntentFilter() {
		IntentFilter filter = new IntentFilter(ACTION);
		return filter;
	}
}
