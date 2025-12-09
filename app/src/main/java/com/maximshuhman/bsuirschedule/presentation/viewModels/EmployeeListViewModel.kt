package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.collect
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.useCases.GetEmployeeListUseCase
import com.maximshuhman.bsuirschedule.presentation.viewModels.EmployeeListUiState.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeListViewModel @Inject constructor(
    private val getEmployeeListUseCase: GetEmployeeListUseCase,
    private val networkState: @JvmSuppressWildcards     Flow<NetworkStatus>,
    ) : ViewModel() {
    init {
        viewModelScope.launch {
            networkState.collect(
                onUnavailable = {

                },
                onAvailable = {
                    if (_uiState.value is EmployeeListUiState.NoConnection) {
                        loadList()
                    }
                }
            )
        }
    }

    var employeeList = listOf<Employee>()

    private val _uiState = MutableStateFlow<EmployeeListUiState>(EmployeeListUiState.Loading)
    val uiState: StateFlow<EmployeeListUiState> = _uiState

    fun loadList() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = EmployeeListUiState.Loading

            getEmployeeListUseCase().collect { result ->
                when(result)
                {
                    is AppResult.Success<List<Employee>> -> {
                        employeeList = result.data.sortedBy { it.isFavorite }

                        _uiState.value = EmployeeListUiState.Success(employeeList)

                    }
                    is AppResult.ApiError<LogicError> -> {

                        when(result.body){
                            LogicError.ConfigureError ->  _uiState.value = Error("Не удалось извлечь расписание!")

                            LogicError.Empty -> _uiState.value = Error("Отсутствуют данные!")
                            is LogicError.FetchDataError -> _uiState.value = Error(result.body.message)
                            LogicError.NoCriticalError -> {

                            }
                            LogicError.NoInternetConnection -> {
                                if(_uiState.value !is EmployeeListUiState.Success)
                                    _uiState.value = EmployeeListUiState.NoConnection
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
            _uiState.emit(EmployeeListUiState.Success(employeeList.filter { it.fio.lowercase().contains(search) }.toList()))
        }
    }
}

sealed class EmployeeListUiState {
    object Loading : EmployeeListUiState()
    data class Success(val groupList: List<Employee>) : EmployeeListUiState()
    data class Error(val message: String) : EmployeeListUiState()
    object NoConnection: EmployeeListUiState()
}