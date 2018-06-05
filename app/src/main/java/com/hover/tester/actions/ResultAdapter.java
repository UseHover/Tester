package com.hover.tester.actions;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hover.tester.R;
import com.hover.tester.utils.Utils;
import com.hover.tester.database.RecyclerViewCursorAdapter;

public class ResultAdapter extends RecyclerViewCursorAdapter<ResultAdapter.ViewHolder> {
	public static final String TAG = "ResultAdapter";
	private HoverAction mAction;

	public ResultAdapter(Context context, Cursor cursor, HoverAction act) { super(context, cursor); mAction = act; }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_result, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
		holder.mResult = new ActionResult(cursor);
		holder.mStatusIcon.setImageResource(getIcon(holder));
		holder.mTextView.setText(holder.mResult.mText);
		holder.mTimestamp.setText(Utils.shortDateFormatTimestamp(holder.mResult.mTimeStamp));

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showResultDialog(holder);
			}
		});
	}

	private int getIcon(final ViewHolder holder) {
		switch (holder.mResult.mStatus) {
			case 0: return R.drawable.circle_fails;
			case 1: return R.drawable.circle_passes;
			case 2: return R.drawable.circle_unknown;
			case -1:
			default: return R.drawable.circle_untested;
		}
	}

	private void showResultDialog(final ViewHolder holder) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(getContext().getString(R.string.transaction_header, holder.mResult.mId))
				.setIcon(getIcon(holder))
				.setMessage(holder.mResult.mSdkUuid + "\r\n \r\n" + holder.mResult.mText + "\r\n \r\n" + holder.mResult.mDetails);
		AlertDialog dialog = builder.create();
		dialog.show();
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
