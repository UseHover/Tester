package com.hover.tester.network;

import android.app.IntentService;
import android.content.Intent;

public abstract class NetworkService extends IntentService {
	public final static String TAG = "NetworkService";
	protected NetworkOps netOps;

	public NetworkService(String tag) {
		super(tag);
		netOps = new NetworkOps(this);
	}

}