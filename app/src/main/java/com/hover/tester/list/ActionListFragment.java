package com.hover.tester.list;

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
import android.widget.TextView;

import com.hover.tester.OperatorAction;
import com.hover.tester.OperatorService;
import com.hover.tester.R;
import com.hover.tester.database.Contract;

public class ActionListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String TAG = "ActionListFragment";
	private OnListFragmentInteractionListener mListener;
	private ActionAdapter mAdapter;
	private OperatorService mOpService;
	private int lastOpServiceId;

	public ActionListFragment() { }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mListener = (OnListFragmentInteractionListener) context;
		lastOpServiceId = OperatorService.getLastUsedId(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_action_list, container, false);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		mAdapter = new ActionAdapter(getActivity(), null, mListener);
		recyclerView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		return view;
	}

//	@Override
//	public void onStart() {
//		super.onStart();
//		if (OperatorService.savedServiceExists(getActivity()))
//			update(new OperatorService(getActivity()), getView());
//	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "creating loader");
		return new CursorLoader(getActivity(), Contract.OperatorActionEntry.CONTENT_URI, Contract.ACTION_PROJECTION,
				Contract.OperatorActionEntry.COLUMN_SERVICE_ID + " = " + lastOpServiceId, null, null);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		Log.d(TAG, "cursor count: " + mAdapter.getItemCount());
		setEmptyState(mAdapter.getItemCount() > 0);
		if (mOpService != null && mOpService.mActions.size() != mAdapter.getItemCount())
			mOpService.saveActions(getActivity());
	}
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	public void update(OperatorService opService) {
		mOpService = opService;
		lastOpServiceId = opService.mId;
		Log.e(TAG, "operator: " + opService.mName);
		Log.e(TAG, "operator action length: " + opService.mActions.size());
		getLoaderManager().restartLoader(0, null, this);
	}

	private void setEmptyState(Boolean areActions) {
		if (getView() == null) return;
		getView().findViewById(R.id.actions_section).setVisibility(areActions ? View.VISIBLE : View.GONE);
		((TextView) getView().findViewById(R.id.add_integration_btn)).setText(areActions ? R.string.change_integration : R.string.add_integration);
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
