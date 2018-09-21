package com.hover.tester.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TesterContentProvider extends ContentProvider {
	DbHelper mDatabaseHelper;
	private static final String AUTHORITY = Contract.CONTENT_AUTHORITY;
	public static final int ROUTE_ACTIONS = 1, ROUTE_ACTIONS_ID = 2,
			ROUTE_VARIABLES = 3, ROUTE_VARIABLES_ID = 4,
			ROUTE_RESULTS = 5, ROUTE_RESULTS_ID = 6,
			ROUTE_SERVICES = 7, ROUTE_SERVICES_ID = 8,
			ROUTE_SCHEDULES = 9, ROUTE_SCHEDULES_ID = 10,
			ROUTE_REPORTS = 11, ROUTE_REPORTS_ID = 12;

	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(AUTHORITY, "reports", ROUTE_REPORTS);
		sUriMatcher.addURI(AUTHORITY, "reports/*", ROUTE_REPORTS_ID);
		sUriMatcher.addURI(AUTHORITY, "services", ROUTE_SERVICES);
		sUriMatcher.addURI(AUTHORITY, "services/*", ROUTE_SERVICES_ID);
		sUriMatcher.addURI(AUTHORITY, "actions", ROUTE_ACTIONS);
		sUriMatcher.addURI(AUTHORITY, "actions/*", ROUTE_ACTIONS_ID);
		sUriMatcher.addURI(AUTHORITY, "schedules", ROUTE_SCHEDULES);
		sUriMatcher.addURI(AUTHORITY, "schedules/*", ROUTE_SCHEDULES_ID);
		sUriMatcher.addURI(AUTHORITY, "variables", ROUTE_VARIABLES);
		sUriMatcher.addURI(AUTHORITY, "variables/*", ROUTE_VARIABLES_ID);
		sUriMatcher.addURI(AUTHORITY, "results", ROUTE_RESULTS);
		sUriMatcher.addURI(AUTHORITY, "results/*", ROUTE_RESULTS_ID);
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,	String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		int uriMatch = sUriMatcher.match(uri);
		Cursor cursor;
		switch (uriMatch) {
			case ROUTE_REPORTS:
				cursor = db.query(Contract.StatusReportEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_REPORTS_ID:
				cursor = db.query(Contract.StatusReportEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_ACTIONS:
				cursor = db.query(Contract.HoverActionEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_ACTIONS_ID:
				cursor = db.query(Contract.HoverActionEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_SCHEDULES:
				cursor = db.query(Contract.ActionScheduleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_SCHEDULES_ID:
				cursor = db.query(Contract.ActionScheduleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_VARIABLES:
				cursor = db.query(Contract.ActionVariableEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_VARIABLES_ID:
				cursor = db.query(Contract.ActionVariableEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_RESULTS:
				cursor = db.query(Contract.ActionResultEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			case ROUTE_RESULTS_ID:
				cursor = db.query(Contract.ActionResultEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder); break;
			default:
				throw new UnsupportedOperationException("Not yet implemented");
		}
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		assert db != null;
		final int match = sUriMatcher.match(uri);
		int count;
		switch (match) {
			case ROUTE_REPORTS:
				count = db.update(Contract.StatusReportEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_REPORTS_ID:
				count = db.update(Contract.StatusReportEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_ACTIONS:
				count = db.update(Contract.HoverActionEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_ACTIONS_ID:
				count = db.update(Contract.HoverActionEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_SCHEDULES:
				count = db.update(Contract.ActionScheduleEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_SCHEDULES_ID:
				count = db.update(Contract.ActionScheduleEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_VARIABLES:
				count = db.update(Contract.ActionVariableEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_VARIABLES_ID:
				count = db.update(Contract.ActionVariableEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_RESULTS:
				count = db.update(Contract.ActionResultEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ROUTE_RESULTS_ID:
				count = db.update(Contract.ActionResultEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Send broadcast to registered ContentObservers, to refresh UI.
		Context ctx = getContext();
		assert ctx != null;
		ctx.getContentResolver().notifyChange(uri, null, false);
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		assert db != null;
		final int match = sUriMatcher.match(uri);
		long id;
		Uri result;
		switch (match) {
			case ROUTE_REPORTS:
				id = db.insertOrThrow(Contract.StatusReportEntry.TABLE_NAME, null, values);
				result = Uri.parse(Contract.StatusReportEntry.CONTENT_URI + "/" + id);
				break;
			case ROUTE_ACTIONS:
				id = db.replace(Contract.HoverActionEntry.TABLE_NAME, null, values);
				result = Uri.parse(Contract.HoverActionEntry.CONTENT_URI + "/" + id);
				break;
			case ROUTE_SCHEDULES:
				id = db.insertOrThrow(Contract.ActionScheduleEntry.TABLE_NAME, null, values);
				result = Uri.parse(Contract.ActionScheduleEntry.CONTENT_URI + "/" + id);
				break;
			case ROUTE_VARIABLES:
				id = db.insertOrThrow(Contract.ActionVariableEntry.TABLE_NAME, null, values);
				result = Uri.parse(Contract.ActionVariableEntry.CONTENT_URI + "/" + id);
				break;
			case ROUTE_RESULTS:
				id = db.insertOrThrow(Contract.ActionResultEntry.TABLE_NAME, null, values);
				result = Uri.parse(Contract.ActionResultEntry.CONTENT_URI + "/" + id);
				break;
			case ROUTE_REPORTS_ID:
			case ROUTE_SERVICES_ID:
			case ROUTE_ACTIONS_ID:
			case ROUTE_SCHEDULES_ID:
			case ROUTE_VARIABLES_ID:
			case ROUTE_RESULTS_ID:
				throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Send broadcast to registered ContentObservers, to refresh UI.
		Context ctx = getContext();
		assert ctx != null;
		ctx.getContentResolver().notifyChange(uri, null, false);
		return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
			case ROUTE_REPORTS:
				return Contract.StatusReportEntry.CONTENT_TYPE;
			case ROUTE_REPORTS_ID:
				return Contract.StatusReportEntry.CONTENT_ITEM_TYPE;
			case ROUTE_ACTIONS:
				return Contract.HoverActionEntry.CONTENT_TYPE;
			case ROUTE_ACTIONS_ID:
				return Contract.HoverActionEntry.CONTENT_ITEM_TYPE;
			case ROUTE_SCHEDULES:
				return Contract.ActionScheduleEntry.CONTENT_TYPE;
			case ROUTE_SCHEDULES_ID:
				return Contract.ActionScheduleEntry.CONTENT_ITEM_TYPE;
			case ROUTE_VARIABLES:
				return Contract.ActionVariableEntry.CONTENT_TYPE;
			case ROUTE_VARIABLES_ID:
				return Contract.ActionVariableEntry.CONTENT_ITEM_TYPE;
			case ROUTE_RESULTS:
				return Contract.ActionResultEntry.CONTENT_TYPE;
			case ROUTE_RESULTS_ID:
				return Contract.ActionResultEntry.CONTENT_ITEM_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
}
