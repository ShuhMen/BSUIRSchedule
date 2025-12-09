package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.collect
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.useCases.GetGroupListUseCase
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupsListUiState.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val getGroupsListUseCase: GetGroupListUseCase,
    private val networkState: @JvmSuppressWildcards     Flow<NetworkStatus>,
) : ViewModel() {

    init {
        viewModelScope.launch {
            networkState.collect(
                onUnavailable = {

                },
                onAvailable = {
                    if (_uiState.value is GroupsListUiState.NoConnection) {
                        loadList()
                    }
                }
            )
        }
    }

    var groupsList = listOf<Group>()

    private val _connectionLabel = MutableStateFlow(false)
    val connectionLabel: StateFlow<Boolean> = _connectionLabel

    private val _uiState = MutableStateFlow<GroupsListUiState>(GroupsListUiState.Loading)
    val uiState: StateFlow<GroupsListUiState> = _uiState

    fun loadList() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = GroupsListUiState.Loading

            getGroupsListUseCase().collect { result ->
                when(result)
                {
                    is AppResult.Success<List<Group>> -> {
                        groupsList = result.data.sortedBy { it.isFavorite }

                        _uiState.value = GroupsListUiState.Success(groupsList)
                    }
                    is AppResult.ApiError<LogicError> -> {

                        when(result.body){
                            LogicError.ConfigureError ->  _uiState.value = Error("Не удалось извлечь расписание!")

                            LogicError.Empty -> _uiState.value = Error("Отсутствуют данные!")
                            is LogicError.FetchDataError -> _uiState.value = Error(result.body.message)
                            LogicError.NoCriticalError -> {

                            }
                            LogicError.NoInternetConnection -> {
                                if(_uiState.value !is GroupsListUiState.Success)
                                    _uiState.value = GroupsListUiState.NoConnection
                                //_connectionLabel.value = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun search(search: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(GroupsListUiState.Success(groupsList.filter { it.name.contains(search) }.toList()))
        }
    }
}

sealed class GroupsListUiState : ViewState() {
    object Loading : GroupsListUiState()
    data class Success(val groupList: List<Group>) : GroupsListUiState()
    data class Error(val message: String) : GroupsListUiState()
    object NoConnection : GroupsListUiState()
}