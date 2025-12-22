package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.repositories.NetError
import com.maximshuhman.bsuirschedule.data.sources.EmployeeDAO
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.NetworkStatusTracker
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.models.toLogicError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEmployeeListUseCase @Inject constructor(
    private val employeeDAO: EmployeeDAO,
    private val repository: ScheduleSource,
    private val networkStatusTracker: NetworkStatusTracker,
) {
    suspend operator fun invoke(): Flow<AppResult<List<Employee>, LogicError>> = flow {

        val employeeDbList = employeeDAO.getAll()
        val favoriteIds = employeeDAO.getFavoriteIds().toSet()

        employeeDbList.forEach { employee ->
            employee.isFavorite = employee.id in favoriteIds
        }

        if(employeeDbList.isNotEmpty())
            emit(AppResult.Success(employeeDbList))

        if(networkStatusTracker.getCurrentNetworkStatus() is NetworkStatus.Unavailable){
            emit(AppResult.ApiError(LogicError.NoInternetConnection))
            return@flow
        }

        val result = repository.getEmployeesList()

        if(result is AppResult.ApiError<NetError>) {
            emit(AppResult.ApiError(result.body.toLogicError()))
            return@flow
        }

        if(result !is AppResult.Success<List<Employee>>) {
            emit(AppResult.ApiError(LogicError.FetchDataError("Не удалось получить список групп")))
            return@flow
        }

        employeeDAO.insertAll(result.data)

        val newEmployeeDbList = employeeDAO.getAll()

        newEmployeeDbList.forEach { employee ->
            employee.isFavorite = employee.id in favoriteIds
        }

        emit(AppResult.Success(newEmployeeDbList))
    }


}