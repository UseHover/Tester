package com.hover.tester.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.api.Hover;
import com.hover.sdk.permissions.PermissionActivity;
import com.hover.tester.R;
import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.HoverAction;
import com.hover.tester.actions.SaveActionTask;
import com.hover.tester.network.HoverIntegratonListService;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.utils.NetworkReceiver;
import com.hover.tester.utils.UpdateReceiver;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public abstract class AbstractMainActivity extends AppCompatActivity
		implements MainFragment.OnListFragmentInteractionListener, AddIntegrationInterface, Hover.ActionChoiceListener, Hover.DownloadListener {
	public final static String TAG = "AMainActivity";
	private NetworkReceiver mNetworkReceiver = null;
	protected MainFragment mFrag;

	private BroadcastReceiver mReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);

//		setUpView();
		initialize();
		updateConfig(null);
		Runnable r = new Runnable() { @Override public void run() {
			goToAction("b2ba9578");
		} }; //fef640f3
		new Handler().postDelayed(r, 1000);
	}

	protected void initialize() {
		Fabric.with(this, new Crashlytics());
		Hover.initialize(this);
		Hover.setBranding("", R.drawable.ic_african_bank_logo, this);
		getActions();
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
//		if (intent != null && intent.getAction() != null && intent.getAction().equals(UpdateReceiver.ACTION))
//			Snackbar.make(findViewById(R.id.nest_container), getString(R.string.updated), Snackbar.LENGTH_LONG).show();
	}

	public void getActions() {
		if (NetworkOps.isConnected(this))
			startService(new Intent(this, HoverIntegratonListService.class));
	}

	public void pickIntegration(View view) {
		if (NetworkOps.isConnected(this)) {
//			try {
//				List<SimInfo> sims = Hover.getPresentSims(this);
////				Hover.requestSimChoice(sims, this, this);
//				for (SimInfo s: sims) {
//					Log.e(TAG, "sim detected: " + s.toString());
//				}
//			} catch (Exception e) { Log.e(TAG, "SIM get failure"); }
			DialogFragment newFragment = AddIntegrationDialogFragment.newInstance(AddIntegrationDialogFragment.CHOOSE_ACTION_STEP, null);
			newFragment.show(getSupportFragmentManager(), AddIntegrationDialogFragment.TAG);
		}
	}

	@Override
	public void addAction(HoverAction action) {
//		Snackbar.make(findViewById(R.id.nest_container),
//				"Saving Action: " + action.mName + ", one moment",
//				Snackbar.LENGTH_LONG).show();
		new SaveActionTask(null, mFrag, this).execute(action);
	}

	@Override
	public void onListFragmentInteraction(HoverAction act) {
		goToAction(act.mId);
	}

	private void goToAction(String id) {
		Intent intent = new Intent(this, ActionDetailActivity.class);
		intent.putExtra(HoverAction.ID, id);
		startActivity(intent);
	}

	public void updateConfig(View view) {
		Toast.makeText(AbstractMainActivity.this, getString(R.string.updating), Toast.LENGTH_SHORT).show();
		Hover.updateActionConfigs(this, this);
		if (NetworkOps.isConnected(this))
			startService(new Intent(this, HoverIntegratonListService.class));
	}

	@Override public void onError(String message) {
		Log.e(TAG, "error: " + message);
//		Snackbar.make(findViewById(R.id.nest_container), message, Snackbar.LENGTH_LONG).show();
	}
	@Override public void onSuccess(ArrayList<com.hover.sdk.actions.HoverAction> actions) {
		Log.e(TAG, "success, action count: " + actions.size());
//		Snackbar.make(findViewById(R.id.nest_container), getString(R.string.updated), Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void onActionChosen(String actionId) {
		Toast.makeText(AbstractMainActivity.this, "Chose Action with ID: " + actionId, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onCanceled() {
		Toast.makeText(AbstractMainActivity.this, "Action Choice Cancelled.", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterNetReceiver();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mFrag != null) mFrag.controlFlow();
	}

	public void grantSystemPermissions(View view) {
		requestAdvancedPerms();
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
	public void requestPhonePerm() { // Fragment frag, int requestCode) {
		Intent i = new Intent(this, PermissionActivity.class);
		startActivityForResult(i, 0);
	}

	public static boolean hasAdvancedPerms(Context c) {
		return Hover.isAccessibilityEnabled(c) && Hover.isOverlayEnabled(c);
	}
	protected void requestAdvancedPerms() {
		startActivityForResult(new Intent(this, PermissionActivity.class), 1);
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
