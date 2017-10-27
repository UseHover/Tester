package com.hover.tester.services;

import android.content.Context;
import android.os.AsyncTask;

import com.hover.tester.KeyStoreHelper;

public class SaveServiceTask extends AsyncTask<OperatorService, Void, Void> {
	public final static String TAG = "SaveServiceTask";
	private Context mContext;
	private SaveFinishedListener mListener;
	private String mPin;

	public SaveServiceTask(String pin, SaveFinishedListener listner, Context c) {
		mContext = c.getApplicationContext();
		mListener = listner;
		mPin = pin;
	}

	protected Void doInBackground(OperatorService... services) {
		int count = services.length;
		for (int i = 0; i < count; i++) {
			services[i].setPin(KeyStoreHelper.encrypt(services[i].mId, mPin, mContext));
			services[i].save(mContext);
		}
		return null;
	}

	protected void onPostExecute(Void result) {
		mListener.onSaveCompleted();
	}
}
