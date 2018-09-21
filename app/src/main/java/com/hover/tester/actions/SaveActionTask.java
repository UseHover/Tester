package com.hover.tester.actions;

import android.content.Context;
import android.os.AsyncTask;

import com.hover.tester.gateway.KeyStoreHelper;

public class SaveActionTask extends AsyncTask<HoverAction, Void, Void> {
	public final static String TAG = "SaveServiceTask";
	private Context mContext;
	private SaveFinishedListener mListener;
	private String mPin = null;

	public SaveActionTask(String pin, SaveFinishedListener listener, Context c) {
		mContext = c.getApplicationContext();
		mListener = listener;
		mPin = pin;
	}

	protected Void doInBackground(HoverAction... actions) {
		int count = actions.length;
		for (int i = 0; i < count; i++) {
//			actions[i].setPin(KeyStoreHelper.encrypt(actions[i].mId, mPin, mContext));
			actions[i].save(mContext);
		}
		return null;
	}

	protected void onPostExecute(Void result) {
//		if (mPin == null)
//			mService.saveAllActions(mContext);
		mListener.onSaveCompleted();
	}
}
