package com.hover.tester;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.hover.sdk.main.HoverParameters;

import java.util.HashMap;

public class ActionDetailActivity extends AppCompatActivity {
	public static final String TAG = "ActionDetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_detail);
		setUpToolbar();
		setUpFab();
		restoreFrag(savedInstanceState);
	}

	private void setUpToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setUpFab() {
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			makeRequest();
			}
		});
	}

	private void makeRequest() {
		ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);
		if (frag != null) {
			OperatorAction action = frag.mAction;
			HoverParameters.Builder hpb = new HoverParameters.Builder(ActionDetailActivity.this)
					.request(action.mSlug).from(action.mOpId);
			addExtras(hpb, action, ActionDetailActivity.this);
			startActivityForResult(hpb.buildIntent(), 0);
		}
	}

	private void addExtras(HoverParameters.Builder hpb, OperatorAction act, Context c) {
		for (HashMap.Entry<String, String> variable : act.mVariables.entrySet()) {
			Log.e(TAG, "Adding Extra: " + variable.getKey() + ": " + variable.getValue());
			hpb.extra(variable.getKey(), variable.getValue());
			if (variable.getKey().equals("amount"))
				hpb.extra("currency", Utils.getSharedPrefs(c).getString(OperatorService.CURRENCY, ""));
		}
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
			OperatorAction.saveWaitingResult(data, this);
		else
			OperatorAction.saveNegativeResult(data, this);
		ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);
		if (frag != null) frag.showResult(resultCode, data);
	}

	private void restoreFrag(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Bundle args = new Bundle();
			args.putString(OperatorAction.SLUG, getIntent().getStringExtra(OperatorAction.SLUG));
			args.putInt(OperatorService.ID, getIntent().getIntExtra(OperatorService.ID, -1));

			ActionDetailFragment fragment = new ActionDetailFragment();
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.detail_container, fragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateUpTo(new Intent(this, ActionListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
