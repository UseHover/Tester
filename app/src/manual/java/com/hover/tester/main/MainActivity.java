package com.hover.tester.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.hover.tester.R;
import com.hover.tester.network.HoverIntegratonListService;
import com.hover.tester.services.OperatorService;

public class MainActivity extends AbstractMainActivity {
	public final static String TAG = "MainActivity";

	public void onIntegrateSuccess(Intent data) {
		OperatorService opService = new OperatorService(data, this);
		HoverIntegratonListService.getActionsList(opService.mId, this);
		Snackbar.make(findViewById(R.id.nest_container), "Saving Service: " + opService.mName + ", one moment", Snackbar.LENGTH_LONG).show();
		opService.save(this);
		opService.saveAllActions(this);
	}

	@TargetApi(27)
	private void callUSSDAPI() {
//		TelephonyManager tm = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
//
//		if (Build.VERSION.SDK_INT >= 26 && hasPhonePerm(this)) {
//			final Handler h = new Handler();
//			try {
//				tm.sendUssdRequest("*149*01*4*5#", new TelephonyManager.UssdResponseCallback() {
//					@Override
//					public void onReceiveUssdResponse(final TelephonyManager telephonyManager, String request, CharSequence response) {
//						super.onReceiveUssdResponse(telephonyManager, request, response);
//						Log.e(TAG, "Success");
//						Log.e(TAG, "Got response: " + response + " for request: " + request);
////						if (Build.VERSION.SDK_INT >= 26)
////							telephonyManager.sendUssdRequest("4", this, h);
//					}
//
//					@Override
//					public void onReceiveUssdResponseFailed(final TelephonyManager telephonyManager, String request, int failureCode) {
//						super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
//						Log.e(TAG, "Fail");
//						Log.e(TAG, "Request: " + request + " failed: " + failureCode);
//					}
//				}, h);
//			} catch (SecurityException e) { Log.e(TAG, "Security Exception", e); }
//		}
	}
}

