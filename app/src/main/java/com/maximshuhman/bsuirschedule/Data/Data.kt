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
import com.maximshuhman.bsuirschedule.DataClasses.Group
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Data {

    private val listOfPairs = mutableListOf<Lesson>()
    val ScheduleList = mutableListOf<Lesson>()
    var GroupsList = mutableListOf<Group>()
    private var listOfGroups = mutableListOf<Group>()
    var FavoritesList = mutableListOf<Group>()
    private var listOfFavorites = mutableListOf<Group>()
    var curGroupID: Int? = 0
    var curGroupName: String = ""
    var curGroupSpeciality: String = ""
    var curGroupCourse: Int? = 0
    lateinit var commonSchedule: CommonSchedule


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
        try {
            val js: JSONArray = json.getJSONArray(arrayName)

            for (i in 0..js.length()) {
                val newRowId = db.insert(
                    DBContract.Schedule.TABLE_NAME,
                    null,
                    addTolist(dayOfWeek, js.getJSONObject(i), grID, db)
                )


                //listOfPairs.add(addTolist(1, monday.getJSONObject(i), grID))
            }
        } catch (e: Exception) {
            Log.v("SCHP", e.toString())

        }
        return 0
    }

    private fun addTolist(
        dayOfWeek: Int,
        startPair: JSONObject,
        groupNum: Int,
        db: SQLiteDatabase
    ): ContentValues {

        /* val studentGroups = startPair.getJSONArray("studentGroups")
         val employees = startPair.getJSONArray("employees")

         val pair = Lesson(
             dayOfWeek,
             Array(startPair.getJSONArray("auditories").length()) {
                 startPair.getJSONArray("auditories").getString(it)
             }.toList(),
             getStringDef(startPair, "endLessonTime"),
             getStringDef(startPair, "lessonTypeAbbrev"),
             getStringDef(startPair, "note"),
             getIntDef(startPair, "numSubgroup"),
             getStringDef(startPair, "startLessonTime"),

             try {
                 startPair.getString("subject")
             } catch (e: Exception) {
                 ""
             },
             try {
                 startPair.getString("subjectFullName")
             } catch (e: Exception) {
                 ""
             },
             Array(startPair.getJSONArray("weekNumber").length()) {
                 startPair.getJSONArray("weekNumber").getInt(it)
             }.toString(),
             Array(startPair.getJSONArray("employees").length()) {
                 Employees(
                     startPair.getJSONArray("employees").getJSONObject(it).getInt("id"),
                     getStringDef(employees, it, "firstName"),
                     getStringDef(employees, it, "middleName"),
                     getStringDef(employees, it, "lastName"),
                     getStringDef(employees, it, "photoLink"),
                     getStringDef(employees, it, "degree"),
                     getStringDef(employees, it, "degreeAbbrev"),
                     getStringDef(employees, it, "rank"),
                     getStringDef(employees, it, "email"),
                     getStringDef(employees, it, "department"),
                     getStringDef(employees, it, "urlId"),
                     getStringDef(employees, it, "calendarId"),
                     getStringDef(employees, it, "jobPositions")
                 )
             }.toList(),
             getStringDef(startPair, "dateLesson"),
             getStringDef(startPair, "startLessonDate"),
             getStringDef(startPair, "endLessonDate")

         )



 */

        var ph: String
        val empId = getIntDef(startPair.getJSONArray("employees"), 0, "id")
        if (empId != 0) {

            val count: Cursor = db.rawQuery(
                "SELECT COUNT(${DBContract.Employees.photo}) as cnt FROM ${DBContract.Employees.TABLE_NAME} WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $empId",
                null
            )
            count.moveToFirst()

            if (count.getInt(0) != 0) {

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
            }

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
            put(
                DBContract.Schedule.employeeID,
                getIntDef(startPair.getJSONArray("employees"), 0, "id")
            )
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



        return values
    }


    private fun fillListOfPairs(db: SQLiteDatabase, grID: Int): Int {


        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Schedule.TABLE_NAME} " +
                    "INNER JOIN ${DBContract.CommonSchedule.TABLE_NAME} ON (${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                    "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                    "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                    "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = $grID " +
                    "ORDER BY ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.day_of_week} ",

            null
        )


        with(c) {

            moveToFirst()
            commonSchedule = CommonSchedule(
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.startDate)),
                getString(getColumnIndexOrThrow(DBContract.CommonSchedule.endDate)),
                "", ""
            )

            while (moveToNext()) {
                listOfPairs.add(
                    Lesson(
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
                        Employees(
                            try {
                                getInt(getColumnIndexOrThrow(DBContract.Employees.employeeID))
                            } catch (e: Exception) {
                                0
                            },
                            try {
                                getString(getColumnIndexOrThrow(DBContract.Employees.firstName))
                            } catch (e: Exception) {
                                ""
                            } as String,
                            try {
                                getString(getColumnIndexOrThrow(DBContract.Employees.middleName))
                            } catch (e: Exception) {
                                ""
                            } as String,
                            try {
                                getString(getColumnIndexOrThrow(DBContract.Employees.lastName))
                            } catch (e: Exception) {
                                ""
                            } as String,
                            try {
                                getString(getColumnIndexOrThrow(DBContract.Employees.photoLink))
                            } catch (e: Exception) {
                                ""
                            } as String,
                            try {
                                getBlob(getColumnIndexOrThrow(DBContract.Employees.photo))
                            } catch (e: Exception) {
                                ByteArray(0)
                            }
                        ),
                        try {
                            getString(getColumnIndexOrThrow(DBContract.Schedule.startLessonDate))
                        } catch (e: Exception) {
                            ""
                        },
                        try {
                            getString(getColumnIndexOrThrow(DBContract.Schedule.endLessonDate))
                        } catch (e: Exception) {
                            ""
                        }
                    )
                )


            }
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

        try {
            grID = json_common.getJSONObject("studentGroupDto").getInt("id")
        } catch (e: JSONException) {
            return 1
        }
        val json = json_common.getJSONObject("schedules")

        val values = ContentValues().apply {
            put(DBContract.CommonSchedule.commonScheduleID, grID)
            put(DBContract.CommonSchedule.startDate, getStringDef(json_common, "startDate"))
            put(DBContract.CommonSchedule.endDate, getStringDef(json_common, "endDate"))
            put(
                DBContract.CommonSchedule.startExamsDate,
                getStringDef(json_common, "startExamsDate")
            )
            put(DBContract.CommonSchedule.endExamsDate, getStringDef(json_common, "endExamsDate"))
        }

        val newRowId = db.insert(DBContract.CommonSchedule.TABLE_NAME, null, values)

        if (newRowId.toInt() == -1)
            return 1

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

    fun makeSchedule(grNum: String, context: Context?, groupID: Int?, mode: Int?): Int {

        if (grNum == "" || groupID == null || context == null)
            return 1

        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase

        ScheduleList.clear()
        listOfPairs.clear()
        val c: Cursor = db.rawQuery(
            "SELECT COUNT(*) as cnt FROM ${DBContract.Schedule.TABLE_NAME} WHERE ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = $groupID",
            null
        )
        c.moveToFirst()

        var response = JSONResponse(0, "", JSONObject())

        if (c.getInt(0) == 0 || mode == 1) {

            response = Requests.getGroupSchedule("https://iis.bsuir.by/api/v1/", grNum)
            if (response.errorCode == 0) {

                db.execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME} WHERE ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = $groupID")
                db.execSQL("DELETE FROM ${DBContract.CommonSchedule.TABLE_NAME} WHERE ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID} = $groupID")
                fillSheduleTable(response.obj, context)
                if (fillListOfPairs(db, groupID) == 1)
                    return 1
            } else {
                if (fillListOfPairs(db, groupID) == 1)
                    return 1
            }

        } else
            if (fillListOfPairs(db, groupID) == 1)
                return 1

        c.close()

        var week: Int

        var calendar: Calendar
        var day: Int
        var ind: Int
        week = Requests.getCurrent().res
        val wk = week
        calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_WEEK)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

        val startLessonsDate = formatter.parse(commonSchedule.startDate)
        val endLessonsDate = formatter.parse(commonSchedule.endDate)

        var curday: Int
        var weeks = 0

        curday = if (day == 1) 7 else day - 1

        ind = listOfPairs.indexOf(listOfPairs.firstOrNull { it.day_of_week == curday })

        if (ind == -1) {
            while (ind == -1) {
                if (curday == 7)
                    week++

                curday = curday % 7 + 1
                calendar.add(Calendar.DATE, 1)

                ind = listOfPairs.indexOf(listOfPairs.firstOrNull { it.day_of_week == curday })
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

        l.day_of_week = 9
        l.note = "${
            when (ScheduleList[i].day_of_week) {
                1 -> "Понедельник"
                2 -> "Вторник"
                3 -> "Среда"
                4 -> "Четверг"
                5 -> "Пятница"
                6 -> "Суббота"
                7 -> "Воскресенье"
                else -> "Ошибка"
            }
        }, " + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                when (calendar.get(Calendar.MONTH)) {
                    0 -> "Января"
                    1 -> "Февраля"
                    2 -> "Марта"
                    3 -> "Апреля"
                    4 -> "Мая"
                    5 -> "Июня"
                    6 -> "Июля"
                    7 -> "Августа"
                    8 -> "Сентября"
                    9 -> "Октября"
                    10 -> "Ноября"
                    11 -> "Декабря"
                    else -> "Ошибка"
                }

        ScheduleList.add(0, l)





        while (i < ScheduleList.size) {

            if ((ScheduleList[i - 1].day_of_week != ScheduleList[i].day_of_week)) {

                if (ScheduleList[i].day_of_week - ScheduleList[i - 1].day_of_week < 0)
                    calendar.add(
                        Calendar.DATE,
                        ScheduleList[i].day_of_week - ScheduleList[i - 1].day_of_week + 7
                    )
                else
                    calendar.add(
                        Calendar.DATE,
                        ScheduleList[i].day_of_week - ScheduleList[i - 1].day_of_week
                    )


                val curent = formatter.parse(formatter.format(calendar.time))

                if (curent?.after(endLessonsDate) == true || startLessonsDate?.after(curent) == true) {
                    ScheduleList.subList(i, ScheduleList.size).clear()
                    break
                }

                val les: Lesson = ScheduleList[0].copy()
                les.day_of_week = 9
                les.note = "${
                    when (ScheduleList[i].day_of_week) {
                        1 -> "Понедельник"
                        2 -> "Вторник"
                        3 -> "Среда"
                        4 -> "Четверг"
                        5 -> "Пятница"
                        6 -> "Суббота"
                        7 -> "Воскресенье"
                        else -> "Ошибка"
                    }
                }, " + calendar.get(Calendar.DAY_OF_MONTH) + " " +
                        when (calendar.get(Calendar.MONTH)) {
                            0 -> "Января"
                            1 -> "Февраля"
                            2 -> "Марта"
                            3 -> "Апреля"
                            4 -> "Мая"
                            5 -> "Июня"
                            6 -> "Июля"
                            7 -> "Августа"
                            8 -> "Сентября"
                            9 -> "Октября"
                            10 -> "Ноября"
                            11 -> "Декабря"
                            else -> "Ошибка"
                        }

                ScheduleList.add(i, les)
                i++
            }

            i++
        }

        week = wk
        calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_WEEK)

        curday = if (day == 1) 7 else day - 1

        ind = listOfPairs.indexOf(listOfPairs.firstOrNull { it.day_of_week == curday })

        if (ind == -1) {
            while (ind == -1) {
                if (curday == 7)
                    week++


                curday = curday % 7 + 1
                calendar.add(Calendar.DATE, 1)

                ind = listOfPairs.indexOf(listOfPairs.firstOrNull { it.day_of_week == curday })
            }

        }

        i = 1



        while (i < ScheduleList.size - 1) {


            if (ScheduleList[i].day_of_week != 9) {

                val curent = formatter.parse(formatter.format(calendar.time))

                if (curent != null && startLessonsDate != null) {
                    if (curent.after(endLessonsDate) || startLessonsDate.after(curent)) {
                        ScheduleList.subList(i, ScheduleList.size).clear()
                        break
                    }
                }

                try {
                    val start = formatter.parse(ScheduleList[i].startLessonDate.toString())
                    val end = formatter.parse(ScheduleList[i].endLessonDate.toString())

                    if (start != null && curent != null) {
                        if ((curent.after(end) || start.after(curent)) && ScheduleList[i].day_of_week != 9) {
                            ScheduleList.removeAt(i--)
                        }

                    }

                } catch (e: ParseException) {
                    Log.v(
                        "DateParce",
                        "can't parse date" + ScheduleList[i].subject + " " + ScheduleList[i].weekNumber + " " + ScheduleList[i].day_of_week
                    )
                }

            } else {

                while (i < ScheduleList.size)
                    if (ScheduleList[i - 1].day_of_week == 9 && ScheduleList[i].day_of_week == 9 && i > 1) {
                        if (ScheduleList[i + 1].day_of_week - ScheduleList[i - 2].day_of_week < 0)
                            calendar.add(
                                Calendar.DATE,
                                -ScheduleList[i + 1].day_of_week + ScheduleList[i - 2].day_of_week - 7
                            )
                        else
                            calendar.add(
                                Calendar.DATE,
                                -ScheduleList[i + 1].day_of_week + ScheduleList[i - 1].day_of_week
                            )
                        ScheduleList.removeAt(i - 1)
                        i--
                    } else
                        break

                if (ScheduleList[i + 1].day_of_week - ScheduleList[i - 1].day_of_week < 0)
                    calendar.add(
                        Calendar.DATE,
                        ScheduleList[i + 1].day_of_week - ScheduleList[i - 1].day_of_week + 7
                    )
                else
                    calendar.add(
                        Calendar.DATE,
                        ScheduleList[i + 1].day_of_week - ScheduleList[i - 1].day_of_week
                    )
                i += 0

            }

            i++

        }

        return if (response.errorCode != 0) response.errorCode else 0
    }


    private fun fillGroupsTable(db: SQLiteDatabase, mode: Int): Int {

        val response: JSONArrayResponse = Requests.getGroupsList("https://iis.bsuir.by/api/v1/")


        val groupsList: JSONArray = response.arr

        if (response.errorCode != 0)
            return response.errorCode

        if (mode == 1) {
            db.execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME}")
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
                        "",
                        getString(getColumnIndexOrThrow(DBContract.Groups.facultyAbbrev)),
                        0,
                        getString(getColumnIndexOrThrow(DBContract.Groups.specialityName)),
                        getString(getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev)),
                        getLong(getColumnIndexOrThrow(DBContract.Groups.course)).toInt(),
                        getLong(getColumnIndexOrThrow(DBContract.Groups.groupID)).toInt(),
                        "",
                        0
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

        } catch (e: Exception) {
            "Ошибка"
        }


        var i = 0

        while (i < listOfGroups.size - 1) {


            GroupsList.add(
                listOfGroups[i].copy()
            )

            if (listOfGroups[i].name!!.substring(0, 3) != listOfGroups[i + 1].name!!.substring(
                    0,
                    3
                ) && listOfGroups[i + 1].type != 3
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

    private fun fillEmployeesTable(db: SQLiteDatabase): Int {
        val response: JSONArrayResponse = Requests.getEmployeesList("https://iis.bsuir.by/api/v1/")

        val employeesList: JSONArray = response.arr

        if (response.errorCode != 0)
            return response.errorCode

        var i = 0
        while (i < employeesList.length() - 1) {

            var dep = ""
            val ar = employeesList.getJSONObject(i).getJSONArray("academicDepartment")

            Array(ar.length()) {
                dep += ar.getString(it).toString() + " "
            }

            val values = ContentValues().apply {
                put(DBContract.Employees.employeeID, getStringDef(employeesList, i, "id"))
                put(DBContract.Employees.firstName, getStringDef(employeesList, i, "firstName"))
                put(
                    DBContract.Employees.middleName,
                    getStringDef(employeesList, i, "middleName")
                )
                put(DBContract.Employees.lastName, getStringDef(employeesList, i, "lastName"))
                put(DBContract.Employees.photoLink, getStringDef(employeesList, i, "photoLink"))
                put(DBContract.Employees.degree, getStringDef(employeesList, i, "degree"))
                put(
                    DBContract.Employees.degreeAbbrev,
                    getStringDef(employeesList, i, "degreeAbbrev")
                )
                put(DBContract.Employees.rank, getStringDef(employeesList, i, "rank"))
                put(DBContract.Employees.department, dep)
                put(DBContract.Employees.fio, getStringDef(employeesList, i, "fio"))
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

        }
        val newRowId = db.insert(DBContract.Employees.TABLE_NAME, null, values)
        return if (newRowId.toInt() == -1) {
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")
            1
        } else 0

    }

    fun makeEmployeesList(context: Context): Int {

        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase

        val exist: Cursor =
            db.rawQuery("SELECT COUNT(*) as cnt FROM ${DBContract.Employees.TABLE_NAME}", null)
        exist.moveToFirst()



        if (exist.getInt(0) == 0) {
            db.execSQL("DELETE FROM ${DBContract.Schedule.TABLE_NAME}")
            db.execSQL("DELETE FROM ${DBContract.Employees.TABLE_NAME}")

            val err = fillEmployeesTable(db)
            if (err != 0)
                return 1
        }

        exist.close()

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
            }

            val newRowId = db.insert(DBContract.Favorites.TABLE_NAME, null, values)

        }
    }


    private fun fillFavoritesList(db: SQLiteDatabase) {
        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Favorites.TABLE_NAME} " +
                    "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID}) " +
                    " ORDER BY " + DBContract.Groups.name,
            null
        )

        with(c) {
            while (moveToNext()) {
                listOfFavorites.add(
                    Group(
                        0,
                        getString(getColumnIndexOrThrow(DBContract.Groups.name)),
                        "",
                        getString(getColumnIndexOrThrow(DBContract.Groups.facultyAbbrev)),
                        0,
                        getString(getColumnIndexOrThrow(DBContract.Groups.specialityName)),
                        getString(getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev)),
                        getLong(getColumnIndexOrThrow(DBContract.Groups.course)).toInt(),
                        getLong(getColumnIndexOrThrow(DBContract.Favorites.groupID)).toInt(),
                        "",
                        0
                    )
                )
            }
        }
        c.close()

        val gr = listOfFavorites[0].copy()

        gr.type = 3

        listOfFavorites.add(gr)
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

        listOfFavorites.clear()
        FavoritesList.clear()
        fillFavoritesList(db)

        val gr = listOfFavorites[0].copy()

        try {

            gr.name = gr.name!!.substring(0, 3)
            gr.type = 1
            FavoritesList.add(gr)

        } catch (e: Exception) {
            "Ошибка"
        }


        var i = 0

        while (i < listOfFavorites.size - 1) {


            FavoritesList.add(
                listOfFavorites[i].copy()
            )

            if (listOfFavorites[i].name!!.substring(
                    0,
                    3
                ) != listOfFavorites[i + 1].name!!.substring(
                    0,
                    3
                ) && listOfFavorites[i + 1].type != 3
            ) {
                val group = listOfFavorites[i + 1].copy()
                group.name = group.name!!.substring(0, 3)
                group.type = 1
                FavoritesList.add(group)
            }

            i++
        }
        return 0
    }


}