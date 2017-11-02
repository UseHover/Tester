package com.hover.tester;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.tester.network.HoverIntegratonListService;


public class AddIntegrationDialogFragment extends DialogFragment {
	public final static String TAG = "AddServiceFragment", STEP = "step", ID = "service_id", TITLE = "title";
	public static int CHOOSE_SERVICE_STEP = 0, ENTER_PIN_STEP = 1, CHOOSE_ACTION_STEP = 2;
	private GatewayIntegrationInterface mListener;
	private int mStep;
	private String mPin;

	public AddIntegrationDialogFragment() { }

	public static AddIntegrationDialogFragment newInstance(int step, int serviceId, String title) {
		AddIntegrationDialogFragment frag = new AddIntegrationDialogFragment();
		Bundle args = new Bundle();
		args.putInt(STEP, step);
		if (serviceId != -1) args.putInt(ID, serviceId);
		if (title != null) args.putString(TITLE, title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onAttach(Context c) {
		super.onAttach(c);
		try { mListener = (GatewayIntegrationInterface) c;
		} catch (ClassCastException e) { Crashlytics.logException(e); }
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStep = getArguments().getInt(STEP);
		if (mStep == CHOOSE_ACTION_STEP)
			return actionChoiceDialog();
		if (mStep == ENTER_PIN_STEP)
			return createPinEntry();
		else
			return serviceChoiceDialog();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mStep == ENTER_PIN_STEP && getDialog() != null && getDialog().findViewById(R.id.pin_entry) != null)
			addTextWatcher((EditText) getDialog().findViewById(R.id.pin_entry), (AlertDialog) getDialog());
	}

	private AlertDialog serviceChoiceDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		CharSequence[] list = HoverIntegratonListService.getServicesList(getActivity());
		if (list.length == 0) {
			builder.setTitle(R.string.services_downloading).setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dismiss();
				}
			});
		} else {
			builder.setTitle(R.string.choose_service)
					.setItems(list, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							addIntegration(HoverIntegratonListService.getServiceId(i, getActivity()));
						}
					});
		}
		return builder.create();
	}

	public void addIntegration(int id) {
		Intent integrationIntent = new Intent(getActivity(), HoverIntegrationActivity.class);
		integrationIntent.putExtra(HoverIntegrationActivity.SERVICE_IDS, new int[] { id });
		getActivity().startActivityForResult(integrationIntent, MainActivity.INTEGRATE_REQUEST);
	}

	private AlertDialog.Builder pinEntryDialog(String serviceName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(serviceName)
			.setView(R.layout.pin_entry)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if (mListener != null) mListener.savePin(mPin);
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dismiss();
				}
		});
		return builder;
	}
	private AlertDialog createPinEntry() {
		AlertDialog dialog = pinEntryDialog(getArguments().getString(TITLE)).create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface d) {
				((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		});
		return dialog;
	}

	private void addTextWatcher(EditText et, final AlertDialog d) {
		et.requestFocus();
		et.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mPin = s.toString();
				if (mPin.length() > 0)
					d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
				else
					d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		});
	}

	private AlertDialog actionChoiceDialog() {
		final int id = getArguments().getInt(ID);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_action)
				.setItems(HoverIntegratonListService.getActionsList(id, getActivity()), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (mListener != null) mListener.addAction(id, i);
					}
				});
		return builder.create();
	}
}
