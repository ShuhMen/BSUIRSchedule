package com.maximshuhman.bsuirschedule.DataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_GROUPS =
        "CREATE TABLE ${DBContract.Groups.TABLE_NAME} ("+
        "${DBContract.Groups.groupID} INTEGER PRIMARY KEY,"+
        "${DBContract.Groups.course} TEXT,"+
        "${DBContract.Groups.specialityAbbrev} TEXT,"+
        "${DBContract.Groups.specialityName} TEXT,"+
        "${DBContract.Groups.facultyAbbrev} TEXT,"+
        "${DBContract.Groups.name} TEXT ) "

    private val SQL_CREATE_COMMONSCHEDULE =
        "CREATE TABLE ${DBContract.CommonSchedule.TABLE_NAME} ("+
                "${DBContract.CommonSchedule.commonScheduleID} INTEGER PRIMARY KEY,"+
                "${DBContract.CommonSchedule.startExamsDate} TEXT,"+
                "${DBContract.CommonSchedule.endExamsDate} TEXT,"+
                "${DBContract.CommonSchedule.startDate} TEXT,"+
                "${DBContract.CommonSchedule.endDate} TEXT,"+
                "FOREIGN KEY (${DBContract.CommonSchedule.commonScheduleID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.groupID}));"

    private val SQL_CREATE_SCHEDULE =
        "CREATE TABLE ${DBContract.Schedule.TABLE_NAME} ("+
                "${DBContract.Schedule.scheduleID       } INTEGER PRIMARY KEY,"+
                "${DBContract.Schedule.day_of_week      } INTEGER,"+
                "${DBContract.Schedule.auditories       } TEXT,"+
                "${DBContract.Schedule.endLessonTime    } TEXT,"+
                "${DBContract.Schedule.lessonTypeAbbrev } TEXT,"+
                "${DBContract.Schedule.note             } TEXT,"+
                "${DBContract.Schedule.numSubgroup      } INTEGER,"+
                "${DBContract.Schedule.startLessonTime  } TEXT,"+
                "${DBContract.Schedule.studentGroups    } TEXT,"+
                "${DBContract.Schedule.subject          } TEXT,"+
                "${DBContract.Schedule.subjectFullName  } TEXT,"+
                "${DBContract.Schedule.weekNumber       } INTEGER,"+
                "${DBContract.Schedule.employeeID       } INTEGER,"+
                "${DBContract.Schedule.groupID          } INTEGER,"+
                "${DBContract.Schedule.startLessonDate  } TEXT,"+
                "${DBContract.Schedule.endLessonDate    } TEXT,"+
                "FOREIGN KEY (${DBContract.Schedule.groupID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.CommonSchedule.commonScheduleID}),"+
                "FOREIGN KEY (${DBContract.Schedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"

    private val SQL_CREATE_EMPlOYEES =
        "CREATE TABLE ${DBContract.Employees.TABLE_NAME} ("+
                "${DBContract.Employees.employeeID    } INTEGER PRIMARY KEY,"+
                "${DBContract.Employees.firstName     } TEXT,"+
                "${DBContract.Employees.middleName    } TEXT,"+
                "${DBContract.Employees.lastName      } TEXT,"+
                "${DBContract.Employees.photoLink     } TEXT,"+
                "${DBContract.Employees.degree        } TEXT,"+
                "${DBContract.Employees.degreeAbbrev  } TEXT,"+
                "${DBContract.Employees.rank          } TEXT,"+
                "${DBContract.Employees.department    } TEXT,"+
                "${DBContract.Employees.fio           } TEXT,"+
                "${DBContract.Employees.photo         } TEXT )"



    private val SQL_CREATE_FAVORITES =
        "CREATE TABLE ${DBContract.Favorites.TABLE_NAME} ("+
                "${DBContract.Favorites.groupID} INTEGER PRIMARY KEY,"+
                "FOREIGN KEY (${DBContract.Favorites.groupID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.groupID}))"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${DBContract.Groups.TABLE_NAME}"


    override fun onCreate(p0: SQLiteDatabase?) {
        Log.v("DBDD", "p0 override fun onCreate(db: SQLiteDatabase?) {")
        p0?.let {

            try{val a1 = it.execSQL(SQL_CREATE_GROUPS)}catch (e:Exception){}
            try{val a2 = it.execSQL(SQL_CREATE_COMMONSCHEDULE)}catch (e:Exception){}
            try{val a3 = it.execSQL(SQL_CREATE_SCHEDULE)}catch (e:Exception){}
            try{val a4 = it.execSQL(SQL_CREATE_EMPlOYEES)}catch (e:Exception){}
            try{val a5 = it.execSQL(SQL_CREATE_FAVORITES)}catch (e:Exception){}
        }
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.v("DBDD", "override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {")

        if(oldVersion < 3 )
        db.execSQL("ALTER TABLE ${DBContract.Employees.TABLE_NAME} ADD COLUMN ${DBContract.Employees.photo} BLOB")
        if(oldVersion < 4) {
            db.execSQL("PRAGMA foreign_keys = OFF")
           // db.execSQL("DROP TABLE ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
           // db.execSQL("DROP TABLE ${DBContract.CommonSchedule.TABLE_NAME}")
            db.execSQL("PRAGMA foreign_keys = ON")
            db.execSQL("ALTER TABLE ${DBContract.Schedule.TABLE_NAME} ADD COLUMN ${DBContract.Schedule.startLessonDate} TEXT")
            db.execSQL("ALTER TABLE ${DBContract.Schedule.TABLE_NAME} ADD COLUMN ${DBContract.Schedule.endLessonDate} TEXT")
        }

        if(oldVersion == 4 ){
            db.execSQL("PRAGMA foreign_keys = OFF")
            // db.execSQL("DROP TABLE ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
            db.execSQL("PRAGMA foreign_keys = ON")
        }


    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 5
        const val DATABASE_NAME = "Schedule"
    }
}