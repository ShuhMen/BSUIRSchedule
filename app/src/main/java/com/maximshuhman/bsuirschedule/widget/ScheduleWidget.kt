package com.maximshuhman.bsuirschedule.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * Implementation of App Widget functionality.
 */
class ScheduleWidget : AppWidgetProvider() {

    val ACTION_UPDATE = "com.maximshuhman.bsuirschedule.action.UPDATE"


    private fun onUpdate(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)
        onUpdate(context, appWidgetManager, appWidgetIds)

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("WIDGET", "onUpdate")

        appWidgetIds.forEach { appWidgetId ->
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            val intent = Intent(context, ListWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.schedule_widget)
            views.setRemoteAdapter(R.id.list_view, intent)
            //  val prefs = PreferenceHelper.defaultPreference(context)

            val db = DbHelper(context).writableDatabase

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

            //   Log.d("WIDGET", " onDataSetChanged() $id")

            if (id != 0) {

                if (type == 1) {
                    val c = db.rawQuery(
                        "SELECT COUNT(*) as cnt FROM " +
                                "${DBContract.Employees.TABLE_NAME} WHERE ${DBContract.Employees.employeeID} = $id",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        val cursor = db.rawQuery(
                            "SELECT * FROM " +
                                    "${DBContract.Employees.TABLE_NAME} WHERE ${DBContract.Employees.employeeID} = $id",
                            null
                        )

                        cursor.moveToFirst()

                        val firstName =
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.firstName))
                        val lastName =
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.lastName))
                        val middleName =
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Employees.middleName))

                        views.setTextViewText(R.id.name_text, "$lastName $firstName $middleName")
                    }
                } else {
                    val c = db.rawQuery(
                        "SELECT COUNT(*) as cnt FROM " +
                                "${DBContract.Groups.TABLE_NAME} WHERE ${DBContract.Groups.groupID} = $id",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        val cursor = db.rawQuery(
                            "SELECT * FROM " +
                                    "${DBContract.Groups.TABLE_NAME} WHERE ${DBContract.Groups.groupID} = $id",
                            null
                        )

                        cursor.moveToFirst()

                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.name))

                        cursor.close()
                        views.setTextViewText(R.id.name_text, "Группа $name")
                    }
                }

                var calendar: Calendar = Calendar.getInstance()
                val formatter =
                    SimpleDateFormat("dd HH:mm", Locale.getDefault(Locale.Category.FORMAT))

                //val curent = formatter.parse(formatter.format(calendar.time))
                //  views.setTextViewText(R.id.name_text, formatter.format(calendar.time).toString())
            }

            views.setEmptyView(R.id.list_view, R.id.empty_view)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view)
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }

        Log.d("WIDGET", "onUpdate end")
        getPendingSelfIntent(context, ACTION_UPDATE)

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if (ACTION_UPDATE == intent.action) {
            onUpdate(context!!)
        } else super.onReceive(context, intent)
    }

    private fun getPendingSelfIntent(
        context: Context,
        action: String,
        vararg content: String
    ): PendingIntent? {
        val intent = Intent(context, ScheduleWidget::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onEnabled(context: Context) {
        Util.scheduleUpdate(context)
    }

    override fun onDisabled(context: Context) {
        Util.clearUpdate(context)
    }

    /*override fun onReceive(context: Context?, intent: Intent?) {

        when(intent!!.action) {
            "inc" -> {
                val curName = intent.getStringExtra("CurName")!!
                val intentType = intent.getStringExtra("UpdateType")!!
                when (intentType) {
                    "INC" -> {
                        val newAmount = intent.getIntExtra("CurAmount", 0) + 1
                        CoroutineScope(Dispatchers.Main).launch {
                            val widgetManager =
                                AppWidgetManager.getInstance(context!!.applicationContext)
                            widgetManager.notifyAppWidgetViewDataChanged(widgetManager.getAppWidgetIds(ComponentName(context.applicationContext.packageName,ScheduleWidget::class.java.name)),
                                R.id.list_view
                            )
                        }
                    }
                    "DEC" -> {
                        val newAmount = intent.getIntExtra("CurAmount", 0) - 1
                        CoroutineScope(Dispatchers.Main).launch {
                            val widgetManager =
                                AppWidgetManager.getInstance(context!!.applicationContext)
                            widgetManager.notifyAppWidgetViewDataChanged(widgetManager.getAppWidgetIds(ComponentName(context.applicationContext.packageName,ScheduleWidget::class.java.name)),
                                R.id.list_view
                            )
                        }
                    }
                    "DEL" -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            val curAmount = intent.getIntExtra("CurAmount", 0)
                            val widgetManager =
                                AppWidgetManager.getInstance(context!!.applicationContext)
                            widgetManager.notifyAppWidgetViewDataChanged(widgetManager.getAppWidgetIds(ComponentName(context.applicationContext.packageName,ScheduleWidget::class.java.name)),
                                R.id.list_view
                            )
                        }
                    }
                }
            }
        }
        super.onReceive(context, intent)
    }*/


}

