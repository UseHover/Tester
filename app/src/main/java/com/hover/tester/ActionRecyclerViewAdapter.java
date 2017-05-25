package com.hover.tester;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hover.tester.ActionListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class ActionRecyclerViewAdapter extends RecyclerView.Adapter<ActionRecyclerViewAdapter.ViewHolder> {

	private final List<OperatorAction> mValues;
	private final OnListFragmentInteractionListener mListener;

	public ActionRecyclerViewAdapter(List<OperatorAction> items, OnListFragmentInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_action, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mAction = mValues.get(position);
		holder.mNameView.setText(holder.mAction.mName);

		if (holder.mAction.mLastRunTime != 0L)
			holder.mTimeStampView.setText(Utils.shortDateFormatTimestamp(holder.mAction.mLastRunTime));
		else
			holder.mTimeStampView.setText(R.string.never);

		switch (holder.mAction.mStatus) {
			case 0: holder.mStatusView.setImageResource(R.drawable.circle_fails); break;
			case 1: holder.mStatusView.setImageResource(R.drawable.circle_passes); break;
			case -1:
			default:
				holder.mStatusView.setImageResource(R.drawable.circle_untested);
		}

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) mListener.onListFragmentInteraction(holder.mAction);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final ImageView mStatusView;
		public final TextView mNameView;
		public final TextView mTimeStampView;
		public OperatorAction mAction;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mStatusView = (ImageView) view.findViewById(R.id.status_icon);
			mNameView = (TextView) view.findViewById(R.id.name);
			mTimeStampView = (TextView) view.findViewById(R.id.time);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mNameView.getText() + "'";
		}
	}
}
