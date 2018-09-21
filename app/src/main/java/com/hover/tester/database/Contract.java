package com.hover.tester.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
	public Contract() {}

	public static final String CONTENT_AUTHORITY = "com.hover.tester.provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	private static final String PATH_REPORTS = "reports", PATH_SERVICES = "services", PATH_ACTIONS = "actions",
			PATH_SCHEDULES = "schedules", PATH_VARIABLES = "variables", PATH_RESULTS = "results";

	public static abstract class StatusReportEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tester.reports";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tester.report";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPORTS).build();
		public static final String TABLE_NAME = "reports";
		public static final String COLUMN_ENTRY_ID = "_id";
		public static final String COLUMN_STATUS = "status";
		public static final String COLUMN_ACTION_ID = "action_id";
		public static final String COLUMN_TRANSACTION = "transaction_info";
		public static final String COLUMN_START_TIMESTAMP = "start_time";
		public static final String COLUMN_FINISH_TIMESTAMP = "end_time";
		public static final String COLUMN_FAILURE_MESSAGE = "fail_msg";
		public static final String COLUMN_FINAL_SESSION_MSG = "final_session_msg";
		public static final String COLUMN_CONFIRMATION_MESSAGE = "confirm_msg";
		public static final String COLUMN_EXTRAS = "extras";
	}

	public static abstract class HoverActionEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tester.actions";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tester.action";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIONS).build();
		public static final String TABLE_NAME = "actions";
		public static final String COLUMN_ENTRY_ID = "_id";
		public static final String COLUMN_NAME = "action_name";
		public static final String COLUMN_NETWORK_NAME = "action_network";
		public static final String COLUMN_SIM_ID = "action_sim";
		public static final String COLUMN_VARIABLES = "action_variables";
		public static final String COLUMN_PIN = "action_pin";
	}

	public static abstract class ActionScheduleEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tester.schedules";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tester.schedule";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULES).build();
		public static final String TABLE_NAME = "schedules";
		public static final String COLUMN_ENTRY_ID = "_id";
		public static final String COLUMN_ACTION_ID = "schedule_action_id";
		public static final String COLUMN_TYPE = "schedule_type";
		public static final String COLUMN_DAY = "schedule_day";
		public static final String COLUMN_HOUR = "schedule_hour";
		public static final String COLUMN_MIN = "schedule_min";
	}

	public static abstract class ActionVariableEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tester.variables";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tester.variable";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VARIABLES).build();
		public static final String TABLE_NAME = "variables";
		public static final String COLUMN_ENTRY_ID = "_id";
		public static final String COLUMN_ACTION_ID = "variable_action_id";
		public static final String COLUMN_NAME = "variable_name";
		public static final String COLUMN_VALUE = "variable_value";
	}

	public static abstract class ActionResultEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tester.results";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tester.result";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESULTS).build();
		public static final String TABLE_NAME = "results";
		public static final String COLUMN_ENTRY_ID = "_id";
		public static final String COLUMN_SDK_UUID = "uuid";
		public static final String COLUMN_ACTION_ID = "variable_action_id";
		public static final String COLUMN_TEXT = "result_text";
		public static final String COLUMN_STATUS = "result_status";
		public static final String COLUMN_TIMESTAMP = "result_timestamp";
		public static final String COLUMN_RETURN_VALUES = "result_values";
	}

	public static String[] REPORT_PROJECTION = {
		StatusReportEntry.COLUMN_ENTRY_ID,
		StatusReportEntry.COLUMN_STATUS,
		StatusReportEntry.COLUMN_ACTION_ID,
		StatusReportEntry.COLUMN_TRANSACTION,
		StatusReportEntry.COLUMN_START_TIMESTAMP,
		StatusReportEntry.COLUMN_FINISH_TIMESTAMP,
		StatusReportEntry.COLUMN_FAILURE_MESSAGE,
		StatusReportEntry.COLUMN_FINAL_SESSION_MSG,
		StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE,
		StatusReportEntry.COLUMN_EXTRAS
	};

	public static String[] ACTION_PROJECTION = {
		HoverActionEntry.COLUMN_ENTRY_ID,
		HoverActionEntry.COLUMN_NAME,
		HoverActionEntry.COLUMN_NETWORK_NAME,
		HoverActionEntry.COLUMN_SIM_ID,
		HoverActionEntry.COLUMN_VARIABLES
	};

	public static String[] ACTION_PIN_PROJECTION = {
			HoverActionEntry.COLUMN_ENTRY_ID,
			HoverActionEntry.COLUMN_PIN
	};

	public static String[] SCHEDULE_PROJECTION = {
		ActionScheduleEntry.COLUMN_ENTRY_ID,
		ActionScheduleEntry.COLUMN_ACTION_ID,
		ActionScheduleEntry.COLUMN_TYPE,
		ActionScheduleEntry.COLUMN_DAY,
		ActionScheduleEntry.COLUMN_HOUR,
		ActionScheduleEntry.COLUMN_MIN
	};

	public static String[] VARIABLE_PROJECTION = {
		ActionVariableEntry.COLUMN_ENTRY_ID,
		ActionVariableEntry.COLUMN_ACTION_ID,
		ActionVariableEntry.COLUMN_NAME,
		ActionVariableEntry.COLUMN_VALUE
	};

	public static String[] RESULT_PROJECTION = {
		ActionResultEntry.COLUMN_ENTRY_ID,
		ActionResultEntry.COLUMN_SDK_UUID,
		ActionResultEntry.COLUMN_ACTION_ID,
		ActionResultEntry.COLUMN_TEXT,
		ActionResultEntry.COLUMN_STATUS,
		ActionResultEntry.COLUMN_TIMESTAMP,
		ActionResultEntry.COLUMN_RETURN_VALUES
	};
}
