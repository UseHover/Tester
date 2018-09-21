package com.hover.tester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 9;
	public static final String DATABASE_NAME = "services.db";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static final String REPORT_TABLE_CREATE = "create table "
			+ Contract.StatusReportEntry.TABLE_NAME + "("
			+ Contract.StatusReportEntry.COLUMN_ENTRY_ID + " integer primary key autoincrement, "
			+ Contract.StatusReportEntry.COLUMN_STATUS + " integer not null, "
			+ Contract.StatusReportEntry.COLUMN_ACTION_ID + " string not null, "
			+ Contract.StatusReportEntry.COLUMN_TRANSACTION + " text, "
			+ Contract.StatusReportEntry.COLUMN_START_TIMESTAMP + " long not null, "
			+ Contract.StatusReportEntry.COLUMN_FINISH_TIMESTAMP + " long, "
			+ Contract.StatusReportEntry.COLUMN_FAILURE_MESSAGE + " text, "
			+ Contract.StatusReportEntry.COLUMN_FINAL_SESSION_MSG + " text, "
			+ Contract.StatusReportEntry.COLUMN_CONFIRMATION_MESSAGE + " text, "
			+ Contract.StatusReportEntry.COLUMN_EXTRAS + " text "
			+ ");";

	private static final String ACTION_TABLE_CREATE = "create table "
			+ Contract.HoverActionEntry.TABLE_NAME + "("
			+ Contract.HoverActionEntry.COLUMN_ENTRY_ID + " string primary key, "
			+ Contract.HoverActionEntry.COLUMN_NAME + " text not null, "
			+ Contract.HoverActionEntry.COLUMN_SIM_ID + " text not null, "
			+ Contract.HoverActionEntry.COLUMN_NETWORK_NAME + " text not null, "
			+ Contract.HoverActionEntry.COLUMN_VARIABLES + " text, "
			+ Contract.HoverActionEntry.COLUMN_PIN + " text "
			+ ");";

	private static final String SCHEDULE_TABLE_CREATE = "create table "
			+ Contract.ActionScheduleEntry.TABLE_NAME + "("
			+ Contract.ActionScheduleEntry.COLUMN_ENTRY_ID + " integer primary key autoincrement, "
			+ Contract.ActionScheduleEntry.COLUMN_ACTION_ID + " string not null, "
			+ Contract.ActionScheduleEntry.COLUMN_TYPE + " integer not null, "
			+ Contract.ActionScheduleEntry.COLUMN_DAY + " integer, "
			+ Contract.ActionScheduleEntry.COLUMN_HOUR + " integer, "
			+ Contract.ActionScheduleEntry.COLUMN_MIN + " integer, "
			+ "UNIQUE (" + Contract.ActionScheduleEntry.COLUMN_ACTION_ID + ") ON CONFLICT REPLACE"
			+ ");";

	private static final String VARIABLE_TABLE_CREATE = "create table "
			+ Contract.ActionVariableEntry.TABLE_NAME + "("
			+ Contract.ActionVariableEntry.COLUMN_ENTRY_ID + " integer primary key autoincrement, "
			+ Contract.ActionVariableEntry.COLUMN_ACTION_ID + " string not null, "
			+ Contract.ActionVariableEntry.COLUMN_NAME + " text not null, "
			+ Contract.ActionVariableEntry.COLUMN_VALUE + " text, "
			+ "UNIQUE (" + Contract.ActionVariableEntry.COLUMN_ACTION_ID + ", " + Contract.ActionVariableEntry.COLUMN_NAME + ") ON CONFLICT REPLACE"
			+ ");";

	private static final String RESULT_TABLE_CREATE = "create table "
			+ Contract.ActionResultEntry.TABLE_NAME + "("
			+ Contract.ActionResultEntry.COLUMN_ENTRY_ID + " integer primary key autoincrement, "
			+ Contract.ActionResultEntry.COLUMN_SDK_UUID + " string not null, "
			+ Contract.ActionResultEntry.COLUMN_ACTION_ID + " string not null, "
			+ Contract.ActionResultEntry.COLUMN_TEXT + " text not null, "
			+ Contract.ActionResultEntry.COLUMN_STATUS + " integer not null, "
			+ Contract.ActionResultEntry.COLUMN_TIMESTAMP + " text not null, "
			+ Contract.ActionResultEntry.COLUMN_RETURN_VALUES + " text, "
			+ "UNIQUE (" + Contract.ActionResultEntry.COLUMN_SDK_UUID + ") ON CONFLICT REPLACE"
			+ ");";

	private static final String SQL_DELETE_REPORTS = "DROP TABLE IF EXISTS " + Contract.StatusReportEntry.TABLE_NAME;
	private static final String SQL_DELETE_SERVICES = "DROP TABLE IF EXISTS services";
	private static final String SQL_DELETE_ACTIONS = "DROP TABLE IF EXISTS " + Contract.HoverActionEntry.TABLE_NAME;
	private static final String SQL_DELETE_SCHEDULES = "DROP TABLE IF EXISTS " + Contract.ActionScheduleEntry.TABLE_NAME;
	private static final String SQL_DELETE_VARIABLES = "DROP TABLE IF EXISTS " + Contract.ActionVariableEntry.TABLE_NAME;
	private static final String SQL_DELETE_RESULTS = "DROP TABLE IF EXISTS " + Contract.ActionResultEntry.TABLE_NAME;

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(REPORT_TABLE_CREATE);
		db.execSQL(ACTION_TABLE_CREATE);
		db.execSQL(SCHEDULE_TABLE_CREATE);
		db.execSQL(VARIABLE_TABLE_CREATE);
		db.execSQL(RESULT_TABLE_CREATE);
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_REPORTS);
		db.execSQL(SQL_DELETE_SERVICES);
		db.execSQL(SQL_DELETE_ACTIONS);
		db.execSQL(SQL_DELETE_SCHEDULES);
		db.execSQL(SQL_DELETE_VARIABLES);
		db.execSQL(SQL_DELETE_RESULTS);
		onCreate(db);
	}
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}

