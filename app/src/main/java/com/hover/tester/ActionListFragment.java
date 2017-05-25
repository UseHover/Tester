package com.hover.tester;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActionListFragment extends Fragment {
	public static final String TAG = "ActionListFragment";
	private OnListFragmentInteractionListener mListener;
	private ActionRecyclerViewAdapter mAdapter;
	private OperatorService mOpService;

	public ActionListFragment() { }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mListener = (OnListFragmentInteractionListener) context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_action_list, container, false);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		recyclerView.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (OperatorService.savedServiceExists(getActivity()))
			update(new OperatorService(getActivity()), getView());
	}

	public void update(OperatorService opService, View view) {
		mOpService = opService;
		Log.e(TAG, "operator: " + opService.mName);
		Log.e(TAG, "operator action length: " + opService.mActions.size());
		mAdapter = new ActionRecyclerViewAdapter(opService.mActions, mListener);
		((RecyclerView) view.findViewById(R.id.list)).swapAdapter(mAdapter, true);
		showActions(view);
		if (isAdded())
			((MainActivity) getActivity()).setToolbarTitle(mOpService);
	}

	private void showActions(View view) {
		view.findViewById(R.id.actions_section).setVisibility(View.VISIBLE);
		view.findViewById(R.id.add_integration_btn).setVisibility(View.GONE);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnListFragmentInteractionListener {
		void onListFragmentInteraction(OperatorAction act);
	}
}
