package com.hover.tester;

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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hover.tester.actions.ActionAdapter;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.services.OperatorService;
import com.hover.tester.services.SaveFinishedListener;
import com.hover.tester.services.ServiceAdapter;

import java.util.HashMap;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SaveFinishedListener {
	public static final String TAG = "MainFragment";
	public static final int SERVICE_LOADER = 0;
	public OnListFragmentInteractionListener mListener;
	private ServiceAdapter mServiceAdapter;
	private SparseArray<ActionAdapter> mActionAdapters;

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
		createServiceList(view);
		return view;
	}

	private void createServiceList(View view) {
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.service_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		mServiceAdapter = new ServiceAdapter(getActivity(), null, this);
		recyclerView.setAdapter(mServiceAdapter);
		getLoaderManager().initLoader(SERVICE_LOADER, null, this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		controlFlow();
	}

	void controlFlow() {
		View view = getView();
		if (MainActivity.hasPhonePerm(getContext()) && NetworkOps.isConnected(getContext()) && OperatorService.count(getContext()) == 0 && getActivity() != null)
			((MainActivity) getActivity()).getServices();

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
		if (id == SERVICE_LOADER)
			return new CursorLoader(getActivity(), Contract.OperatorServiceEntry.CONTENT_URI, Contract.SERVICE_PROJECTION, null, null, null);
		else
			return new CursorLoader(getContext(), Contract.OperatorActionEntry.CONTENT_URI, Contract.ACTION_PROJECTION,
					Contract.OperatorActionEntry.COLUMN_SERVICE_ID + " = " + id, null, null);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getView() != null) ((ContentLoadingProgressBar) getView().findViewById(R.id.loading_progress)).hide();
		if (loader.getId() == SERVICE_LOADER) {
			mServiceAdapter.swapCursor(cursor);
			Log.e(TAG, "service cursor count: " + mServiceAdapter.getItemCount());
		} else if (mActionAdapters != null && mActionAdapters.get(loader.getId()) != null) {
			mActionAdapters.get(loader.getId()).swapCursor(cursor);
			Log.e(TAG, "service " + loader.getId() + " actions cursor count: " + mActionAdapters.get(loader.getId()).getItemCount());
		}
	}
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == SERVICE_LOADER)
			mServiceAdapter.swapCursor(null);
		else if (mActionAdapters != null && mActionAdapters.get(loader.getId()) != null)
			mActionAdapters.get(loader.getId()).swapCursor(null);
	}

	public void createActionAdapter(int id) {
		if (getView() != null && getView().findViewWithTag(id) != null) {
			RecyclerView list = (RecyclerView) getView().findViewWithTag(id);
			ActionAdapter adapter = new ActionAdapter(getContext(), null, mListener);
			list.setAdapter(adapter);
			if (mActionAdapters == null)
				mActionAdapters = new SparseArray<>();
			mActionAdapters.put(id, adapter);
			getLoaderManager().initLoader(id, null, this);
		}
	}

	@Override
	public void onSaveCompleted() {
		update();
	}
	public void update() {
		getLoaderManager().restartLoader(SERVICE_LOADER, null, this);
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
