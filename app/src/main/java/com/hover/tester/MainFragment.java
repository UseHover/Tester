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
import android.widget.TextView;

import com.hover.tester.OperatorAction;
import com.hover.tester.OperatorService;
import com.hover.tester.R;
import com.hover.tester.database.Contract;
import com.hover.tester.list.ActionAdapter;

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
		View view = inflater.inflate(R.layout.frag_main, container, false);

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.service_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		mServiceAdapter = new ServiceAdapter(getActivity(), null, this);
		recyclerView.setAdapter(mServiceAdapter);
		getLoaderManager().initLoader(SERVICE_LOADER, null, this);
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
		if (id == SERVICE_LOADER)
			return new CursorLoader(getActivity(), Contract.OperatorServiceEntry.CONTENT_URI, Contract.SERVICE_PROJECTION, null, null, null);
		else
			return new CursorLoader(getActivity(), Contract.OperatorActionEntry.CONTENT_URI, Contract.ACTION_PROJECTION,
					Contract.OperatorActionEntry.COLUMN_SERVICE_ID + " = " + id, null, null);

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

	public void update(OperatorService opService) {
		mOpService = opService;
		lastOpServiceId = opService.mId;
		Log.e(TAG, "operator: " + opService.mName);
		Log.e(TAG, "operator action length: " + opService.mActions.size());
		getLoaderManager().restartLoader(0, null, this);
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
