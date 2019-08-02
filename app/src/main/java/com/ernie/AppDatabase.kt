package com.ernie

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ernie.model.Entry
import com.ernie.model.User

/**
 * Created by timbuchalka for the Android Oreo using Kotlin course
 * from www.learnprogramming.academy
 *
 * Basic database class for the application.
 *
 * The only class that should use this is [AppProvider].
 */

private const val TAG = "AppDatabase"

private const val DATABASE_NAME = "ErnieApp.db"
private const val DATABASE_VERSION = 3

class AppDatabase(context: Context,
                  factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, DATABASE_NAME,
                factory, DATABASE_VERSION) {

    init {

    }

    override fun onCreate(db: SQLiteDatabase) {
        // CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER);
        Log.d(TAG, "onCreate: starts")

        val createUserTableSQL = """CREATE TABLE IF NOT EXISTS User (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT NOT NULL,
                        password TEXT NOT NULL,
                        hourly_rate TEXT NOT NULL,
                        contract_id INTEGER UNSIGNED,
                        FOREIGN KEY (contract_id) REFERENCES Contract(id)
                        ON DELETE RESTRICT ON UPDATE CASCADE
                    );""".replaceIndent(" ")

        val createEntryTableSQL = """CREATE TABLE IF NOT EXISTS Entry (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        date_recorded DATE DEFAULT (datetime(current_timestamp)),
                        start_time TEXT NOT NULL,
                        end_time TEXT NOT NULL,
                        break_duration INTEGER DEFAULT 0,
                        earned INTEGER DEFAULT 0,
                        FOREIGN KEY (user_id) REFERENCES User(id)
                        ON DELETE RESTRICT ON UPDATE CASCADE
                    );""".replaceIndent(" ")

        val createContractTableSQL = """CREATE TABLE IF NOT EXISTS Contract (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT,
                        monStart TEXT NOT NULL,
                        monEnd TEXT NOT NULL,
                        tueStart TEXT NOT NULL,
                        tueEnd TEXT NOT NULL,
                        wedStart TEXT NOT NULL,
                        wedEnd TEXT NOT NULL,
                        thurStart TEXT NOT NULL,
                        thurEnd TEXT NOT NULL,
                        friStart TEXT NOT NULL,
                        friEnd TEXT NOT NULL,
                        satStart TEXT NOT NULL,
                        satEnd TEXT NOT NULL,
                        sunStart TEXT NOT NULL,
                        sunEnd TEXT NOT NULL,
                        user_id INTEGER UNSIGNED NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES User(id)
                        ON DELETE RESTRICT ON UPDATE CASCADE
                    );""".replaceIndent(" ")

        db.execSQL(createUserTableSQL)
        db.execSQL(createEntryTableSQL)
        db.execSQL(createContractTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: starts")
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("""DROP TABLE User;
            |         DROP TABLE Entry;
            |         DROP TABLE Contract
        """.trimMargin())
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }


    fun addUser(user: User) {
        val values = ContentValues().apply {
            put("name", user.name)
            put("email", user.email)
            put("password", user.password)
            put("hourly_rate", user.hourly_rate)
            put("contract_id", 0)
        }
        val db = this.writableDatabase
        db.insert("User", null, values)
        db.close()


    }

    fun getAllUsers(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM User", null)
    }


    fun addEntry(entry: Entry) {
        val values = ContentValues().apply {
            put("start_time", entry.start_time)
            put("end_time", entry.end_time)
            put("break_duration", entry.break_duration)
            put("earned", entry.earned)
        }
        val db = this.writableDatabase
        db.insert("Entry", null, values)
        db.close()
        Log.d(TAG, "Entry added to db")

    }

    fun getAllEntries(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM Entry", null)
    }

    fun numberOfEntries(): Int {
        return getAllEntries()!!.count
    }





    companion object {
        private val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ErnieApp.db"
        val TABLE_NAME = "User"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "name"
        val COLUMN_START = "start_time"
    }
}
