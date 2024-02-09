package com.maximshuhman.bsuirschedule.widget

import CommonSchedule
import Employees
import Lesson
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.maximshuhman.bsuirschedule.Data.EmployeeData
import com.maximshuhman.bsuirschedule.Data.StudentData
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.DataClasses.EmployeeLesson
import com.maximshuhman.bsuirschedule.DataClasses.Group
import com.maximshuhman.bsuirschedule.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListRemoteViewsFactory(applicationContext, intent)
    }


    inner class ListRemoteViewsFactory(
        private val context: Context,
        intent: Intent
    ) : RemoteViewsFactory {

        private var widgetItems: ArrayList<Pair<Lesson?, EmployeeLesson?>> = ArrayList()
        private val appWidgetId: Int = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )


        override fun onCreate() {
            widgetItems.clear()
            Log.d("WIDGET", "onCreate")
        }

        @SuppressLint("Recycle")
        override fun onDataSetChanged() {

            val db = DbHelper(context).writableDatabase

            // widgetItems.clear()
            val settings = db.rawQuery(
                "SELECT ${DBContract.Settings.widgetID}, ${DBContract.Settings.widgetOpened} FROM ${DBContract.Settings.TABLE_NAME}",
                null
            )

            settings.moveToFirst()

            val id =
                settings.getInt(settings.getColumnIndexOrThrow(DBContract.Settings.widgetID))  //prefs.openedGroup
            val type =
                settings.getInt(settings.getColumnIndexOrThrow(DBContract.Settings.widgetOpened))  //prefs.openedType
            settings.close()


            Log.d("WIDGET", " onDataSetChanged() $id")

            if (id != 0) {
                val dbHelper = DbHelper(context)
                val db = dbHelper.writableDatabase

                if (type == 1) {
                    val c = db.rawQuery(
                        "SELECT COUNT(*) as cnt FROM " +
                                "${DBContract.CommonEmployee.TABLE_NAME} WHERE ${DBContract.CommonEmployee.commonEmployeeID} = ${id}",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        fillEmployee(id, db)

                    } else
                        c.close()
                } else {
                    val c = db.rawQuery(
                        "SELECT COUNT(*) as cnt FROM " +
                                "${DBContract.CommonSchedule.TABLE_NAME} WHERE ${DBContract.CommonSchedule.commonScheduleID} = ${id}",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        fillGroup(id, db)
                    } else
                        c.close()
                }
            }
        }

        override fun onDestroy() {
            widgetItems.clear()

        }

        override fun getCount(): Int {
            return widgetItems.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val remoteViews =
                RemoteViews(RemoteViews(context.packageName, R.layout.widget_item))
            if (widgetItems[position].second == null) {

                remoteViews.removeAllViews(R.id.outer)
                val time = RemoteViews(RemoteViews(context.packageName, R.layout.time_widget))
                var calendar: Calendar = Calendar.getInstance()
                val formatter =
                    SimpleDateFormat("dd HH:mm", Locale.getDefault(Locale.Category.FORMAT))

                //val curent = formatter.parse(formatter.format(calendar.time))
                time.setTextViewText(
                    R.id.start_time_text,
                    widgetItems[position].first!!.startLessonTime/*formatter.format(calendar.time).toString()*/
                )
                time.setTextViewText(
                    R.id.end_time_text,
                    widgetItems[position].first!!.endLessonTime
                )
                remoteViews.addView(R.id.outer, time)

                when (widgetItems[position].first!!.lessonTypeAbbrev) {
                    "ПЗ" -> remoteViews.addView(
                        R.id.outer, RemoteViews(
                            RemoteViews(
                                context.packageName,
                                R.layout.divider_praktikal
                            )
                        )
                    )

                    "ЛК" -> remoteViews.addView(
                        R.id.outer, RemoteViews(
                            RemoteViews(
                                context.packageName,
                                R.layout.divider_lectures
                            )
                        )
                    )

                    "ЛР" -> remoteViews.addView(
                        R.id.outer, RemoteViews(
                            RemoteViews(
                                context.packageName,
                                R.layout.divider_labs
                            )
                        )
                    )
                }

                val additionalInf = RemoteViews(
                    RemoteViews(
                        context.packageName,
                        R.layout.additional_information_widget
                    )
                )
                additionalInf.setTextViewText(
                    R.id.pair_name_text,
                    widgetItems[position].first!!.subject
                )
                if (widgetItems[position].first!!.note == null) {
                    additionalInf.setViewVisibility(R.id.note_text, View.GONE)
                } else
                    additionalInf.setTextViewText(
                        R.id.note_text,
                        widgetItems[position].first!!.note
                    )

                additionalInf.setTextViewText(R.id.note, widgetItems[position].first!!.note)
                var emp: String = ""
                for (i in 0 until widgetItems[position].first!!.employees.size) {
                    if (widgetItems[position].first!!.employees[i].firstName != "") {
                        if (i != 0)
                            emp += "\n"
                        emp +=
                            "${widgetItems[position].first!!.employees[i].lastName} " +
                                    if(widgetItems[position].first!!.employees[i].firstName.isNullOrBlank())
                                        ""
                                    else
                                    "${
                                        widgetItems[position].first!!.employees[i].firstName!!.substring(
                                            0,
                                            1
                                        )
                                    }. " +
                                    if(widgetItems[position].first!!.employees[i].middleName.isNullOrBlank())
                                        ""
                                    else
                                    "${
                                        widgetItems[position].first!!.employees[i].middleName!!.substring(
                                            0,
                                            1
                                        )
                                    }."
                    }
                }
                additionalInf.setTextViewText(R.id.employees_text, emp)
                additionalInf.setTextViewText(
                    R.id.auditory_text,
                    widgetItems[position].first!!.auditories
                )
                remoteViews.addView(R.id.outer, additionalInf)

            } else {

                remoteViews.removeAllViews(R.id.outer)
                val time = RemoteViews(RemoteViews(context.packageName, R.layout.time_widget))
                var calendar: Calendar = Calendar.getInstance()
                val formatter =
                    SimpleDateFormat("dd HH:mm", Locale.getDefault(Locale.Category.FORMAT))

                //val curent = formatter.parse(formatter.format(calendar.time))
                time.setTextViewText(
                    R.id.start_time_text,
                    widgetItems[position].second!!.startLessonTime/*formatter.format(calendar.time).toString()*/
                )
                time.setTextViewText(
                    R.id.end_time_text,
                    widgetItems[position].second!!.endLessonTime
                )
                remoteViews.addView(R.id.outer, time)

                when (widgetItems[position].second!!.lessonTypeAbbrev) {
                    "ПЗ" -> remoteViews.addView(
                        R.id.outer, RemoteViews(
                            RemoteViews(
                                context.packageName,
                                R.layout.divider_praktikal
                            )
                        )
                    )

                    "ЛК" -> remoteViews.addView(
                        R.id.outer, RemoteViews(
                            RemoteViews(
                                context.packageName,
                                R.layout.divider_lectures
                            )
                        )
                    )

                    "ЛР" -> remoteViews.addView(
                        R.id.outer, RemoteViews(
                            RemoteViews(
                                context.packageName,
                                R.layout.divider_labs
                            )
                        )
                    )
                }

                val additionalInf = RemoteViews(
                    RemoteViews(
                        context.packageName,
                        R.layout.additional_information_widget
                    )
                )
                additionalInf.setTextViewText(
                    R.id.pair_name_text,
                    widgetItems[position].second!!.subject
                )
                if (widgetItems[position].second!!.note == null) {
                    additionalInf.setViewVisibility(R.id.note_text, View.GONE)
                } else
                    additionalInf.setTextViewText(
                        R.id.note_text,
                        widgetItems[position].second!!.note
                    )
                additionalInf.setTextViewText(R.id.note, widgetItems[position].second!!.note)
                var emp: String = ""
                for (i in 0 until widgetItems[position].second!!.groups.size) {
                    if (i % 2 != 0)
                        emp += ", "
                    if (i != 0 && i % 2 == 0)
                        emp += "\n"
                    emp += "${widgetItems[position].second!!.groups[i].name}"
                }
                additionalInf.setTextViewText(R.id.employees_text, emp)
                additionalInf.setTextViewText(
                    R.id.auditory_text,
                    widgetItems[position].second!!.auditories
                )
                remoteViews.addView(R.id.outer, additionalInf)
            }

            return remoteViews
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {

            Log.d("WIDGET", "getViewTypeCount")

            return 1
        }

        override fun getItemId(position: Int): Long {
            Log.d("WIDGET", "getItemId")

            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        fun fillGroup(groupID: Int, db: SQLiteDatabase): Int {
            Log.d("WIDGET", "fill")

            widgetItems.clear()
            var commonSchedule: CommonSchedule


            val exist = db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.CommonSchedule.TABLE_NAME} " +
                        "WHERE ${DBContract.CommonSchedule.commonScheduleID} = $groupID ",

                null
            )
            exist.moveToFirst()
            if(exist.getInt(0) != 0) {
                exist.close()
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

                StudentData.commonSchedule = commonSchedule

                common.close()


                var calendar: Calendar = Calendar.getInstance()
                val formatter =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

                val curent = formatter.parse(formatter.format(calendar.time))
                if (commonSchedule.lastBuild != null && commonSchedule.lastBuild != "") {
                    if (formatter.parse(commonSchedule.lastBuild).before(curent)) {

                        StudentData.fillScheduleList(calendar, formatter, context)
                        StudentData.finalBuild(db, groupID)

                        calendar = Calendar.getInstance()

                        val values = ContentValues().apply {
                            put(
                                DBContract.CommonSchedule.lastBuild,
                                formatter.format(calendar.time)
                            )
                        }

                        db.update(
                            DBContract.CommonSchedule.TABLE_NAME,
                            values,
                            "${DBContract.CommonSchedule.commonScheduleID} = $groupID",
                            null
                        )

                    }
                }

                val c: Cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.finalSchedule.TABLE_NAME} " +
                            "INNER JOIN ${DBContract.CommonSchedule.TABLE_NAME} ON (${DBContract.finalSchedule.TABLE_NAME}.${DBContract.Schedule.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                            "INNER JOIN ${DBContract.Groups.TABLE_NAME} ON (${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID}) " +
                            //  "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.finalSchedule.TABLE_NAME}.${DBContract.Schedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                            "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = $groupID " +
                            "ORDER BY ${DBContract.finalSchedule.TABLE_NAME}.${DBContract.finalSchedule.dayIndex} ",

                    null
                )
                var i = 0

                with(c) {
                    moveToFirst()
                    while (moveToNext()) {

                        if (getInt(getColumnIndexOrThrow(DBContract.Schedule.day_of_week)) == 9 && i != 0)
                            break

                        var inScheduleIDLocal =
                            getInt(getColumnIndexOrThrow(DBContract.Schedule.inScheduleID))

                        var list = ArrayList<Employees>()
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

                        widgetItems.add(
                            Pair(
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
                                ), null
                            )
                        )

                        i++
                    }
                }
                c.close()

                return if (widgetItems.size != 0) {
                    0
                } else
                    1

            }else {
                exist.close()

                return 1
            }
        }

        fun fillEmployee(employeeID: Int, db: SQLiteDatabase): Int {

            widgetItems.clear()

            var commonSchedule: CommonSchedule

            var calendar = Calendar.getInstance()
            val formatter =
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

            val curent = formatter.parse(formatter.format(calendar.time))

            val exist = db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.CommonEmployee.TABLE_NAME} " +
                        "WHERE ${DBContract.CommonEmployee.commonEmployeeID} = $employeeID ",

                null
            )
            exist.moveToFirst()
            if (exist.getInt(0) != 0){
                exist.close()

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

                EmployeeData.commonSchedule = commonSchedule

            common.close()

            if (commonSchedule.lastBuild != null && commonSchedule.lastBuild != "") {
                if (formatter.parse(commonSchedule.lastBuild).before(curent)) {
                    EmployeeData.fillScheduleList(calendar, formatter, context)
                    EmployeeData.finalBuild(db, employeeID)

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

                }

                val c: Cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.finalEmployeeSchedule.TABLE_NAME} " +
                            "INNER JOIN ${DBContract.CommonEmployee.TABLE_NAME} ON (${DBContract.finalEmployeeSchedule.TABLE_NAME}.${DBContract.finalEmployeeSchedule.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                            "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID}) " +
                            //  "INNER JOIN ${DBContract.Employees.TABLE_NAME} ON (${DBContract.finalEmployeeSchedule.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID}) " +
                            "WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $employeeID " +
                            "ORDER BY ${DBContract.finalEmployeeSchedule.TABLE_NAME}.${DBContract.finalEmployeeSchedule.dayIndex} ",

                    null
                )
                var i = 0

                with(c) {
                    moveToFirst()
                    while (moveToNext()) {
                        if (getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.day_of_week)) == 9 && i != 0)
                            break

                        var inScheduleIDLocal =
                            getInt(getColumnIndexOrThrow(DBContract.EmployeeSchedule.inScheduleID))

                        var list = ArrayList<Group>()
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

                        widgetItems.add(
                            Pair(
                                null,
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
                        )
                        i++
                    }
                }
                c.close()


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

                if (EmployeeData.fillListOfPairs(db, employeeID) == 1)
                    return 1

                EmployeeData.fillScheduleList(calendar, formatter, context)
                EmployeeData.finalBuild(db, employeeID)
                return 0
            }
            return if (widgetItems.size != 0) {
                0
            } else
                1
            }else {
                exist.close()
            return 1
        }
        }
    }
}