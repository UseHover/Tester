package com.hover.tester.network;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.hover.tester.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkOps {
	public final static String TAG = "NetworkOps";
	private Context mContext;
	public int responseCode;
	public String responseMessage;

	public NetworkOps(Context c) {
		mContext = c;
	}

	private HttpsURLConnection makeRequest(String urlEnd, String type, JSONObject data) throws IOException {
		URL url = new URL(mContext.getString(R.string.url_builder, mContext.getString(R.string.base_url), urlEnd));
		return makeRequest(url, type, data);
	}
	private HttpsURLConnection makeRequest(URL url, String type, JSONObject data) throws IOException {
		if (!isConnected(mContext)) throw new IOException(mContext.getString(R.string.error_network));

		Log.i(TAG, "hitting URl: " + url);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Token token=" + getApiKey(mContext));
		conn.setRequestMethod(type);
		if (type.equals("POST") || type.equals("PUT"))
			addData(conn, data);
		else
			conn.setDoInput(true);
		conn.connect();
		return conn;
	}

	public static boolean isConnected(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	private void addData(HttpsURLConnection conn, JSONObject data) throws IOException {
		conn.setDoOutput(true);
		conn.setChunkedStreamingMode(0);
		OutputStream out = new BufferedOutputStream(conn.getOutputStream());
		OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
		osw.write(data.toString());
		osw.flush();
		osw.close();
		out.close();
	}

	public JSONObject convertStringToJson(String response) throws JSONException {
		return new JSONObject(response);
	}

	public String download(String urlEnd) throws IOException {
		InputStream is = null;
		Log.i(TAG, "making request: " + urlEnd);
		HttpsURLConnection conn = makeRequest(urlEnd, "GET", null);
		responseCode = conn.getResponseCode();
		responseMessage = conn.getResponseMessage();
		Log.i(TAG, "response code: " + responseCode);
		if (responseCode == 200) {
			is = conn.getInputStream();
			return convertStreamToString(is);
		}
		if (is != null) { try { is.close(); } catch (IOException e) {} }
		return "failure";
	}

	public String upload(String url, JSONObject object, String httpType) throws IOException {
		HttpsURLConnection conn = makeRequest(new URL(url), httpType, object);
		responseCode = conn.getResponseCode();
		Log.i(TAG, "response code: " + responseCode);
		InputStream is = conn.getInputStream();
		String response = convertStreamToString(is);
		if (is != null) { is.close(); }
		return response;
	}

	private String getApiKey(Context c) {
		try {
			ApplicationInfo ai = c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
			Log.i(TAG, "apikey found: " + ai.metaData.getString("com.hover.ApiKey"));
			return ai.metaData.getString("com.hover.ApiKey");
		} catch (PackageManager.NameNotFoundException e) {
		}
		return null;
	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			Log.d(TAG, "Failed to convert download to String", e);
			Crashlytics.logException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.d(TAG, "Failed close input stream", e);
				Crashlytics.logException(e);
			}
		}
		return sb.toString();
	}
}
