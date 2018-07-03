package com.hover.tester.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.actions.ActionsDownloadTask;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverConfigException;
import com.hover.sdk.onboarding.PermissionActivity;
import com.hover.tester.R;
import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.HoverAction;
import com.hover.tester.actions.SaveActionTask;
import com.hover.tester.network.HoverIntegratonListService;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.utils.NetworkReceiver;
import com.hover.tester.utils.UpdateReceiver;

import io.fabric.sdk.android.Fabric;

public abstract class AbstractMainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener, AddIntegrationInterface {
	public final static String TAG = "AMainActivity";
	private NetworkReceiver mNetworkReceiver = null;
	protected MainFragment mFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		setUpView();
	}

	protected void initialize() {
		Fabric.with(this, new Crashlytics());
	}

	protected void setUpView() {
		setContentView(R.layout.activity_main);
		mFrag = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		showConfigUpdated(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getAction().equals(NetworkReceiver.ACTION) && NetworkOps.isConnected(this)) {
			unregisterNetReceiver();
			getActions();
		} else showConfigUpdated(intent);
	}

	private void showConfigUpdated(Intent intent) {
		if (intent != null && intent.getAction() != null && intent.getAction().equals(UpdateReceiver.ACTION))
			Snackbar.make(findViewById(R.id.nest_container), getString(R.string.updated), Snackbar.LENGTH_LONG).show();
	}

	public void getActions() {
		try {
//			if (!Hover.deviceRegistered(this))
				Hover.initialize(this);
		} catch (HoverConfigException e) { Log.e(TAG, e.getMessage(), e); }
		startService(new Intent(this, HoverIntegratonListService.class));
	}

	public void pickIntegration(View view) {
		if (NetworkOps.isConnected(this)) {
			DialogFragment newFragment = AddIntegrationDialogFragment.newInstance(AddIntegrationDialogFragment.CHOOSE_ACTION_STEP, null);
			newFragment.show(getSupportFragmentManager(), AddIntegrationDialogFragment.TAG);
		}
	}

	@Override
	public void addAction(HoverAction action) {
		Snackbar.make(findViewById(R.id.nest_container),
				"Saving Action: " + action.mName + ", one moment",
				Snackbar.LENGTH_LONG).show();
		new SaveActionTask(null, mFrag, this).execute(action);
	}

	@Override
	public void onListFragmentInteraction(HoverAction act) {
		Intent intent = new Intent(this, ActionDetailActivity.class);
		intent.putExtra(HoverAction.ID, act.mId);
		startActivity(intent);
	}

	public void updateConfig(View view) {
		Toast.makeText(AbstractMainActivity.this, getString(R.string.updating), Toast.LENGTH_SHORT).show();
		startService(new Intent(getApplicationContext(), ActionsDownloadTask.class));
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterNetReceiver();
	}

	public void requestPhonePerm(Fragment frag, int requestCode) {
//		if (Build.VERSION.SDK_INT >= 23)
//			frag.requestPermissions(new String[]{ Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE }, requestCode);
//		else
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE}, requestCode);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,	String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mFrag != null) mFrag.controlFlow();
		if (!hasAdvancedPerms(this)) requestAdvancedPerms();
	}

	public static boolean meetsAllRequirements(Context c) { return hasPhonePerm(c) && hasAdvancedPerms(c); }
	public static boolean meetsAppRequirements(Context c) { return hasPhonePerm(c); }

	public static boolean hasPhonePerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || (ContextCompat.checkSelfPermission(c, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission(c, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
	}
	public static boolean hasSmsPerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
	}
	public static boolean hasAdvancedPerms(Context c) {
		return Hover.isAccessibilityEnabled(c) && Hover.isOverlayEnabled(c);
	}
	protected void requestAdvancedPerms() {
		startActivity(new Intent(this, PermissionActivity.class));
	}


	public void registerNetReceiver() {
		if (mNetworkReceiver == null) {
			mNetworkReceiver = new NetworkReceiver();
			registerReceiver(mNetworkReceiver, NetworkReceiver.buildIntentFilter());
		}
	}
	public void unregisterNetReceiver() {
		try {
			if (mNetworkReceiver != null) {
				unregisterReceiver(mNetworkReceiver);
				mNetworkReceiver = null;
			}
		} catch (Exception e) { }
	}
}
