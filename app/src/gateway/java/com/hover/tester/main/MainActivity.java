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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hover.sdk.utils.HoverHelper;
import com.hover.tester.R;
import com.hover.tester.notifications.DeviceInfoService;
import com.hover.tester.wake.WakeUpHelper;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.network.HoverIntegratonListService;
import com.hover.tester.services.OperatorService;
import com.hover.tester.services.SaveServiceTask;

import org.json.JSONException;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AbstractMainActivity implements GatewayIntegrationInterface {
	public final static String TAG = "MainActivity";
	private OperatorService pendingOpService;
	public static final int INTEGRATE_REQUEST = 111;

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

	public void onIntegrateSuccess(Intent data) {
		pendingOpService = new OperatorService(data, this);
		DialogFragment newFragment = AddIntegrationDialogFragment.newInstance(AddIntegrationDialogFragment.ENTER_PIN_STEP, -1, pendingOpService.mName);
		newFragment.show(getSupportFragmentManager(), AddIntegrationDialogFragment.TAG);
	}

	@Override
	public void savePin(final String pin) {
		Snackbar.make(findViewById(R.id.nest_container), "Saving Service " + pendingOpService.mName, Snackbar.LENGTH_LONG).show();
		((ContentLoadingProgressBar) findViewById(R.id.loading_progress)).show();
		new SaveServiceTask(pin, mFrag, this).execute(pendingOpService);
		pickAction(pendingOpService.mId);
//		Decrypt: KeyStoreHelper.decrypt(operatorService.getPin(), OpService.mId, c);
	}

	public void pickAction(View v) {
		pickAction((int) ((ViewGroup) v.getParent().getParent()).findViewById(R.id.action_list).getTag());
	}
	@Override
	public void pickAction(int serviceId) {
		DialogFragment newFragment = AddIntegrationDialogFragment.newInstance(AddIntegrationDialogFragment.CHOOSE_ACTION_STEP, serviceId, null);
		newFragment.show(getSupportFragmentManager(), AddIntegrationDialogFragment.TAG);
	}
	@Override
	public void addAction(int serviceId, int actionIdx) {
		try {
			OperatorAction newAction = new OperatorAction(HoverIntegratonListService.getAction(serviceId, actionIdx, this), serviceId);
			newAction.save(this);
			Snackbar.make(findViewById(R.id.nest_container), "Saving Action: " + newAction.mName + ", one moment", Snackbar.LENGTH_LONG).show();
		} catch (JSONException e) {
			Crashlytics.logException(e);
			Toast.makeText(this, "Could not save action, please try again", Toast.LENGTH_SHORT).show();
		}
	}

	public static boolean meetsAllRequirements(Context c) {
		return meetsAppRequirements(c) && hasSmsPerm(c) && HoverHelper.isAccessibilityEnabled(c) && HoverHelper.isOverlayEnabled(c);
	}
	public static boolean hasSmsPerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
	}

	public static boolean meetsAppRequirements(Context c) {
		return hasWakeLockPerm(c) && usableAndroidVersion();
	}
	public static boolean hasWakeLockPerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
	}
	public static boolean usableAndroidVersion() {
		return Build.VERSION.SDK_INT >= 18 && Build.VERSION.SDK_INT < 26;
	}
}
