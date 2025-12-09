package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.dto.FavoriteEntity
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.collect
import com.maximshuhman.bsuirschedule.domain.models.Favorites
import com.maximshuhman.bsuirschedule.domain.models.GroupReadySchedule
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.useCases.GetFavoritesUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.GetGroupScheduleUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.SetFavoriteEntity
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupScheduleUiState.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupScheduleViewModel @Inject constructor(
    private val getGroupSchedule: GetGroupScheduleUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val setFavoriteEntity: SetFavoriteEntity,
    private val networkFlow: @JvmSuppressWildcards Flow<NetworkStatus>,
    private val settingsDAO: SettingsDAO,
    ): ViewModel() {

    private var lastLoadedId: Int = -1

    private val _connectionLabel = MutableStateFlow(false)
    val connectionLabel: StateFlow<Boolean> = _connectionLabel

    private val _uiState = MutableStateFlow<GroupScheduleUiState>(GroupScheduleUiState.Loading)
    val uiState: StateFlow<GroupScheduleUiState> = _uiState

    init{
        viewModelScope.launch {
            networkFlow.collect(
                onUnavailable = {

                },
                onAvailable = {
                    if (_uiState.value is GroupScheduleUiState.NoConnection && lastLoadedId != -1) {
                        loadSchedule(lastLoadedId)
                    }
                }
            )

        }
    }


    fun loadSchedule(groupID: Int) {

        lastLoadedId = groupID

        viewModelScope.launch(Dispatchers.IO) {

            _uiState.value = GroupScheduleUiState.Loading

            getGroupSchedule(groupID).collect { result ->
                when (result) {
                    is AppResult.Success<GroupReadySchedule> -> {
                        _uiState.value = GroupScheduleUiState.Success(
                            result.data,
                            0,
                            result.data.group.isFavorite
                        )

                        if(result.data.group.isFavorite)
                            settingsDAO.setLastOpenedId(lastLoadedId, 0)
                    }

                    is AppResult.ApiError<LogicError> -> {

                        when (result.body) {
                            LogicError.ConfigureError -> _uiState.value =
                                Error("Не удалось извлечь расписание!")

                            LogicError.Empty -> _uiState.value =
                                Error("Расписание отсутствует!")

                            is LogicError.FetchDataError -> _uiState.value =
                                Error(result.body.message)

                            LogicError.NoCriticalError -> {

                            }

                            LogicError.NoInternetConnection -> {
                                if(_uiState.value !is GroupScheduleUiState.Success)
                                _uiState.value = GroupScheduleUiState.NoConnection
                            }
                        }

                    }
                }
            }
        }
    }


    fun clickSubgroup() {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value is GroupScheduleUiState.Success)
                _uiState.value = (uiState.value as GroupScheduleUiState.Success).copy(
                    numSubgroup = ((uiState.value as GroupScheduleUiState.Success).numSubgroup + 1) % 3
                )
        }
    }

    fun clickFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value is GroupScheduleUiState.Success) {

                val state = (uiState.value as GroupScheduleUiState.Success)

                _uiState.value = state.copy(
                    isFavorite = !state.isFavorite
                )

                setFavoriteEntity(FavoriteEntity(state.schedule.group.id, 0), !state.isFavorite)

                if(!state.isFavorite)
                    settingsDAO.setLastOpenedId(lastLoadedId, 0)

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

sealed class GroupScheduleUiState {
    object Loading : GroupScheduleUiState()
    data class Success(val schedule: GroupReadySchedule, val numSubgroup: Int, val isFavorite: Boolean) : GroupScheduleUiState()
    data class Error(val message: String) : GroupScheduleUiState()
    object NoConnection : GroupScheduleUiState()
}