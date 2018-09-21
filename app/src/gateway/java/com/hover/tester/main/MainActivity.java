package com.hover.tester.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hover.sdk.api.HoverHelper;
import com.hover.tester.R;
import com.hover.tester.actions.HoverAction;
import com.hover.tester.actions.SaveActionTask;
import com.hover.tester.notifications.DeviceInfoService;
import com.hover.tester.wake.WakeUpHelper;


import io.fabric.sdk.android.Fabric;

public class MainActivity extends AbstractMainActivity {
	public final static String TAG = "MainActivity";
	public static final int INTEGRATE_REQUEST = 111;
	private HoverAction pendingAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WakeUpHelper.releaseAlarms(this);
		if (getIntent().hasExtra(DeviceInfoService.UPLOAD_DEVICE_INFO))
			startService(new Intent(this, DeviceInfoService.class));
	}

	protected void initialize() {
		Fabric.with(this, new Crashlytics());
		String token = FirebaseInstanceId.getInstance().getToken();
		if (token != null && !token.isEmpty())
			Log.i(TAG, FirebaseInstanceId.getInstance().getToken());
	}

	@Override
	public void addAction(HoverAction action) {
		pendingAction = action;
		DialogFragment newFragment = AddIntegrationDialogFragment.newInstance(AddIntegrationDialogFragment.ENTER_PIN_STEP, pendingAction.mName);
		newFragment.show(getSupportFragmentManager(), AddIntegrationDialogFragment.TAG);
	}

	@Override
	public void savePin(final String pin) {
		Snackbar.make(findViewById(R.id.nest_container),
				"Saving Action: " + pendingAction.mName + ", one moment",
				Snackbar.LENGTH_LONG).show();
		((ContentLoadingProgressBar) findViewById(R.id.loading_progress)).show();
		new SaveActionTask(pin, mFrag, this).execute(pendingAction);
//		Decrypt: KeyStoreHelper.decrypt(operatorService.getPin(), OpService.mId, c);
	}

	public static boolean meetsAllRequirements(Context c) {
		return meetsAppRequirements(c) && hasSmsPerm(c) && HoverHelper.isAccessibilityEnabled(c) && HoverHelper.isOverlayEnabled(c);
	}

	public static boolean meetsAppRequirements(Context c) {
		return hasWakeLockPerm(c) && usableAndroidVersion();
	}
	private static boolean hasWakeLockPerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
	}
	private static boolean usableAndroidVersion() {
		return Build.VERSION.SDK_INT >= 18 && Build.VERSION.SDK_INT < 26;
	}
}
