package com.mentebit.deviceinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;
    public static final String db_name = "MobContacts.db";
    public static final String bizcalllog_table = "BizCallLog_table";

    public static final String CallLogID = "CallLogID";
    public static final String callLogName = "callLogName";
    public static final String callLogMobNo = "callLogMobNo";
    public static final String callLogType = "callLogType";
    public static final String callLogDate = "callLogDate";
    public static final String callLogDuration = "callLogDuration";
    public static final String callLogRecording = "callLogRecording";
    public static final String callLogUploaded = "callLogUploaded";

    public DBHelper(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + bizcalllog_table + " (CallLogID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, callLogName TEXT, callLogMobNo TEXT, callLogType TEXT, callLogDate TEXT, callLogDuration TEXT, callLogRecording TEXT, callLogUploaded TEXT)");
        Log.d("dbase", "Table Created..");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(db);*/
    }

    //----------------------insert call log-------------------------------
    public long insertCallLogs(DataAutoCallRecord dataContactLogs) {
        sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("callLogName", dataContactLogs.getLeadName());
        cv.put("callLogMobNo", dataContactLogs.getLeadNumber());
        cv.put("callLogType", dataContactLogs.getLeadType());
        cv.put("callLogDate", dataContactLogs.getLeadDateCompare());
        cv.put("callLogDuration", dataContactLogs.getLeadTime());
        cv.put("callLogRecording", dataContactLogs.getLeadRecName());
        cv.put("callLogUploaded", dataContactLogs.getLeadDBinsert());
        return sqLiteDatabase.insert(bizcalllog_table, null, cv);
    }

    public ArrayList<DataAutoCallRecord> getAllDBCallLogs() {
        sqLiteDatabase = getReadableDatabase();

        Cursor c = sqLiteDatabase.query(bizcalllog_table, null, null, null, null, null, "CallLogID DESC");

        ArrayList<DataAutoCallRecord> logArrayList = new ArrayList<>();

        while (c.moveToNext()) {
            logArrayList.add(new DataAutoCallRecord(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7)));
        }
        c.close();
        sqLiteDatabase.close();
        return logArrayList;
    }

    //----------------------update call log-------------------------------
    public Cursor findCallLogUpload() {
        sqLiteDatabase = getReadableDatabase();

        String[] projections = {CallLogID,callLogName,callLogMobNo,callLogType,callLogDate,callLogDuration,callLogRecording,callLogUploaded};
        String selection = callLogUploaded + " LIKE ?";
        String[] selection_args = {"0"};
        Cursor cursor = sqLiteDatabase.query(bizcalllog_table, projections, selection, selection_args, null, null, null);
        return cursor;
    }

    public void updateCallLog(String uploaded) {
        sqLiteDatabase = this.getWritableDatabase();

        ContentValues data = new ContentValues();
        data.put("callLogUploaded", "1");

        sqLiteDatabase.update(bizcalllog_table, data, "CallLogID = '" + uploaded + "'", null);
    }
}
