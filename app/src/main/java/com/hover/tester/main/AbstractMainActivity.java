package com.hover.tester.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.operators.OperatorUpdateService;
import com.hover.tester.R;
import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.network.HoverIntegratonListService;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.services.OperatorService;
import com.hover.tester.utils.NetworkReceiver;
import com.hover.tester.utils.UpdateReceiver;

import io.fabric.sdk.android.Fabric;

public abstract class AbstractMainActivity extends AppCompatActivity implements MainFragment.OnListFragmentInteractionListener {
	public final static String TAG = "AMainActivity";
	private NetworkReceiver mNetworkReceiver = null;
	protected MainFragment mFrag;
	private OperatorService pendingOpService;
	public static final int INTEGRATE_REQUEST = 111;

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

		findViewById(R.id.update_config).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(AbstractMainActivity.this, getString(R.string.updating), Toast.LENGTH_SHORT).show();
				startService(new Intent(getApplicationContext(), OperatorUpdateService.class));
			}
		});
		showConfigUpdated(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getAction().equals(NetworkReceiver.ACTION) && NetworkOps.isConnected(this)) {
			unregisterNetReceiver();
			getServices();
		} else showConfigUpdated(intent);
	}

	private void showConfigUpdated(Intent intent) {
		if (intent != null && intent.getAction() != null && intent.getAction().equals(UpdateReceiver.ACTION))
			Snackbar.make(findViewById(R.id.nest_container), getString(R.string.updated), Snackbar.LENGTH_LONG).show();
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

	public abstract void onIntegrateSuccess(Intent data);

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

	@Override
	public void onRequestPermissionsResult(int requestCode,	String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mFrag != null) mFrag.controlFlow();
	}

	public static boolean meetsAllRequirements(Context c) { return true; }
	public static boolean meetsAppRequirements(Context c) { return true; }

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
