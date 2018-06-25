package com.hover.tester.actions;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hover.tester.main.MainActivity;
import com.hover.tester.schedules.Scheduler;
import com.hover.tester.R;
import com.hover.tester.wake.WakeUpHelper;

public class ActionDetailFragment extends AbstractActionDetailFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String TAG = "ActionDetailFragment";
	Scheduler mSchedule;

	public ActionDetailFragment() { }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAction == null) {
			((ActionDetailActivity) getActivity()).sendGatewayBroadcast(Activity.RESULT_CANCELED, new Intent().putExtra("error", "Action not found. Has it been added in the user interface?"));
			return;
		}
		mSchedule = Scheduler.load(mAction.mId, getActivity());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mAction != null) {
			if (getActivity() != null && getArguments().containsKey(WakeUpHelper.SOURCE)) {
				if (!MainActivity.meetsAllRequirements(getActivity())) {
					String msg = "Permissions required. App needs manual intervention";
					Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
					((ActionDetailActivity) getActivity()).sendGatewayBroadcast(Activity.RESULT_CANCELED, new Intent().putExtra("error", msg));
				} else
					((ActionDetailActivity) getActivity()).makeRequest(getArguments());
			}
		}
	}

	protected void fillInfo(View view) {
		((ActionDetailActivity) getActivity()).setTitle(mAction.mId + ". " + mAction.mName, mAction.mNetworkName);
		view.findViewById(R.id.scheduler).setVisibility(View.VISIBLE);
		if (mSchedule != null) {
			view.findViewById(R.id.add_schedule).setVisibility(View.GONE);
			((TextView) view.findViewById(R.id.schedule_text)).setText(getString(R.string.schedule, mSchedule.getString(getActivity())));
			view.findViewById(R.id.schedule_info).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.add_schedule).setVisibility(View.VISIBLE);
			view.findViewById(R.id.schedule_info).setVisibility(View.GONE);
		}
	}
}
