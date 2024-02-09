package com.maximshuhman.bsuirschedule.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.maximshuhman.bsuirschedule.widget.Util.scheduleUpdate


class SampleBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    ScheduleWidget::class.java
                )
            )
            if (ids.isNotEmpty()) {
                scheduleUpdate(context)
            }

        }
    }
}

object Util {

    fun scheduleUpdate(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intervalMillis = (80 * 60 * 1000).toLong()
        val pi = getAlarmIntent(context)
        am.cancel(pi)
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), intervalMillis, pi)
    }

    private fun getAlarmIntent(context: Context): PendingIntent {
        val intent = Intent(context, ScheduleWidget::class.java)
        intent.action = ScheduleWidget().ACTION_UPDATE
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun clearUpdate(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(getAlarmIntent(context))
    }
}