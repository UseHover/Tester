package com.hover.tester;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.main.Hover;
import com.hover.sdk.main.HoverIntegration;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements HoverIntegration.HoverListener {
    public final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        setUpToolbar();
        addListeners();

        Log.d(TAG, "Creating activity");
        checkPermission();
        Log.d(TAG, "Op: " + Utils.getOperator(this));
        if (Utils.getSharedPrefs(this).contains(Utils.OPERATOR)) fillOpInfo();
        chooseAction(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        chooseAction(intent);
    }
    private void chooseAction(Intent intent) {
        if (intent.hasExtra("sdk_action")) {
            ViewGroup parent = (ViewGroup) findViewById(R.id.actions);
            for (int j = 0; j < parent.getChildCount(); j++) {
                if (((String) parent.getChildAt(j).getTag()).equals(intent.getStringExtra("sdk_action"))) {
                    setResultInView(parent.getChildAt(j), Utils.getOperator(this), intent.getStringExtra("sdk_action"));
                    if (Utils.isActive(this)) {
                        if (j < parent.getChildCount() - 1) {
                            performAction(j + 1);
                            return;
                        }
                        Utils.setActive(false, this);
                    }
                }
            }
        }
    }

    private void performAllActions() {
        Utils.setActive(true, this);
        if (HoverIntegration.getActionsList(Utils.getOperator(this), this).length > 0)
            performAction(0);
    }

    void performAction(int id) {
        findViewById(id).findViewById(R.id.rerun).callOnClick();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAllActions();
            }
        });
    }

    @Override
    public void onSIMError() {
        Toast.makeText(this, "Sim error", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onSuccess(String operatorSlug, String countryName, String currency) {
        Toast.makeText(this, "Success! " + currency, Toast.LENGTH_SHORT).show();
        if (!Utils.getOperator(this).equals(operatorSlug)) {
            Utils.setOperator(operatorSlug, this);
            Utils.setCountry(countryName, this);
            Utils.setCurrency(currency, this);
            fillOpInfo();
        }
    }
    @Override
    public void onUserDenied() {  Toast.makeText(this, "User denied", Toast.LENGTH_SHORT).show(); }

    private void fillOpInfo() {
        Log.d(TAG, "Filling op info");
        try {
            ((TextView) findViewById(R.id.operator)).setText(Utils.getOperator(this));
            ((TextView) findViewById(R.id.country)).setText(getString(R.string.country, Utils.getCountry(this), Utils.getCurrency(this)));
            addActions();
        } catch (Exception e) {
            Log.d(TAG, "Fail: " + e.getMessage());
        }
    }

    private void addActions() {
        String[] actions = HoverIntegration.getActionsList(Utils.getOperator(this), this);
        Log.d(TAG, "Setting actions: " + actions[0]);
        ((LinearLayout) findViewById(R.id.actions)).removeAllViews();
        for (int i = 0; i < actions.length; i++)
            addActionLayout(actions[i], i);
    }

    private void addActionLayout(final String name, final int num) {
        Log.d(TAG, "Adding action: " + name);
        String opSlug = Utils.getOperator(this);
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.test_action, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 72));
        view.setId(num);
        view.setTag(name);
        ((TextView) view.findViewById(R.id.name)).setText(name);
        view.findViewById(R.id.rerun).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Context c = getApplicationContext();
                Hover.Builder hb = new Hover.Builder(c).request(name);
                addExtras(hb, c);
                Intent i = hb.fromAny();
                startActivityForResult(i, num);
            }
        });
        setResultInView(view, opSlug, name);
        ((LinearLayout) findViewById(R.id.actions)).addView(view);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String opSlug = Utils.getOperator(this);
        View v = findViewById(requestCode);
        if (resultCode == RESULT_CANCELED) {
            Utils.saveActionResult(opSlug, (String) v.getTag(), false, this);
            setResultInView(v, opSlug, (String) v.getTag());
        } else {
            Utils.saveActionResult(opSlug, (String) v.getTag(), false, this);
            setIcon(v, R.drawable.circle_unknown);
        }
    }

    private void setResultInView(View parent, String opSlug, String action) {
        if (Utils.hasActionResult(opSlug, action, this)) {
            ((TextView) parent.findViewById(R.id.time)).setText(Utils.getActionResultTime(opSlug, action, this));
            if (Utils.actionResultPositive(opSlug, action, this))
                setIcon(parent, R.drawable.circle_passes);
            else
                setIcon(parent, R.drawable.circle_fails);
        }
    }

    private void setIcon(View parent, int drawable) {
        ((ImageView) parent.findViewById(R.id.status_icon)).setImageDrawable(getResources().getDrawable(drawable));
    }

    private void addExtras(Hover.Builder hb, Context c) {
        hb.extra("amount", Utils.getAmount(c));
        hb.extra("currency", Utils.getCurrency(c));
        hb.extra("who", Utils.getPhone(c));
        hb.extra("merchant", Utils.getMerchant(c));
        hb.extra("paybill", Utils.getPaybill(c));
        hb.extra("paybill_acct", Utils.getPaybillAcct(c));
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
        ((EditText) findViewById(R.id.phone)).setText(Utils.getPhone(this));
        ((EditText) findViewById(R.id.phone)).addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setPhone(s.toString(), getApplicationContext());
            }
        });
        ((EditText) findViewById(R.id.merchant)).setText(Utils.getMerchant(this));
        ((EditText) findViewById(R.id.merchant)).addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setMerchant(s.toString(), getApplicationContext());
            }
        });
        ((EditText) findViewById(R.id.paybill)).setText(Utils.getPaybill(this));
        ((EditText) findViewById(R.id.paybill)).addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setPaybill(s.toString(), getApplicationContext());
            }
        });
        ((EditText) findViewById(R.id.paybill_acct)).setText(Utils.getPaybillAcct(this));
        ((EditText) findViewById(R.id.paybill_acct)).addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setPaybillAcct(s.toString(), getApplicationContext());
            }
        });
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_PHONE_STATE }, 0);
        } else addHoverIntegration();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,	String permissions[], int[] grantResults) {
        if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
            addHoverIntegration();
        else
            finish();
    }

    private void addHoverIntegration() {
        HoverIntegration.add(this, this);
    }
}
