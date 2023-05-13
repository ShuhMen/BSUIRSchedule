package com.maximshuhman.bsuirschedule.widget

import CommonSchedule
import Employees
import Lesson
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
import com.maximshuhman.bsuirschedule.Data.StudentData
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.PreferenceHelper
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedGroup
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedType
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

        private var widgetItems: ArrayList<Lesson> = ArrayList<Lesson>()
        private val appWidgetId: Int = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )


        override fun onCreate() {
            widgetItems.clear()
            Log.d("WIDGET", "onCreate")
        }

        override fun onDataSetChanged() {

            val prefs = PreferenceHelper.defaultPreference(context)
            val ID = prefs.openedGroup
            val type = prefs.openedType
            Log.d("WIDGET", " onDataSetChanged() ${prefs.openedGroup}")

            if (ID != 0) {
                val dbHelper = DbHelper(context)
                val db = dbHelper.writableDatabase

                if (type == 1) {
                    val c = db.rawQuery(
                        "SELECT COUNT(*) as cnt FROM " +
                                "${DBContract.CommonEmployee.TABLE_NAME} WHERE ${DBContract.CommonEmployee.commonEmployeeID} = $ID",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        val cursor = db.rawQuery(
                            "SELECT ${DBContract.CommonEmployee.lastBuild} FROM " +
                                    "${DBContract.CommonEmployee.TABLE_NAME} WHERE ${DBContract.CommonEmployee.commonEmployeeID} = $ID",
                            null
                        )

                        cursor.moveToFirst()

                        /* val lastBuild =
                             cursor.getString(cursor.getColumnIndexOrThrow(DBContract.CommonSchedule.lastBuild))

                         var calendar: Calendar = Calendar.getInstance()
                         val formatter =
                             SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

                         val curent = formatter.parse(formatter.format(calendar.time))
                         val lastBuildDate = formatter.parse(lastBuild)
                         if (curent != null) {
                             if (curent.after(lastBuildDate)) {
                                 var err = 0

                                 val executors = Executors.newSingleThreadExecutor()
                                 executors.execute {

                                     err = StudentData.makeSchedule(
                                         StudentData.curGroupName,
                                         context,
                                         StudentData.curGroupID,
                                         1
                                     )
                                     Handler(Looper.getMainLooper()).post {
                                         when (err) {
                                             0 -> {

                                             }
                                             4 -> {
                                                 //endOfSchedule.visibility = View.VISIBLE
                                             }
                                             5 -> try {
                                                 /* Toast.makeText(
                                                                  context,
                                                                  "Расписание отсутствует!",
                                                                  Toast.LENGTH_SHORT
                                                              ).show()*/
                                             } catch (e: java.lang.NullPointerException) {
                                                 Firebase.crashlytics.log("SсheduleFragmentToast5")
                                             }
                                             6 -> try {
                                                 /*Toast.makeText(
                                                                 context,
                                                                 "Что-то пошло не так",
                                                                 Toast.LENGTH_SHORT
                                                             ).show()*/
                                             } catch (e: java.lang.NullPointerException) {
                                                 Firebase.crashlytics.log("SсheduleFragmentToast6")
                                             }
                                             else -> try {
                                                 /*Toast.makeText(
                                                                 context,
                                                                 "Ошибка получения данных",
                                                                 Toast.LENGTH_SHORT
                                                             ).show()*/
                                             } catch (e: java.lang.NullPointerException) {
                                                 Firebase.crashlytics.log("SсheduleFragmentToastElse")
                                             }
                                         }
                                     }
                                 }
                             } else {



                             }
                         }*/
                        // fill(ID, db)

                    } else
                        c.close()
                } else {
                    val c = db.rawQuery(
                        "SELECT COUNT(*) as cnt FROM " +
                                "${DBContract.CommonSchedule.TABLE_NAME} WHERE ${DBContract.CommonSchedule.commonScheduleID} = $ID",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        val cursor = db.rawQuery(
                            "SELECT ${DBContract.CommonSchedule.lastBuild} FROM " +
                                    "${DBContract.CommonSchedule.TABLE_NAME} WHERE ${DBContract.CommonSchedule.commonScheduleID} = $ID",
                            null
                        )

                        cursor.moveToFirst()

                        /* val lastBuild =
                             cursor.getString(cursor.getColumnIndexOrThrow(DBContract.CommonSchedule.lastBuild))

                         var calendar: Calendar = Calendar.getInstance()
                         val formatter =
                             SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

                         val curent = formatter.parse(formatter.format(calendar.time))
                         val lastBuildDate = formatter.parse(lastBuild)
                         if (curent != null) {
                             if (curent.after(lastBuildDate)) {
                                 var err = 0

                                 val executors = Executors.newSingleThreadExecutor()
                                 executors.execute {

                                     err = StudentData.makeSchedule(
                                         StudentData.curGroupName,
                                         context,
                                         StudentData.curGroupID,
                                         1
                                     )
                                     Handler(Looper.getMainLooper()).post {
                                         when (err) {
                                             0 -> {

                                             }
                                             4 -> {
                                                 //endOfSchedule.visibility = View.VISIBLE
                                             }
                                             5 -> try {
                                                 /* Toast.makeText(
                                                                  context,
                                                                  "Расписание отсутствует!",
                                                                  Toast.LENGTH_SHORT
                                                              ).show()*/
                                             } catch (e: java.lang.NullPointerException) {
                                                 Firebase.crashlytics.log("SсheduleFragmentToast5")
                                             }
                                             6 -> try {
                                                 /*Toast.makeText(
                                                                 context,
                                                                 "Что-то пошло не так",
                                                                 Toast.LENGTH_SHORT
                                                             ).show()*/
                                             } catch (e: java.lang.NullPointerException) {
                                                 Firebase.crashlytics.log("SсheduleFragmentToast6")
                                             }
                                             else -> try {
                                                 /*Toast.makeText(
                                                                 context,
                                                                 "Ошибка получения данных",
                                                                 Toast.LENGTH_SHORT
                                                             ).show()*/
                                             } catch (e: java.lang.NullPointerException) {
                                                 Firebase.crashlytics.log("SсheduleFragmentToastElse")
                                             }
                                         }
                                     }
                                 }
                             } else {



                             }
                         }*/
                        fillGroup(ID, db)

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
            var remoteViews = RemoteViews(RemoteViews(context.packageName, R.layout.widget_item))
            remoteViews.removeAllViews(R.id.outer)
            val time = RemoteViews(RemoteViews(context.packageName, R.layout.time_widget))
            var calendar: Calendar = Calendar.getInstance()
            val formatter =
                SimpleDateFormat("dd HH:mm", Locale.getDefault(Locale.Category.FORMAT))

            val curent = formatter.parse(formatter.format(calendar.time))
            time.setTextViewText(R.id.start_time_text, formatter.format(calendar.time).toString())
            time.setTextViewText(R.id.end_time_text, widgetItems[position].endLessonTime)
            remoteViews.addView(R.id.outer, time)

            when (widgetItems[position].lessonTypeAbbrev) {
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
            additionalInf.setTextViewText(R.id.pair_name_text, widgetItems[position].subject)
            if (widgetItems[position].note == null) {
                additionalInf.setViewVisibility(R.id.note_text, View.GONE)
            } else
                additionalInf.setTextViewText(R.id.note_text, widgetItems[position].note)
            additionalInf.setTextViewText(R.id.note, widgetItems[position].note)
            var emp: String = ""
            for (i in 0 until widgetItems[position].employees.size) {
                if (widgetItems[position].employees[i].firstName != "") {
                    if (i != 0)
                        emp += "\n"
                    emp +=
                        "${widgetItems[position].employees[i].lastName} " +
                                "${
                                    widgetItems[position].employees[i].firstName.substring(
                                        0,
                                        1
                                    )
                                }. " +
                                "${
                                    widgetItems[position].employees[i].middleName.substring(0, 1)
                                }."
                }
            }
            additionalInf.setTextViewText(R.id.employees_text, emp)
            additionalInf.setTextViewText(R.id.auditory_text, widgetItems[position].auditories)
            remoteViews.addView(R.id.outer, additionalInf)

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
                        put(DBContract.CommonSchedule.lastBuild, formatter.format(calendar.time))
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
                moveToNext()
                do {

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
                            Employees(
                                0,
                                try {
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Employees.employeeID))
                                } catch (e: Exception) {
                                    0
                                },
                                try {
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.firstName))
                                } catch (e: Exception) {
                                    ""
                                } as String,
                                try {
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.middleName))
                                } catch (e: Exception) {
                                    ""
                                } as String,
                                try {
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.lastName))
                                } catch (e: Exception) {
                                    ""
                                } as String,
                                try {
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.photoLink))
                                } catch (e: Exception) {
                                    ""
                                } as String,
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
                        )
                    } while (cursor.moveToNext())


                    cursor.close()

                    widgetItems.add(
                        Lesson(
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

                    i++
                } while (moveToNext())
            }
            c.close()

            return if (widgetItems.size != 0) {
                0
            } else
                1


        }
    }
}