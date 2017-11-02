package com.hover.tester;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.network.HoverIntegratonListService;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.services.OperatorService;
import com.hover.tester.services.SaveServiceTask;
import com.hover.tester.utils.NetworkReceiver;

import org.json.JSONException;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener, GatewayIntegrationInterface {
	public final static String TAG = "MainActivity";
	private NetworkReceiver mNetworkReceiver = null;
	private MainFragment mFrag;
	private OperatorService pendingOpService;
	public static final int INTEGRATE_REQUEST = 111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());

		WakeUpHelper.releaseAlarms(this);
		String token = FirebaseInstanceId.getInstance().getToken();
		if (token != null && !token.isEmpty()) Log.e(TAG, FirebaseInstanceId.getInstance().getToken());

		setContentView(R.layout.activity_main);
		mFrag = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getAction().equals(NetworkReceiver.ACTION) && NetworkOps.isConnected(this)) {
			unregisterNetReceiver();
			getServices();
		}
	}

	public void getServices() {
		Intent i = new Intent(this, HoverIntegratonListService.class);
		// Get SIM Ids from sdk
//		i.putExtra(HoverIntegratonListService.SIM_IDS, "");
		startService(i);
	}

	public void pickIntegration(View view) {
		if (NetworkOps.isConnected(this)) {
			DialogFragment newFragment = AddIntegrationDialogFragment.newInstance(AddIntegrationDialogFragment.CHOOSE_SERVICE_STEP, -1, null);
			newFragment.show(getSupportFragmentManager(), AddIntegrationDialogFragment.TAG);
		}
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == INTEGRATE_REQUEST && resultCode == RESULT_OK)
			onIntegrateSuccess(data);
		else if (requestCode == INTEGRATE_REQUEST)
			Toast.makeText(this, getString(R.string.error_integration), Toast.LENGTH_SHORT).show();
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
			Toast.makeText(this, "Could not save action, please try again", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onListFragmentInteraction(OperatorAction act) {
		Intent intent = new Intent(this, ActionDetailActivity.class);
		intent.putExtra(OperatorAction.ID, act.mId);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterNetReceiver();
	}

	public void grantSystemPermissions(View view) {
		ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_PHONE_STATE, Manifest.permission.WAKE_LOCK }, 0);
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,	String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mFrag != null) mFrag.controlFlow();
	}

	public static boolean meetsAllRequirements(Context c) {
		return meetsAppRequirements(c) && hasSmsPerm(c) && HoverIntegrationActivity.isAccessibilityEnabled(c) && HoverIntegrationActivity.isOverlayEnabled(c);
	}
	public static boolean hasSmsPerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
	}

	public static boolean meetsAppRequirements(Context c) {
		return hasPhonePerm(c) && hasWakeLockPerm(c) && usableAndroidVersion();
	}
	public static boolean hasPhonePerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
	}
	public static boolean hasWakeLockPerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
	}
	public static boolean usableAndroidVersion() {
		return Build.VERSION.SDK_INT >= 18 && Build.VERSION.SDK_INT < 26;
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
