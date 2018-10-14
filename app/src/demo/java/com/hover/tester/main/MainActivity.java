package com.hover.tester.main;

import android.annotation.TargetApi;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.hover.tester.R;
import com.hover.tester.utils.Utils;


public class MainActivity extends AbstractMainActivity {
	public final static String TAG = "MainActivity";

	@Override
	public void savePin(final String pin) { }

	public void grantSystemPermissions(View view) {
//		if (!hasPhonePerm(this) && mFrag != null)
//			requestPhonePerm();
//		if (!hasAdvancedPerms(this))
			requestAdvancedPerms();
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

