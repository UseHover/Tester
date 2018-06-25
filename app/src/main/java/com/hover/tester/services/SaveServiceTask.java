package com.hover.tester.services;

import android.content.Context;
import android.os.AsyncTask;

import com.hover.tester.gateway.KeyStoreHelper;

public class SaveServiceTask extends AsyncTask<OperatorService, Void, Void> {
	public final static String TAG = "SaveServiceTask";
	private Context mContext;
	OperatorService mService;
	private SaveFinishedListener mListener;
	private String mPin = null;

	public SaveServiceTask(SaveFinishedListener listner, Context c) {
		mContext = c.getApplicationContext();
		mListener = listner;
	}

	public SaveServiceTask(String pin, SaveFinishedListener listner, Context c) {
		mContext = c.getApplicationContext();
		mListener = listner;
		mPin = pin;
	}

	protected Void doInBackground(OperatorService... services) {
		mService = services[0];
		int count = services.length;
		for (int i = 0; i < count; i++) {
			if (mPin != null)
				services[i].setPin(KeyStoreHelper.encrypt(services[i].mId, mPin, mContext));
			services[i].save(mContext);
		}
		return null;
	}

	protected void onPostExecute(Void result) {
		if (mPin == null)
			mService.saveAllActions(mContext);
		mListener.onSaveCompleted();
	}
}
