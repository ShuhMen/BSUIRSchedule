package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maximshuhman.bsuirschedule.data.entities.Settings

@Dao
abstract class SettingsDAO {

    @Query("SELECT * FROM `settings_table` WHERE id = 1")
    abstract fun getSettings(): Settings?

    @Upsert
    abstract fun upsertSettings(settings: Settings)

    fun setCurrentWeek(lastWeekUpdate: String, week: Int) {
        val settings = getSettings() ?: Settings()
        upsertSettings(settings.copy(lastWeekUpdate = lastWeekUpdate, week = week))
    }

    fun setLastOpenedId(lastOpenedID: Int, type: Int) {
        val settings = getSettings() ?: Settings()
        upsertSettings(settings.copy(lastOpenedID = lastOpenedID, openedType = type))
    }
}