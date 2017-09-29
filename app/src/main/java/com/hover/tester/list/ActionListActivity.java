package com.hover.tester.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.sdk.operators.OperatorUpdateService;
import com.hover.sdk.operators.Permission;
import com.hover.tester.OperatorAction;
import com.hover.tester.OperatorService;
import com.hover.tester.R;
import com.hover.tester.detail.ActionDetailActivity;
import com.hover.tester.detail.ActionDetailFragment;
import com.hover.tester.network.HoverIntegratonListService;

import io.fabric.sdk.android.Fabric;

public class ActionListActivity extends AppCompatActivity implements ActionListFragment.OnListFragmentInteractionListener {
    public final static String TAG = "ActionListActivity";
    private final int INTEGRATE_REQUEST = 111;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        startService(new Intent(this, HoverIntegratonListService.class));
        setContentView(R.layout.activity_main);
        setUpToolbar();
        if (findViewById(R.id.detail_container) != null)
            mTwoPane = true;
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();
    }

    public void pickIntegration(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_service)
                .setItems(HoverIntegratonListService.getServices(this), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addIntegration(HoverIntegratonListService.getServiceId(i, ActionListActivity.this));
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
        registerReceiver(mConfigReceiver, new IntentFilter
                (getPackageName() + ".CONFIG_UPDATED"));
        startService(new Intent(getApplicationContext(), OperatorUpdateService.class));
    }
    private BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar.make(ActionListActivity.this.findViewById(R.id.action_list_fragment), "Configuration Updated", Snackbar.LENGTH_LONG).show();
            if (OperatorService.getLastUsedId(ActionListActivity.this) != -1) addIntegration(OperatorService.getLastUsedId(ActionListActivity.this));
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
        ActionListFragment frag = (ActionListFragment) getSupportFragmentManager().findFragmentById(R.id.action_list_fragment);
        OperatorService opService = new OperatorService(data, this);
        frag.update(opService);
        setToolbarTitle();
    }

    @Override
    public void onListFragmentInteraction(OperatorAction act) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(OperatorAction.ID, act.mId);
            ActionDetailFragment fragment = new ActionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ActionDetailActivity.class);
            intent.putExtra(OperatorAction.ID, act.mId);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try { unregisterReceiver(mConfigReceiver); }
        catch (Exception e) {}
    }
}
