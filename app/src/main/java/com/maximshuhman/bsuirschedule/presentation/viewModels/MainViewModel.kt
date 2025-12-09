package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.sources.EmployeeDAO
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import com.maximshuhman.bsuirschedule.domain.useCases.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class MainViewModel @Inject constructor(
    private val groupsDAO: GroupsDAO,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val settingsDAO: SettingsDAO,
    private val employeeDAO: EmployeeDAO
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val uiState : StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        getLastScreen()
    }

    fun getLastScreen(){

        viewModelScope.launch(Dispatchers.IO) {

            val settings = settingsDAO.getSettings()

            if(settings?.lastOpenedID == null || settings.openedType == null){
                _uiState.emit(MainActivityUiState.Empty)
                return@launch
            }

            if(settings.openedType == 0) {
                val group = groupsDAO.getById(settings.lastOpenedID)

                if (group == null) {
                    _uiState.emit(MainActivityUiState.Empty)
                    return@launch

                }

                _uiState.emit(MainActivityUiState.GroupSuccess(group))
            }else{
                val employee = employeeDAO.getById(settings.lastOpenedID)

                if (employee == null) {
                    _uiState.emit(MainActivityUiState.Empty)
                    return@launch
                }

                _uiState.emit(MainActivityUiState.EmployeeSuccess(employee))
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