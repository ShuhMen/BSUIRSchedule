package com.maximshuhman.bsuirschedule.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_GROUPS =
        "CREATE TABLE ${DBContract.Groups.TABLE_NAME} (" +
                "${DBContract.Groups.groupID} INTEGER PRIMARY KEY," +
                "${DBContract.Groups.course} TEXT," +
                "${DBContract.Groups.specialityAbbrev} TEXT," +
                "${DBContract.Groups.specialityName} TEXT," +
                "${DBContract.Groups.facultyAbbrev} TEXT," +
                "${DBContract.Groups.name} TEXT ) "

    private val SQL_CREATE_COMMONSCHEDULE =
        "CREATE TABLE ${DBContract.CommonSchedule.TABLE_NAME} (" +
                "${DBContract.CommonSchedule.commonScheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.CommonSchedule.startExamsDate} TEXT," +
                "${DBContract.CommonSchedule.endExamsDate} TEXT," +
                "${DBContract.CommonSchedule.startDate} TEXT," +
                "${DBContract.CommonSchedule.endDate} TEXT," +
                "${DBContract.CommonSchedule.lastUpdate} TEXT," +
                "${DBContract.CommonSchedule.lastBuild} TEXT," +
                "FOREIGN KEY (${DBContract.CommonSchedule.commonScheduleID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.groupID}));"

    private val SQL_CREATE_SCHEDULE =
        "CREATE TABLE ${DBContract.Schedule.TABLE_NAME} (" +
                "${DBContract.Schedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.Schedule.inScheduleID} INTEGER," +
                "${DBContract.Schedule.day_of_week} INTEGER," +
                "${DBContract.Schedule.auditories} TEXT," +
                "${DBContract.Schedule.endLessonTime} TEXT," +
                "${DBContract.Schedule.lessonTypeAbbrev} TEXT," +
                "${DBContract.Schedule.note} TEXT," +
                "${DBContract.Schedule.numSubgroup} INTEGER," +
                "${DBContract.Schedule.startLessonTime} TEXT," +
                "${DBContract.Schedule.studentGroups} TEXT," +
                "${DBContract.Schedule.subject} TEXT," +
                "${DBContract.Schedule.subjectFullName} TEXT," +
                "${DBContract.Schedule.weekNumber} INTEGER," +
                "${DBContract.Schedule.groupID} INTEGER," +
                "${DBContract.Schedule.startLessonDate} TEXT," +
                "${DBContract.Schedule.endLessonDate} TEXT," +
                "FOREIGN KEY (${DBContract.Schedule.groupID}) REFERENCES ${DBContract.CommonSchedule.TABLE_NAME}(${DBContract.CommonSchedule.commonScheduleID}))"//," +
    // "FOREIGN KEY (${DBContract.Schedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"

    private val SQL_CREATE_FINALSCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.finalSchedule.TABLE_NAME} (" +
                "${DBContract.finalSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.Schedule.inScheduleID} INTEGER," +
                "${DBContract.finalSchedule.dayIndex} INTEGER," +
                "${DBContract.finalSchedule.day_of_week} INTEGER," +
                "${DBContract.finalSchedule.auditories} TEXT," +
                "${DBContract.finalSchedule.endLessonTime} TEXT," +
                "${DBContract.finalSchedule.lessonTypeAbbrev} TEXT," +
                "${DBContract.finalSchedule.note} TEXT," +
                "${DBContract.finalSchedule.numSubgroup} INTEGER," +
                "${DBContract.finalSchedule.startLessonTime} TEXT," +
                "${DBContract.finalSchedule.studentGroups} TEXT," +
                "${DBContract.finalSchedule.subject} TEXT," +
                "${DBContract.finalSchedule.subjectFullName} TEXT," +
                "${DBContract.finalSchedule.weekNumber} INTEGER," +
                "${DBContract.finalSchedule.groupID} INTEGER," +
                "${DBContract.finalSchedule.startLessonDate} TEXT," +
                "${DBContract.finalSchedule.endLessonDate} TEXT," +
                "FOREIGN KEY (${DBContract.finalSchedule.groupID}) REFERENCES ${DBContract.CommonSchedule.TABLE_NAME}(${DBContract.CommonSchedule.commonScheduleID}))"//," +
    // "FOREIGN KEY (${DBContract.finalSchedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"

    private val SQL_CREATE_EMPlOYEES =
        "CREATE TABLE ${DBContract.Employees.TABLE_NAME} (" +
                "${DBContract.Employees.employeeID} INTEGER PRIMARY KEY," +
                "${DBContract.Employees.firstName} TEXT," +
                "${DBContract.Employees.middleName} TEXT," +
                "${DBContract.Employees.lastName} TEXT," +
                "${DBContract.Employees.photoLink} TEXT," +
                "${DBContract.Employees.degree} TEXT," +
                "${DBContract.Employees.degreeAbbrev} TEXT," +
                "${DBContract.Employees.rank} TEXT," +
                "${DBContract.Employees.department} TEXT," +
                "${DBContract.Employees.fio} TEXT," +
                "${DBContract.Employees.photo} TEXT )"


    private val SQL_CREATE_FAVORITES =
        "CREATE TABLE ${DBContract.Favorites.TABLE_NAME} (" +
                "${DBContract.Favorites.groupID} INTEGER PRIMARY KEY," +
                "FOREIGN KEY (${DBContract.Favorites.groupID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.groupID}))"

    private val SQL_CREATE_EXAMS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Exams.TABLE_NAME} (" +
                "${DBContract.Schedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.Schedule.day_of_week} INTEGER," +
                "${DBContract.Schedule.auditories} TEXT," +
                "${DBContract.Schedule.endLessonTime} TEXT," +
                "${DBContract.Schedule.lessonTypeAbbrev} TEXT," +
                "${DBContract.Schedule.note} TEXT," +
                "${DBContract.Schedule.numSubgroup} INTEGER," +
                "${DBContract.Schedule.startLessonTime} TEXT," +
                "${DBContract.Schedule.studentGroups} TEXT," +
                "${DBContract.Schedule.subject} TEXT," +
                "${DBContract.Schedule.subjectFullName} TEXT," +
                "${DBContract.Schedule.weekNumber} INTEGER," +
                "${DBContract.Schedule.employeeID} INTEGER," +
                "${DBContract.Schedule.groupID} INTEGER," +
                "${DBContract.Exams.dateLesson} TEXT," +
                "FOREIGN KEY (${DBContract.Schedule.groupID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.CommonSchedule.commonScheduleID})," +
                "FOREIGN KEY (${DBContract.Schedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${DBContract.Groups.TABLE_NAME}"

    private val SQL_CREATE_PAIRTOEMPLOYER =
        "CREATE TABLE IF NOT EXISTS ${DBContract.PairToEmployers.TABLE_NAME} (" +
                "${DBContract.PairToEmployers.lessonID} INTEGER," +
                "${DBContract.PairToEmployers.employeeID} INTEGER," +
                "${DBContract.PairToEmployers.groupID} INTEGER," +
                //   "FOREIGN KEY (${DBContract.PairToEmployers.lessonID}) REFERENCES ${DBContract.Schedule.TABLE_NAME}(${DBContract.Schedule.scheduleID})," +
                "FOREIGN KEY (${DBContract.PairToEmployers.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"

    private val SQL_CREATE_SCHEDULE_INDEX =
        "CREATE INDEX lessonID ON ${DBContract.Schedule.TABLE_NAME} (${DBContract.Schedule.groupID})"

    override fun onCreate(p0: SQLiteDatabase?) {
        Log.v("DBDD", "p0 override fun onCreate(db: SQLiteDatabase?) {")
        p0?.let {

            try {
                val a1 = it.execSQL(SQL_CREATE_GROUPS)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_GROUPS ERROR")
            }
            try {
                val a2 = it.execSQL(SQL_CREATE_COMMONSCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_COMMONSCHEDULE ERROR")
            }
            try {
                val a3 = it.execSQL(SQL_CREATE_SCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_SCHEDULE ERROR")
            }
            try {
                val a4 = it.execSQL(SQL_CREATE_EMPlOYEES)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_EMPlOYEES ERROR")
            }
            try {
                val a5 = it.execSQL(SQL_CREATE_FAVORITES)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_FAVORITES ERROR")
            }

            try {
                val a6 = it.execSQL(SQL_CREATE_EXAMS)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_EXAMS ERROR")
            }

            try {
                val a7 = it.execSQL(SQL_CREATE_FINALSCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_FINALSCHEDULE ERROR")
            }

            try {
                val a8 = it.execSQL(SQL_CREATE_PAIRTOEMPLOYER)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_PAIRTOEMPLOYER ERROR")
            }
        }
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.v(
            "DBDD",
            "override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {"
        )

        if (oldVersion < 3)
            db.execSQL("ALTER TABLE ${DBContract.Employees.TABLE_NAME} ADD COLUMN ${DBContract.Employees.photo} BLOB")
        if (oldVersion < 4) {
            db.execSQL("PRAGMA foreign_keys = OFF")
            // db.execSQL("DROP TABLE ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.CommonSchedule.TABLE_NAME}")
            db.execSQL("PRAGMA foreign_keys = ON")
            db.execSQL("ALTER TABLE ${DBContract.Schedule.TABLE_NAME} ADD COLUMN ${DBContract.Schedule.startLessonDate} TEXT")
            db.execSQL("ALTER TABLE ${DBContract.Schedule.TABLE_NAME} ADD COLUMN ${DBContract.Schedule.endLessonDate} TEXT")
        }

        if (oldVersion == 4) {
            db.execSQL("PRAGMA foreign_keys = OFF")
            db.execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.CommonSchedule.TABLE_NAME}")

            db.execSQL("PRAGMA foreign_keys = ON")
        }

        if (oldVersion < 6) {
            db.execSQL("PRAGMA foreign_keys = OFF")
            // db.execSQL("DROP TABLE ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.CommonSchedule.TABLE_NAME}")

            db.execSQL("PRAGMA foreign_keys = ON")
        }

        if (oldVersion < 7) {
            try {
                val a5 = db.execSQL(SQL_CREATE_EXAMS)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_EXAMS ERROR")
            }

            db.execSQL("PRAGMA foreign_keys = OFF")
            // db.execSQL("DROP TABLE ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.CommonSchedule.TABLE_NAME}")

            db.execSQL("PRAGMA foreign_keys = ON")
        }

        if (oldVersion < 8) {
            try {
                db.execSQL("ALTER TABLE ${DBContract.CommonSchedule.TABLE_NAME} ADD COLUMN ${DBContract.CommonSchedule.lastUpdate} TEXT")
            } catch (e: Exception) {
            }
            val c: Cursor = db.rawQuery(
                "SELECT * FROM ${DBContract.CommonSchedule.TABLE_NAME} " +
                        "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID})",
                null
            )

            with(c) {
                //  moveToFirst()

                while (moveToNext()) {
                    val lastUpdate = SimpleDateFormat(
                        "dd.MM.yyyy", Locale.getDefault(
                            Locale.Category.FORMAT
                        )
                    ).format(Calendar.getInstance().time)   //Requests.getLastUpdate(getString(getColumnIndexOrThrow(DBContract.Groups.name)))
                    val id =
                        getInt(getColumnIndexOrThrow(DBContract.CommonSchedule.commonScheduleID))
                    val values = ContentValues().apply {
                        put(DBContract.CommonSchedule.lastUpdate, lastUpdate)
                    }


                    db.update(
                        DBContract.CommonSchedule.TABLE_NAME,
                        values,
                        "${DBContract.CommonSchedule.commonScheduleID} = $id",
                        null
                    ) //rawQuery("UPDATE ${DBContract.CommonSchedule.TABLE_NAME} SET ${DBContract.CommonSchedule.lastUpdate} = ${lastUpdate.res.toString()} WHERE ${DBContract.CommonSchedule.commonScheduleID} = $id", null)
                }
            }

            c.close()
        }

        if (oldVersion < 9) {
            try {
                db.execSQL("ALTER TABLE ${DBContract.CommonSchedule.TABLE_NAME} ADD COLUMN ${DBContract.CommonSchedule.lastBuild} TEXT")
            } catch (e: Exception) {

            }
            try {
                val a5 = db.execSQL(SQL_CREATE_FINALSCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_FINALSCHEDULE ERROR")
            }
        }

        if (oldVersion < 10) {
            try {
                val a6 = db.execSQL(SQL_CREATE_EXAMS)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CHECK_EXAMS ERROR")
            }

            try {
                val a7 = db.execSQL(SQL_CREATE_FINALSCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CHECK_EXAMS ERROR")
            }

        }
        if (oldVersion < 11) {
            val a1 = db.execSQL("DROP TABLE IF EXISTS ${DBContract.finalSchedule.TABLE_NAME}")
            val a2 = db.execSQL("DROP TABLE IF EXISTS ${DBContract.Schedule.TABLE_NAME}")

            try {
                val a3 = db.execSQL(SQL_CREATE_SCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_SCHEDULE ERROR")
            }

            try {
                val a7 = db.execSQL(SQL_CREATE_FINALSCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_FINALSCHEDULE ERROR")
            }

            try {
                val a8 = db.execSQL(SQL_CREATE_PAIRTOEMPLOYER)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_PAIRTOEMPLOYER ERROR")
            }
        }


    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 11
        const val DATABASE_NAME = "Schedule"
    }
}