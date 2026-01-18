package com.maximshuhman.bsuirschedule.data.repositories

import com.maximshuhman.bsuirschedule.ApplicationThemes
import com.maximshuhman.bsuirschedule.LauncherIcons
import com.maximshuhman.bsuirschedule.data.entities.Settings
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDAO: SettingsDAO
) {

    suspend fun getSettings() : Settings {
        return settingsDAO.getSettings() ?: Settings()
    }

    val settings: StateFlow<Settings> =
        settingsDAO.getSettingsFlow()
            .filterNotNull()
            .distinctUntilChanged()
            .stateIn(
                scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Settings()
            )

    suspend fun setTheme(theme: ApplicationThemes) {
        settingsDAO.setTheme(theme)
    }

    suspend fun setIcon(icon: LauncherIcons) {
        settingsDAO.setIcon(icon)
    }

    suspend fun setCurrentWeek(format: String, week: Int) {
        settingsDAO.setCurrentWeek(format,week)
    }

    suspend fun setLastOpenedId(lastLoadedId: Int, i2: Int) {
        settingsDAO.setLastOpenedId(lastLoadedId, i2)
    }
}