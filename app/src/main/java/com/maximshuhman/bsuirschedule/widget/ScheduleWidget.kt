package com.maximshuhman.bsuirschedule.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.PreferenceHelper
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedGroup
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedType
import com.maximshuhman.bsuirschedule.R


/**
 * Implementation of App Widget functionality.
 */
class ScheduleWidget : AppWidgetProvider() {


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
                                "${DBContract.Employees.TABLE_NAME} WHERE ${DBContract.Employees.employeeID} = $ID",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        val cursor = db.rawQuery(
                            "SELECT * FROM " +
                                    "${DBContract.Employees.TABLE_NAME} WHERE ${DBContract.Employees.employeeID} = $ID",
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
                                "${DBContract.Groups.TABLE_NAME} WHERE ${DBContract.Groups.groupID} = $ID",
                        null
                    )
                    c.moveToFirst()
                    if (c.getInt(0) != 0) {
                        c.close()
                        val cursor = db.rawQuery(
                            "SELECT * FROM " +
                                    "${DBContract.Groups.TABLE_NAME} WHERE ${DBContract.Groups.groupID} = $ID",
                            null
                        )

                        cursor.moveToFirst()

                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.name))

                        views.setTextViewText(R.id.name_text, "Группа $name")
                    }
                }
            }

            views.setEmptyView(R.id.list_view, R.id.empty_view)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }

        Log.d("WIDGET", "onUpdate end")

        /* appWidgetIds.forEach { appWidgetId ->

             // Set up the intent that starts the StackViewService, which
             // provides the views for this collection.
             val intent = Intent(context, ListWidgetService::class.java).apply {
                 // Add the widget ID to the intent extras.
                 putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                 //data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
             }
             // Instantiate the RemoteViews object for the widget layout.
             val views = RemoteViews(context.packageName, R.layout.schedule_widget).apply {
                 // Set up the RemoteViews object to use a RemoteViews adapter.
                 // This adapter connects to a RemoteViewsService through the
                 // specified intent.
                 // This is how you populate the data.
                 Log.d("WIDGET", "onUpdate inner")

                 setRemoteAdapter(R.id.list_view, intent)

                 // The empty view is displayed when the collection has no items.
                 // It must be in the same layout used to instantiate the
                 // RemoteViews object.
                        setEmptyView(R.id.list_view, R.id.empty_view)
             }

             // Do additional processing specific to this widget.

             appWidgetManager.updateAppWidget(appWidgetId, views)
         }
 */
        super.onUpdate(context, appWidgetManager, appWidgetIds)
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
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

