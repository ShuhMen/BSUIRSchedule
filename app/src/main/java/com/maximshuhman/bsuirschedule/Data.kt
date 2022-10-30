package com.maximshuhman.bsuirschedule

import Lesson
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.DataClass.Group
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


object Data {

    private val listOfPairs = mutableListOf<Lesson>()
    val ScheduleList = mutableListOf<Lesson>()
    var GroupsList = mutableListOf<Group>()
    var listOfGroups = mutableListOf<Group>()

    private fun getStringDef(jsonArray: JSONArray, index: Int, valueName: String): String? = try {
        jsonArray.getJSONObject(index).getString(valueName)
    } catch (e: Exception) {
        null
    }

    private fun getIntDef(jsonArray: JSONArray, index: Int, valueName: String): Int? = try {
        jsonArray.getJSONObject(index).getInt(valueName)
    } catch (e: Exception) {
        null
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

    private fun addLessonToDB(json: JSONObject ,arrayName: String, db: SQLiteDatabase, grID: Int, dayOfWeek: Int) = try {
        val monday: JSONArray = json.getJSONArray(arrayName)

        for (i in 0..monday.length()) {
            val newRowId = db?.insert(
                DBContract.Schedule.TABLE_NAME,
                null,
                addTolist(dayOfWeek, monday.getJSONObject(i), grID)
            )
            //listOfPairs.add(addTolist(1, monday.getJSONObject(i), grID))
        }
    } catch (e: Exception) {
        Log.v("SCHP", e.toString())
    }

    private fun addTolist(dayOfWeek: Int, startPair: JSONObject, groupNum:Int): ContentValues {

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

        var week_numbers : String = ""
        Array(startPair.getJSONArray("weekNumber").length()) {
            week_numbers += startPair.getJSONArray("weekNumber").getInt(it).toString()
        }

        var auditories: String = ""
        Array(startPair.getJSONArray("auditories").length()) {
            auditories += startPair.getJSONArray("auditories").getString(it).toString() + " "
        }

      /*  var employees: String = ""
        Array(startPair.getJSONArray("employees").length()) {
            employees += startPair.getJSONArray("employees").getJSONObject(it).getInt("id").toString() + " "
        }*/

        val values = ContentValues().apply {
            put(DBContract.Schedule.groupID    , groupNum)
            put(DBContract.Schedule.day_of_week    , dayOfWeek)
            put(DBContract.Schedule.auditories     , auditories)
            put(DBContract.Schedule.endLessonTime  , getStringDef(startPair, "endLessonTime"))
            put(DBContract.Schedule.lessonTypeAbbrev,getStringDef(startPair, "lessonTypeAbbrev"))
            put(DBContract.Schedule.note           , getStringDef(startPair, "note"))
            put(DBContract.Schedule.numSubgroup    , getIntDef(startPair, "numSubgroup"))
            put(DBContract.Schedule.startLessonTime, getStringDef(startPair, "startLessonTime"))
            put(DBContract.Schedule.subject        , try { startPair.getString("subject") }
                                            catch (e: Exception) { "" })
            put(DBContract.Schedule.subjectFullName, try { startPair.getString("subjectFullName") }
                                            catch (e: Exception) { "" })
            put(DBContract.Schedule.weekNumber     , week_numbers)
            put(DBContract.Schedule.employeeID, getIntDef(startPair.getJSONArray("employees"), 0,"id"))
        }

        return values
    }



    private fun fillListOfPairs(db: SQLiteDatabase, grID: Int) : Int {




        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Schedule.TABLE_NAME} " +
                    "INNER JOIN ${DBContract.CommonSchedule.TABLE_NAME} ON (${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID})" +
                    "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID})" +
                    "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${grID} " +
                    "ORDER BY ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.day_of_week}",
            null
        )


        with(c) {
            while (moveToNext()) {
                listOfPairs.add(Lesson(
                    getInt(getColumnIndexOrThrow(DBContract.Schedule.day_of_week)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.auditories)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.endLessonTime)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.lessonTypeAbbrev)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.note)),
                    getInt(getColumnIndexOrThrow(DBContract.Schedule.numSubgroup)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.startLessonTime)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.subject)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.subjectFullName)),
                    getString(getColumnIndexOrThrow(DBContract.Schedule.weekNumber))
                    //Array<Employees>(0) = {},
                ))

            }

        }
        c.close()

        if(listOfPairs.size != 0) {
            val tim: Lesson = listOfPairs[0].copy()

            tim.day_of_week = 8

            listOfPairs.add(tim)
            return 0
        }else
            return 1
    }

    private fun fillSheduleTable(json_common: JSONObject, context: Context){
        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase

        val grID: Int =  json_common.getJSONObject("studentGroupDto").getInt("id")
        val json = json_common.getJSONObject("schedules")

        val values = ContentValues().apply {
            put(DBContract.CommonSchedule.commonScheduleID, grID)
            put(DBContract.CommonSchedule.startExamsDate, getStringDef(json_common, "startDate"))
            put(DBContract.CommonSchedule.endExamsDate,getStringDef(json_common, "endDate"))
            put(DBContract.CommonSchedule.endExamsDate,getStringDef(json_common, "startExamsDate"))
            put(DBContract.CommonSchedule.endExamsDate,getStringDef(json_common, "endExamsDate"))
        }

        var newRowId = db?.insert(DBContract.CommonSchedule.TABLE_NAME, null, values)

        addLessonToDB(json, "Понедельник", db, grID ,1)
        addLessonToDB(json, "Вторник", db, grID, 2)
        addLessonToDB(json, "Среда", db, grID, 3)
        addLessonToDB(json, "Четверг", db, grID, 4)
        addLessonToDB(json, "Пятница", db, grID, 5)
        addLessonToDB(json, "Суббота", db, grID, 6)
        addLessonToDB(json, "Воскресенье", db, grID, 7)
    }

    fun makeSchedule(grNum: String, context: Context, groupID:Int): Int{

        if(grNum == "")
            return 1

        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase
        val req = Requests()
        ScheduleList.clear()
        listOfPairs.clear()
        val c: Cursor = db.rawQuery("SELECT COUNT(*) as cnt FROM ${DBContract.Schedule.TABLE_NAME} WHERE ${DBContract.Schedule.TABLE_NAME}.${DBContract.Schedule.groupID} = $groupID", null)
        c.moveToFirst()

        if(c.getInt(0) == 0){


            val response: JSONResponse = req.getGroupSchedule("https://iis.bsuir.by/api/v1/", grNum)


            fillSheduleTable(response.obj, context)
            if(fillListOfPairs(db, groupID) == 1)
                return 0
        }else
        if(fillListOfPairs(db, groupID) == 1)
            return 0

        var week: Int = req.getCurrent().res
        val calendar: Calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_WEEK)

        var curday: Int
        var weeks = 0

        curday = if(day == 1) 7 else day - 1

        var ind: Int = listOfPairs.indexOf(listOfPairs.firstOrNull { it.day_of_week == curday })

        if(ind == -1){
            while (ind == -1){
                if(curday == 7)
                    week++


                curday = curday % 7 + 1

                ind = listOfPairs.indexOf(listOfPairs.firstOrNull { it.day_of_week == curday })
            }

        }

        var i: Int = ind

        while (i < listOfPairs.size) {
            if ( listOfPairs[i].day_of_week == 8)
            {
                week = week % 4 + 1
                ++weeks

                if (weeks == 4)
                    break

                i = 0
            } else {
                if (listOfPairs[i].weekNumber!!.toString().contains(week.toString()) && listOfPairs[i].day_of_week != 8)
                    ScheduleList.add(listOfPairs[i])

                i++
            }
        }

        i = 2

        val l: Lesson = ScheduleList[0].copy()

        l.day_of_week = 9
        l.note = "${
            when (ScheduleList[0].day_of_week) {
                1 -> "Понедельник"
                2 -> "Вторник"
                3 -> "Среда"
                4 -> "Четверг"
                5 -> "Пятница"
                6 -> "Суббота"
                7 -> "Воскресенье"
                else -> "Ошибка"
            }
        } "
        ScheduleList.add(0, l)

        while (i < ScheduleList.size) {
            if ((ScheduleList[i-1].day_of_week != ScheduleList[i].day_of_week)) {
                val l: Lesson = ScheduleList[0].copy()

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
                } "
                ScheduleList.add(i, l)

                i++
            }
            i++
        }
        return  0
    }


    private fun fillGroupsTable(db: SQLiteDatabase){
        val req = Requests()
        val response: JSONArrayResponse = req.getGroupsList("https://iis.bsuir.by/api/v1/")


        val groupsList: JSONArray = response.arr

        var i = 0
        while (i < groupsList.length()-1) {


            val values = ContentValues().apply {
                put(DBContract.Groups.groupID, getIntDef(groupsList, i, "id")  )
                put(DBContract.Groups.course, getIntDef(groupsList, i, "course").toString())
                put(DBContract.Groups.specialityAbbrev, getStringDef(groupsList, i, "specialityAbbrev").toString())
                put(DBContract.Groups.specialityName,   getStringDef(groupsList, i, "specialityName").toString())
                put(DBContract.Groups.name,  getStringDef(groupsList, i, "name").toString())
            }

            val newRowId = db?.insert(DBContract.Groups.TABLE_NAME, null, values)
            i++
        }
    }

    private fun fillGroupsList(db: SQLiteDatabase){
        val c: Cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.Groups.TABLE_NAME} ORDER BY " + DBContract.Groups.name,
            null
        )

        with(c) {
            while (moveToNext()) {
                listOfGroups.add(
                    Group(0,
                        getString(getColumnIndexOrThrow(DBContract.Groups.name)),
                        "",
                        "",
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
    }

    fun makeGroupsList(context: Context){
        val dbHelper = DbHelper(context)
        val db = dbHelper.writableDatabase

        val exist: Cursor = db.rawQuery("SELECT COUNT(*) as cnt FROM ${DBContract.Groups.TABLE_NAME}", null)
        exist.moveToFirst()

        if(exist.getInt(0) == 0) {

            fillGroupsTable(db)

        }
        listOfGroups.clear()
        GroupsList.clear()
        fillGroupsList(db)

        var gr = listOfGroups[0].copy()

        try{

            gr.name = gr.name!!.substring(0, 3)
            gr.type = 1
            GroupsList.add(gr)

        }catch(e:Exception){"Ошибка"}



        var i = 0

        while (i < listOfGroups.size-1) {


            GroupsList.add(
                listOfGroups[i].copy()
            )

            if (listOfGroups[i].name!!.substring(0, 3) != listOfGroups[i + 1].name!!.substring(0, 3)){
                val group = listOfGroups[i + 1].copy()
                group.name = group.name!!.substring(0, 3)
                group.type = 1
                GroupsList.add(group)
        }

            i++
        }
    }

    fun makeEmployeesList(jsonArray: JSONArray){



    }


}