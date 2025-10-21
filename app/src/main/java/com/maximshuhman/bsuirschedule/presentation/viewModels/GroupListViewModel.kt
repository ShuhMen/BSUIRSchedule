package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.models.Group
import com.maximshuhman.bsuirschedule.domain.LogicError
import com.maximshuhman.bsuirschedule.domain.useCases.GetGroupListUseCase
import com.maximshuhman.bsuirschedule.domain.useCases.SetGroupListUseCase
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupsListUiState.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val getGroupsListUseCase: GetGroupListUseCase,
    private val setGroupsListUseCase: SetGroupListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<GroupsListUiState>(GroupsListUiState.Loading)
    val uiState: StateFlow<GroupsListUiState> = _uiState

    fun loadList() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = GroupsListUiState.Loading
            when(val result = getGroupsListUseCase())
            {
                is AppResult.Success<List<Group>> -> {
                    _uiState.value = GroupsListUiState.Success(result.data)

                    setGroupsListUseCase(result.data)

                }
                is AppResult.ApiError<LogicError> -> {

                    when(result.body){
                        LogicError.ConfigureError ->  _uiState.value = Error("Не удалось извлечь расписание!")

                        LogicError.Empty -> _uiState.value = Error("Отсутствуют данные!")
                        is LogicError.FetchDataError -> _uiState.value = Error(result.body.message)
                    }

                }
            }

        }
    }
}

sealed class GroupsListUiState {
    object Loading : GroupsListUiState()
    data class Success(val groupList: List<Group>) : GroupsListUiState()
    data class Error(val message: String) : GroupsListUiState()
}