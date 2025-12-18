package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.entities.FavoriteEntity
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.collect
import com.maximshuhman.bsuirschedule.domain.models.EmployeeReadySchedule
import com.maximshuhman.bsuirschedule.domain.models.Favorites
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.useCases.GetEmployeeScheduleUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.GetFavoritesUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.SetFavoriteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EmployeeScheduleViewModel @Inject constructor(
    private val getEmployeeSchedule: GetEmployeeScheduleUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val setFavoriteEntity: SetFavoriteEntity,
    private val networkFlow: @JvmSuppressWildcards Flow<NetworkStatus>,
    private val settingsDAO: SettingsDAO,
): ViewModel() {

    private var lastLoadedId: Int = -1

    private val _uiState = MutableStateFlow<EmployeeScheduleUiState>(EmployeeScheduleUiState.Loading)
    val uiState: StateFlow<EmployeeScheduleUiState> = _uiState

    init{
        viewModelScope.launch {
            networkFlow.collect(
                onUnavailable = {

                },
                onAvailable = {
                    if (_uiState.value is EmployeeScheduleUiState.NoConnection && lastLoadedId != -1) {
                        loadSchedule(lastLoadedId)
                    }
                }
            )

        }
    }


    fun loadSchedule(employeeID: Int) {

        lastLoadedId = employeeID

        viewModelScope.launch(Dispatchers.IO) {

            _uiState.value = EmployeeScheduleUiState.Loading

            getEmployeeSchedule(employeeID).collect { result ->
                when (result) {
                    is AppResult.Success<EmployeeReadySchedule> -> {
                        _uiState.value = EmployeeScheduleUiState.Success(
                            result.data,
                            result.data.employee.isFavorite
                        )

                        if(result.data.employee.isFavorite)
                            settingsDAO.setLastOpenedId(employeeID, 1)
                    }

                    is AppResult.ApiError<LogicError> -> {

                        when (result.body) {
                            is LogicError.ConfigureError -> _uiState.value =
                                EmployeeScheduleUiState.Error(result.body.message)

                            LogicError.Empty -> _uiState.value =
                                EmployeeScheduleUiState.Error("Расписание отсутствует!")

                            is LogicError.FetchDataError -> _uiState.value =
                                EmployeeScheduleUiState.Error(result.body.message)

                            LogicError.NoCriticalError -> {

                            }

                            LogicError.NoInternetConnection -> {
                                if(_uiState.value !is EmployeeScheduleUiState.Success)
                                _uiState.value = EmployeeScheduleUiState.NoConnection
                            }
                        }

                    }
                }
            }
        }
    }

    fun clickFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value is EmployeeScheduleUiState.Success) {

                val state = (uiState.value as EmployeeScheduleUiState.Success)

                _uiState.value = state.copy(
                    isFavorite = !state.isFavorite
                )

                setFavoriteEntity(FavoriteEntity(state.schedule.employee.id, 1), !state.isFavorite)

                if(!state.isFavorite)
                    settingsDAO.setLastOpenedId(lastLoadedId, 1)

                getFavorites()
            }
        }
    }


    private val _favorites = MutableStateFlow<Favorites>(Favorites())
    val favorites = _favorites.asStateFlow()

    fun getFavorites (){

        viewModelScope.launch(Dispatchers.IO) {

            val favorites = getFavoritesUseCase()

            _favorites.emit(favorites)
        }

    }

}

sealed class EmployeeScheduleUiState {
    object Loading : EmployeeScheduleUiState()
    data class Success(val schedule: EmployeeReadySchedule, val isFavorite: Boolean) : EmployeeScheduleUiState()
    data class Error(val message: String) : EmployeeScheduleUiState()
    object NoConnection : EmployeeScheduleUiState()
}