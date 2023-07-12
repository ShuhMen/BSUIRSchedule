package com.maximshuhman.bsuirschedule.Data

import CommonSchedule
import Employees
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.DataClasses.EmployeeExam
import com.maximshuhman.bsuirschedule.DataClasses.EmployeeLesson
import com.maximshuhman.bsuirschedule.DataClasses.Group
import com.maximshuhman.bsuirschedule.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

object EmployeeData {

    private fun getStringDef(jsonArray: JSONArray, index: Int, valueName: String): String? = try {
        jsonArray.getJSONObject(index).getString(valueName)
    } catch (e: Exception) {
        null
    }

    private fun getIntDef(jsonArray: JSONArray, index: Int, valueName: String): Int? = try {
        jsonArray.getJSONObject(index).getInt(valueName)
    } catch (e: Exception) {
        0
    }

    private fun getStringDef(jsonArray: JSONObject, valueName: String): String? = try {
        jsonArray.getString(valueName)
    } catch (e: Exception) {
        null
    }

    private fun getIntDef(jsonArray: JSONObject, valueName: String): Int? = try {
        jsonArray.getInt(valueName)
    } catch (e: Exception) {
        null
    }


    var curEmployeeID: Int? = 0
    var curEmployeeName: String = ""
    var curEmployeeUrlId: String = ""

    //  var curGroupSpeciality: String = ""
    //var curGroupCourse: Int? = 0

    lateinit var commonSchedule: CommonSchedule
    private var inScheduleID = 0
    private var inExamsID = 0
    private val listOfPairs = mutableListOf<EmployeeLesson>()
    val ScheduleList = mutableListOf<EmployeeLesson>()
    val employeesList = mutableListOf<Employees>()
    private val listOfEmployees = mutableListOf<Employees>()
    val ExamsList = mutableListOf<EmployeeExam>()

    private fun addLessonToDB(
        json: JSONObject,
        arrayName: String,
        db: SQLiteDatabase,
        grID: Int,
        dayOfWeek: Int
    ): Int {

        val js: JSONArray? = json.optJSONArray(arrayName)

        if (js != null) {
            for (i in 0 until js.length()) {
                if (getStringDef(js.getJSONObject(i), "lessonTypeAbbrev") != "Консультация" &&
                    getStringDef(js.getJSONObject(i), "lessonTypeAbbrev") != "Экзамен"
                ) {
                    db.insert(
                        DBContract.EmployeeSchedule.TABLE_NAME,
                        null,
                        addPairToList(dayOfWeek, js.getJSONObject(i), grID, db)
                    )
                    inScheduleID++

                }

            }
        }
        return 0
    }

    private fun addPairToList(
        dayOfWeek: Int,
        startPair: JSONObject,
        employeeID: Int,
        db: SQLiteDatabase
    ): ContentValues {


        var week_numbers = ""
        Array(startPair.getJSONArray("weekNumber").length()) {
            week_numbers += startPair.getJSONArray("weekNumber").getInt(it).toString()
        }

        var auditories = ""
        Array(startPair.getJSONArray("auditories").length()) {
            auditories += startPair.getJSONArray("auditories").getString(it).toString() + " "
        }

        val values = ContentValues().apply {
            put(DBContract.EmployeeSchedule.employeeID, employeeID)
            put(DBContract.EmployeeSchedule.inScheduleID, inScheduleID)
            put(DBContract.EmployeeSchedule.day_of_week, dayOfWeek)
            put(DBContract.EmployeeSchedule.auditories, auditories)
            put(
                DBContract.EmployeeSchedule.endLessonTime,
                getStringDef(startPair, "endLessonTime")
            )
            put(
                DBContract.EmployeeSchedule.lessonTypeAbbrev,
                getStringDef(startPair, "lessonTypeAbbrev")
            )
            put(DBContract.EmployeeSchedule.note, getStringDef(startPair, "note"))
            put(DBContract.EmployeeSchedule.numSubgroup, getIntDef(startPair, "numSubgroup"))
            put(
                DBContract.EmployeeSchedule.startLessonTime,
                getStringDef(startPair, "startLessonTime")
            )
            put(
                DBContract.EmployeeSchedule.subject, try {
                    startPair.getString("subject")
                } catch (e: Exception) {
                    ""
                }
            )
            put(
                DBContract.EmployeeSchedule.subjectFullName, try {
                    startPair.getString("subjectFullName")
                } catch (e: Exception) {
                    ""
                }
            )
            put(DBContract.EmployeeSchedule.weekNumber, week_numbers)

            put(
                DBContract.EmployeeSchedule.startLessonDate, try {
                    startPair.getString("startLessonDate")
                } catch (e: Exception) {
                    ""
                }
            )
            put(
                DBContract.EmployeeSchedule.endLessonDate, try {
                    startPair.getString("endLessonDate")
                } catch (e: Exception) {
                    ""
                }
            )
        }


        for (i in 0 until startPair.getJSONArray("studentGroups").length()) {
            val connect = ContentValues().apply {
                put(DBContract.EmployeeToPair.lessonID, inScheduleID)
                put(
                    DBContract.EmployeeToPair.employeeID,
                    employeeID
                )
                put(
                    DBContract.EmployeeToPair.groupName,
                    getStringDef(startPair.getJSONArray("studentGroups"), i, "name")
                )
            }

            db.insert(DBContract.EmployeeToPair.TABLE_NAME, null, connect)

        }

        return values
    }


    private fun addExamToDB(
        json: JSONArray,
        db: SQLiteDatabase,
        grID: Int
    ): Int {

        for (i in 0 until json.length()) {
            db.insert(
                DBContract.EmployeeExams.TABLE_NAME,
                null,
                addExamToList(json.getJSONObject(i), grID, db)
            )

            inExamsID++
        }

        return 0
    }

    private fun addExamToList(
        startPair: JSONObject,
        employeeId: Int,
        db: SQLiteDatabase

    ): ContentValues {

        var week_numbers = ""
        Array(startPair.getJSONArray("weekNumber").length()) {
            week_numbers += startPair.getJSONArray("weekNumber").getInt(it).toString()
        }

        var auditories = ""
        Array(startPair.getJSONArray("auditories").length()) {
            auditories += startPair.getJSONArray("auditories").getString(it).toString() + " "
        }

        val values = ContentValues().apply {
            put(DBContract.EmployeeSchedule.employeeID, employeeId)
            put(DBContract.EmployeeSchedule.inScheduleID, inExamsID)
            put(DBContract.EmployeeSchedule.auditories, auditories)
            put(
                DBContract.EmployeeSchedule.endLessonTime,
                getStringDef(startPair, "endLessonTime")
            )
            put(
                DBContract.EmployeeSchedule.lessonTypeAbbrev,
                getStringDef(startPair, "lessonTypeAbbrev")
            )
            put(DBContract.EmployeeSchedule.note, getStringDef(startPair, "note"))
            put(DBContract.EmployeeSchedule.numSubgroup, getIntDef(startPair, "numSubgroup"))
            put(
                DBContract.EmployeeSchedule.startLessonTime,
                getStringDef(startPair, "startLessonTime")
            )
            put(
                DBContract.EmployeeSchedule.subject, try {
                    startPair.getString("subject")
                } catch (e: Exception) {
                    ""
                }
            )
            put(
                DBContract.EmployeeSchedule.subjectFullName, try {
                    startPair.getString("subjectFullName")
                } catch (e: Exception) {
                    ""
                }
            )
            put(DBContract.EmployeeSchedule.weekNumber, week_numbers)

            put(
                DBContract.Exams.dateLesson, try {
                    startPair.getString("dateLesson")
                } catch (e: Exception) {
                    null
                }
            )
        }
        for (i in 0 until startPair.getJSONArray("studentGroups").length()) {
            val connect = ContentValues().apply {
                put(DBContract.EmployeeToExam.lessonID, inExamsID)
                put(
                    DBContract.EmployeeToExam.employeeID,
                    employeeId
                )
                put(
                    DBContract.EmployeeToExam.groupName,
                    getStringDef(startPair.getJSONArray("studentGroups"), i, "name")
                )
            }

            db.insert(DBContract.EmployeeToExam.TABLE_NAME, null, connect)

        }

        return values
    }


    private fun fillExamsTable(json_common: JSONObject, context: Context): Int {
        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase
        val grID: Int

        try {
            grID = json_common.getJSONObject("employeeDto").getInt("id")
        } catch (e: JSONException) {
            Log.v("ExamsTable", e.toString())
            return 1
        }

        val json: JSONArray

        try {
            json = json_common.getJSONArray("exams")
        } catch (e: JSONException) {
            return 5
        }

        if (addExamToDB(json, db, grID) != 0)
            return 1
        return 0
    }

    fun makeExams(context: Context, employeeID: Int): Int {

        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase

        ExamsList.clear()

        val count: Cursor = db.rawQuery(
            "SELECT COUNT(*) as cnt FROM ${DBContract.EmployeeExams.TABLE_NAME} WHERE ${DBContract.EmployeeExams.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = $employeeID",
            null
        )
        count.moveToFirst()

        if (count.getInt(0) != 0) {
            count.close()

            val c: Cursor = db.rawQuery(
                "SELECT * FROM ${DBContract.EmployeeExams.TABLE_NAME} " +
                        "INNER JOIN ${DBContract.CommonEmployee.TABLE_NAME} ON (${DBContract.EmployeeExams.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                        "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                        "WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $employeeID " //+
                /*"ORDER BY ${DBContract.EmployeeExams.dateLesson}"*/,

                null
            )

            with(c) {
                c.moveToFirst()

                do {
                    val inScheduleIDLocal =
                        getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.inScheduleID))


                    val cursor = db.rawQuery(
                        "SELECT * FROM ${DBContract.EmployeeToExam.TABLE_NAME} " +
                                "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON " +
                                "(${DBContract.EmployeeToExam.TABLE_NAME}.${DBContract.EmployeeToExam.groupName} = ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.name}) " +
                                "WHERE ${DBContract.EmployeeToExam.lessonID} = $inScheduleIDLocal " +
                                "AND ${DBContract.EmployeeToExam.employeeID} = $employeeID",
                        null
                    )

                    cursor.moveToFirst()


                    val list = ArrayList<Group>()

                    do {
                        list.add(
                            try {
                                Group(
                                    0,
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.name)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.facultyAbbrev)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.specialityName)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Groups.course)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Groups.groupID))
                                )
                            } catch (e: Exception) {
                                Group(0, "", "", "", "", 0, 0)
                            }
                        )
                    } while (cursor.moveToNext())


                    cursor.close()

                    //    moveToFirst()

                    ExamsList.add(
                        EmployeeExam(
                            getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.day_of_week)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.auditories)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.endLessonTime)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.lessonTypeAbbrev)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.note)),
                            getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.numSubgroup)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.startLessonTime)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.subject)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.subjectFullName)),
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.weekNumber)),
                            list, "",
                            "",

                            try {
                                getString(getColumnIndexOrThrow(DBContract.Exams.dateLesson))
                            } catch (e: Exception) {
                                ""
                            }

                        )
                    )


                } while (moveToNext())
            }
            c.close()

        } else {
            count.close()
            return 1
        }

        Calendar.getInstance()
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

        ExamsList.sortWith(DateComparator)
        return 0
    }

    class DateComparator {

        companion object : Comparator<EmployeeExam> {

            override fun compare(a: EmployeeExam, b: EmployeeExam): Int {
                val formatter =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))
                if (formatter.parse(a.dateLesson.toString())!!
                        .after(formatter.parse(b.dateLesson.toString()))
                )
                    return 0
                else
                    return 1
            }
        }
    }

    fun fillListOfPairs(db: SQLiteDatabase, employeeID: Int): Int {


        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.EmployeeSchedule.TABLE_NAME} " +
                    "INNER JOIN ${DBContract.CommonEmployee.TABLE_NAME} ON (${DBContract.EmployeeSchedule.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                    "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                    // "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.EmployeeSchedule.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                    "WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $employeeID " +
                    "ORDER BY ${DBContract.EmployeeSchedule.TABLE_NAME}.${DBContract.EmployeeSchedule.day_of_week} ",

            null
        )


        with(c) {

            moveToFirst()
            commonSchedule = CommonSchedule(
                getString(getColumnIndexOrThrow(DBContract.CommonEmployee.startDate)),
                getString(getColumnIndexOrThrow(DBContract.CommonEmployee.endDate)),
                "", "",
                getString(getColumnIndexOrThrow(DBContract.CommonEmployee.lastBuild))
            )

            do {

                val inScheduleIDLocal =
                    getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.inScheduleID))


                val cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.EmployeeToPair.TABLE_NAME} " +
                            "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON " +
                            "(${DBContract.EmployeeToPair.TABLE_NAME}.${DBContract.EmployeeToPair.groupName} = ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.name}) " +
                            "WHERE ${DBContract.EmployeeToPair.lessonID} = $inScheduleIDLocal " +
                            "AND ${DBContract.EmployeeToPair.employeeID} = $employeeID",
                    null
                )

                cursor.moveToFirst()

                val list = ArrayList<Group>()

                do {
                    list.add(
                        try {
                            Group(
                                0,
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.name)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.facultyAbbrev)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.specialityName)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Groups.course)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Groups.groupID))
                            )
                        } catch (e: Exception) {
                            null
                        } as Group
                    )
                } while (cursor.moveToNext())


                cursor.close()

                listOfPairs.add(
                    EmployeeLesson(
                        getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.inScheduleID)),
                        getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.day_of_week)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.auditories)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.endLessonTime)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.lessonTypeAbbrev)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.note)),
                        getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.numSubgroup)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.startLessonTime)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.subject)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.subjectFullName)),
                        getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.weekNumber)),
                        list,
                        try {
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.startLessonDate))
                        } catch (e: Exception) {
                            ""
                        },
                        try {
                            getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.endLessonDate))
                        } catch (e: Exception) {
                            ""
                        }, null
                    )
                )


            } while (moveToNext())
        }
        c.close()

        return if (listOfPairs.size != 0) {
            val tim: EmployeeLesson = listOfPairs[0].copy()

            tim.day_of_week = 8

            listOfPairs.add(tim)
            0
        } else
            1
    }

    private fun fillSheduleTable(json_common: JSONObject, context: Context): Int {
        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase
        val grID: Int

        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))
        val cur: String = formatter.format(calendar.time)

        try {
            grID = json_common.getJSONObject("employeeDto").getInt("id")
        } catch (e: JSONException) {
            return 1
        }


        val values = ContentValues().apply {
            put(DBContract.CommonEmployee.commonEmployeeID, grID)
            put(
                DBContract.CommonEmployee.startDate,
                getStringDef(json_common, "startDate")
            )
            put(DBContract.CommonEmployee.endDate, getStringDef(json_common, "endDate"))
            put(
                DBContract.CommonEmployee.startExamsDate,
                getStringDef(json_common, "startExamsDate")
            )
            put(
                DBContract.CommonEmployee.endExamsDate,
                getStringDef(json_common, "endExamsDate")
            )
            put(DBContract.CommonEmployee.lastUpdate, cur)
        }

        val newRowId = db.insert(DBContract.CommonEmployee.TABLE_NAME, null, values)

        if (newRowId.toInt() == -1)
            return 1

        val json: JSONObject

        try {
            json = json_common.getJSONObject("schedules")

        } catch (e: JSONException) {
            return 4
        }

        inScheduleID = 0

        if (addLessonToDB(json, "Понедельник", db, grID, 1) +
            addLessonToDB(json, "Вторник", db, grID, 2) +
            addLessonToDB(json, "Среда", db, grID, 3) +
            addLessonToDB(json, "Четверг", db, grID, 4) +
            addLessonToDB(json, "Пятница", db, grID, 5) +
            addLessonToDB(json, "Суббота", db, grID, 6) +
            addLessonToDB(json, "Воскресенье", db, grID, 7) != 0
        )
            return 1
        return 0
    }

    fun fillScheduleList(
        calendar: Calendar,
        formatter: SimpleDateFormat,
        context: Context
    ) {
        var week: Int

        var ind: Int
        val db = DbHelper(context).writableDatabase

        val settings = db.rawQuery(
            "SELECT ${DBContract.Settings.week}, ${DBContract.Settings.lastWeekUpdate} FROM ${DBContract.Settings.TABLE_NAME}",
            null
        )

        settings.moveToFirst()
        val previous =
            settings.getString(settings.getColumnIndexOrThrow(DBContract.Settings.lastWeekUpdate)) //prefs.openedGroup
        // val type = settings.getInt(settings.getColumnIndexOrThrow(DBContract.Settings.openedID))  //prefs.openedType

        week = Requests.getCurrent().res

        if (week == 0) {
            if (previous != "") {
                week = settings.getInt(settings.getColumnIndexOrThrow(DBContract.Settings.week))

                val prevday = formatter.parse(previous)!!.day


                val diff = ChronoUnit.DAYS.between(
                    formatter.parse(previous)!!.toInstant(),
                    formatter.parse(formatter.format(calendar.time))!!.toInstant()
                )

                week = ((week + (prevday + diff) / 7) % 4).toInt()

                if (week == 0)
                    week = 1
            } else
                week = Requests.getCurrent().res
        }

        val values = ContentValues().apply {
            put(DBContract.Settings.week, week)
            put(DBContract.Settings.lastWeekUpdate, formatter.format(calendar.time))
        }

        db.update(DBContract.Settings.TABLE_NAME, values, null, null)

        settings.close()

        val day: Int = calendar.get(Calendar.DAY_OF_WEEK)

        val startLessonsDate = formatter.parse(commonSchedule.startDate)
        val endLessonsDate = formatter.parse(commonSchedule.endDate)

        var curday: Int
        var weeks = 0

        curday = if (day == 1) 7 else day - 1

        ind = listOfPairs.indexOf(listOfPairs.firstOrNull {
            (it.day_of_week == curday && it.weekNumber.contains(week.toString()))
        })

        if (ind == -1) {
            while (ind == -1) {
                if (curday == 7)
                    week = week % 4 + 1

                curday = curday % 7 + 1
                calendar.add(Calendar.DATE, 1)

                ind = listOfPairs.indexOf(listOfPairs.firstOrNull {
                    (it.day_of_week == curday && it.weekNumber.contains(week.toString()))
                })
            }

        }

        var i: Int = ind

        while (i < listOfPairs.size) {
            if (listOfPairs[i].day_of_week == 8) {
                week = week % 4 + 1
                ++weeks

                if (weeks == 4)
                    break

                i = 0
            } else {
                if (listOfPairs[i].weekNumber
                        .contains(week.toString()) && listOfPairs[i].day_of_week != 8
                )
                    ScheduleList.add(listOfPairs[i])

                i++

            }
        }

        i = 2

        val l = ScheduleList[0].copy()

        val system = context
        l.day_of_week = 9
        l.note = "${
            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                2 -> system.getString(R.string.monday)
                3 -> system.getString(R.string.tuesday)
                4 -> system.getString(R.string.wednesday)
                5 -> system.getString(R.string.thursday)
                6 -> system.getString(R.string.friday)
                7 -> system.getString(R.string.saturday)
                1 -> system.getString(R.string.sunday)
                else -> "Ошибка"
            }
        }, " + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                when (calendar.get(Calendar.MONTH)) {
                    0 -> system.getString(R.string.yanuary)
                    1 -> system.getString(R.string.february)
                    2 -> system.getString(R.string.march)
                    3 -> system.getString(R.string.april)
                    4 -> system.getString(R.string.may)
                    5 -> system.getString(R.string.june)
                    6 -> system.getString(R.string.july)
                    7 -> system.getString(R.string.august)
                    8 -> system.getString(R.string.september)
                    9 -> system.getString(R.string.october)
                    10 -> system.getString(R.string.november)
                    11 -> system.getString(R.string.december)
                    else -> "Ошибка"
                }

        ScheduleList.add(0, l)

        while (i < ScheduleList.size) {

            if (ScheduleList[i - 1].day_of_week != ScheduleList[i].day_of_week || (ScheduleList[i - 1].startLessonTime!! > ScheduleList[i].startLessonTime!! || ScheduleList[i - 1].inLessonID == ScheduleList[i].inLessonID)) {

                var k: Int = i - 1
                //i--
                var curent = formatter.parse(formatter.format(calendar.time))


                var delta = ScheduleList[i].day_of_week - ScheduleList[i - 1].day_of_week

                if (delta == 0)
                    delta = 7

                while (ScheduleList[k].day_of_week != 9) {


                    try {
                        val start = formatter.parse(ScheduleList[k].startLessonDate.toString())
                        val end = formatter.parse(ScheduleList[k].endLessonDate.toString())

                        if (start != null && curent != null) {
                            if ((curent.after(end) || start.after(curent))) {
                                ScheduleList.removeAt(k)
                                i--
                            }
                        }

                    } catch (e: ParseException) {
                        Log.v(
                            "DateParce",
                            "can't parse date" + ScheduleList[k].subject + " " + ScheduleList[k].weekNumber + " " + ScheduleList[k].day_of_week
                        )
                    } catch (_: java.lang.IndexOutOfBoundsException) {

                    }

                    k--
                }

                if (delta < 0)
                    calendar.add(
                        Calendar.DATE,
                        delta + 7
                    )
                else
                    calendar.add(
                        Calendar.DATE,
                        delta
                    )


                curent = formatter.parse(formatter.format(calendar.time))

                if (curent?.after(endLessonsDate) == true) {
                    ScheduleList.subList(i, ScheduleList.size).clear()
                    break
                }

                val les: EmployeeLesson = ScheduleList[0].copy()
                les.day_of_week = 9
                les.note = "${
                    when (ScheduleList[i].day_of_week) {
                        1 -> system.getString(R.string.monday)
                        2 -> system.getString(R.string.tuesday)
                        3 -> system.getString(R.string.wednesday)
                        4 -> system.getString(R.string.thursday)
                        5 -> system.getString(R.string.friday)
                        6 -> system.getString(R.string.saturday)
                        7 -> system.getString(R.string.sunday)
                        else -> "Ошибка"
                    }
                }, " + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                        when (calendar.get(Calendar.MONTH)) {
                            0 -> system.getString(R.string.yanuary)
                            1 -> system.getString(R.string.february)
                            2 -> system.getString(R.string.march)
                            3 -> system.getString(R.string.april)
                            4 -> system.getString(R.string.may)
                            5 -> system.getString(R.string.june)
                            6 -> system.getString(R.string.july)
                            7 -> system.getString(R.string.august)
                            8 -> system.getString(R.string.september)
                            9 -> system.getString(R.string.october)
                            10 -> system.getString(R.string.november)
                            11 -> system.getString(R.string.december)
                            else -> "Ошибка"
                        }

                ScheduleList.add(i, les)
                i++
            }
            i++
        }

        var k: Int = i - 1

        val curent = formatter.parse(formatter.format(calendar.time))
        while (ScheduleList[k].day_of_week != 9) {

            try {
                val start = formatter.parse(ScheduleList[k].startLessonDate.toString())
                val end = formatter.parse(ScheduleList[k].endLessonDate.toString())

                if (start != null && curent != null) {
                    if ((curent.after(end) || start.after(curent))) {
                        ScheduleList.removeAt(k)
                    }
                }

            } catch (e: ParseException) {
                Log.v(
                    "DateParce",
                    "can't parse date" + ScheduleList[k].subject + " " + ScheduleList[k].weekNumber + " " + ScheduleList[k].day_of_week
                )
            } catch (_: java.lang.IndexOutOfBoundsException) {

            }

            k--
        }

        i = ScheduleList.size - 1

        while (i > 1) {
            if (ScheduleList[i - 1].day_of_week == 9 && ScheduleList[i].day_of_week == 9)
                ScheduleList.removeAt(i - 1)
            i--
        }



        if (ScheduleList[ScheduleList.size - 1].day_of_week == 9)
            ScheduleList.removeAt(ScheduleList.size - 1)

        /* if (ScheduleList[0].day_of_week == ScheduleList[1].day_of_week)
             ScheduleList.removeAt(0)*/


    }

    fun finalBuild(db: SQLiteDatabase, employeeID: Int) {
        db.execSQL("DELETE FROM ${DBContract.finalEmployeeSchedule.TABLE_NAME} WHERE ${DBContract.EmployeeSchedule.employeeID} = $employeeID")
        for (i in 0 until ScheduleList.size) {
            val values = ContentValues().apply {
                put(DBContract.finalEmployeeSchedule.employeeID, employeeID)
                put(DBContract.finalEmployeeSchedule.inScheduleID, ScheduleList[i].inLessonID)
                put(DBContract.finalEmployeeSchedule.dayIndex, i)
                put(DBContract.finalEmployeeSchedule.day_of_week, ScheduleList[i].day_of_week)
                put(DBContract.finalEmployeeSchedule.auditories, ScheduleList[i].auditories)
                put(DBContract.finalEmployeeSchedule.endLessonTime, ScheduleList[i].endLessonTime)
                put(
                    DBContract.finalEmployeeSchedule.lessonTypeAbbrev,
                    ScheduleList[i].lessonTypeAbbrev
                )
                put(DBContract.finalEmployeeSchedule.note, ScheduleList[i].note)
                put(DBContract.finalEmployeeSchedule.numSubgroup, ScheduleList[i].numSubgroup)
                put(
                    DBContract.finalEmployeeSchedule.startLessonTime,
                    ScheduleList[i].startLessonTime
                )
                put(DBContract.finalEmployeeSchedule.subject, ScheduleList[i].subject)
                put(
                    DBContract.finalEmployeeSchedule.subjectFullName,
                    ScheduleList[i].subjectFullName
                )
                put(DBContract.finalEmployeeSchedule.weekNumber, ScheduleList[i].weekNumber)
                put(
                    DBContract.finalEmployeeSchedule.startLessonDate,
                    ScheduleList[i].startLessonDate
                )
                put(DBContract.finalEmployeeSchedule.endLessonDate, ScheduleList[i].endLessonDate)
            }

            db.insert(DBContract.finalEmployeeSchedule.TABLE_NAME, null, values)
        }
    }

    fun loadFromFinal(
        employeeID: Int,
        formatter: SimpleDateFormat,
        curent: Date,
        db: SQLiteDatabase,
        context: Context
    ): Int {


        var calendar = Calendar.getInstance()
        val common: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.CommonEmployee.TABLE_NAME} " +
                    "WHERE ${DBContract.CommonEmployee.commonEmployeeID} = $employeeID ",

            null
        )

        with(common) {
            moveToFirst()
            commonSchedule = CommonSchedule(
                getString(getColumnIndexOrThrow(DBContract.CommonEmployee.startDate)),
                getString(getColumnIndexOrThrow(DBContract.CommonEmployee.endDate)),
                "", "",
                getString(getColumnIndexOrThrow(DBContract.CommonEmployee.lastBuild))
            )
        }

        common.close()

        if (commonSchedule.lastBuild != null && commonSchedule.lastBuild != "") {
            if (formatter.parse(commonSchedule.lastBuild).before(curent)) {
                if (fillListOfPairs(db, employeeID) == 1)
                    return 1
                fillScheduleList(calendar, formatter, context)
                finalBuild(db, employeeID)

                calendar = Calendar.getInstance()

                val values = ContentValues().apply {
                    put(DBContract.CommonEmployee.lastBuild, formatter.format(calendar.time))
                }

                db.update(
                    DBContract.CommonEmployee.TABLE_NAME,
                    values,
                    "${DBContract.CommonEmployee.commonEmployeeID} = $employeeID",
                    null
                )
                return 0

            } else {
                val c: Cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.finalEmployeeSchedule.TABLE_NAME} " +
                            "INNER JOIN ${DBContract.CommonEmployee.TABLE_NAME} ON (${DBContract.finalEmployeeSchedule.TABLE_NAME}.${DBContract.finalEmployeeSchedule.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                            "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                            //  "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.finalEmployeeSchedule.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                            "WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $employeeID " +
                            "ORDER BY ${DBContract.finalEmployeeSchedule.TABLE_NAME}.${DBContract.finalEmployeeSchedule.dayIndex} ",

                    null
                )

                with(c) {
                    moveToFirst()
                    while (moveToNext()) {

                        val inScheduleIDLocal =
                            getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.inScheduleID))

                        val list = ArrayList<Group>()
                        val cursor: Cursor = db.rawQuery(
                            "SELECT * FROM ${DBContract.EmployeeToPair.TABLE_NAME} " +
                                    "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON " +
                                    "(${DBContract.EmployeeToPair.TABLE_NAME}.${DBContract.EmployeeToPair.groupName} = ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.name})" +
                                    "WHERE ${DBContract.EmployeeToPair.lessonID} = $inScheduleIDLocal " +
                                    "AND ${DBContract.EmployeeToPair.employeeID} = $employeeID",
                            null
                        )


                        cursor.moveToFirst()

                        do {
                            list.add(
                                try {
                                    Group(
                                        0,
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.name)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.facultyAbbrev)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.specialityName)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev)),
                                        cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Groups.course)),
                                        cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Groups.groupID))
                                    )
                                } catch (e: Exception) {
                                    null
                                } as Group
                            )
                        } while (cursor.moveToNext())


                        cursor.close()

                        ScheduleList.add(
                            EmployeeLesson(
                                getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.inScheduleID)),
                                getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.day_of_week)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.auditories)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.endLessonTime)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.lessonTypeAbbrev)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.note)),
                                getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.numSubgroup)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.startLessonTime)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.subject)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.subjectFullName)),
                                getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.weekNumber)),
                                list,
                                try {
                                    getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.startLessonDate))
                                } catch (e: Exception) {
                                    ""
                                },
                                try {
                                    getString(getColumnIndexOrThrow(DBContract.EmployeeSchedule.endLessonDate))
                                } catch (e: Exception) {
                                    ""
                                }, null
                            )
                        )


                    }
                }
                c.close()

                return if (ScheduleList.size != 0) {
                    0
                } else
                    4

            }
        } else {
            val values = ContentValues().apply {
                put(DBContract.CommonEmployee.lastBuild, formatter.format(calendar.time))
            }

            db.update(
                DBContract.CommonEmployee.TABLE_NAME,
                values,
                "${DBContract.CommonEmployee.commonEmployeeID} = $employeeID",
                null
            )

            if (fillListOfPairs(db, employeeID) == 1)
                return 1

            fillScheduleList(calendar, formatter, context)
            finalBuild(db, employeeID)
            return 0
        }
    }

    fun makeSchedule(
        emplIdenticator: String,
        context: Context?,
        employeeID: Int?,
        mode: Int?
    ): Int {

        try {
            if (employeeID == null || context == null)
                return 1

            val dbHelper = DbHelper(context)
            val db = dbHelper.writableDatabase

            var calendar: Calendar = Calendar.getInstance()
            val formatter =
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

            val curent = formatter.parse(formatter.format(calendar.time))

            ScheduleList.clear()
            listOfPairs.clear()
            val c: Cursor = db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.EmployeeSchedule.TABLE_NAME} WHERE " +
                        "${DBContract.EmployeeSchedule.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = $employeeID",
                null
            )
            c.moveToFirst()

            var response = JSONResponse(0, "", JSONObject())

            if (c.getInt(0) == 0 || mode == 1) {

                response = Requests.getEmployeeSchedule(emplIdenticator)
                if (response.errorCode == 0) {
                    c.close()

                    db.execSQL("DELETE FROM ${DBContract.EmployeeSchedule.TABLE_NAME} WHERE ${DBContract.EmployeeSchedule.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = $employeeID")
                    db.execSQL("DELETE FROM ${DBContract.finalEmployeeSchedule.TABLE_NAME} WHERE ${DBContract.finalEmployeeSchedule.TABLE_NAME}.${DBContract.finalEmployeeSchedule.employeeID} = $employeeID")
                    db.execSQL("DELETE FROM ${DBContract.EmployeeToExam.TABLE_NAME} WHERE ${DBContract.EmployeeToExam.TABLE_NAME}.${DBContract.EmployeeToExam.employeeID} = $employeeID")
                    db.execSQL("DELETE FROM ${DBContract.EmployeeExams.TABLE_NAME} WHERE ${DBContract.EmployeeExams.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = $employeeID")
                    db.execSQL("DELETE FROM ${DBContract.EmployeeToPair.TABLE_NAME} WHERE ${DBContract.EmployeeToPair.TABLE_NAME}.${DBContract.EmployeeToPair.employeeID} = $employeeID")
                    db.execSQL("DELETE FROM ${DBContract.CommonEmployee.TABLE_NAME} WHERE ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID} = $employeeID")

                    var err = 0

                    if (fillSheduleTable(response.obj, context) != 0)
                        err = 4

                    if (fillExamsTable(response.obj, context) != 0)
                        err = 5

                    if (err != 0)
                        return err

                    if (fillListOfPairs(db, employeeID) == 1)
                        return 1

                    if (ScheduleList.size == 0)
                        return 4

                    fillScheduleList(calendar, formatter, context)

                    finalBuild(db, employeeID)

                    calendar = Calendar.getInstance()

                    val values = ContentValues().apply {
                        put(DBContract.CommonEmployee.lastBuild, formatter.format(calendar.time))
                    }

                    db.update(
                        DBContract.CommonEmployee.TABLE_NAME,
                        values,
                        "${DBContract.CommonEmployee.commonEmployeeID} = $employeeID",
                        null
                    )

                } else
                    if (c.getInt(0) != 0) {
                        c.close()
                        if (loadFromFinal(employeeID, formatter, curent, db, context) == 1)
                            return 1
                    } else
                        return 5
            } else {

                c.close()
                if (loadFromFinal(employeeID, formatter, curent, db, context) == 1)
                    return 1


            }




            if (ScheduleList.size == 0)
                return 4

            return if (response.errorCode != 0)
                response.errorCode
            else 0
        } catch (e: CursorIndexOutOfBoundsException) {
            return 5
        } catch (e: Exception) {
            return 6
        }
    }

    private fun fillEmployeesTable(db: SQLiteDatabase, employeesArray: JSONArray): Int {

        var i = 0
        while (i < employeesArray.length() - 1) {

            var dep = ""
            val ar = employeesArray.getJSONObject(i).getJSONArray("academicDepartment")

            Array(ar.length()) {
                dep += ar.getString(it).toString() + " "
            }

            val values = ContentValues().apply {
                put(
                    DBContract.Employees.employeeID,
                    getStringDef(employeesArray, i, "id")
                )
                put(
                    DBContract.Employees.firstName,
                    getStringDef(employeesArray, i, "firstName")
                )
                put(
                    DBContract.Employees.middleName,
                    getStringDef(employeesArray, i, "middleName")
                )
                put(
                    DBContract.Employees.lastName,
                    getStringDef(employeesArray, i, "lastName")
                )
                put(
                    DBContract.Employees.photoLink,
                    getStringDef(employeesArray, i, "photoLink")
                )
                put(
                    DBContract.Employees.degree,
                    getStringDef(employeesArray, i, "degree")
                )
                put(
                    DBContract.Employees.degreeAbbrev,
                    getStringDef(employeesArray, i, "degreeAbbrev")
                )
                put(DBContract.Employees.rank, getStringDef(employeesArray, i, "rank"))
                put(DBContract.Employees.department, dep)
                put(DBContract.Employees.fio, getStringDef(employeesArray, i, "fio"))
                put(
                    DBContract.Employees.urlId,
                    getStringDef(employeesArray, i, "urlId")
                )
            }

            val newRowId = db.insert(DBContract.Employees.TABLE_NAME, null, values)
            if (newRowId.toInt() == -1) {
                //db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
                return 1
            }

            i++
        }

        val values = ContentValues().apply {
            put(DBContract.Employees.employeeID, 0)
            put(DBContract.Employees.firstName, "")
            put(DBContract.Employees.middleName, "")
            put(DBContract.Employees.lastName, "")
            put(DBContract.Employees.photoLink, "")
            put(DBContract.Employees.degree, "")
            put(DBContract.Employees.degreeAbbrev, "")
            put(DBContract.Employees.rank, "")
            put(DBContract.Employees.department, "")
            put(DBContract.Employees.fio, "")
            put(DBContract.Employees.urlId, "")
        }
        val newRowId = db.insert(DBContract.Employees.TABLE_NAME, null, values)
        return if (newRowId.toInt() == -1) {
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
            1
        } else 0

    }

    fun makeEmployeesList(context: Context, mode: Int): Int {

        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase
        employeesList.clear()
        listOfEmployees.clear()

        val exist: Cursor =
            db.rawQuery("SELECT COUNT(*) as cnt FROM ${DBContract.Employees.TABLE_NAME}", null)
        exist.moveToFirst()

        if (exist.getInt(0) == 0 || mode == 1) {
            exist.close()

            //    db.execSQL("DELETE FROM ${DBContract.EmployeeSchedule.TABLE_NAME}")
            //  db.execSQL("DELETE FROM ${DBContract.finalEmployeeSchedule.TABLE_NAME}")
            val response: JSONArrayResponse =
                Requests.getEmployeesList("https://iis.bsuir.by/api/v1/")

            val employeesArray: JSONArray = response.arr

            if (response.errorCode != 0)
                return response.errorCode
            /*db.execSQL("DELETE FROM ${DBContract.EmployeeSchedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.finalEmployeeSchedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.EmployeeToExam.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.EmployeeExams.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.EmployeeToPair.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.CommonEmployee.TABLE_NAME}")*/
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")


            val err = fillEmployeesTable(db, employeesArray)

            for (i in 0 until employeesArray.length()) {
                val employee = employeesArray.getJSONObject(i)


                listOfEmployees.add(
                    Employees(
                        0,
                        employee.getInt("id"),
                        employee.getString("firstName"),
                        employee.getString("middleName"),
                        employee.getString("lastName"),
                        employee.getString("photoLink"),
                        null,
                        employee.getString("urlId")
                    )
                )
            }

            var emp = listOfEmployees[0].copy()
            emp.type = 5
            listOfEmployees.add(emp)

            emp = listOfEmployees[0].copy()
            try {

                emp.lastName = emp.lastName[0].toString()
                emp.type = 1
                employeesList.add(emp)

            } catch (_: Exception) {

            }


            var i = 0

            while (i < listOfEmployees.size - 1) {


                employeesList.add(listOfEmployees[i].copy())

                if (listOfEmployees[i].lastName[0] != listOfEmployees[i + 1].lastName[0] && listOfEmployees[i + 1].type != 5) {
                    val group = listOfEmployees[i + 1].copy()
                    group.lastName = group.lastName[0].toString()
                    group.type = 1
                    employeesList.add(group)
                }

                i++
            }


            if (err != 0)
                return 1
        } else {
            exist.close()
            loadFromDB(db)
        }



        return 0
    }

    private fun loadFromDB(db: SQLiteDatabase): Int {
        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Employees.TABLE_NAME} ORDER BY ${DBContract.Employees.lastName}",
            null
        )
        with(c) {
            moveToFirst()
            while (moveToNext()) {
                listOfEmployees.add(
                    Employees(
                        0,
                        getInt(getColumnIndexOrThrow("employeeID")),
                        getString(getColumnIndexOrThrow("firstName")),
                        getString(getColumnIndexOrThrow("middleName")),
                        getString(getColumnIndexOrThrow("lastName")),
                        getString(getColumnIndexOrThrow("photoLink")),
                        null,
                        getString(getColumnIndexOrThrow("urlId"))
                    )
                )
            }
        }

        var emp = listOfEmployees[0].copy()
        emp.type = 5
        listOfEmployees.add(emp)

        emp = listOfEmployees[0].copy()
        try {

            emp.lastName = emp.lastName[0].toString()
            emp.type = 1
            employeesList.add(emp)

        } catch (_: Exception) {
        }


        var i = 0

        while (i < listOfEmployees.size - 1) {


            employeesList.add(listOfEmployees[i].copy())

            if (listOfEmployees[i].lastName[0] != listOfEmployees[i + 1].lastName[0] && listOfEmployees[i + 1].type != 5) {
                val group = listOfEmployees[i + 1].copy()
                group.lastName = group.lastName[0].toString()
                group.type = 1
                employeesList.add(group)
            }

            i++
        }

        return 0
    }

    fun add_removeFavGroup(context: Context, mode: Int, employeeID: Int) {

        val dbHelper = DbHelper(context)

        val db = dbHelper.writableDatabase

        if (mode == 1)
            db.execSQL("DELETE FROM ${DBContract.Favorites.TABLE_NAME} WHERE ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID} = $employeeID")
        else {

            val values = ContentValues().apply {
                put(DBContract.Favorites.groupID, employeeID)
                put(DBContract.Favorites.type, 1)
            }

            db.insert(DBContract.Favorites.TABLE_NAME, null, values)

        }
    }
}