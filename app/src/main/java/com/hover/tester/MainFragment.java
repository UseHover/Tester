package com.hover.tester;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;
import com.hover.tester.network.NetworkOps;
import com.hover.tester.services.OperatorService;
import com.hover.tester.services.ServiceAdapter;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String TAG = "MainFragment";
	public static final int SERVICE_LOADER = 0;
	public OnListFragmentInteractionListener mListener;
	private ServiceAdapter mServiceAdapter;

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
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.service_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		mServiceAdapter = new ServiceAdapter(getActivity(), null, this);
		recyclerView.setAdapter(mServiceAdapter);
		getLoaderManager().initLoader(SERVICE_LOADER, null, this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		controlFlow();
	}

	void controlFlow() {
		Log.e(TAG, "controlling flow");
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
		Log.e(TAG, "asking for perms");
		view.findViewById(R.id.add_integration_btn).setVisibility(View.GONE);
		view.findViewById(R.id.grant_permissions_btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.internet_message).setVisibility(View.GONE);
	}

	private void askForNet(View view) {
		Log.e(TAG, "asking for net");
		view.findViewById(R.id.add_integration_btn).setVisibility(View.GONE);
		view.findViewById(R.id.grant_permissions_btn).setVisibility(View.GONE);
		view.findViewById(R.id.internet_message).setVisibility(View.VISIBLE);
		if (getActivity() != null)
			((MainActivity) getActivity()).registerNetReceiver();
	}
	private void showIntegrations(View view) {
		Log.e(TAG, "showing integrations");
		view.findViewById(R.id.add_integration_btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.grant_permissions_btn).setVisibility(View.GONE);
		view.findViewById(R.id.internet_message).setVisibility(View.GONE);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		if (id == SERVICE_LOADER)
			return new CursorLoader(getActivity(), Contract.OperatorServiceEntry.CONTENT_URI, Contract.SERVICE_PROJECTION, null, null, null);
//		else
//			return new CursorLoader(getActivity(), Contract.OperatorActionEntry.CONTENT_URI, Contract.ACTION_PROJECTION,
//					Contract.OperatorActionEntry.COLUMN_SERVICE_ID + " = " + id, null, null);

	}
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mServiceAdapter.swapCursor(cursor);
		Log.d(TAG, "cursor count: " + mServiceAdapter.getItemCount());
		setEmptyState(mServiceAdapter.getItemCount() > 0);
//		if (mOpService != null && mOpService.mActions.size() != mServiceAdapter.getItemCount())
//			mOpService.saveActions(getActivity());
	}
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mServiceAdapter.swapCursor(null);
	}

	public void update() {
		getLoaderManager().restartLoader(SERVICE_LOADER, null, this);
	}

	private void setEmptyState(Boolean areServices) {
		if (getView() == null) return;
		getView().findViewById(R.id.service_list).setVisibility(areServices ? View.VISIBLE : View.GONE);
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
