package com.hover.tester.actions;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hover.tester.R;
import com.hover.tester.utils.Utils;
import com.hover.tester.database.RecyclerViewCursorAdapter;
import com.hover.tester.main.MainFragment.OnListFragmentInteractionListener;

public class ActionAdapter extends RecyclerViewCursorAdapter<ActionAdapter.ViewHolder> {
	private final OnListFragmentInteractionListener mListener;

	public ActionAdapter(Context context, Cursor cursor, OnListFragmentInteractionListener listener) {
		super(context, cursor);
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_li, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
		holder.mAction = new HoverAction(cursor, getContext());
		holder.mNameView.setText(holder.mAction.mId + ". " + holder.mAction.mName);

		if (holder.mAction.mLastResult != null) {
			if (holder.mAction.mLastResult.mTimeStamp != 0L)
				holder.mTimeStampView.setText(Utils.shortDateFormatTimestamp(holder.mAction.mLastResult.mTimeStamp));

			switch (holder.mAction.mLastResult.mStatus) {
				case 0:	holder.mStatusView.setImageResource(R.drawable.circle_fails); break;
				case 1:	holder.mStatusView.setImageResource(R.drawable.circle_passes); break;
				case 2:	holder.mStatusView.setImageResource(R.drawable.circle_unknown); break;
				case -1:
				default: holder.mStatusView.setImageResource(R.drawable.circle_untested);
			}
		}

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) mListener.onListFragmentInteraction(holder.mAction);
			}
		});
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final ImageView mStatusView;
		final TextView mNameView;
		final TextView mTimeStampView;
		public HoverAction mAction;

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
