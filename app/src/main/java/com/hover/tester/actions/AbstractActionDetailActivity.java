package com.hover.tester.actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hover.sdk.api.HoverParameters;
import com.hover.tester.R;
import com.hover.tester.main.MainActivity;
import com.hover.tester.utils.Utils;


public abstract class AbstractActionDetailActivity extends AppCompatActivity {
	public static final String TAG = "AActionDetailActivity";

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

	void makeRequest(Bundle extras) {
		try {
			HoverParameters.Builder hpb = startRequest(getFrag());
			for (String key : extras.keySet()) {
				if (extras.get(key) != null) hpb.extra(key, extras.get(key).toString());
			}
			makeRequest(hpb, getFrag());
		} catch (NullPointerException e) {
			Toast.makeText(this, getString(R.string.error_variables), Toast.LENGTH_SHORT).show();
		}
	}
	public void makeRequest(View view) {
		try {
			ActionDetailFragment frag = getFrag();
			HoverParameters.Builder hpb = startRequest(frag);
			frag.addAndSaveExtras(hpb);
			makeRequest(hpb, frag);
		} catch (NullPointerException e) {
			Toast.makeText(this, getString(R.string.error_variables), Toast.LENGTH_SHORT).show();
		}
	}

	private HoverParameters.Builder startRequest(ActionDetailFragment frag) {
		if (frag != null) {
			HoverAction action = frag.mAction;
			Log.i(TAG, "Starting request: " + action.mName + " " + action.mId);
			return new HoverParameters.Builder(AbstractActionDetailActivity.this).request(action.mId);
		}
		return null;
	}
	protected void makeRequest(HoverParameters.Builder hpb, ActionDetailFragment frag) {
		if (Utils.isInDebugMode(this)) hpb.setEnvironment(HoverParameters.DEBUG_ENV);
//		hpb.setEnvironment(HoverParameters.TEST_ENV);
		startActivityForResult(hpb.buildIntent(), 0);
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ActionDetailFragment frag = getFrag();
		if (frag != null && data != null) {
			new ActionResult(frag.mAction.mId, resultCode, data).save(this);
			frag.showResult(resultCode, data);
		}
	}

	protected void restoreFrag(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Intent i = getIntent();
			Bundle args = new Bundle();

			if (i.getStringExtra(HoverAction.ID) == null)
				return;

			args.putAll(i.getExtras());
			args.putString(HoverAction.ID, i.getStringExtra(HoverAction.ID));

			ActionDetailFragment fragment = new ActionDetailFragment();
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.action_detail, fragment)
					.commit();
		}
	}

	protected ActionDetailFragment getFrag() {
		return (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.action_detail);
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
