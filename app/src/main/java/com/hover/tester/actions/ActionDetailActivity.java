package com.hover.tester.actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.hover.sdk.main.HoverParameters;
import com.hover.tester.BuildConfig;
import com.hover.tester.MainActivity;
import com.hover.tester.R;

public class ActionDetailActivity extends AppCompatActivity {
	public static final String TAG = "ActionDetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_detail);
		setUpToolbar();
		restoreFrag(savedInstanceState);
	}

	private void setUpToolbar() {
		final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

		final View titleLayout = findViewById(R.id.layout_title);
		titleLayout.post(new Runnable() {
			@Override
			public void run() {
				CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
				layoutParams.height = titleLayout.getHeight() + getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
				toolbar.setLayoutParams(layoutParams);
			}
		});
	}

	void setTitle(String title, String subtitle) {
		if (findViewById(R.id.layout_title) != null) {
			((TextView) findViewById(R.id.title)).setText(title);
			((TextView) findViewById(R.id.subtitle)).setText(subtitle);
		}
	}

	public void makeRequest(View view) {
		ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.action_detail);
		if (frag != null) {
			OperatorAction action = frag.mAction;
			HoverParameters.Builder hpb = new HoverParameters.Builder(ActionDetailActivity.this)
					.request(action.mSlug).from(action.mOpId);
			frag.addAndSaveExtras(hpb);
			Log.e(TAG, BuildConfig.BUILD_TYPE);
			if (BuildConfig.BUILD_TYPE.equals("debug")) hpb.debugMode();
			startActivityForResult(hpb.buildIntent(), 0);
		}
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.action_detail);
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
					.add(R.id.action_detail, fragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateUpTo(new Intent(this, MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
