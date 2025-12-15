package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.SourceError
import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.repositories.ScheduleDataBaseSourceImpl
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.data.sources.SettingsDAO
import com.maximshuhman.bsuirschedule.domain.GetScheduleUseCase
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.NetworkStatusTracker
import com.maximshuhman.bsuirschedule.domain.models.GroupReadySchedule
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.models.toLogicError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGroupScheduleUseCase @Inject constructor(
    private val repository: ScheduleSource,
    private val groupsSource: GroupsDAO,
    private val databaseRepository: ScheduleDataBaseSourceImpl,
    private val networkStatusTracker: NetworkStatusTracker,
    private val settingsDAO: SettingsDAO
): GetScheduleUseCase(repository, networkStatusTracker, settingsDAO) {

    operator fun invoke(groupId: Int): Flow<AppResult<GroupReadySchedule, LogicError>> = flow {

        val group = groupsSource.getById(groupId)

        if (group == null) {
            emit(AppResult.ApiError(LogicError.Empty))
            return@flow
        }

        group.isFavorite = groupsSource.getFavoriteIds().contains(group.id)

        var dbSchedule = databaseRepository.getGroupSchedule(group.name)

        if(dbSchedule is AppResult.ApiError){
            emit(AppResult.ApiError(dbSchedule.body.toLogicError()))
        }else {

            val configureResult = configureSchedule((dbSchedule as AppResult.Success).data)
            val configureExams = configureExams(dbSchedule.data)

            if(configureResult is AppResult.ApiError){
                emit(configureResult)
                return@flow
            }

            if(configureExams is AppResult.ApiError){
                emit(configureExams)
                return@flow
            }

            emit(AppResult.Success(GroupReadySchedule(group, (configureResult as AppResult.Success).data, (configureExams as AppResult.Success).data)))
        }

        if(networkStatusTracker.getCurrentNetworkStatus() is NetworkStatus.Unavailable){
            emit(AppResult.ApiError(LogicError.NoInternetConnection))
            return@flow
        }

        val result = repository.getGroupSchedule(group.name)

        if (result is AppResult.ApiError<SourceError>) {
            emit(AppResult.ApiError(result.body.toLogicError()))
            return@flow
        }

        val commonSchedule = (result as AppResult.Success<CommonSchedule>).data

        databaseRepository.setGroupSchedule(commonSchedule)

        dbSchedule = databaseRepository.getGroupSchedule(group.name)

        if(dbSchedule is AppResult.ApiError){
            emit(AppResult.ApiError(dbSchedule.body.toLogicError()))
            return@flow
        }

        val configureResult = configureSchedule((dbSchedule as AppResult.Success).data)
        val configureExams = configureExams(dbSchedule.data)

        if(configureResult is AppResult.ApiError){
            emit(configureResult)
            return@flow
        }

        if(configureExams is AppResult.ApiError){
            emit(configureExams)
            return@flow
        }



        emit(AppResult.Success(GroupReadySchedule(group, (configureResult as AppResult.Success).data, (configureExams as AppResult.Success).data)))

    }

    suspend inline fun analyseSchedule(group: Group, schedule: AppResult<CommonSchedule, SourceError>): AppResult<GroupReadySchedule, LogicError>  {

        if(schedule is AppResult.ApiError){
            return (AppResult.ApiError(schedule.body.toLogicError()))
        }else {

            val configureResult = configureSchedule((schedule as AppResult.Success).data)
            val configureExams = configureExams(schedule.data)

            if(configureResult is AppResult.ApiError){
                return (configureResult)
            }

            if(configureExams is AppResult.ApiError){
                return(configureExams)
            }

            return (AppResult.Success(GroupReadySchedule(group, (configureResult as AppResult.Success).data, (configureExams as AppResult.Success).data)))
        }
    }
}