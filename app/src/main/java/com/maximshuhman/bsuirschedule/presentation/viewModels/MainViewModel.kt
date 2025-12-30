package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.entities.Settings
import com.maximshuhman.bsuirschedule.data.repositories.SettingsRepository
import com.maximshuhman.bsuirschedule.data.sources.EmployeeDAO
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val groupsDAO: GroupsDAO,
    private val employeeDAO: EmployeeDAO
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val settings: StateFlow<Settings> = settingsRepository.settings

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getLastScreen(settingsRepository.getSettings())
        }
    }

    private fun getLastScreen(settings: Settings) {
        viewModelScope.launch(Dispatchers.IO) {
            if (settings.lastOpenedID == null || settings.openedType == null) {
                _uiState.emit(MainActivityUiState.Empty)
                return@launch
            }

            if (settings.openedType == 0) {
                val group = groupsDAO.getById(settings.lastOpenedID)
                _uiState.emit(
                    group?.let { MainActivityUiState.GroupSuccess(it) }
                        ?: MainActivityUiState.Empty
                )
            } else {
                val employee = employeeDAO.getById(settings.lastOpenedID)
                _uiState.emit(
                    employee?.let { MainActivityUiState.EmployeeSuccess(it) }
                        ?: MainActivityUiState.Empty
                )
            }
        }
    }
}


sealed class ViewState {
    object NoConnection : ViewState()
    data class Error(val message: String) : ViewState()
}

sealed class MainActivityUiState {
    object Loading : MainActivityUiState()
    data class GroupSuccess(val group: Group) : MainActivityUiState()
    data class EmployeeSuccess(val employee: Employee) : MainActivityUiState()
    object Empty : MainActivityUiState()
}