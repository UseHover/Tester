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
import com.hover.sdk.main.HoverParameters;
import com.hover.sdk.main.HoverIntegration;
import com.hover.sdk.operators.Permission;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
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
                    setResultInView(parent.getChildAt(j), Utils.getServiceId(this), intent.getStringExtra("sdk_action"));
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
        if (HoverIntegration.getActionsList(Utils.getServiceId(this), this).length > 0)
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
        String[] actions = HoverIntegration.getActionsList(Utils.getServiceId(this), this);
        Log.d(TAG, "Setting actions: " + actions[0]);
        ((LinearLayout) findViewById(R.id.actions)).removeAllViews();
        for (int i = 0; i < actions.length; i++)
            addActionLayout(actions[i], i);
    }

    private void addActionLayout(final String name, final int num) {
        Log.d(TAG, "Adding action: " + name);
        Integer serviceId = Utils.getServiceId(this);
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.test_action, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 72));
        view.setId(num);
        view.setTag(name);
        ((TextView) view.findViewById(R.id.name)).setText(name);
        view.findViewById(R.id.rerun).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Context c = getApplicationContext();
                Intent i = new HoverParameters.Builder(c).request(name).
                        from(Utils.getServiceId(c)).buildIntent();
                startActivityForResult(i, num);
            }
        });
        view.findViewById(R.id.status_icon).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Context c = getApplicationContext();
                Intent i = new Intent(c, ParsedValuesListActivity.class);
                i.putExtra(Utils.ACTION, name);
                startActivity(i);
            }
        });
        setResultInView(view, serviceId, name);
        ((LinearLayout) findViewById(R.id.actions)).addView(view);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, Integer.toString(requestCode));
        Integer serviceId = Utils.getServiceId(this);
        View v = findViewById(requestCode);
        if (resultCode == RESULT_CANCELED) {
            Utils.saveActionResult(serviceId, (String) v.getTag(), false, this);
            setResultInView(v, serviceId, (String) v.getTag());
        } else {
            Utils.saveActionResult(serviceId, (String) v.getTag(), false, this);
            setIcon(v, R.drawable.circle_unknown);
        }
    }

    private void setResultInView(View parent, Integer serviceId, String action) {
        if (Utils.hasActionResult(serviceId, action, this)) {
            ((TextView) parent.findViewById(R.id.time)).setText(Utils.getActionResultTime(serviceId, action, this));
            if (Utils.actionResultPositive(serviceId, action, this))
                setIcon(parent, R.drawable.circle_passes);
            else
                setIcon(parent, R.drawable.circle_fails);
        }
    }

    private void setIcon(View parent, int drawable) {
        ((ImageView) parent.findViewById(R.id.status_icon)).setImageDrawable(getResources().getDrawable(drawable));
    }

    private void addExtras(HoverParameters.Builder hb, Context c) {
        hb.extra("amount", Utils.getAmount(c));
        hb.extra("currency", Utils.getCurrency(c));
        hb.extra("who", Utils.getPhone(c));
        hb.extra("merchant", Utils.getMerchant(c));
        hb.extra("paybill", Utils.getPaybill(c));
        hb.extra("paybill_acct", Utils.getPaybillAcct(c));
        hb.extra("recipient_nrc", Utils.getRecipientNRC(c));
        hb.extra("withdrawal_code", Utils.getWithdrawalCode(c));
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
        ((EditText) findViewById(R.id.recipient_nrc)).setText(Utils.getPaybillAcct(this));
        ((EditText) findViewById(R.id.recipient_nrc)).addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setRecipientNRC(s.toString(), getApplicationContext());
            }
        });
        ((EditText) findViewById(R.id.withdrawal_code)).setText(Utils.getPaybillAcct(this));
        ((EditText) findViewById(R.id.withdrawal_code)).addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setWithdrawalCode(s.toString(), getApplicationContext());
            }
        });
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_PHONE_STATE }, 0);
        } else addHoverIntegration();
    }

    private void addHoverIntegration() {
        HoverIntegration.add(21, Permission.NORMAL, new BasicListener(), this);;
    }

    private class BasicListener implements HoverIntegration.HoverListener {
        public void onError(String error) {
            Log.d(TAG, "Error: " + error);
        };
        public void onSIMError(String error) {
            Log.d(TAG, "SIM Error : " + error);
        };
        public void onUserDenied() {
            Log.d(TAG, "User Denied");
        };
        public void onSuccess(int serviceId, String serviceName, String operatorName, String countryName, String currency) {
            Log.d(TAG, "Success");
            Context c = getApplicationContext();
            Toast.makeText(c, "Success! " + currency, Toast.LENGTH_SHORT).show();
            if (!Utils.getServiceId(c).equals(serviceId)) {
                Utils.setServiceId(serviceId, c);
                Utils.setOperator(operatorName, c);
                Utils.setCountry(countryName, c);
                Utils.setCurrency(currency, c);
                fillOpInfo();
            }
        };
    }
}
