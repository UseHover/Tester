package com.hover.tester.actions;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hover.tester.R;
import com.hover.tester.database.Contract;
import com.hover.tester.database.RecyclerViewCursorAdapter;

public class VariableAdapter extends RecyclerViewCursorAdapter<VariableAdapter.ViewHolder> {
	public static final String TAG = "VariableAdapter";
	private HoverAction mAction;

	public VariableAdapter(Context context, Cursor cursor, HoverAction act) { super(context, cursor); mAction = act; }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_variable, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
		holder.mVariable = new ActionVariable(cursor.getString(cursor.getColumnIndex(Contract.ActionVariableEntry.COLUMN_NAME)),
			cursor.getString(cursor.getColumnIndex(Contract.ActionVariableEntry.COLUMN_VALUE)), mAction.mId);
		holder.mView.setTag(holder.mVariable.mName);
		holder.mTextLayout.setHint(holder.mVariable.mName);
		holder.mEdit.setText(holder.mVariable.mValue);
		holder.mEdit.addTextChangedListener(new TextWatcher() {
			@Override public void afterTextChanged(Editable s) { }
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				holder.mVariable.mValue = s.toString();
			}
		});
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final TextInputLayout mTextLayout;
		final EditText mEdit;
		public ActionVariable mVariable;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mTextLayout = (TextInputLayout) view.findViewById(R.id.textinput);
			mEdit = (EditText) view.findViewById(R.id.edit);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mTextLayout.getHint() + "': '" + mEdit.getText() + "'";
		}
	}
}
