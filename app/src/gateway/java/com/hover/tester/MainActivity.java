package com.hover.tester;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.network.HoverIntegratonListService;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.services.OperatorService;
import com.hover.tester.utils.NetworkReceiver;

import com.hover.sdk.onboarding.HoverIntegrationActivity;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener {
	public final static String TAG = "MainActivity";
	private NetworkReceiver mNetworkReceiver = null;
	private MainFragment mFrag;
	private final int INTEGRATE_REQUEST = 111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
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
		if (NetworkOps.isConnected(this))
			showServiceDialog();
	}

	public void grantSystemPermissions(View view) {
		ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_PHONE_STATE, Manifest.permission.WAKE_LOCK }, 0);
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,	String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mFrag != null) mFrag.controlFlow();
	}

	private void showServiceDialog() {
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

//	public void updateConfig(View view) {
//		registerReceiver(mConfigReceiver, new IntentFilter(getPackageName() + ".CONFIG_UPDATED"));
//		startService(new Intent(getApplicationContext(), OperatorUpdateService.class));
//	}
//	private BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Snackbar.make(MainActivity.this.findViewById(R.id.main_fragment), "Configuration Updated", Snackbar.LENGTH_LONG).show();
//			if (OperatorService.getLastUsedId(MainActivity.this) != -1) addIntegration(OperatorService.getLastUsedId(MainActivity.this));
//		}
//	};

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
		frag.update();
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

	public static boolean meetsAllRequirements(Context c) {
		return hasPhonePerm(c) && hasWakeLockPerm(c) && usableAndroidVersion();
	}
	public static boolean hasPhonePerm(Context c) {
		boolean p = Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
		Log.e(TAG, "has phone perm: " + p);
		return p;
	}
	public static boolean hasWakeLockPerm(Context c) {
		boolean w = Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(c, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
		Log.e(TAG, "has wake lock perm: " + w);
		return w;
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
