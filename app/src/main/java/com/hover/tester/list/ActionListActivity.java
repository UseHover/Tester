package com.hover.tester.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.sdk.operators.Permission;
import com.hover.tester.OperatorAction;
import com.hover.tester.OperatorService;
import com.hover.tester.R;
import com.hover.tester.detail.ActionDetailActivity;
import com.hover.tester.detail.ActionDetailFragment;

import io.fabric.sdk.android.Fabric;

public class ActionListActivity extends AppCompatActivity implements ActionListFragment.OnListFragmentInteractionListener {
    public final static String TAG = "ActionListActivity";
    private final int INTEGRATE_REQUEST = 111;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        setUpToolbar();
        if (findViewById(R.id.detail_container) != null)
            mTwoPane = true;
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void addIntegration(View view) {
        Intent integrationIntent = new Intent(this, HoverIntegrationActivity.class);
        integrationIntent.putExtra(HoverIntegrationActivity.SERVICE_IDS, new int[] { 1, 2, 4, 5, 8, 11, 17, 19 });
        integrationIntent.putExtra(HoverIntegrationActivity.PERM_LEVEL, Permission.NORMAL);
        startActivityForResult(integrationIntent, INTEGRATE_REQUEST);
    }

    public void setToolbarTitle(OperatorService opService) {
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(opService.mName);
        ((Toolbar) findViewById(R.id.toolbar)).setSubtitle(getString(R.string.country, opService.mCountryIso, opService.mCurrencyIso));
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTEGRATE_REQUEST && resultCode == RESULT_OK)
            onIntegrateSuccess(data);
        else if (requestCode == INTEGRATE_REQUEST)
            Toast.makeText(this, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
    }

    public void onIntegrateSuccess(Intent data) {
        ActionListFragment frag = (ActionListFragment) getSupportFragmentManager().findFragmentById(R.id.action_list_fragment);
        frag.update(new OperatorService(data, this));
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
}
