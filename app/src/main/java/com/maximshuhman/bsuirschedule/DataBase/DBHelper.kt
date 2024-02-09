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
                "${DBContract.Groups.course} varchar(7)," +
                "${DBContract.Groups.specialityAbbrev} varchar(10)," +
                "${DBContract.Groups.specialityName} varchar(50)," +
                "${DBContract.Groups.facultyAbbrev} varchar(10)," +
                "${DBContract.Groups.name} varchar(10) ) "

    private val SQL_CREATE_COMMONSCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.CommonSchedule.TABLE_NAME} (" +
                "${DBContract.CommonSchedule.commonScheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.CommonSchedule.startExamsDate} varchar(10)," +
                "${DBContract.CommonSchedule.endExamsDate} varchar(10)," +
                "${DBContract.CommonSchedule.startDate} varchar(10)," +
                "${DBContract.CommonSchedule.endDate} varchar(10)," +
                "${DBContract.CommonSchedule.lastUpdate} varchar(10)," +
                "${DBContract.CommonSchedule.lastBuild} varchar(10)," +
                "FOREIGN KEY (${DBContract.CommonSchedule.commonScheduleID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.groupID}));"

    private val SQL_CREATE_SCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Schedule.TABLE_NAME} (" +
                "${DBContract.Schedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.Schedule.inScheduleID} varchar(10)," +
                "${DBContract.Schedule.day_of_week} varchar(10)," +
                "${DBContract.Schedule.auditories} varchar(10)," +
                "${DBContract.Schedule.endLessonTime} varchar(10)," +
                "${DBContract.Schedule.lessonTypeAbbrev} varchar(7)," +
                "${DBContract.Schedule.note} varchar(50)," +
                "${DBContract.Schedule.numSubgroup} INTEGER," +
                "${DBContract.Schedule.startLessonTime} varchar(10)," +
                "${DBContract.Schedule.studentGroups} varchar(30)," +
                "${DBContract.Schedule.subject} varchar(20)," +
                "${DBContract.Schedule.subjectFullName} varchar(40)," +
                "${DBContract.Schedule.weekNumber} INTEGER," +
                "${DBContract.Schedule.groupID} INTEGER," +
                "${DBContract.Schedule.startLessonDate} varchar(10)," +
                "${DBContract.Schedule.endLessonDate} varchar(10)," +
                "FOREIGN KEY (${DBContract.Schedule.groupID}) REFERENCES ${DBContract.CommonSchedule.TABLE_NAME}(${DBContract.CommonSchedule.commonScheduleID}))"//," +
    // "FOREIGN KEY (${DBContract.Schedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"

    private val SQL_CREATE_FINALSCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.finalSchedule.TABLE_NAME} (" +
                "${DBContract.finalSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.Schedule.inScheduleID} INTEGER," +
                "${DBContract.finalSchedule.dayIndex} INTEGER," +
                "${DBContract.finalSchedule.day_of_week} INTEGER," +
                "${DBContract.finalSchedule.auditories} varchar(20)," +
                "${DBContract.finalSchedule.endLessonTime} varchar(10)," +
                "${DBContract.finalSchedule.lessonTypeAbbrev} varchar(7)," +
                "${DBContract.finalSchedule.note} varchar(50)," +
                "${DBContract.finalSchedule.numSubgroup} INTEGER," +
                "${DBContract.finalSchedule.startLessonTime} varchar(10)," +
                "${DBContract.finalSchedule.studentGroups} varchar(30)," +
                "${DBContract.finalSchedule.subject} varchar(10)," +
                "${DBContract.finalSchedule.subjectFullName} varchar(30)," +
                "${DBContract.finalSchedule.weekNumber} INTEGER," +
                "${DBContract.finalSchedule.groupID} INTEGER," +
                "${DBContract.finalSchedule.startLessonDate} varchar(10)," +
                "${DBContract.finalSchedule.endLessonDate} varchar(10)," +
                "FOREIGN KEY (${DBContract.finalSchedule.groupID}) REFERENCES ${DBContract.CommonSchedule.TABLE_NAME}(${DBContract.CommonSchedule.commonScheduleID}))"//," +
    // "FOREIGN KEY (${DBContract.finalSchedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"

    private val SQL_CREATE_EMPlOYEES =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Employees.TABLE_NAME} (" +
                "${DBContract.Employees.employeeID} INTEGER PRIMARY KEY," +
                "${DBContract.Employees.firstName} varchar(20)," +
                "${DBContract.Employees.middleName} varchar(20)," +
                "${DBContract.Employees.lastName} varchar(20)," +
                "${DBContract.Employees.photoLink} TEXT," +
                "${DBContract.Employees.degree} varchar(20)," +
                "${DBContract.Employees.degreeAbbrev} varchar(10)," +
                "${DBContract.Employees.rank} varchar(20)," +
                "${DBContract.Employees.department} varchar(30)," +
                "${DBContract.Employees.fio} varchar(20)," +
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
                "${DBContract.Schedule.auditories} varchar(30)," +
                "${DBContract.Schedule.endLessonTime} varchar(10)," +
                "${DBContract.Schedule.lessonTypeAbbrev} varchar(10)," +
                "${DBContract.Schedule.note} varchar(40)," +
                "${DBContract.Schedule.numSubgroup} INTEGER," +
                "${DBContract.Schedule.startLessonTime} varchar(10)," +
                "${DBContract.Schedule.studentGroups} varchar(30)," +
                "${DBContract.Schedule.subject} varchar(10)," +
                "${DBContract.Schedule.subjectFullName} varchar(30)," +
                "${DBContract.Schedule.weekNumber} INTEGER," +
                "${DBContract.Schedule.employeeID} INTEGER," +
                "${DBContract.Schedule.groupID} INTEGER," +
                "${DBContract.Exams.dateLesson} varchar(10)," +
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
                "${DBContract.CommonEmployee.startExamsDate} varchar(10)," +
                "${DBContract.CommonEmployee.endExamsDate} varchar(10)," +
                "${DBContract.CommonEmployee.startDate} varchar(10)," +
                "${DBContract.CommonEmployee.endDate} varchar(10)," +
                "${DBContract.CommonEmployee.lastUpdate} varchar(10)," +
                "${DBContract.CommonEmployee.lastBuild} varchar(10)," +
                "FOREIGN KEY (${DBContract.CommonEmployee.commonEmployeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}));"

    private val SQL_CREATE_EMPLOYEESCHEDULE =
        "CREATE TABLE ${DBContract.EmployeeSchedule.TABLE_NAME} (" +
                "${DBContract.EmployeeSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.EmployeeSchedule.inScheduleID} INTEGER," +
                "${DBContract.EmployeeSchedule.day_of_week} INTEGER," +
                "${DBContract.EmployeeSchedule.auditories} varchar(20)," +
                "${DBContract.EmployeeSchedule.endLessonTime} varchar(10)," +
                "${DBContract.EmployeeSchedule.lessonTypeAbbrev} varchar(10)," +
                "${DBContract.EmployeeSchedule.note} varchar(40)," +
                "${DBContract.EmployeeSchedule.numSubgroup} INTEGER," +
                "${DBContract.EmployeeSchedule.startLessonTime} varchar(10)," +
                "${DBContract.EmployeeSchedule.studentGroups} varchar(30)," +
                "${DBContract.EmployeeSchedule.subject} varchar(30)," +
                "${DBContract.EmployeeSchedule.subjectFullName} varchar(30)," +
                "${DBContract.EmployeeSchedule.weekNumber} INTEGER," +
                "${DBContract.EmployeeSchedule.employeeID} INTEGER," +
                "${DBContract.EmployeeSchedule.startLessonDate} varchar(10)," +
                "${DBContract.EmployeeSchedule.endLessonDate} varchar(10)," +
                "FOREIGN KEY (${DBContract.EmployeeSchedule.employeeID}) REFERENCES ${DBContract.CommonEmployee.TABLE_NAME}(${DBContract.CommonEmployee.commonEmployeeID}))"


    private val SQL_CREATE_FINALEMPLOYEESCHEDULE =
        "CREATE TABLE IF NOT EXISTS ${DBContract.finalEmployeeSchedule.TABLE_NAME} (" +
                "${DBContract.finalEmployeeSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.finalEmployeeSchedule.inScheduleID} INTEGER," +
                "${DBContract.finalEmployeeSchedule.day_of_week} INTEGER," +
                "${DBContract.finalEmployeeSchedule.auditories} varchar(20)," +
                "${DBContract.finalEmployeeSchedule.endLessonTime} varchar(10)," +
                "${DBContract.finalEmployeeSchedule.lessonTypeAbbrev} varchar(10)," +
                "${DBContract.finalEmployeeSchedule.note} varchar(40)," +
                "${DBContract.finalEmployeeSchedule.numSubgroup} INTEGER," +
                "${DBContract.finalEmployeeSchedule.startLessonTime} varchar(10)," +
                "${DBContract.finalEmployeeSchedule.studentGroups} varchar(30)," +
                "${DBContract.finalEmployeeSchedule.subject} varchar(30)," +
                "${DBContract.finalEmployeeSchedule.subjectFullName} varchar(30)," +
                "${DBContract.finalEmployeeSchedule.weekNumber} INTEGER," +
                "${DBContract.finalEmployeeSchedule.employeeID} INTEGER," +
                "${DBContract.finalEmployeeSchedule.startLessonDate} varchar(10)," +
                "${DBContract.finalEmployeeSchedule.endLessonDate} varchar(10)," +
                "FOREIGN KEY (${DBContract.finalEmployeeSchedule.employeeID}) REFERENCES ${DBContract.CommonEmployee.TABLE_NAME}(${DBContract.CommonEmployee.commonEmployeeID}))"
    // "FOREIGN KEY (${DBContract.finalSchedule.employeeID}) REFERENCES ${DBContract.Employees.TABLE_NAME}(${DBContract.Employees.employeeID}))"


    private val SQL_CREATE_EMPLOYEETOPAIR =
        "CREATE TABLE IF NOT EXISTS ${DBContract.EmployeeToPair.TABLE_NAME} (" +
                "${DBContract.EmployeeToPair.lessonID} INTEGER," +
                "${DBContract.EmployeeToPair.employeeID} INTEGER," +
                "${DBContract.EmployeeToPair.groupName} INTEGER," +
                //   "FOREIGN KEY (${DBContract.PairToEmployers.lessonID}) REFERENCES ${DBContract.Schedule.TABLE_NAME}(${DBContract.Schedule.scheduleID})," +
                "FOREIGN KEY (${DBContract.EmployeeToPair.groupName}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.name}))"

    /*fun createGroupScheduleIndex(db:SQLiteDatabase){
        db.execSQL(SQL_CREATE_SCHEDULE_INDEX)
    }
*/
    private val SQL_CREATE_EMPLOYEEEXAMS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.EmployeeExams.TABLE_NAME} (" +
                "${DBContract.EmployeeSchedule.scheduleID} INTEGER PRIMARY KEY," +
                "${DBContract.EmployeeSchedule.inScheduleID} INTEGER," +
                "${DBContract.EmployeeSchedule.day_of_week} INTEGER," +
                "${DBContract.EmployeeSchedule.auditories} varchar(20)," +
                "${DBContract.EmployeeSchedule.endLessonTime} varchar(10)," +
                "${DBContract.EmployeeSchedule.lessonTypeAbbrev} varchar(20)," +
                "${DBContract.EmployeeSchedule.note} varchar(100)," +
                "${DBContract.EmployeeSchedule.numSubgroup} INTEGER," +
                "${DBContract.EmployeeSchedule.startLessonTime} varchar(7)," +
                "${DBContract.EmployeeSchedule.studentGroups} varchar(7)," +
                "${DBContract.EmployeeSchedule.subject} varchar(20)," +
                "${DBContract.EmployeeSchedule.subjectFullName} varchar(50)," +
                "${DBContract.EmployeeSchedule.weekNumber} INTEGER," +
                "${DBContract.EmployeeSchedule.employeeID} INTEGER," +
                "${DBContract.EmployeeExams.dateLesson} varchar(10)," +
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
                "${DBContract.Settings.lastWeekUpdate} varchar(10), " +
                "${DBContract.Settings.week} INTEGER, " +
                "${DBContract.Settings.widgetID} INTEGER," +
                "${DBContract.Settings.widgetOpened} INTEGER )"

    override fun onCreate(p0: SQLiteDatabase?) {
        Log.v("DBDD", "p0 override fun onCreate(db: SQLiteDatabase?) {")
        with(p0!!) {
            try {
                /*val a1  = */execSQL(SQL_CREATE_GROUPS)
                /*val a2  = */execSQL(SQL_CREATE_COMMONSCHEDULE)
                /*val a3  = */execSQL(SQL_CREATE_SCHEDULE)
                /*val a4  = */execSQL(SQL_CREATE_EMPlOYEES)
                /*val a5  = */execSQL(SQL_CREATE_FAVORITES)
                /*val a6  = */execSQL(SQL_CREATE_EXAMS)
                /*val a7  = */execSQL(SQL_CREATE_FINALSCHEDULE)
                /*val a8  = */execSQL(SQL_CREATE_PAIRTOEMPLOYER)
                /*val a9  = */execSQL(SQL_CREATE_COMMONEMPLOYEE)
                /*val a10 = */execSQL(SQL_CREATE_EMPLOYEESCHEDULE)
                /*val a11 = */execSQL(SQL_CREATE_EMPLOYEETOPAIR)
                /*val a12 = */execSQL(SQL_CREATE_EMPLOYEEEXAMS)
                /*val a13 = */execSQL(SQL_CREATE_FINALEMPLOYEESCHEDULE)
                /*val a14 = */execSQL(SQL_CREATE_EMPLOYEETOEXAM)
                /*val a15 = */execSQL(SQL_CREATE_SUBGROUPSETTINGS)
                /*val a16 = */execSQL(SQL_CREATE_SETTINGS)


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
        /*   Log.v(
               "DBDD",
               "override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {"
           )*/

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
           // db.execSQL("ALTER TABLE ${DBContract.Settings.TABLE_NAME} ADD COLUMN ${DBContract.Settings.widgetID} INTEGER ")
           // db.execSQL("ALTER TABLE ${DBContract.Settings.TABLE_NAME} ADD COLUMN ${DBContract.Settings.widgetOpened} INTEGER ")
            val values = ContentValues().apply {
                put(DBContract.Settings.widgetID, 0)
                put(DBContract.Settings.widgetOpened, 0)
            }
            db.update(DBContract.Settings.TABLE_NAME, values, null, null)
        }

        if(oldVersion < 16) {



        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 16
        const val DATABASE_NAME = "Schedule"
    }
}