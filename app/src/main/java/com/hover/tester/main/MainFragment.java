package com.hover.tester.main;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hover.tester.R;
import com.hover.tester.actions.ActionAdapter;
import com.hover.tester.actions.HoverAction;
import com.hover.tester.database.Contract;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.actions.SaveFinishedListener;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SaveFinishedListener {
	public static final String TAG = "MainFragment";
	public OnListFragmentInteractionListener mListener;
	private ActionAdapter mActionAdapter;

	public MainFragment() { }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mListener = (OnListFragmentInteractionListener) context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		View view = inflater.inflate(R.layout.frag_main, container, false);
		createActionList(view);
		return view;
	}

	private void createActionList(View view) {
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.action_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		mActionAdapter = new ActionAdapter(getActivity(), null, (MainActivity) getActivity());
		recyclerView.setAdapter(mActionAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		controlFlow();
	}

	void controlFlow() {
		View view = getView();
		if (NetworkOps.isConnected(getContext()) && getActivity() != null) //&& MainActivity.hasPhonePerm(getContext()) && OperatorService.count(getContext()) == 0)
			((MainActivity) getActivity()).getActions();

		if (!MainActivity.meetsAllRequirements(getContext()))
			askForPerms(view);
		else if (!NetworkOps.isConnected(getContext()))
			askForNet(view);
		else
			showIntegrations(view);
	}
	private void askForPerms(View view) {
		view.findViewById(R.id.add_integration_btn).setVisibility(View.GONE);
		view.findViewById(R.id.grant_permissions_btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.internet_message).setVisibility(View.GONE);
	}

	private void askForNet(View view) {
		view.findViewById(R.id.add_integration_btn).setVisibility(View.GONE);
		view.findViewById(R.id.grant_permissions_btn).setVisibility(View.GONE);
		view.findViewById(R.id.internet_message).setVisibility(View.VISIBLE);
		if (getActivity() != null)
			((MainActivity) getActivity()).registerNetReceiver();
	}
	private void showIntegrations(View view) {
		view.findViewById(R.id.add_integration_btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.grant_permissions_btn).setVisibility(View.GONE);
		view.findViewById(R.id.internet_message).setVisibility(View.GONE);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getContext(), Contract.HoverActionEntry.CONTENT_URI, Contract.ACTION_PROJECTION,
				null, null, null);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getView() != null) ((ContentLoadingProgressBar) getView().findViewById(R.id.loading_progress)).hide();
		mActionAdapter.swapCursor(cursor);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mActionAdapter.swapCursor(null);
	}

	@Override
	public void onSaveCompleted() {
		update();
	}
	public void update() {
		Log.e(TAG, "Updating");
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnListFragmentInteractionListener {
		void onListFragmentInteraction(HoverAction act);
	}
}
