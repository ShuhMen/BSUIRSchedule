package com.maximshuhman.bsuirschedule.DataBase

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        if(!context.databaseList().contains(DbHelper.DATABASE_NAME))
            return

        val oldDbHelper = DbHelper(context)
        val oldDb = oldDbHelper.readableDatabase

        val cursor = oldDb.rawQuery("SELECT groupID, type FROM ${DBContract.Favorites.TABLE_NAME}", null)

        db.beginTransaction()
        try {
            while (cursor.moveToNext()) {
                db.execSQL(
                    "INSERT INTO `favorites` (id, type) VALUES (?, ?)",
                    arrayOf(
                        cursor.getLong(cursor.getColumnIndexOrThrow("groupID")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("type"))
                    )
                )
            }
            cursor.close()

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        db.beginTransaction()
        try {
            val cursorSub = oldDb.rawQuery("SELECT groupID, subGroup FROM ${DBContract.SubgroupSettings.TABLE_NAME}", null)
            while (cursorSub.moveToNext()) {
                val groupID = cursorSub.getInt(cursorSub.getColumnIndexOrThrow("groupID"))
                val subGroup = cursorSub.getInt(cursorSub.getColumnIndexOrThrow("subGroup"))

                db.execSQL(
                    "INSERT INTO SubgroupEntity (id, subGroup) VALUES (?, ?)",
                    arrayOf(groupID, subGroup)
                )
            }
            cursorSub.close()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        db.beginTransaction()
        try {
            val cursorSettings = oldDb.rawQuery("SELECT * FROM ${DBContract.Settings.TABLE_NAME}", null)
            if (cursorSettings.moveToFirst()) {

                val openedType = cursorSettings.getInt(cursorSettings.getColumnIndexOrThrow( "openedType"))
                val openedID = cursorSettings.getInt(cursorSettings.getColumnIndexOrThrow( "openedID"))
                val lastWeekUpdate = cursorSettings.getString(cursorSettings.getColumnIndexOrThrow("lastWeekUpdate"))
                val week = cursorSettings.getInt(cursorSettings.getColumnIndexOrThrow( "week"))
                val widgetID = cursorSettings.getInt(cursorSettings.getColumnIndexOrThrow( "widgetID"))
                val widgetOpened = cursorSettings.getInt(cursorSettings.getColumnIndexOrThrow("widgetOpened"))

                db.execSQL(
                    """
                    INSERT INTO settings_table 
                    (id, lastOpenedID, openedType, lastWeekUpdate, week, widgetID, widgetOpened) 
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """,
                    arrayOf(
                        1,
                        openedID,
                        openedType,
                        lastWeekUpdate,
                        week,
                        widgetID,
                        widgetOpened
                    )
                )
            }
            cursorSettings.close()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        oldDb.close()
        oldDbHelper.close()

        context.deleteDatabase(DbHelper.DATABASE_NAME)
    }
}