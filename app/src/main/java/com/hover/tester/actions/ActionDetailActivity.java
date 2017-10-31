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
import android.widget.Toast;

import com.hover.sdk.main.HoverParameters;
import com.hover.tester.BuildConfig;
import com.hover.tester.MainActivity;
import com.hover.tester.R;
import com.hover.tester.WakeUpHelper;
import com.hover.tester.WakeUpService;
import com.hover.tester.schedules.AbstractScheduleActivity;

public class ActionDetailActivity extends AbstractScheduleActivity {
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

	void makeRequest(Bundle extras) {
		try {
			ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.action_detail);
			HoverParameters.Builder hpb = startRequest(frag);
			for (String key : extras.keySet())
				hpb.extra(key, extras.get(key).toString());
			makeRequest(hpb);
		} catch (NullPointerException e) {
			Toast.makeText(this, getString(R.string.error_variables), Toast.LENGTH_SHORT).show(); // FIXME: Generate failed status report
		}
	}
	public void makeRequest(View view) {
		try {
			ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.action_detail);
			HoverParameters.Builder hpb = startRequest(frag);
			frag.addAndSaveExtras(hpb);
			makeRequest(hpb);
		} catch (NullPointerException e) {
			Toast.makeText(this, getString(R.string.error_variables), Toast.LENGTH_SHORT).show();
		}
	}

	private HoverParameters.Builder startRequest(ActionDetailFragment frag) {
		if (frag != null) {
			OperatorAction action = frag.mAction;
			Log.e(TAG, "Starting action: " + action.mSlug + " " + action.mOpId);
			return new HoverParameters.Builder(ActionDetailActivity.this).request(action.mSlug).from(action.mOpId);
		}
		return null;
	}
	private void makeRequest(HoverParameters.Builder hpb) {
		Log.e(TAG, BuildConfig.BUILD_TYPE);
		if (BuildConfig.BUILD_TYPE.equals("debug")) hpb.debugMode();
		startActivityForResult(hpb.buildIntent(), 0);
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ActionDetailFragment frag = (ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.action_detail);
		if (frag != null) {
			new ActionResult(frag.mAction.mId, resultCode, data).save(this);
			frag.showResult(resultCode, data);
		}
		releaseWake();
	}

	private void restoreFrag(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Intent i = getIntent();
			Bundle args = new Bundle();

			Log.e(TAG, "Restoring frag. Action Id: " + i.getIntExtra(OperatorAction.ID, -1));
			if (i.getIntExtra(OperatorAction.ID, -1) == -1) {
				releaseWake();
				return;
			}

			args.putAll(i.getExtras());
			args.putInt(OperatorAction.ID, i.getIntExtra(OperatorAction.ID, -1));

			ActionDetailFragment fragment = new ActionDetailFragment();
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.action_detail, fragment)
					.commit();
		}
	}

	void releaseWake() {
		Intent i = new Intent(this, WakeUpService.class);
		i.putExtra(WakeUpHelper.CMD, WakeUpHelper.DONE);
		startService(i);
	}

	void shutDown() {
		releaseWake();
		finish();
	}

	public void addSchedule(View view) {
		try {
			addSchedule(((ActionDetailFragment) getSupportFragmentManager().findFragmentById(R.id.action_detail)).mAction.mId);
		} catch (NullPointerException e) { Toast.makeText(this, "Could not start scheduler, try again", Toast.LENGTH_SHORT).show(); }
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
