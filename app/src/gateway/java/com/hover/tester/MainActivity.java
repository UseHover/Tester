package com.hover.tester;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.sdk.operators.OperatorUpdateService;
import com.hover.tester.detail.ActionDetailActivity;
import com.hover.tester.network.HoverIntegratonListService;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener {
	public final static String TAG = "MainActivity";
	private final int INTEGRATE_REQUEST = 111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_main);
		controlFlow();
	}

	private void controlFlow() {
		if (!meetsAllRequirements(this)) {
			askPermissions();
		} else if (OperatorService.getLastUsedId(this) != -1) {
			getServices();
		}
	}

	private void askPermissions() {
		// Phone
		// Wakelock
		// have internet
		// Android version
	}

	private void getServices() {
		Intent i = new Intent(this, HoverIntegratonListService.class);
		// Get SIM Ids from sdk
		i.putExtra(HoverIntegratonListService.SIM_IDS, "");
		startService(i);
	}

	public void pickIntegration(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_service)
				.setItems(HoverIntegratonListService.getServices(this), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						addIntegration(HoverIntegratonListService.getServiceId(i, MainActivity.this));
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void addIntegration(int id) {
		Intent integrationIntent = new Intent(this, HoverIntegrationActivity.class);
		integrationIntent.putExtra(HoverIntegrationActivity.SERVICE_IDS, new int[] { id });
		startActivityForResult(integrationIntent, INTEGRATE_REQUEST);
	}

	public void setToolbarTitle() {
		if (OperatorService.getLastUsedId(this) != -1) {
			OperatorService opService = new OperatorService(this);
			getSupportActionBar().setTitle(opService.mName);
			getSupportActionBar().setSubtitle(getString(R.string.country, opService.mCountryIso, opService.mCurrencyIso));
			findViewById(R.id.update_config).setVisibility(View.VISIBLE);
		}
	}

	public void updateConfig(View view) {
		registerReceiver(mConfigReceiver, new IntentFilter(getPackageName() + ".CONFIG_UPDATED"));
		startService(new Intent(getApplicationContext(), OperatorUpdateService.class));
	}
	private BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Snackbar.make(MainActivity.this.findViewById(R.id.main_fragment), "Configuration Updated", Snackbar.LENGTH_LONG).show();
			if (OperatorService.getLastUsedId(MainActivity.this) != -1) addIntegration(OperatorService.getLastUsedId(MainActivity.this));
		}
	};

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == INTEGRATE_REQUEST && resultCode == RESULT_OK)
			onIntegrateSuccess(data);
		else if (requestCode == INTEGRATE_REQUEST)
			Toast.makeText(this, getString(R.string.error_integration), Toast.LENGTH_SHORT).show();
	}

	public void onIntegrateSuccess(Intent data) {
		MainFragment frag = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
		OperatorService opService = new OperatorService(data, this);
		frag.update(opService);
		setToolbarTitle();
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
		try { unregisterReceiver(mConfigReceiver); }
		catch (Exception e) {}
		unregisterNetReceiver();
	}

	public static boolean meetsAllRequirements(Context c) {
		return hasPhonePerm(c) && hasWakeLockPerm(c) && usableAndroidVersion();
	}
	public static boolean hasPhonePerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
	}
	public static boolean hasWakeLockPerm(Context c) {
		return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
	}
	public static boolean usableAndroidVersion() {
		return Build.VERSION.SDK_INT >= 18 && Build.VERSION.SDK_INT < 26;
	}

	public BroadcastReceiver mNetReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			if (VolleySingleton.isConnected(context)) {
//				getToolbar().setTitle(com.hover.sdk.R.string.hsdk_api_key_check_in_progress);
//				mView.findViewById(com.hover.sdk.R.id.key_check_layout).setVisibility(View.GONE);
//				context.startService(new Intent(context, UpdateService.class));
//			} else {
//
//			}
		}
	};
	private void unregisterNetReceiver() {
		try { unregisterReceiver(mNetReceiver);
		} catch (Exception e) { }
	}
}
