package com.hover.tester.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
	public Contract() {}

	public static final String CONTENT_AUTHORITY = "com.hover.tester.provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	private static final String PATH_SERVICES = "services", PATH_ACTIONS = "actions";

	public static abstract class OperatorServiceEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tester.services";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tester.service";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SERVICES).build();
		public static final String TABLE_NAME = "services";
		public static final String COLUMN_ENTRY_ID = "_id";
		public static final String COLUMN_SERVICE_ID = "service_id";
		public static final String COLUMN_NAME = "service_name";
		public static final String COLUMN_SLUG = "service_slug";
		public static final String COLUMN_CURRENCY = "service_currency";
		public static final String COLUMN_COUNTRY = "service_country";
		public static final String COLUMN_ACTIONS = "service_actions";
	}

	public static abstract class OperatorActionEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tester.actions";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tester.action";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIONS).build();
		public static final String TABLE_NAME = "actions";
		public static final String COLUMN_ENTRY_ID = "_id";
		public static final String COLUMN_NAME = "action_name";
		public static final String COLUMN_SLUG = "action_slug";
		public static final String COLUMN_SERVICE_ID = "action_service_id";
		public static final String COLUMN_VARIABLES = "action_variables";
		public static final String COLUMN_LAST_RUN = "action_timestamp";
		public static final String COLUMN_LAST_STATUS = "action_status";
		public static final String COLUMN_LAST_RESULT = "action_result";
		public static final String COLUMN_RESULTS = "action_results";
	}

	public static String[] serviceColumns = {
		OperatorServiceEntry.COLUMN_ENTRY_ID,
		OperatorServiceEntry.COLUMN_SERVICE_ID,
		OperatorServiceEntry.COLUMN_NAME,
		OperatorServiceEntry.COLUMN_SLUG,
		OperatorServiceEntry.COLUMN_CURRENCY,
		OperatorServiceEntry.COLUMN_COUNTRY,
		OperatorServiceEntry.COLUMN_ACTIONS
	};

	public static String[] actionColumns = {
		OperatorActionEntry.COLUMN_ENTRY_ID,
		OperatorActionEntry.COLUMN_NAME,
		OperatorActionEntry.COLUMN_SLUG,
		OperatorActionEntry.COLUMN_SERVICE_ID,
		OperatorActionEntry.COLUMN_VARIABLES,
		OperatorActionEntry.COLUMN_LAST_RUN,
		OperatorActionEntry.COLUMN_LAST_STATUS,
		OperatorActionEntry.COLUMN_LAST_RESULT,
		OperatorActionEntry.COLUMN_RESULTS,
	};
}
