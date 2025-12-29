package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.SourceError
import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.repositories.ScheduleDataBaseSourceImpl
import com.maximshuhman.bsuirschedule.data.sources.EmployeeDAO
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import com.maximshuhman.bsuirschedule.domain.GetScheduleUseCase
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.NetworkStatusTracker
import com.maximshuhman.bsuirschedule.domain.models.EmployeeReadySchedule
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.models.toLogicError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEmployeeScheduleUseCase @Inject constructor(
    private val repository: ScheduleSource,
    private val employeeDAO: EmployeeDAO,
    private val databaseRepository: ScheduleDataBaseSourceImpl,
    private val networkStatusTracker: NetworkStatusTracker,
    private val settingsDAO: SettingsDAO
): GetScheduleUseCase(repository, networkStatusTracker, settingsDAO) {

    operator fun invoke(groupId: Int): Flow<AppResult<EmployeeReadySchedule, LogicError>> = flow {

        val employee = employeeDAO.getById(groupId)

        if (employee == null) {
            emit(AppResult.ApiError(LogicError.Empty))
            return@flow
        }

        employee.isFavorite = employeeDAO.getFavoriteIds().contains(employee.id)

        var dbSchedule = databaseRepository.getEmployeeSchedule(employee.urlId)

        if(dbSchedule is AppResult.ApiError){
            emit(AppResult.ApiError(dbSchedule.body.toLogicError()))
        }else {

            val configureResult = configureSchedule((dbSchedule as AppResult.Success).data)
            val configureExams = configureExams(dbSchedule.data)

            when{
                configureResult is AppResult.ApiError && configureExams is AppResult.ApiError -> {
                    emit(configureResult)
                    return@flow
                }
                configureResult is AppResult.Success && configureExams is AppResult.ApiError ->{
                    emit(AppResult.Success(EmployeeReadySchedule(employee, configureResult.data, listOf())))
                    return@flow
                }
                configureResult is AppResult.ApiError && configureExams is AppResult.Success ->{
                    emit(AppResult.Success(EmployeeReadySchedule(employee, listOf(), configureExams.data)))
                    return@flow
                }
                else -> {
                    emit(AppResult.Success(EmployeeReadySchedule(employee, (configureResult as AppResult.Success).data, (configureExams as AppResult.Success).data)))
                }

            }
        }

        if(networkStatusTracker.getCurrentNetworkStatus() is NetworkStatus.Unavailable){
            emit(AppResult.ApiError(LogicError.NoInternetConnection))
            return@flow
        }

        val result = repository.getEmployeeSchedule(employee.urlId)

        if (result is AppResult.ApiError<SourceError>) {
            emit(AppResult.ApiError(result.body.toLogicError()))
            return@flow
        }

        val commonSchedule = (result as AppResult.Success<CommonSchedule>).data

        databaseRepository.setEmployeeSchedule(commonSchedule)

        dbSchedule = databaseRepository.getEmployeeSchedule(employee.urlId)

        if(dbSchedule is AppResult.ApiError){
            emit(AppResult.ApiError(dbSchedule.body.toLogicError()))
            return@flow
        }

        val configureResult = configureSchedule((dbSchedule as AppResult.Success).data)
        val configureExams = configureExams(dbSchedule.data)

        when{
            configureResult is AppResult.ApiError && configureExams is AppResult.ApiError -> {
                emit(configureResult)
                return@flow
            }
            configureResult is AppResult.Success && configureExams is AppResult.ApiError ->{
                emit(AppResult.Success(EmployeeReadySchedule(employee, configureResult.data, listOf())))
                return@flow
            }
            configureResult is AppResult.ApiError && configureExams is AppResult.Success ->{
                emit(AppResult.Success(EmployeeReadySchedule(employee, listOf(), configureExams.data)))
                return@flow
            }
            else -> {
                emit(AppResult.Success(EmployeeReadySchedule(employee, (configureResult as AppResult.Success).data, (configureExams as AppResult.Success).data)))
            }

        }
    }
}