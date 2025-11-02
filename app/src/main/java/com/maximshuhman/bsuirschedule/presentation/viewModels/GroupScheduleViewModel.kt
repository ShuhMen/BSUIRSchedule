package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.dto.FavoriteEntity
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.domain.models.Favorites
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.models.ReadySchedule
import com.maximshuhman.bsuirschedule.domain.useCases.GetFavoritesUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.GetGroupScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupScheduleViewModel @Inject constructor(
    private val getGroupSchedule: GetGroupScheduleUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val groupsSource: GroupsDAO
): ViewModel() {

    private val _uiState = MutableStateFlow<GroupScheduleUiState>(GroupScheduleUiState.Loading)
    val uiState: StateFlow<GroupScheduleUiState> = _uiState

    fun loadSchedule(groupID: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            _uiState.value = GroupScheduleUiState.Loading

            when (val result = getGroupSchedule(groupID)) {
                is AppResult.Success<ReadySchedule> -> {
                    _uiState.value = GroupScheduleUiState.Success(result.data, 0, result.data.group.isFavorite)
                }

                is AppResult.ApiError<LogicError> -> {

                    when (result.body) {
                        LogicError.ConfigureError -> _uiState.value =
                            GroupScheduleUiState.Error("Не удалось извлечь расписание!")

                        LogicError.Empty -> _uiState.value =
                            GroupScheduleUiState.Error("Расписание отсутствует!")

                        is LogicError.FetchDataError -> _uiState.value =
                            GroupScheduleUiState.Error(result.body.message)
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

                if(state.isFavorite)
                    groupsSource.deleteFavorite(FavoriteEntity(state.schedule.group.id, 0))
                else
                    groupsSource.upsertFavorite(FavoriteEntity(state.schedule.group.id, 0))

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
    data class Success(val schedule: ReadySchedule, val numSubgroup: Int, val isFavorite: Boolean) : GroupScheduleUiState()
    data class Error(val message: String) : GroupScheduleUiState()
}