package com.hover.tester;

import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONException;

import java.util.HashMap;

public class ActionDetailFragment extends Fragment {
	public static final String TAG = "ActionDetailFragment";
	private OperatorAction mAction;

	public ActionDetailFragment() { }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(OperatorAction.SLUG) && getArguments().containsKey(OperatorService.ID)) {
			try {
				mAction = new OperatorAction(getArguments().getString(OperatorAction.SLUG), getArguments().getInt(OperatorService.ID), getActivity());
			} catch (JSONException e) {}

			CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
			if (appBarLayout != null)
				appBarLayout.setTitle(mAction.mName);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.action_detail, container, false);
		if (mAction != null) {
			for (HashMap.Entry<String, String> variable : mAction.mVariables.entrySet()) {
				View v = inflater.inflate(R.layout.action_variable, null);
				EditText et = (EditText) v.findViewById(R.id.edit);
				((TextInputLayout) v.findViewById(R.id.textinput)).setHint(variable.getKey());
				et.setText(variable.getValue());
				addListener(et, variable.getKey());
				((ViewGroup) root).addView(v);
			}
		}
		return root;
	}

	private void addListener(EditText et, final String variableName) {
		et.addTextChangedListener(new TextWatcher() {
			@Override public void afterTextChanged(Editable s) { }
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				mAction.saveVariableValue(getActivity(), variableName, s.toString());
			}
		});
	}
}
