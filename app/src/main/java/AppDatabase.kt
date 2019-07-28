package learnprogramming.academy.tasktimer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by timbuchalka for the Android Oreo using Kotlin course
 * from www.learnprogramming.academy
 *
 * Basic database class for the application.
 *
 * The only class that should use this is [AppProvider].
 */

private const val TAG = "AppDatabase"

private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 3

internal class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        Log.d(TAG, "AppDatabase: initialising")
    }

    override fun onCreate(db: SQLiteDatabase) {
        // CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER);
        Log.d(TAG, "onCreate: starts")
        val sSQL = """CREATE TABLE User (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT NOT NULL,
                        password TEXT NOT NULL,
                        hourly_rate TEXT NOT NULL,
                        contract_id INTEGER UNSIGNED,
                        FOREIGN KEY (contract_id) REFERENCES Contract(id)
                        ON DELETE RESTRICT ON UPDATE CASCADE
                    );

                    CREATE TABLE Entry (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        date_recorded DATE DEFAULT (datetime(current_timestamp)),
                        start_time TEXT NOT NULL,
                        end_time TEXT NOT NULL,
                        break_duration INTEGER DEFAULT 0,
                        earned INTEGER DEFAULT 0,
                        FOREIGN KEY (user_id) REFERENCES User(id)
                        ON DELETE RESTRICT ON UPDATE CASCADE
                    );

                    CREATE TABLE Contract (
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
        db.execSQL(sSQL)
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

}
