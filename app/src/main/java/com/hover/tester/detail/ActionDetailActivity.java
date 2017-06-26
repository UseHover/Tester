package com.hover.tester.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.hover.sdk.main.HoverParameters;
import com.hover.tester.ActionResult;
import com.hover.tester.list.ActionListActivity;
import com.hover.tester.OperatorAction;
import com.hover.tester.R;

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
			frag.addAndSaveExtras(hpb);
			startActivityForResult(hpb.buildIntent(), 0);
		}
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container);
		if (frag != null) {
			new ActionResult(frag.mAction.mId, resultCode, data).save(this);
			frag.showResult(resultCode, data);
		}
	}

	private void restoreFrag(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Bundle args = new Bundle();
			args.putInt(OperatorAction.ID, getIntent().getIntExtra(OperatorAction.ID, 1));

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
