package com.hover.tester.detail;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hover.tester.ActionResult;
import com.hover.tester.OperatorAction;
import com.hover.tester.R;
import com.hover.tester.Utils;
import com.hover.tester.database.RecyclerViewCursorAdapter;

public class ResultAdapter extends RecyclerViewCursorAdapter<ResultAdapter.ViewHolder> {
	public static final String TAG = "ResultAdapter";
	private OperatorAction mAction;

	public ResultAdapter(Context context, Cursor cursor, OperatorAction act) { super(context, cursor); mAction = act; }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_result, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
		holder.mResult = new ActionResult(cursor);
		switch (holder.mResult.mStatus) {
			case 0: holder.mStatusIcon.setImageResource(R.drawable.circle_fails); break;
			case 1: holder.mStatusIcon.setImageResource(R.drawable.circle_passes); break;
			case 2: holder.mStatusIcon.setImageResource(R.drawable.circle_unknown); break;
			case -1:
			default: holder.mStatusIcon.setImageResource(R.drawable.circle_untested); break;
		}
		holder.mTextView.setText(holder.mResult.mText);
		holder.mTimestamp.setText(Utils.shortDateFormatTimestamp(holder.mResult.mTimeStamp));
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final ImageView mStatusIcon;
		final TextView mTextView, mTimestamp;
		public ActionResult mResult;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mStatusIcon = (ImageView) view.findViewById(R.id.status_icon);
			mTextView = (TextView) view.findViewById(R.id.text);
			mTimestamp = (TextView) view.findViewById(R.id.timestamp);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mTextView.getText() + "'";
		}
	}
}
