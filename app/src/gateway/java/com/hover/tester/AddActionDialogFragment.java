package com.hover.tester;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.hover.tester.network.HoverIntegratonListService;


public class AddActionDialogFragment extends DialogFragment {
	public final static String TAG = "AddActionDialogFragment", STEP = "step", TITLE = "title", ID = "id";
	public static int CHOOSE_ACTION_STEP = 0, ENTER_DETAILS_STEP = 1, CHOOSE_TRIGGER_STEP = 2,
			FCM_DEETS_STEP = 3, SCHEDULE_DEETS_STEP = 4,
			SCHEDULE_TRIGGER = 0, FCM_TRIGGER = 1;
	private int mStep, mId;

	public AddActionDialogFragment() { }

	public static AddActionDialogFragment newInstance(int step, int id) {
		AddActionDialogFragment frag = new AddActionDialogFragment();
		Bundle args = new Bundle();
		args.putInt(STEP, step);
		args.putInt(ID, id);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStep = getArguments().getInt(STEP);
		mId = getArguments().getInt(ID);
		if (mStep == CHOOSE_TRIGGER_STEP)
			return actionTriggerDialog();
		else if (mStep == FCM_DEETS_STEP)
			return fcmDialog();
		else if (mStep == SCHEDULE_DEETS_STEP)
			return scheduleDialog();
		else
			return actionChoiceDialog();
	}

	@Override
	public void onResume() {
		super.onResume();
//		if (mStep == ENTER_PIN_STEP && getDialog() != null && getDialog().findViewById(R.id.pin_entry) != null)
//			addTextWatcher((EditText) getDialog().findViewById(R.id.pin_entry), (AlertDialog) getDialog());
	}

	private AlertDialog actionChoiceDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_action)
				.setItems(HoverIntegratonListService.getActionsList(mId, getActivity()), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						((MainActivity) getActivity()).addAction(mId, i);
					}
				});
		return builder.create();
	}

	private AlertDialog actionTriggerDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_trigger)
				.setItems(R.array.trigger_choices, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						((MainActivity) getActivity()).setTrigger(mId, i);
					}
				});
		return builder.create();
	}

	private AlertDialog fcmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.enter_fcm_deets);
		return builder.create();
	}

	private AlertDialog scheduleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.set_schedule);
		return builder.create();
	}
}
