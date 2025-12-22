package com.maximshuhman.bsuirschedule.DataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.maximshuhman.bsuirschedule.data.entities.FavoriteEntity


class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_FAVORITES =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Favorites.TABLE_NAME} (" +
                "${DBContract.Favorites.groupID} INTEGER PRIMARY KEY," +
                "${DBContract.Favorites.type} INTEGER ," +
                "FOREIGN KEY (${DBContract.Favorites.groupID}) REFERENCES ${DBContract.Groups.TABLE_NAME}(${DBContract.Groups.groupID}))"


    private val SQL_CREATE_SUBGROUPSETTINGS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.SubgroupSettings.TABLE_NAME} (" +
                "${DBContract.SubgroupSettings.groupID} INTEGER," +
                "${DBContract.SubgroupSettings.subGroup} INTEGER )"

    private val SQL_CREATE_SETTINGS =
        "CREATE TABLE IF NOT EXISTS ${DBContract.Settings.TABLE_NAME} (" +
                "${DBContract.Settings.openedType} INTEGER," +
                "${DBContract.Settings.openedID} INTEGER, " +
                "${DBContract.Settings.lastWeekUpdate} TEXT, " +
                "${DBContract.Settings.week} INTEGER, " +
                "${DBContract.Settings.widgetID} INTEGER," +
                "${DBContract.Settings.widgetOpened} INTEGER )"

    fun getFavorites(): List<FavoriteEntity>{
        val db = writableDatabase

        val c = db.rawQuery("SELECT * FROM ${DBContract.Favorites.TABLE_NAME}", null)

        val favorites = mutableListOf<FavoriteEntity>()

        while (!c.isAfterLast){

            favorites.add(
                FavoriteEntity(
                    c.getInt(c.getColumnIndexOrThrow(DBContract.Favorites.groupID)),
                    c.getInt(c.getColumnIndexOrThrow(DBContract.Favorites.type))
                )
            )
        }
        c.close()

        return favorites
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 15
        const val DATABASE_NAME = "Schedule"
    }
}
