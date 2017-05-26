package com.hover.tester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "services.db";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static final String SERVICE_TABLE_CREATE = "create table "
			+ Contract.OperatorServiceEntry.TABLE_NAME + "("
			+ Contract.OperatorServiceEntry.COLUMN_ENTRY_ID + " integer primary key autoincrement, "
			+ Contract.OperatorServiceEntry.COLUMN_SERVICE_ID + " integer not null, "
			+ Contract.OperatorServiceEntry.COLUMN_NAME + " text not null, "
			+ Contract.OperatorServiceEntry.COLUMN_SLUG + " text not null, "
			+ Contract.OperatorServiceEntry.COLUMN_COUNTRY + " text not null, "
			+ Contract.OperatorServiceEntry.COLUMN_CURRENCY + " text not null, "
			+ Contract.OperatorServiceEntry.COLUMN_ACTIONS + " text"
			+ ");";

	private static final String ACTION_TABLE_CREATE = "create table "
			+ Contract.OperatorActionEntry.TABLE_NAME + "("
			+ Contract.OperatorActionEntry.COLUMN_ENTRY_ID + " integer primary key autoincrement, "
			+ Contract.OperatorActionEntry.COLUMN_NAME + " text not null, "
			+ Contract.OperatorActionEntry.COLUMN_SLUG + " text not null, "
			+ Contract.OperatorActionEntry.COLUMN_SERVICE_ID + " integer not null, "
			+ Contract.OperatorActionEntry.COLUMN_VARIABLES + " text, "
			+ Contract.OperatorActionEntry.COLUMN_LAST_RUN + " long not null, "
			+ Contract.OperatorActionEntry.COLUMN_LAST_STATUS + " integer not null, "
			+ Contract.OperatorActionEntry.COLUMN_LAST_RESULT + " text, "
			+ Contract.OperatorActionEntry.COLUMN_RESULTS + " text"
			+ ");";

	private static final String SQL_DELETE_SERVICES = "DROP TABLE IF EXISTS " + Contract.OperatorServiceEntry.TABLE_NAME;
	private static final String SQL_DELETE_ACTIONS = "DROP TABLE IF EXISTS " + Contract.OperatorActionEntry.TABLE_NAME;

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SERVICE_TABLE_CREATE);
		db.execSQL(ACTION_TABLE_CREATE);
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_SERVICES);
		db.execSQL(SQL_DELETE_ACTIONS);
		onCreate(db);
	}
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}

