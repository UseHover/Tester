package com.hover.tester;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.sdk.operators.Permission;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements ActionListFragment.OnListFragmentInteractionListener {
    public final static String TAG = "MainActivity";
    private final int INTEGRATE_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        setUpToolbar();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void addIntegration(View view) {
        Intent integrationIntent = new Intent(this, HoverIntegrationActivity.class);
        integrationIntent.putExtra(HoverIntegrationActivity.SERVICE_IDS, new int[] { 1, 4 });
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
            Toast.makeText(this, data.getStringExtra("error"), Toast.LENGTH_SHORT).show();
//        else if (resultCode == RESULT_CANCELED) {
//            Utils.saveActionResult(serviceId, (String) v.getTag(), false, this);
//            setResultInView(v, serviceId, (String) v.getTag());
//        } else {
//            Utils.saveActionResult(serviceId, (String) v.getTag(), false, this);
//            setIcon(v, R.drawable.circle_unknown);
//        }
    }

    public void onIntegrateSuccess(Intent data) {
        ActionListFragment frag = (ActionListFragment) getSupportFragmentManager().findFragmentById(R.id.action_list_fragment);
        frag.update(new OperatorService(data, this), frag.getView());
    }

    @Override
    public void onListFragmentInteraction(OperatorAction act) {

    }

    private void addListeners() {
        ((EditText) findViewById(R.id.amount)).setText(Utils.getAmount(this));
        ((EditText) findViewById(R.id.amount)).addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setAmount(s.toString(), getApplicationContext());
            }
        });
    }
}
