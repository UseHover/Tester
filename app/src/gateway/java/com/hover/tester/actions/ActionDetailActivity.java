package com.hover.tester.actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.BuildConfig;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.tester.GatewayReceiver;
import com.hover.sdk.api.HoverParameters;
import com.hover.tester.TransactionReceiver;
import com.hover.tester.R;
import com.hover.tester.database.Contract;
import com.hover.tester.schedules.AddScheduleDialogFragment;
import com.hover.tester.schedules.Scheduler;
import com.hover.tester.schedules.SchedulerInterface;

import static com.hover.tester.schedules.Scheduler.HOURLY;
import static com.hover.tester.schedules.Scheduler.TEN_MIN;


public class ActionDetailActivity extends AbstractActionDetailActivity implements SchedulerInterface {
	public static final String TAG = "ActionDetailActivity";
	private Scheduler mScheduler;

	@Override
	public void addSchedule(String actionId) {
		mScheduler = Scheduler.getInstance();
		mScheduler.setId(actionId);
		DialogFragment newFragment = AddScheduleDialogFragment.newInstance(AddScheduleDialogFragment.ADD_SCHEDULE_STEP, actionId);
		newFragment.show(getSupportFragmentManager(), AddScheduleDialogFragment.TAG);
	}

	@Override
	public void setType(int type) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.setType(type);
		if (type == TEN_MIN || type == HOURLY)
			saveSchedule();
	}

	@Override
	public void chooseTime(int day) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		if (day != -1)
			mScheduler.setDay(day);
		DialogFragment newFragment = AddScheduleDialogFragment.newInstance(AddScheduleDialogFragment.TIME_PICKER_STEP, mScheduler.getId());
		newFragment.show(getSupportFragmentManager(), AddScheduleDialogFragment.TAG);
	}

	@Override
	public void setTime(int hour, int min) {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.setTime(hour, min);
		saveSchedule();
	}

	@Override
	public void saveSchedule() {
		if (mScheduler == null) mScheduler = Scheduler.getInstance();
		mScheduler.save(this);
	}

	protected void restoreFrag(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Intent i = getIntent();
			Bundle args = new Bundle();

			if (i.getStringExtra(HoverAction.ID) == null) {
				sendGatewayBroadcast(RESULT_CANCELED, new Intent(i).putExtra("error", "No Action ID specified"));
				return;
			}

			args.putAll(i.getExtras());
			args.putString(HoverAction.ID, i.getStringExtra(HoverAction.ID));

			ActionDetailFragment fragment = new ActionDetailFragment();
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.action_detail, fragment)
					.commit();
		}
	}

	protected void makeRequest(HoverParameters.Builder hpb, ActionDetailFragment frag) {
		if (BuildConfig.BUILD_TYPE.equals("debug")) hpb.setEnvironment(HoverParameters.DEBUG_ENV);
		hpb.extra("pin", frag.mAction.getPin(this));
		startActivityForResult(hpb.buildIntent(), 0);
	}

	void sendGatewayBroadcast(int resultCode, Intent data) {
		Intent i = new Intent(getPackageName() + TransactionReceiver.TRANSACTION_UPDATED);
		i.putExtra(HoverAction.ID, data.getStringExtra(HoverAction.ID));
		if (resultCode == RESULT_CANCELED) {
			i.putExtra("cmd", "done");
			i.putExtra(Contract.StatusReportEntry.COLUMN_FAILURE_MESSAGE, data.getStringExtra("error"));
		} else {
			i.putExtra("cmd", "update");
			i.putExtra(Contract.StatusReportEntry.COLUMN_FINAL_SESSION_MSG, data.getStringExtra("response_message"));
		}
		sendBroadcast(i);
		finish();
	}

	public void addSchedule(View view) {
		try {
			if (!getFrag().hasMissingExtras())
				addSchedule(getFrag().mAction.mId);
			else if (getFrag().getView() != null)
				Snackbar.make(getFrag().getView(), "Please fill out all variable fields before setting a Schedule", Snackbar.LENGTH_LONG).show();
		} catch (NullPointerException e) {
			Crashlytics.logException(e);
			Toast.makeText(this, "Could not start scheduler, try again", Toast.LENGTH_SHORT).show();
		}
	}
}
