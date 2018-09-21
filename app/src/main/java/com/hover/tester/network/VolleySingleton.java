package com.hover.tester.network;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hover.sdk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class VolleySingleton {
	public final static String TAG = "VolleySingleton";
	private static VolleySingleton mInstance;
	private RequestQueue mRequestQueue;
	private static Context mCtx;

	private VolleySingleton(Context context) {
		mCtx = context;
		mRequestQueue = getRequestQueue();
	}

	public static synchronized VolleySingleton getInstance(Context context) {
		if (mInstance == null)
			mInstance = new VolleySingleton(context);
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null)
			mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new CustomHurlStack());
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

	public static JSONObject uploadNow(Context c, int requestType, String url_end, JSONObject json) throws InterruptedException, ExecutionException, TimeoutException {
		String url = c.getString(R.string.hsdk_url_builder, c.getString(R.string.hsdk_base_url), url_end);
		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		VolleySingleton.getInstance(c).addToRequestQueue(new JsonObjectRequest(requestType, url, json, future, future));
//		{
//			@Override
//			protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//				if (response != null) {
//					int status = response.statusCode;
//				}
//				return super.parseNetworkResponse(response);
//			}
//		});
		return future.get(30, TimeUnit.SECONDS);
	}
	public static JSONObject uploadNow(Context c, int requestType, String url_end, JSONObject json, Response.Listener<JSONObject> listener, Response.ErrorListener eListener) throws InterruptedException, ExecutionException, TimeoutException {
		String url = c.getString(R.string.hsdk_url_builder, c.getString(R.string.hsdk_base_url), url_end);
		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		VolleySingleton.getInstance(c).addToRequestQueue(new JsonObjectRequest(requestType, url, json, listener, eListener));
		return future.get(30, TimeUnit.SECONDS);
	}

	public static JSONObject downloadNow(Context c, String url_end) throws InterruptedException, ExecutionException, TimeoutException {
		String url = c.getString(R.string.hsdk_url_builder, c.getString(R.string.hsdk_base_url), url_end);
		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		VolleySingleton.getInstance(c).addToRequestQueue(new JsonObjectRequest(url, null, future, future));
		return future.get(30, TimeUnit.SECONDS);
	}
	public static JSONArray downloadArrayNow(Context c, String url_end) throws InterruptedException, ExecutionException, TimeoutException {
		String url = c.getString(R.string.hsdk_url_builder, c.getString(R.string.hsdk_base_url), url_end);
		RequestFuture<JSONArray> future = RequestFuture.newFuture();
		VolleySingleton.getInstance(c).addToRequestQueue(new JsonArrayRequest(url, future, future));
		return future.get(30, TimeUnit.SECONDS);
	}

	public static String uploadJsonNow(Context c, int requestType, String url_end, final JSONObject json) throws InterruptedException, ExecutionException, TimeoutException {
		return uploadJsonNowAbsolute(c, requestType, c.getString(R.string.hsdk_url_builder, c.getString(R.string.hsdk_base_url), url_end), json);
	}
	public static String uploadJsonNowAbsolute(Context c, int requestType, String full_url, final JSONObject json) throws InterruptedException, ExecutionException, TimeoutException {
		RequestFuture<String> future = RequestFuture.newFuture();
		VolleySingleton.getInstance(c).addToRequestQueue(new StringRequest(requestType, full_url, future, future) {
			@Override
			public byte[] getBody() throws AuthFailureError { return json.toString().getBytes(); }
			@Override
			public String getBodyContentType() { return "application/json"; }
		});
		return future.get(30, TimeUnit.SECONDS);
	}
	public static void download(Context c, String url_end, Response.Listener<JSONObject> listener, Response.ErrorListener eListener) {
		String url = c.getString(R.string.hsdk_url_builder, c.getString(R.string.hsdk_base_url), url_end);
		VolleySingleton.getInstance(c).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, url, null, listener, eListener));
	}

	public static boolean isConnected(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	public class CustomHurlStack extends HurlStack {
		@Override
		protected HttpURLConnection createConnection(URL url) throws IOException {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Token token=" + getApiKey());
			return connection;
		}
	}

	private String getApiKey() {
		try {
			ApplicationInfo ai = mCtx.getPackageManager().getApplicationInfo(mCtx.getPackageName(), PackageManager.GET_META_DATA);
			Log.i(TAG, "apikey found: " + ai.metaData.getString("com.hover.ApiKey"));
			return ai.metaData.getString("com.hover.ApiKey");
		} catch (PackageManager.NameNotFoundException e) {
		}
		return null;
	}
}

