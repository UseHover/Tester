package com.hover.tester.services;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hover.tester.R;
import com.hover.tester.database.RecyclerViewCursorAdapter;
import com.hover.tester.main.MainFragment;

public class ServiceAdapter extends RecyclerViewCursorAdapter<ServiceAdapter.ViewHolder> {
	public static final String TAG = "ServiceAdapter";
	private MainFragment mFrag;

	public ServiceAdapter(Context context, Cursor cursor, MainFragment frag) {
		super(context, cursor);
		mFrag = frag;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_li, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
		holder.mService = new OperatorService(cursor, getContext());
		holder.mActionsView.setTag(holder.mService.mId);
		holder.mNameView.setText(holder.mService.mName);
	}

	@Override
	public void onViewAttachedToWindow(ViewHolder holder) {
		mFrag.createActionAdapter(holder.mService.mId);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mNameView;
		public final RecyclerView mActionsView;
		public OperatorService mService;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mNameView = (TextView) view.findViewById(R.id.name);
			mActionsView = (RecyclerView) view.findViewById(R.id.action_list);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mNameView.getText() + "'";
		}
	}
}
