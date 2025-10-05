package com.maximshuhman.bsuirschedule.Data

import CommonSchedule
import Employees
import Lesson
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.DataClasses.Exam
import com.maximshuhman.bsuirschedule.DataClasses.Group
import com.maximshuhman.bsuirschedule.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale


object StudentData {

    private val listOfPairs = mutableListOf<Lesson>()
    var ScheduleList = mutableListOf<Lesson>()

    // private val listOfExams = mutableListOf<Lesson>()
    val ExamsList = mutableListOf<Exam>()
    var GroupsList = mutableListOf<Group>()
    var listOfGroups = mutableListOf<Group>()
    var FavoritesList = mutableListOf<Pair<Group?, Employees?>>()
    private var listOfFavoriteGroups = mutableListOf<Group>()
    private var listOfFavoriteEmployees = mutableListOf<Employees>()
    var curGroupID: Int? = 0
    var curGroupName: String = ""
    var curGroupSpeciality: String = ""
    var curGroupCourse: Int? = 0
    lateinit var commonSchedule: CommonSchedule
    private var inScheduleID = 0


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
                if (js.getJSONObject(i).optString("lessonTypeAbbrev") != "Консультация" &&
                    js.getJSONObject(i).optString("lessonTypeAbbrev") != "Экзамен"
                ) {
                    db.insert(
                        DBContract.Schedule.TABLE_NAME,
                        null,
                        addPairToList(dayOfWeek, js.getJSONObject(i), grID, db)
                    )
                    inScheduleID++

                }

                //listOfPairs.add(addPairToList(1, monday.getJSONObject(i), grID))
            }
        }
        return 0
    }

    private fun addPairToList(
        dayOfWeek: Int,
        startPair: JSONObject,
        groupID: Int,
        db: SQLiteDatabase
    ): ContentValues {

        var ph: String
        val empId = startPair.getJSONArray("employees").getJSONObject(0).optInt( "id")
        if (empId != 0) {

            val count: Cursor = db.rawQuery(
                "SELECT COUNT(${DBContract.Employees.photo}) as cnt FROM ${DBContract.Employees.TABLE_NAME} WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $empId",
                null
            )
            count.moveToFirst()

            if (count.getInt(0) == 0) {

                count.close()

                val c: Cursor = db.rawQuery(
                    "SELECT ${DBContract.Employees.photoLink} FROM ${DBContract.Employees.TABLE_NAME}  WHERE ${DBContract.Employees.employeeID} = $empId",
                    null
                )
                //   photo = Requests.getEmployeePhoto(getStringDef(employeesList, i, "id")!!)

                c.moveToFirst()

                with(c) {

                    ph = try {
                        getString(getColumnIndexOrThrow(DBContract.Employees.photoLink))
                    } catch (e: Exception) {
                        ""
                    } as String

                }

                c.close()
                var byte = ByteArray(0)

                try {
                    val `in` =
                        java.net.URL(ph).openStream()
                    val bitmap = BitmapFactory.decodeStream(`in`)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
                    byte = stream.toByteArray()
                } catch (e: Exception) {
                    Log.v("Request", "Failed to load photo $empId")
                }

                if (!byte.contentEquals(ByteArray(0))) {
                    val values = ContentValues()

                    values.put(DBContract.Employees.photo, byte)
                    db.update(
                        DBContract.Employees.TABLE_NAME,
                        values,
                        "${DBContract.Employees.employeeID} = $empId",
                        arrayOf()
                    )

                }
            } else
                count.close()

        }

        var week_numbers = ""
        Array(startPair.getJSONArray("weekNumber").length()) {
            week_numbers += startPair.getJSONArray("weekNumber").getInt(it).toString()
        }

        var auditories = ""
        Array(startPair.getJSONArray("auditories").length()) {
            auditories += startPair.getJSONArray("auditories").getString(it).toString() + " "
        }

        val values = ContentValues().apply {
            put(DBContract.Schedule.groupID, groupID)
            put(DBContract.Schedule.inScheduleID, inScheduleID)
            put(DBContract.Schedule.day_of_week, dayOfWeek)
            put(DBContract.Schedule.auditories, auditories)
            put(DBContract.Schedule.endLessonTime, getStringDef(startPair, "endLessonTime"))
            put(DBContract.Schedule.lessonTypeAbbrev, getStringDef(startPair, "lessonTypeAbbrev"))
            put(DBContract.Schedule.note, getStringDef(startPair, "note"))
            put(DBContract.Schedule.numSubgroup, getIntDef(startPair, "numSubgroup"))
            put(DBContract.Schedule.startLessonTime, getStringDef(startPair, "startLessonTime"))
            put(
                DBContract.Schedule.subject, try {
                    startPair.getString("subject")
                } catch (e: Exception) {
                    ""
                }
            )
            put(
                DBContract.Schedule.subjectFullName, try {
                    startPair.getString("subjectFullName")
                } catch (e: Exception) {
                    ""
                }
            )
            put(DBContract.Schedule.weekNumber, week_numbers)
            /*put(
                DBContract.Schedule.employeeID,
                getIntDef(startPair.getJSONArray("employees"), 0,"id")
            )*/
            put(
                DBContract.Schedule.startLessonDate, try {
                    startPair.getString("startLessonDate")
                } catch (e: Exception) {
                    ""
                }
            )
            put(
                DBContract.Schedule.endLessonDate, try {
                    startPair.getString("endLessonDate")
                } catch (e: Exception) {
                    ""
                }
            )
        }



        for (i in 0 until startPair.getJSONArray("employees").length()) {
            val connect = ContentValues().apply {
                put(DBContract.PairToEmployers.lessonID, inScheduleID)
                put(
                    DBContract.PairToEmployers.employeeID,
                    startPair.getJSONArray("employees").getJSONObject(i).getInt("id")
                )
                put(DBContract.PairToEmployers.groupID, groupID)
            }

            db.insert(DBContract.PairToEmployers.TABLE_NAME, null, connect)

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
                DBContract.Exams.TABLE_NAME,
                null,
                addExamToList(json.getJSONObject(i), grID, db)
            )

        }

        return 0
    }

    private fun addExamToList(
        startPair: JSONObject,
        groupNum: Int,
        db: SQLiteDatabase
    ): ContentValues {

        var ph: String
        val empId = getIntDef(startPair.getJSONArray("employees"), 0, "id")
        if (empId != 0) {

            val count: Cursor = db.rawQuery(
                "SELECT COUNT(${DBContract.Employees.photo}) as cnt FROM ${DBContract.Employees.TABLE_NAME} WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $empId",
                null
            )
            count.moveToFirst()

            if (count.getInt(0) == 0) {

                count.close()

                val c: Cursor = db.rawQuery(
                    "SELECT ${DBContract.Employees.photoLink} FROM ${DBContract.Employees.TABLE_NAME}  WHERE ${DBContract.Employees.employeeID} = $empId",
                    null
                )
                //   photo = Requests.getEmployeePhoto(getStringDef(employeesList, i, "id")!!)

                c.moveToFirst()

                with(c) {

                    ph = try {
                        getString(getColumnIndexOrThrow(DBContract.Employees.photoLink))
                    } catch (e: Exception) {
                        ""
                    } as String

                }

                c.close()
                var byte = ByteArray(0)

                try {
                    val `in` =
                        java.net.URL(ph).openStream()
                    val bitmap = BitmapFactory.decodeStream(`in`)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    byte = stream.toByteArray()
                } catch (e: Exception) {
                    Log.v("Request", "Failed to load photo $empId")
                }

                if (!byte.contentEquals(ByteArray(0))) {
                    val values = ContentValues()

                    values.put(DBContract.Employees.photo, byte)
                    db.update(
                        DBContract.Employees.TABLE_NAME,
                        values,
                        "${DBContract.Employees.employeeID} = $empId",
                        arrayOf()
                    )

                }
            } else
                count.close()

        }

        var week_numbers = ""
        Array(startPair.getJSONArray("weekNumber").length()) {
            week_numbers += startPair.getJSONArray("weekNumber").getInt(it).toString()
        }

        var auditories = ""
        Array(startPair.getJSONArray("auditories").length()) {
            auditories += startPair.getJSONArray("auditories").getString(it).toString() + " "
        }


        val values = ContentValues().apply {
            put(DBContract.Schedule.groupID, groupNum)
            put(DBContract.Schedule.auditories, auditories)
            put(DBContract.Schedule.endLessonTime, getStringDef(startPair, "endLessonTime"))
            put(DBContract.Schedule.lessonTypeAbbrev, getStringDef(startPair, "lessonTypeAbbrev"))
            put(DBContract.Schedule.note, getStringDef(startPair, "note"))
            put(DBContract.Schedule.numSubgroup, getIntDef(startPair, "numSubgroup"))
            put(DBContract.Schedule.startLessonTime, getStringDef(startPair, "startLessonTime"))
            put(
                DBContract.Schedule.subject, try {
                    startPair.getString("subject")
                } catch (e: Exception) {
                    ""
                }
            )
            put(
                DBContract.Schedule.subjectFullName, try {
                    startPair.getString("subjectFullName")
                } catch (e: Exception) {
                    ""
                }
            )
            put(DBContract.Schedule.weekNumber, week_numbers)
            put(
                DBContract.Schedule.employeeID,
                getIntDef(startPair.getJSONArray("employees"), 0, "id")
            )
            put(
                DBContract.Exams.dateLesson, try {
                    startPair.getString("dateLesson")
                } catch (e: Exception) {
                    null
                }
            )
        }

        return values
    }


    private fun fillExamsTable(json_common: JSONObject, context: Context): Int {
        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase
        val grID: Int

        try {
            grID = json_common.getJSONObject("studentGroupDto").getInt("id")
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

    fun makeExams(context: Context, groupID: Int?): Int {

        if (groupID != null) {
            val dbHelper = DbHelper(context)
            val db = dbHelper.writableDatabase

            ExamsList.clear()

            val count: Cursor = db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.Exams.TABLE_NAME} WHERE ${DBContract.Exams.TABLE_NAME}.${DBContract.Schedule.groupID} = $groupID",
                null
            )
            count.moveToFirst()

            if (count.getInt(0) != 0) {
                count.close()

                val c: Cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.Exams.TABLE_NAME} " +
                            "INNER JOIN ${DBContract.CommonSchedule.TABLE_NAME} ON (${DBContract.Exams.TABLE_NAME}.${DBContract.Schedule.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                            "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                            "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Exams.TABLE_NAME}.${DBContract.Schedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                            "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = $groupID " +
                            "ORDER BY ${DBContract.Exams.dateLesson}",

                    null
                )

                with(c) {

                    //    moveToFirst()
                    while (moveToNext()) {
                        ExamsList.add(
                            Exam(
                                getInt(getColumnIndexOrThrow(DBContract.Schedule.day_of_week)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.auditories)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.endLessonTime)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.lessonTypeAbbrev)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.note)),
                                getInt(getColumnIndexOrThrow(DBContract.Schedule.numSubgroup)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.startLessonTime)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.subject)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.subjectFullName)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.weekNumber)),
                                try {
                                    Employees(
                                        0,
                                        getInt(getColumnIndexOrThrow(DBContract.Employees.employeeID)),
                                        getString(getColumnIndexOrThrow(DBContract.Employees.firstName)),
                                        getString(getColumnIndexOrThrow(DBContract.Employees.middleName)),
                                        getString(getColumnIndexOrThrow(DBContract.Employees.lastName)),
                                        getString(getColumnIndexOrThrow(DBContract.Employees.photoLink)),
                                        getBlob(getColumnIndexOrThrow(DBContract.Employees.photo)),
                                        getString(getColumnIndexOrThrow(DBContract.Employees.urlId))
                                    )
                                } catch (_: Exception) {

                                } as Employees, "",
                                "",
                                try {
                                    getString(getColumnIndexOrThrow(DBContract.Exams.dateLesson))
                                } catch (e: Exception) {
                                    ""
                                }

                            )
                        )


                    }
                }
                c.close()

            } else {
                count.close()
                return 1
            }

        } else
            return 1

        ExamsList.sortWith(DateComparator1)
        return 0
    }

    class DateComparator1 {

        companion object : Comparator<Exam> {

            override fun compare(a: Exam, b: Exam): Int {
                val formatter =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))
                return if (formatter.parse(a.dateLesson!!)!!.after(formatter.parse(b.dateLesson!!)))
                    1
                else
                    -1
            }
        }
    }

    fun fillListOfPairs(db: SQLiteDatabase, grID: Int): Int {


        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Schedule.TABLE_NAME} " +
                    "INNER JOIN ${DBContract.CommonSchedule.TABLE_NAME} ON (${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                    "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                    // "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                    "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = $grID " +
                    "ORDER BY ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.day_of_week} ",

            null
        )


        with(c) {

            moveToFirst()
            commonSchedule = CommonSchedule(
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.startDate)),
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.endDate)),
                "", "",
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.lastBuild))
            )

            do {

                val inScheduleIDLocal =
                    getInt(getColumnIndexOrThrow(DBContract.Schedule.inScheduleID))


                val cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.PairToEmployers.TABLE_NAME} " +
                            "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON " +
                            "(${DBContract.PairToEmployers.TABLE_NAME}.${DBContract.PairToEmployers.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                            "WHERE ${DBContract.PairToEmployers.lessonID} = $inScheduleIDLocal " +
                            "AND ${DBContract.PairToEmployers.groupID} = $grID",
                    null
                )
                cursor.moveToFirst()

                val list = ArrayList<Employees>()

                do {
                    list.add(
                        try {
                            Employees(
                                0,
                                cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Employees.employeeID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.firstName)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.middleName)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.lastName)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.photoLink)),
                                try {
                                    cursor.getBlob(cursor.getColumnIndexOrThrow(DBContract.Employees.photo))
                                } catch (e: Exception) {
                                    ByteArray(0)
                                },
                                try {
                                    cursor.getString(getColumnIndexOrThrow(DBContract.Employees.urlId))
                                } catch (e: Exception) {
                                    ""
                                }
                            )
                        } catch (e: Exception) {
                            Employees(0, 0, "", "", "", "", ByteArray(0), "")
                        }
                    )
                } while (cursor.moveToNext())

                cursor.close()

                listOfPairs.add(
                    Lesson(
                        getInt(getColumnIndexOrThrow("_id")),
                        getInt(getColumnIndexOrThrow(DBContract.Schedule.inScheduleID)),
                        getInt(getColumnIndexOrThrow(DBContract.Schedule.day_of_week)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.auditories)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.endLessonTime)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.lessonTypeAbbrev)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.note)),
                        getInt(getColumnIndexOrThrow(DBContract.Schedule.numSubgroup)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.startLessonTime)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.subject)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.subjectFullName)),
                        getString(getColumnIndexOrThrow(DBContract.Schedule.weekNumber)),
                        list,
                        try {
                            getString(getColumnIndexOrThrow(DBContract.Schedule.startLessonDate))
                        } catch (e: Exception) {
                            ""
                        },
                        try {
                            getString(getColumnIndexOrThrow(DBContract.Schedule.endLessonDate))
                        } catch (e: Exception) {
                            ""
                        }, null
                    )
                )


            } while (moveToNext())
        }
        c.close()

        return if (listOfPairs.size != 0) {
            val tim: Lesson = listOfPairs[0].copy()

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
            grID = json_common.getJSONObject("studentGroupDto").getInt("id")
        } catch (e: JSONException) {
            return 1
        }


        val values = ContentValues().apply {
            put(DBContract.CommonSchedule.commonScheduleID, grID)
            put(DBContract.CommonSchedule.startDate, getStringDef(json_common, "startDate"))
            put(DBContract.CommonSchedule.endDate, getStringDef(json_common, "endDate"))
            put(
                DBContract.CommonSchedule.startExamsDate,
                getStringDef(json_common, "startExamsDate")
            )
            put(DBContract.CommonSchedule.endExamsDate, getStringDef(json_common, "endExamsDate"))
            put(DBContract.CommonSchedule.lastUpdate, cur)
        }

        val newRowId = db.insert(DBContract.CommonSchedule.TABLE_NAME, null, values)

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

    fun fillScheduleList(calendar: Calendar, formatter: SimpleDateFormat, context: Context) {
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


        //val startLessonsDate = formatter.parse(commonSchedule.startDate)

        val endLessonsDate = formatter.parse(commonSchedule.endDate)

        var weeks = 0

        var curday = if (day == 1) 7 else day - 1

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
        //l.numSubgroup = 0
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
        var delta = 0
        while (i < ScheduleList.size) {

            if (ScheduleList[i - 1].day_of_week != ScheduleList[i].day_of_week || (ScheduleList[i - 1].startLessonTime!! > ScheduleList[i].startLessonTime!! || ScheduleList[i - 1].inLessonID == ScheduleList[i].inLessonID)) {

                var k: Int = i - 1
                //i--
                var curent = formatter.parse(formatter.format(calendar.time))


                delta = ScheduleList[i].day_of_week - ScheduleList[i - 1].day_of_week

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
                    } catch (e: IndexOutOfBoundsException) {
                        Log.d("STUDENTDATA", "fillScheduleList ${e.message}")
                    }

                    k--
                }

                if (i <= ScheduleList.size)
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

                val les: Lesson = ScheduleList[0].copy()
                les.day_of_week = 9
               // les.numSubgroup = 0
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


        if (delta < 0)
            calendar.add(
                Calendar.DATE,
                -(delta + 7)
            )
        else
            calendar.add(
                Calendar.DATE,
                -delta
            )


        if (ScheduleList[ScheduleList.size - 1].day_of_week == 9)
            ScheduleList.removeAt(ScheduleList.size - 1)

        /* if (ScheduleList[0].day_of_week == ScheduleList[1].day_of_week)
             ScheduleList.removeAt(0)
 */

    }

    fun finalBuild(db: SQLiteDatabase, groupID: Int) {
        db.execSQL("DELETE FROM ${DBContract.finalSchedule.TABLE_NAME} WHERE ${DBContract.Schedule.groupID} = $groupID")
        for (i in 0 until ScheduleList.size) {
            val values = ContentValues().apply {
                put(DBContract.finalSchedule.groupID, groupID)
                put(DBContract.finalSchedule.inScheduleID, ScheduleList[i].inLessonID)
                put(DBContract.finalSchedule.dayIndex, i)
                put(DBContract.finalSchedule.day_of_week, ScheduleList[i].day_of_week)
                put(DBContract.finalSchedule.auditories, ScheduleList[i].auditories)
                put(DBContract.finalSchedule.endLessonTime, ScheduleList[i].endLessonTime)
                put(DBContract.finalSchedule.lessonTypeAbbrev, ScheduleList[i].lessonTypeAbbrev)
                put(DBContract.finalSchedule.note, ScheduleList[i].note)
                put(DBContract.finalSchedule.numSubgroup, ScheduleList[i].numSubgroup)
                put(DBContract.finalSchedule.startLessonTime, ScheduleList[i].startLessonTime)
                put(DBContract.finalSchedule.subject, ScheduleList[i].subject)
                put(DBContract.finalSchedule.subjectFullName, ScheduleList[i].subjectFullName)
                put(DBContract.finalSchedule.weekNumber, ScheduleList[i].weekNumber)
                put(
                    DBContract.finalSchedule.startLessonDate, ScheduleList[i].startLessonDate
                )
                put(DBContract.finalSchedule.endLessonDate, ScheduleList[i].endLessonDate)
            }

            db.insert(DBContract.finalSchedule.TABLE_NAME, null, values)
        }
    }

    fun loadFromFinal(
        groupID: Int,
        formatter: SimpleDateFormat,
        curent: Date,
        db: SQLiteDatabase,
        context: Context
    ): Int {


        var calendar = Calendar.getInstance()
        val common: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.CommonSchedule.TABLE_NAME} " +
                    "WHERE ${DBContract.CommonSchedule.commonScheduleID} = $groupID ",

            null
        )

        with(common) {
            moveToFirst()
            commonSchedule = CommonSchedule(
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.startDate)),
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.endDate)),
                "", "",
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.lastBuild))
            )
        }

        common.close()

        if (commonSchedule.lastBuild != null && commonSchedule.lastBuild != "") {
            if (formatter.parse(commonSchedule.lastBuild!!)!!.before(curent)) {
                if (fillListOfPairs(db, groupID) == 1)
                    return 1
                fillScheduleList(calendar, formatter, context)
                finalBuild(db, groupID)

                calendar = Calendar.getInstance()

                val values = ContentValues().apply {
                    put(DBContract.CommonSchedule.lastBuild, formatter.format(calendar.time))
                }

                db.update(
                    DBContract.CommonSchedule.TABLE_NAME,
                    values,
                    "${DBContract.CommonSchedule.commonScheduleID} = $groupID",
                    null
                )
                return 0

            } else {
                val c: Cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.finalSchedule.TABLE_NAME} " +
                            "INNER JOIN ${DBContract.CommonSchedule.TABLE_NAME} ON (${DBContract.finalSchedule.TABLE_NAME}.${DBContract.Schedule.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                            "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                            //  "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.finalSchedule.TABLE_NAME}.${DBContract.Schedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                            "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = $groupID " +
                            "ORDER BY ${DBContract.finalSchedule.TABLE_NAME}.${DBContract.finalSchedule.dayIndex} ",

                    null
                )

                with(c) {
                    while (moveToNext()) {

                        val inScheduleIDLocal =
                            getInt(getColumnIndexOrThrow(DBContract.Schedule.inScheduleID))

                        val list = ArrayList<Employees>()
                        val cursor: Cursor = db.rawQuery(
                            "SELECT * FROM ${DBContract.PairToEmployers.TABLE_NAME} " +
                                    "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON " +
                                    "(${DBContract.PairToEmployers.TABLE_NAME}.${DBContract.PairToEmployers.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID})" +
                                    "WHERE ${DBContract.PairToEmployers.lessonID} = $inScheduleIDLocal " +
                                    "AND ${DBContract.PairToEmployers.groupID} = $groupID",
                            null
                        )


                        cursor.moveToFirst()

                        do {
                            list.add(
                                try {
                                    Employees(
                                        0,
                                        cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Employees.employeeID)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.firstName)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.middleName)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.lastName)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.photoLink)),
                                        try {
                                            cursor.getBlob(cursor.getColumnIndexOrThrow(DBContract.Employees.photo))
                                        } catch (e: Exception) {
                                            ByteArray(0)
                                        },
                                        try {
                                            cursor.getString(getColumnIndexOrThrow(DBContract.Employees.urlId))
                                        } catch (e: Exception) {
                                            ""
                                        }
                                    )
                                } catch (e: Exception) {
                                    Employees(0, 0, "", "", "", "", ByteArray(0), "")
                                }
                            )
                        } while (cursor.moveToNext())


                        cursor.close()

                        ScheduleList.add(
                            Lesson(
                                getInt(getColumnIndexOrThrow("_id")),
                                getInt(getColumnIndexOrThrow(DBContract.Schedule.inScheduleID)),
                                getInt(getColumnIndexOrThrow(DBContract.Schedule.day_of_week)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.auditories)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.endLessonTime)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.lessonTypeAbbrev)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.note)),
                                getInt(getColumnIndexOrThrow(DBContract.Schedule.numSubgroup)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.startLessonTime)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.subject)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.subjectFullName)),
                                getString(getColumnIndexOrThrow(DBContract.Schedule.weekNumber)),
                                list,
                                try {
                                    getString(getColumnIndexOrThrow(DBContract.Schedule.startLessonDate))
                                } catch (e: Exception) {
                                    ""
                                },
                                try {
                                    getString(getColumnIndexOrThrow(DBContract.Schedule.endLessonDate))
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
                put(DBContract.CommonSchedule.lastBuild, formatter.format(calendar.time))
            }

            db.update(
                DBContract.CommonSchedule.TABLE_NAME,
                values,
                "${DBContract.CommonSchedule.commonScheduleID} = $groupID",
                null
            )

            if (fillListOfPairs(db, groupID) == 1)
                return 1

            fillScheduleList(calendar, formatter, context)
            finalBuild(db, groupID)
            return 0
        }
    }

    fun makeSchedule(grNum: String, context: Context?, groupID: Int?, mode: Int?): Int {

        try {
            if (grNum == "" || groupID == null || context == null)
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
                "SELECT COUNT(*) as cnt FROM ${DBContract.Schedule.TABLE_NAME} " +
                        "WHERE ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = $groupID",
                null
            )
            c.moveToFirst()

            val response: JSONResponse

            if (c.getInt(0) == 0 || mode == 1) {

                response = Requests.getGroupSchedule("https://iis.bsuir.by/api/v1/", grNum)
                if (response.errorCode == 0) {
                    c.close()

                    db.execSQL(
                        "DELETE FROM ${DBContract.Schedule.TABLE_NAME} " +
                                "WHERE ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = $groupID"
                    )
                    db.execSQL(
                        "DELETE FROM ${DBContract.finalSchedule.TABLE_NAME} " +
                                "WHERE ${DBContract.finalSchedule.TABLE_NAME}.${DBContract.finalSchedule.groupID} = $groupID"
                    )
                    db.execSQL(
                        "DELETE FROM ${DBContract.Exams.TABLE_NAME} " +
                                "WHERE ${DBContract.Exams.TABLE_NAME}.${DBContract.Schedule.groupID} = $groupID"
                    )
                    db.execSQL(
                        "DELETE FROM ${DBContract.PairToEmployers.TABLE_NAME} " +
                                "WHERE ${DBContract.PairToEmployers.TABLE_NAME}.${DBContract.PairToEmployers.groupID} = $groupID"
                    )
                    db.execSQL(
                        "DELETE FROM ${DBContract.CommonSchedule.TABLE_NAME} " +
                                "WHERE ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID} = $groupID"
                    )

                    var err = 0

                    if (fillSheduleTable(response.obj, context) != 0)
                        err = 4

                    if (fillExamsTable(response.obj, context) != 0)
                        err = 5


                    if (err != 0)
                        return err
                }
                if (fillListOfPairs(db, groupID) == 1)
                    return 1

                if (listOfPairs.size == 0)
                    return 4

                fillScheduleList(calendar, formatter, context)

                finalBuild(db, groupID)

                calendar = Calendar.getInstance()

                val values = ContentValues().apply {
                    put(DBContract.CommonSchedule.lastBuild, formatter.format(calendar.time))
                }

                db.update(
                    DBContract.CommonSchedule.TABLE_NAME,
                    values,
                    "${DBContract.CommonSchedule.commonScheduleID} = $groupID",
                    null
                )


            } else {
                c.close()
                if (loadFromFinal(groupID, formatter, curent, db, context) == 1)
                    return 1


            }




            if (ScheduleList.size == 0) {
                Log.d("DATA", "Empty schedule list")
                return 4
            }

            return 0
        } catch (e: Exception) {
            Log.d("DATA", "Something wrong ${e.message}")

            return 6
        }
    }

    private fun fillGroupsTable(db: SQLiteDatabase, mode: Int): Int {

        val response: JSONArrayResponse = Requests.getGroupsList("https://iis.bsuir.by/api/v1/")


        val groupsList: JSONArray = response.arr

        if (response.errorCode != 0)
            return response.errorCode

        if (mode == 1) {
            db.execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.finalSchedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.CommonSchedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Groups.TABLE_NAME}")
        }

        var i = 0
        while (i < groupsList.length() - 1) {


            val values = ContentValues().apply {
                put(DBContract.Groups.groupID, getIntDef(groupsList, i, "id"))
                put(DBContract.Groups.course, getIntDef(groupsList, i, "course").toString())
                put(
                    DBContract.Groups.specialityAbbrev,
                    getStringDef(groupsList, i, "specialityAbbrev").toString()
                )
                put(
                    DBContract.Groups.specialityName,
                    getStringDef(groupsList, i, "specialityName").toString()
                )
                put(
                    DBContract.Groups.facultyAbbrev,
                    getStringDef(groupsList, i, "facultyAbbrev").toString()
                )
                put(DBContract.Groups.name, getStringDef(groupsList, i, "name").toString())
            }

            val newRowId = db.insert(DBContract.Groups.TABLE_NAME, null, values)

            if (newRowId.toInt() == -1 && mode == 0) {
                db.execSQL("DELETE FROM ${DBContract.Groups.TABLE_NAME}")
                return 2
            }

            i++
        }


        return 0
    }

    private fun fillGroupsList(db: SQLiteDatabase) {
        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Groups.TABLE_NAME} ORDER BY " + DBContract.Groups.name,
            null
        )

        with(c) {
            while (moveToNext()) {
                listOfGroups.add(
                    Group(
                        0,
                        getString(getColumnIndexOrThrow(DBContract.Groups.name)),
                        getString(getColumnIndexOrThrow(DBContract.Groups.facultyAbbrev)),
                        getString(getColumnIndexOrThrow(DBContract.Groups.specialityName)),
                        getString(getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev)),
                        getLong(getColumnIndexOrThrow(DBContract.Groups.course)).toInt(),
                        getLong(getColumnIndexOrThrow(DBContract.Groups.groupID)).toInt(),
                    )
                )
            }
        }
        c.close()

        val gr = listOfGroups[0].copy()

        gr.type = 3

        listOfGroups.add(gr)
    }

    fun makeGroupsList(context: Context?, mode: Int): Int {
        if (context == null)
            return 1

        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase

        val exist: Cursor =
            db.rawQuery("SELECT COUNT(*) as cnt FROM ${DBContract.Groups.TABLE_NAME}", null)
        exist.moveToFirst()

        if (exist.getInt(0) == 0 || mode == 1) {

            val err = fillGroupsTable(db, mode)

            if (err != 0)
                return err
        }

        exist.close()
        listOfGroups.clear()
        GroupsList.clear()
        fillGroupsList(db)

        val gr = listOfGroups[0].copy()

        try {

            gr.name = gr.name!!.substring(0, 3)
            gr.type = 1
            GroupsList.add(gr)

        } catch (_: Exception) {
        }


        var i = 0

        while (i < listOfGroups.size - 1) {


            GroupsList.add(
                listOfGroups[i].copy()
            )

            if (listOfGroups[i].name!!.substring(0, 3) !=
                listOfGroups[i + 1].name!!.substring(0, 3) && listOfGroups[i + 1].type != 3
            ) {
                val group = listOfGroups[i + 1].copy()
                group.name = group.name!!.substring(0, 3)
                group.type = 1
                GroupsList.add(group)
            }

            i++
        }
        // listOfGroups
        return 0
    }

    fun add_removeFavGroup(context: Context, mode: Int, grID: Int) {

        val dbHelper = DbHelper(context)

        val db = dbHelper.writableDatabase

        if (mode == 1)
            db.execSQL("DELETE FROM ${DBContract.Favorites.TABLE_NAME} WHERE ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID} = $grID")
        else {

            val values = ContentValues().apply {
                put(DBContract.Favorites.groupID, grID)
                put(DBContract.Favorites.type, 0)

            }

            db.insert(DBContract.Favorites.TABLE_NAME, null, values)

        }
    }

    private fun fillFavoritesList(db: SQLiteDatabase) {

        var c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Favorites.TABLE_NAME} " +
                    "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID}) " +
                    "WHERE ${DBContract.Favorites.type} = 0 " +
                    "ORDER BY " + DBContract.Groups.name,
            null
        )

        with(c) {
            while (moveToNext()) {
                if (getInt(getColumnIndexOrThrow(DBContract.Favorites.type)) == 0)
                    listOfFavoriteGroups.add(
                        Group(
                            0,
                            getString(getColumnIndexOrThrow(DBContract.Groups.name)),
                            getString(getColumnIndexOrThrow(DBContract.Groups.facultyAbbrev)),
                            getString(getColumnIndexOrThrow(DBContract.Groups.specialityName)),
                            getString(getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev)),
                            getInt(getColumnIndexOrThrow(DBContract.Groups.course)),
                            getInt(getColumnIndexOrThrow(DBContract.Favorites.groupID)),
                        )
                    )
            }
        }
        c.close()

        c = db.rawQuery(
            "SELECT * FROM ${DBContract.Favorites.TABLE_NAME} " +
                    "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID}) " +
                    "WHERE ${DBContract.Favorites.type} = 1 " +
                    "ORDER BY " + DBContract.Employees.lastName,
            null
        )

        with(c) {
            while (moveToNext()) {
                if (getInt(getColumnIndexOrThrow(DBContract.Favorites.type)) == 1)
                    listOfFavoriteEmployees.add(
                        Employees(
                            1,
                            getInt(getColumnIndexOrThrow(DBContract.Employees.employeeID)),
                            getString(getColumnIndexOrThrow(DBContract.Employees.firstName)),
                            getString(getColumnIndexOrThrow(DBContract.Employees.middleName)),
                            getString(getColumnIndexOrThrow(DBContract.Employees.lastName)),
                            getString(getColumnIndexOrThrow(DBContract.Employees.photoLink)),
                            getBlob(getColumnIndexOrThrow(DBContract.Employees.photo)),
                            getString(getColumnIndexOrThrow(DBContract.Employees.urlId))
                        )
                    )
            }
        }
        c.close()

    }

    fun makeFavoritesList(context: Context): Int {
        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase

        val exist: Cursor =
            db.rawQuery("SELECT COUNT(*) as cnt FROM ${DBContract.Favorites.TABLE_NAME}", null)
        exist.moveToFirst()

        if (exist.getInt(0) == 0) {
            return 1
        }

        exist.close()

        listOfFavoriteGroups.clear()
        listOfFavoriteEmployees.clear()
        FavoritesList.clear()
        fillFavoritesList(db)


        if (listOfFavoriteGroups.size != 0) {
            var gr = listOfFavoriteGroups[0].copy()
            gr.type = 5
            listOfFavoriteGroups.add(gr)

            gr = listOfFavoriteGroups[0].copy()

            try {

                gr.name = gr.name!!.substring(0, 3)
                gr.type = 1
                FavoritesList.add(Pair(gr, null))

            } catch (_: Exception) {
            }


            var i = 0

            while (i < listOfFavoriteGroups.size - 1) {


                FavoritesList.add(Pair(listOfFavoriteGroups[i].copy(), null))

                if (listOfFavoriteGroups[i].name!!.substring(
                        0,
                        3
                    ) != listOfFavoriteGroups[i + 1].name!!.substring(
                        0,
                        3
                    ) && listOfFavoriteGroups[i + 1].type != 5
                ) {
                    val group = listOfFavoriteGroups[i + 1].copy()
                    group.name = group.name!!.substring(0, 3)
                    group.type = 1
                    FavoritesList.add(Pair(group, null))
                }

                i++
            }
        }

        if (listOfFavoriteEmployees.size != 0) {
            var emp = listOfFavoriteEmployees[0].copy()
            emp.type = 5
            listOfFavoriteEmployees.add(emp)

            emp = listOfFavoriteEmployees[0].copy()
            try {

                emp.lastName = emp.lastName[0].toString()
                emp.type = 3
                FavoritesList.add(Pair(null, emp))

            } catch (_: Exception) {
            }


            var i = 0

            while (i < listOfFavoriteEmployees.size - 1) {


                FavoritesList.add(Pair(null, listOfFavoriteEmployees[i].copy()))

                if (listOfFavoriteEmployees[i].lastName[0] != listOfFavoriteEmployees[i + 1].lastName[0] && listOfFavoriteEmployees[i + 1].type != 5) {
                    val group = listOfFavoriteEmployees[i + 1].copy()
                    group.lastName = group.lastName[0].toString()
                    group.type = 3
                    FavoritesList.add(Pair(null, group))
                }

                i++
            }

        }

        return 0
    }

    fun setSubGroup(context: Context, subgroup: Int, grID: Int) {

        val dbHelper = DbHelper(context)

        val db = dbHelper.writableDatabase
        val row = db.execSQL(
            "UPDATE ${DBContract.SubgroupSettings.TABLE_NAME} SET ${DBContract.SubgroupSettings.subGroup} = $subgroup " +
                    "WHERE ${DBContract.SubgroupSettings.groupID} = $grID"
        )

        return row
    }

}