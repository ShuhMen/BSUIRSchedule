/*
package com.maximshuhman.bsuirschedule.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_GROUPS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Groups.TABLE_NAME} (" +
                "${DBContract.Groups.groupID} INTEGER PRIMARY KEY," +
                "${DBContract.Groups.course} TEXT," +
                "${DBContract.Groups.specialityAbbrev} TEXT," +
                "${DBContract.Groups.specialityName} TEXT," +
                "${DBContract.Groups.facultyAbbrev} TEXT," +
                "${DBContract.Groups.name} TEXT ) "

    private val SQL_CREATE_COMMONSCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.CommonSchedule.TABLE_NAME} (" +
                "${DBContract.CommonSchedule.commonScheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.CommonSchedule.startExamsDate} TEXT," +
                "${DBContract.CommonSchedule.endExamsDate} TEXT," +
                "${DBContract.CommonSchedule.startDate} TEXT," +
                "${DBContract.CommonSchedule.endDate} TEXT," +
                "${DBContract.CommonSchedule.lastUpdate} TEXT," +
                "${DBContract.CommonSchedule.lastBuild} TEXT," +
                "FOREIGN KEY (${DBContract.CommonSchedule.commonScheduleID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.groupID}));"

    private val SQL_CREATE_SCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Schedule.TABLE_NAME} (" +
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
        "CREATE TABLE IF NOT EXISTS ${DBContract.Employees.TABLE_NAME} (" +
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
                "${DBContract.Employees.urlId} TEXT," +
                "${DBContract.Employees.photo} BLOB )"


    private val SQL_CREATE_FAVORITES =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Favorites.TABLE_NAME} (" +
                "${DBContract.Favorites.groupID} INTEGER PRIMARY KEY," +
                "${DBContract.Favorites.type} INTEGER ," +
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


    private val SQL_CREATE_COMMONEMPLOYEE =
        "CREATE TABLE ${DBContract.CommonEmployee.TABLE_NAME} (" +
                "${DBContract.CommonEmployee.commonEmployeeID} INTEGER PRIMARY KEY," +
                "${DBContract.CommonEmployee.startExamsDate} TEXT," +
                "${DBContract.CommonEmployee.endExamsDate} TEXT," +
                "${DBContract.CommonEmployee.startDate} TEXT," +
                "${DBContract.CommonEmployee.endDate} TEXT," +
                "${DBContract.CommonEmployee.lastUpdate} TEXT," +
                "${DBContract.CommonEmployee.lastBuild} TEXT," +
                "FOREIGN KEY (${DBContract.CommonEmployee.commonEmployeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}));"

    private val SQL_CREATE_EMPLOYEESCHEDULE =
        "CREATE TABLE ${DBContract.EmployeeSchedule.TABLE_NAME} (" +
                "${DBContract.EmployeeSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.EmployeeSchedule.inScheduleID} INTEGER," +
                "${DBContract.EmployeeSchedule.day_of_week} INTEGER," +
                "${DBContract.EmployeeSchedule.auditories} TEXT," +
                "${DBContract.EmployeeSchedule.endLessonTime} TEXT," +
                "${DBContract.EmployeeSchedule.lessonTypeAbbrev} TEXT," +
                "${DBContract.EmployeeSchedule.note} TEXT," +
                "${DBContract.EmployeeSchedule.numSubgroup} INTEGER," +
                "${DBContract.EmployeeSchedule.startLessonTime} TEXT," +
                "${DBContract.EmployeeSchedule.studentGroups} TEXT," +
                "${DBContract.EmployeeSchedule.subject} TEXT," +
                "${DBContract.EmployeeSchedule.subjectFullName} TEXT," +
                "${DBContract.EmployeeSchedule.weekNumber} INTEGER," +
                "${DBContract.EmployeeSchedule.employeeID} INTEGER," +
                "${DBContract.EmployeeSchedule.startLessonDate} TEXT," +
                "${DBContract.EmployeeSchedule.endLessonDate} TEXT," +
                "FOREIGN KEY (${DBContract.EmployeeSchedule.employeeID}) REFERENCES ${DBContract.CommonEmployee.TABLE_NAME}(${DBContract.CommonEmployee.commonEmployeeID}))"


    private val SQL_CREATE_FINALEMPLOYEESCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.finalEmployeeSchedule.TABLE_NAME} (" +
                "${DBContract.finalEmployeeSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.finalEmployeeSchedule.inScheduleID} INTEGER," +
                "${DBContract.finalEmployeeSchedule.dayIndex} INTEGER," +
                "${DBContract.finalEmployeeSchedule.day_of_week} INTEGER," +
                "${DBContract.finalEmployeeSchedule.auditories} TEXT," +
                "${DBContract.finalEmployeeSchedule.endLessonTime} TEXT," +
                "${DBContract.finalEmployeeSchedule.lessonTypeAbbrev} TEXT," +
                "${DBContract.finalEmployeeSchedule.note} TEXT," +
                "${DBContract.finalEmployeeSchedule.numSubgroup} INTEGER," +
                "${DBContract.finalEmployeeSchedule.startLessonTime} TEXT," +
                "${DBContract.finalEmployeeSchedule.studentGroups} TEXT," +
                "${DBContract.finalEmployeeSchedule.subject} TEXT," +
                "${DBContract.finalEmployeeSchedule.subjectFullName} TEXT," +
                "${DBContract.finalEmployeeSchedule.weekNumber} INTEGER," +
                "${DBContract.finalEmployeeSchedule.employeeID} INTEGER," +
                "${DBContract.finalEmployeeSchedule.startLessonDate} TEXT," +
                "${DBContract.finalEmployeeSchedule.endLessonDate} TEXT," +
                "FOREIGN KEY (${DBContract.finalEmployeeSchedule.employeeID}) REFERENCES ${DBContract.CommonEmployee.TABLE_NAME}(${DBContract.CommonEmployee.commonEmployeeID}))"
    // "FOREIGN KEY (${DBContract.finalSchedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"


    private val SQL_CREATE_EMPLOYEETOPAIR =
        "CREATE TABLE IF NOT EXISTS ${DBContract.EmployeeToPair.TABLE_NAME} (" +
                "${DBContract.EmployeeToPair.lessonID} INTEGER," +
                "${DBContract.EmployeeToPair.employeeID} INTEGER," +
                "${DBContract.EmployeeToPair.groupName} INTEGER," +
                //   "FOREIGN KEY (${DBContract.PairToEmployers.lessonID}) REFERENCES ${DBContract.Schedule.TABLE_NAME}(${DBContract.Schedule.scheduleID})," +
                "FOREIGN KEY (${DBContract.EmployeeToPair.groupName}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.name}))"

    */
/*fun createGroupScheduleIndex(db:SQLiteDatabase){
        db.execSQL(SQL_CREATE_SCHEDULE_INDEX)
    }
*//*

    private val SQL_CREATE_EMPLOYEEEXAMS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.EmployeeExams.TABLE_NAME} (" +
                "${DBContract.EmployeeSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.EmployeeSchedule.inScheduleID} INTEGER," +
                "${DBContract.EmployeeSchedule.day_of_week} INTEGER," +
                "${DBContract.EmployeeSchedule.auditories} TEXT," +
                "${DBContract.EmployeeSchedule.endLessonTime} TEXT," +
                "${DBContract.EmployeeSchedule.lessonTypeAbbrev} TEXT," +
                "${DBContract.EmployeeSchedule.note} TEXT," +
                "${DBContract.EmployeeSchedule.numSubgroup} INTEGER," +
                "${DBContract.EmployeeSchedule.startLessonTime} TEXT," +
                "${DBContract.EmployeeSchedule.studentGroups} TEXT," +
                "${DBContract.EmployeeSchedule.subject} TEXT," +
                "${DBContract.EmployeeSchedule.subjectFullName} TEXT," +
                "${DBContract.EmployeeSchedule.weekNumber} INTEGER," +
                "${DBContract.EmployeeSchedule.employeeID} INTEGER," +
                "${DBContract.EmployeeExams.dateLesson} TEXT," +
                "FOREIGN KEY (${DBContract.EmployeeSchedule.employeeID}) REFERENCES ${DBContract.CommonEmployee.TABLE_NAME}(${DBContract.CommonEmployee.commonEmployeeID}))"

    private val SQL_CREATE_EMPLOYEETOEXAM =
        "CREATE TABLE IF NOT EXISTS ${DBContract.EmployeeToExam.TABLE_NAME} (" +
                "${DBContract.EmployeeToExam.lessonID} INTEGER," +
                "${DBContract.EmployeeToExam.employeeID} INTEGER," +
                "${DBContract.EmployeeToExam.groupName} INTEGER," +
                //   "FOREIGN KEY (${DBContract.PairToEmployers.lessonID}) REFERENCES ${DBContract.Schedule.TABLE_NAME}(${DBContract.Schedule.scheduleID})," +
                "FOREIGN KEY (${DBContract.EmployeeToExam.groupName}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.name}))"

    private val SQL_CREATE_SUBGROUPSETTINGS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.SubgroupSettings.TABLE_NAME} (" +
                "${DBContract.SubgroupSettings.groupID} INTEGER," +
                "${DBContract.SubgroupSettings.subGroup} INTEGER )"

    private val SQL_CREATE_SETTINGS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Settings.TABLE_NAME} (" +
                "${DBContract.Settings.openedType} INTEGER," +
                "${DBContract.Settings.openedID} INTEGER, " +
                "${DBContract.Settings.lastWeekUpdate} TEXT, " +
                "${DBContract.Settings.week} INTEGER, " +
                "${DBContract.Settings.widgetID} INTEGER," +
                "${DBContract.Settings.widgetOpened} INTEGER )"

    override fun onCreate(p0: SQLiteDatabase?) {
        Log.v("DBDD", "p0 override fun onCreate(db: SQLiteDatabase?) {")
        with(p0!!) {
            try {
                */
/*val a1  = *//*
execSQL(SQL_CREATE_GROUPS)
                */
/*val a2  = *//*
execSQL(SQL_CREATE_COMMONSCHEDULE)
                */
/*val a3  = *//*
execSQL(SQL_CREATE_SCHEDULE)
                */
/*val a4  = *//*
execSQL(SQL_CREATE_EMPlOYEES)
                */
/*val a5  = *//*
execSQL(SQL_CREATE_FAVORITES)
                */
/*val a6  = *//*
execSQL(SQL_CREATE_EXAMS)
                */
/*val a7  = *//*
execSQL(SQL_CREATE_FINALSCHEDULE)
                */
/*val a8  = *//*
execSQL(SQL_CREATE_PAIRTOEMPLOYER)
                */
/*val a9  = *//*
execSQL(SQL_CREATE_COMMONEMPLOYEE)
                */
/*val a10 = *//*
execSQL(SQL_CREATE_EMPLOYEESCHEDULE)
                */
/*val a11 = *//*
execSQL(SQL_CREATE_EMPLOYEETOPAIR)
                */
/*val a12 = *//*
execSQL(SQL_CREATE_EMPLOYEEEXAMS)
                */
/*val a13 = *//*
execSQL(SQL_CREATE_FINALEMPLOYEESCHEDULE)
                */
/*val a14 = *//*
execSQL(SQL_CREATE_EMPLOYEETOEXAM)
                */
/*val a15 = *//*
execSQL(SQL_CREATE_SUBGROUPSETTINGS)
                */
/*val a16 = *//*
execSQL(SQL_CREATE_SETTINGS)


            } catch (e: Exception) {
                Log.v("DBDD", "SQL_ONCREATE_ERROR")
            }
            val values = ContentValues().apply {
                put(DBContract.Settings.week, 0)
            }
            insert(DBContract.Settings.TABLE_NAME, "week", values)
        }
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        */
/*   Log.v(
               "DBDD",
               "override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {"
           )*//*


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
                val a8 = db.execSQL(SQL_CREATE_SCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_SCHEDULE ERROR")
            }

            try {
                val a9 = db.execSQL(SQL_CREATE_FINALSCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_FINALSCHEDULE ERROR")
            }

            try {
                val a10 = db.execSQL(SQL_CREATE_PAIRTOEMPLOYER)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_PAIRTOEMPLOYER ERROR")
            }
        }

        if (oldVersion < 12) {
            try {
                with(db) {
                    execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME}")
                    execSQL("DELETE FROM ${DBContract.PairToEmployers.TABLE_NAME}")
                    execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
                    execSQL("ALTER TABLE ${DBContract.Employees.TABLE_NAME} ADD COLUMN ${DBContract.Employees.urlId} TEXT")
                }
            } catch (_: Exception) {

            }

            try {
                val a11 = db.execSQL(SQL_CREATE_COMMONEMPLOYEE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_PAIRTOEMPLOYER ERROR")
            }

            try {
                val a12 = db.execSQL(SQL_CREATE_EMPLOYEESCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_PAIRTOEMPLOYER ERROR")
            }

            try {
                val a13 = db.execSQL(SQL_CREATE_EMPLOYEETOPAIR)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_EMPLOYEETOPAIR ERROR")
            }

            try {
                val a14 = db.execSQL(SQL_CREATE_EMPLOYEEEXAMS)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_EMPLOYEEEXAMS ERROR")
            }

            try {
                val a15 = db.execSQL(SQL_CREATE_FINALEMPLOYEESCHEDULE)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_FINALEMPLOYEESCHEDULE ERROR")
            }

            try {
                val a16 = db.execSQL(SQL_CREATE_EMPLOYEETOEXAM)
            } catch (e: Exception) {
                Log.v("DBDD", "SQL_CREATE_EMPLOYEETOEXAM ERROR")
            }

            try {
                db.execSQL("ALTER TABLE ${DBContract.Favorites.TABLE_NAME} ADD COLUMN ${DBContract.Favorites.type} INTEGER")
            } catch (e: java.lang.Exception) {

            }

            db.rawQuery(
                "UPDATE ${DBContract.Favorites.TABLE_NAME} SET ${DBContract.Favorites.type} = 0",
                null
            ).close()
        }

        if (oldVersion < 13) {
            val a1 = db.execSQL(SQL_CREATE_SUBGROUPSETTINGS)

            val c: Cursor = db.rawQuery(
                "SELECT ${DBContract.CommonSchedule.commonScheduleID} " +
                        "FROM ${DBContract.CommonSchedule.TABLE_NAME}", null
            )

            with(c) {
                while (moveToNext()) {
                    val group =
                        getInt(getColumnIndexOrThrow(DBContract.CommonSchedule.commonScheduleID))


                    val values = ContentValues().apply {
                        put(DBContract.SubgroupSettings.groupID, group)
                        put(DBContract.SubgroupSettings.subGroup, 0)
                    }

                    val newRowId = db.insert(DBContract.SubgroupSettings.TABLE_NAME, null, values)

                }
            }

            c.close()
        }

        if (oldVersion < 14) {
            db.execSQL(SQL_CREATE_SETTINGS)

            val values = ContentValues().apply {
                put(DBContract.Settings.openedID, 0)
            }
            db.insert(DBContract.Settings.TABLE_NAME, null, values)
        }

        if (oldVersion < 15) {
            db.execSQL("ALTER TABLE ${DBContract.Settings.TABLE_NAME} ADD COLUMN ${DBContract.Settings.widgetID} INTEGER ")
            db.execSQL("ALTER TABLE ${DBContract.Settings.TABLE_NAME} ADD COLUMN ${DBContract.Settings.widgetOpened} INTEGER ")
            val values = ContentValues().apply {
                put(DBContract.Settings.widgetID, 0)
                put(DBContract.Settings.widgetOpened, 0)
            }
            db.update(DBContract.Settings.TABLE_NAME, values, null, null)
        }

        */
/*if(oldVersion < 16){

            val a1 = db.execSQL("DROP TABLE IF EXISTS ${DBContract.SubgroupSettings.TABLE_NAME}")
            val a2 = db.execSQL("DROP TABLE IF EXISTS ${DBContract.Settings.TABLE_NAME}")

            db.execSQL(SQL_CREATE_SUBGROUPSETTINGS)
            db.execSQL(SQL_CREATE_SETTINGS)

            val values = ContentValues().apply{
                put(DBContract.Settings.widgetID, 0)
                put(DBContract.Settings.widgetOpened, 0)
                put(DBContract.Settings.openedID, 0)
                put(DBContract.Settings.openedType, 0)

            }

            db.insert(DBContract.Settings.TABLE_NAME,"week" , values)

            val c: Cursor = db.rawQuery(
                "SELECT ${DBContract.CommonSchedule.commonScheduleID} " +
                        "FROM ${DBContract.CommonSchedule.TABLE_NAME}"
                ,null)

            with(c) {
                while (moveToNext()) {
                    val group = getInt(getColumnIndexOrThrow(DBContract.CommonSchedule.commonScheduleID))


                    val values = ContentValues().apply {
                        put(DBContract.SubgroupSettings.groupID, group)
                        put(DBContract.SubgroupSettings.subGroup, 0)
                    }

                    val newRowId = db.insert(DBContract.SubgroupSettings.TABLE_NAME, null, values)

                }
            }

            c.close()
        }*//*


    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 15
        const val DATABASE_NAME = "Schedule"
    }
}*/
