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

	protected void setUpView() {
		super.setUpView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		final MenuItem menuItem = menu.findItem(R.id.debug_mode);
		CheckBox checkBox = (CheckBox) menuItem.getActionView();
		checkBox.setText(getString(R.string.debug_mode));
		checkBox.setChecked(Utils.isInDebugMode(this));
		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onOptionsItemSelected(menuItem);
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.debug_mode) {
			Utils.setDebugMode(!Utils.isInDebugMode(this), this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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

