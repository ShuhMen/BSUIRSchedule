package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.DataClasses.Employee
import com.maximshuhman.bsuirschedule.domain.LogicError
import com.maximshuhman.bsuirschedule.domain.useCases.GetEmployeeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeListViewModel @Inject constructor(
    private val getEmployeeListUseCase: GetEmployeeListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<EmployeeListUiState>(EmployeeListUiState.Loading)
    val uiState: StateFlow<EmployeeListUiState> = _uiState

    fun loadList() {
        viewModelScope.launch {
            _uiState.value = EmployeeListUiState.Loading
            when(val result = getEmployeeListUseCase())
            {
                is AppResult.Success<List<Employee>> -> {
                    _uiState.value = EmployeeListUiState.Success(result.data)
                }
                is AppResult.ApiError<LogicError> -> {

                    when(result.body){
                        LogicError.ConfigureError ->  _uiState.value = EmployeeListUiState.Error("Не удалось извлечь расписание!")

                        LogicError.Empty -> _uiState.value = EmployeeListUiState.Error("Отсутствуют данные!")
                        is LogicError.FetchDataError -> _uiState.value = EmployeeListUiState.Error(result.body.message)
                    }

                }
            }

        }
    }
}

sealed class EmployeeListUiState {
    object Loading : EmployeeListUiState()
    data class Success(val groupList: List<Employee>) : EmployeeListUiState()
    data class Error(val message: String) : EmployeeListUiState()
}