package com.maximshuhman.bsuirschedule

import Employees
import Lesson
import StudentGroups
import android.util.Log
import com.maximshuhman.bsuirschedule.DataClass.Group
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


object Data {

    private val listOfPairs = mutableListOf<Lesson>()
    val ScheduleList = mutableListOf<Lesson>()
    var GroupsList = mutableListOf<Group>()

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

    private fun addTolist(dayOfWeek: Int, startPair: JSONObject): Lesson {

        val studentGroups = startPair.getJSONArray("studentGroups")
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
            Array(startPair.getJSONArray("studentGroups").length()) {
                StudentGroups(
                    getStringDef(studentGroups, it, "specialityName"),
                    getStringDef(studentGroups, it, "specialityCode"),
                    getIntDef(studentGroups, it, "numberOfStudents"),
                    getIntDef(studentGroups, it, "name"),
                    getIntDef(studentGroups, it, "educationDegree")
                )
            }.toList(),
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
            }.toList(),
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
            getStringDef(startPair, "endLessonDate"),
            getStringDef(startPair, "announcementStart"),
            getStringDef(startPair, "announcementEnd"),
            try {
                startPair.getBoolean("announcement")
            } catch (e: Exception) {
                false
            },
            try {
                startPair.getBoolean("split")
            } catch (e: Exception) {
                false
            }
        )
        return pair
    }

    fun makeList(json: JSONObject) {
        try {
            val monday: JSONArray = json.getJSONArray("Понедельник")

            for (i in 0..monday.length())
                listOfPairs.add(addTolist(1, monday.getJSONObject(i)))
        } catch (e: Exception) {
            Log.v("SCHP", e.toString())
        }

        try {
            val tuesday: JSONArray = json.getJSONArray("Вторник")
            for (i in 0..tuesday.length())
                listOfPairs.add(addTolist(2, tuesday.getJSONObject(i)))
        } catch (_: Exception) {

        }

        try {
            val wednesday: JSONArray = json.getJSONArray("Среда")

            for (i in 0..wednesday.length())
                listOfPairs.add(addTolist(3, wednesday.getJSONObject(i)))
        } catch (e: Exception) {
            Log.v("SCHP", e.toString())
        }

        try {
            val thursday: JSONArray = json.getJSONArray("Четверг")

            for (i in 0..thursday.length())
                listOfPairs.add(addTolist(4, thursday.getJSONObject(i)))
        } catch (_: Exception) {

        }

        try {


            val friday: JSONArray = json.getJSONArray("Пятница")
            for (i in 0..friday.length())
                listOfPairs.add(addTolist(5, friday.getJSONObject(i)))
        } catch (_: Exception) {

        }

        try {
            val saturday: JSONArray = json.getJSONArray("Суббота")

            for (i in 0..saturday.length())
                listOfPairs.add(addTolist(6, saturday.getJSONObject(i)))
        } catch (_: Exception) {

        }

        try {
            val sunday: JSONArray = json.getJSONArray("Воскресенье")

            for (i in 0..sunday.length())
                listOfPairs.add(addTolist(7, sunday.getJSONObject(i)))

        } catch (_: Exception) {

        }

        val tim: Lesson = listOfPairs[0].copy()

        tim.day_of_week = 8

        listOfPairs.add(tim)

    }

    fun makeSchedule(grNum: String) {

        ScheduleList.clear()
        listOfPairs.clear()
        ///Заплатка, нужно переделать

        val req = Requests()
        val response: JSONResponse = req.getGroupSchedule("https://iis.bsuir.by/api/v1/", grNum)

        makeList(response.obj)

        var week: Int = req.getCurrent().res
        val calendar: Calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_WEEK)

        var curday: Int
        var weeks = 0
        var isSunday = false

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
            if (listOfPairs[i].day_of_week == 8)
            {
                week = week % 4 + 1
                ++weeks

                if (weeks == 4)
                    break

                i = 0
            } else {
                if (listOfPairs[i].weekNumber!!.contains(week) && listOfPairs[i].day_of_week != 8)
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
    }

    fun makeGroupsList(){

        if (GroupsList.size != 0)
            return

        val req = Requests()
        val response: JSONArrayResponse = req.getGroupsList("https://iis.bsuir.by/api/v1/")

        val groupsList: JSONArray = response.arr
        GroupsList.add(
            Group(1,
                try{getStringDef(groupsList, 0, "name")!!.substring(0, 3)}catch(e:Exception){"Ошибка"},
                getStringDef(groupsList, 0, "facultyId"),
                getStringDef(groupsList, 0, "facultyId"),
                getIntDef(groupsList, 0, "specialityDepartmentEducationFormId"),
                getStringDef(groupsList, 0, "specialityName"),
                getIntDef(groupsList, 0, "course"),
                getIntDef(groupsList, 0, "id"),
                getStringDef(groupsList, 0, "calendarId"),
                getIntDef(groupsList, 0, "educationDegree")

            )
        )

        var i = 1

        while (i < groupsList.length()-1) {
            GroupsList.add(
                Group(0,
                    getStringDef(groupsList, i, "name"),
                    getStringDef(groupsList, i, "facultyId"),
                    getStringDef(groupsList, i, "facultyId"),
                    getIntDef(groupsList, i, "specialityDepartmentEducationFormId"),
                    getStringDef(groupsList, i, "specialityName"),
                    getIntDef(groupsList, i, "course"),
                    getIntDef(groupsList, i, "id"),
                    getStringDef(groupsList, i, "calendarId"),
                    getIntDef(groupsList, i, "educationDegree")

                )

            )

            if(getStringDef(groupsList, i, "name")!!.substring(0, 3) != getStringDef(groupsList, i+1, "name")!!.substring(0, 3))
                GroupsList.add(
                    Group(1,
                        try{getStringDef(groupsList, i+1, "name")!!.substring(0, 3)}
                        catch(e:Exception){"Ошибка"},
                        getStringDef(groupsList, i+1, "facultyId"),
                        getStringDef(groupsList, i+1, "facultyId"),
                        getIntDef(groupsList, i+1, "specialityDepartmentEducationFormId"),
                        getStringDef(groupsList, i+1, "specialityName"),
                        getIntDef(groupsList, i+1, "course"),
                        getIntDef(groupsList, i+1, "id"),
                        getStringDef(groupsList, i+1, "calendarId"),
                        getIntDef(groupsList, i+1, "educationDegree")
                    )
                )

            i++
        }

    }
}