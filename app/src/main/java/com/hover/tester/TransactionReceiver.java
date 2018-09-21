package com.hover.tester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hover.tester.actions.ActionDetailActivity;
import com.hover.tester.actions.ActionResult;
import com.hover.tester.actions.HoverAction;
import com.hover.tester.database.Contract;
import com.hover.tester.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransactionReceiver extends BroadcastReceiver {
	public final static String TAG = "TransactionReceiver", GATEWAY_UPDATE = "TRANSACTION_UPDATED";
	public TransactionReceiver() { }

	@Override
	public void onReceive(Context context, Intent i) {
		Log.e(TAG, "Transaction received. Trans id: " + i.getStringExtra("uuid") + ", Action: " + i.getStringExtra(Utils.ACTION));
		ActionResult ar = ActionResult.getByUuid(i.getStringExtra("uuid"), context);
		if (ar != null) {
			Log.e(TAG, "Updating result");
			ar.mStatus = ActionResult.STATUS_SUCCEEDED;
			ar.mTimeStamp = i.getLongExtra("response_timestamp", 0L);
			ar.mText = i.getStringExtra("response_message");
			ar.save(context);
		}
		sendGatewayBroadcast(context, i);
		openActivity(context, i);
	}

	private void openActivity(Context c, Intent i) {
		i = new Intent(i);
		i.setClass(c, ActionDetailActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		i.putExtra(HoverAction.ID, i.getStringExtra(HoverAction.ID));
		c.startActivity(i);
	}

	private void sendGatewayBroadcast(Context c, Intent intent) {
		Intent i = new Intent(c.getPackageName() + GATEWAY_UPDATE);
		i.putExtra("cmd", "done");
		i.putExtra(HoverAction.ID, intent.getStringExtra(HoverAction.ID));
		i.putExtra("status", "success");
		i.putExtra(Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE, intent.getStringExtra("response_message"));
		i.putExtra("transaction", convertTinfoToJsonString(intent.getExtras(), new JSONObject()));
		c.sendBroadcast(i);
	}

	public static String convertTinfoToJsonString(Bundle extras, JSONObject json) {
		for (String key : extras.keySet()) {
			if (extras.get(key) != null) {
				try {
					if (key.equals("transaction_extras")) {
						for (Map.Entry<String, String> entry : ((HashMap<String, String>) extras.get(key)).entrySet())
							json.put(entry.getKey(), entry.getValue());
					}
					else if (extras.get(key) != null && !extras.get(key).toString().isEmpty())
						json.put(key, extras.get(key).toString());
				} catch (NullPointerException | JSONException e) { }
			}
		}
		return json.toString();
	}

	private void printExtras(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				if (value != null)
					Log.i(TAG, "key: " + key + ", value: " + value.toString());
			}
		}
		Log.e(TAG, bundle.toString());
	}
}
