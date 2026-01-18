package com.maximshuhman.bsuirschedule.presentation.viewModels

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.maximshuhman.bsuirschedule.ApplicationThemes
import com.maximshuhman.bsuirschedule.LauncherIcons
import com.maximshuhman.bsuirschedule.data.entities.Settings
import com.maximshuhman.bsuirschedule.data.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {


    val settings: StateFlow<Settings> = settingsRepository.settings


    @SuppressLint("QueryPermissionsNeeded")
    fun setIcon(context: Context, launcherIcon: LauncherIcons) {

        Log.d("IconSwitch", "Changing icon: ${settings.value.iconInstalled} → ${launcherIcon.name}")

        if (settings.value.iconInstalled == launcherIcon) return

        try {
            setComponentEnabled(
                launcherIcon.name,
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            )

            setComponentEnabled(
                settings.value.iconInstalled.name,
                context,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            )

            viewModelScope.launch(Dispatchers.IO) {
                settingsRepository.setIcon(launcherIcon)
            }
            //_settings.value = settings.value.copy(iconInstalled = launcherIcon)
        } catch (e: PackageManager.NameNotFoundException) {
            Firebase.crashlytics.recordException(e)
        }
    }

    private fun setComponentEnabled(componentShortName: String, context: Context, state: Int) {
        val componentName = ComponentName(context, context.packageName + "." + componentShortName)

        context.packageManager.setComponentEnabledSetting(
            componentName,
            state,
            PackageManager.DONT_KILL_APP
        )
    }

    fun setTheme(theme: ApplicationThemes){

        if(settings.value.theme == theme)
            return

        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setTheme(theme)
        }
       // _settings.value = settings.value.copy(theme = theme)
    }

}