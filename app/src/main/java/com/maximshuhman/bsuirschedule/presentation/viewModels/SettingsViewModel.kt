package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDAO: SettingsDAO
) : ViewModel() {


    init {

    }



}