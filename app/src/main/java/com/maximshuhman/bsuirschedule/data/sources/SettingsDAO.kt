package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.maximshuhman.bsuirschedule.ApplicationThemes
import com.maximshuhman.bsuirschedule.LauncherIcons
import com.maximshuhman.bsuirschedule.data.entities.Settings
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SettingsDAO {

    @Query("SELECT * FROM `settings_table` WHERE id = 1")
    abstract fun getSettings(): Settings?

    @Query("SELECT * FROM `settings_table` WHERE id = 1")
    abstract fun getSettingsFlow(): Flow<Settings?>

    @Insert(onConflict = REPLACE)
    abstract suspend fun upsertSettings(settings: Settings)

    suspend fun setCurrentWeek(lastWeekUpdate: String, week: Int) {
        val settings = getSettings() ?: Settings()
        upsertSettings(settings.copy(lastWeekUpdate = lastWeekUpdate, week = week))
    }

    suspend fun setLastOpenedId(lastOpenedID: Int, type: Int) {
        val settings = getSettings() ?: Settings()
        upsertSettings(settings.copy(lastOpenedID = lastOpenedID, openedType = type))
    }

    suspend fun setIcon(launcherIcon: LauncherIcons){
        val settings = getSettings() ?: Settings()
        upsertSettings(settings.copy(iconInstalled = launcherIcon))
    }

    suspend fun setTheme(theme: ApplicationThemes){
        val settings = getSettings() ?: Settings()
        upsertSettings(settings.copy(theme = theme))

    }
}