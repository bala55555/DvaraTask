package com.dvaratask

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "AppDatabase"
        private val DATABASE_VERSION = 1

        private val TABLE_INTERNET_DETAILS = "InternetDetails"
        private val COLUMN_ID = "ID"
        private val COLUMN_MOBILE_NO = "MobileNo"
        private val COLUMN_UPLOAD_SPEED = "UploadSpeed"
        private val COLUMN_DOWNLOAD_SPEED = "DownloadSpeed"
        private val COLUMN_TIME_STAMP = "TimeStamp"
    }

    private val CREATE_TABLE_INTERNET_DETAILS = ("CREATE TABLE " + TABLE_INTERNET_DETAILS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_MOBILE_NO + " TEXT,"
            + COLUMN_UPLOAD_SPEED + " TEXT," + COLUMN_DOWNLOAD_SPEED + " TEXT,"
            + COLUMN_TIME_STAMP + " TEXT" + ")")

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_INTERNET_DETAILS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE $TABLE_INTERNET_DETAILS")
        db?.execSQL(CREATE_TABLE_INTERNET_DETAILS)
    }

    fun insertData(data: InternetData) {
        val database = writableDatabase

        val values = ContentValues()
        values.put(COLUMN_MOBILE_NO, data.mobileNo)
        values.put(COLUMN_UPLOAD_SPEED, data.uploadSpeed)
        values.put(COLUMN_DOWNLOAD_SPEED, data.downloadSpeed)
        values.put(COLUMN_TIME_STAMP, data.timeStamp)
        database.insert(TABLE_INTERNET_DETAILS, null, values)

        database.close()
    }

    fun getData(mobileNo: String): InternetData? {
        val database = readableDatabase

        var returnData: InternetData? = null
        val query =
            "SELECT * FROM $TABLE_INTERNET_DETAILS WHERE $COLUMN_MOBILE_NO='$mobileNo' ORDER BY 1 DESC LIMIT 1"
        val cursor = database.rawQuery(query, null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToNext()
                val getMobileNo = cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE_NO))
                val downloadSpeed = cursor.getString(cursor.getColumnIndex(COLUMN_DOWNLOAD_SPEED))
                val uploadSpeed = cursor.getString(cursor.getColumnIndex(COLUMN_UPLOAD_SPEED))
                val timeStamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIME_STAMP))

                returnData = InternetData(getMobileNo, downloadSpeed, uploadSpeed, timeStamp)
            }
            cursor.close()
        }

        database.close()
        return returnData
    }
}