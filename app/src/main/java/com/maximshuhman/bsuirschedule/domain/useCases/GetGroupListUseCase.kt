package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.repositories.NetError
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.domain.NetworkStatus
import com.maximshuhman.bsuirschedule.domain.NetworkStatusTracker
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.models.toLogicError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGroupListUseCase @Inject constructor(
    private val repository: ScheduleSource,
    private val groupsDAO: GroupsDAO,
    private val networkStatusTracker: NetworkStatusTracker,
) {

    operator fun invoke(): Flow<AppResult<List<Group>, LogicError>> = flow {

        val groupsDbList = groupsDAO.getAll()
        val favoriteIds = groupsDAO.getFavoriteIds().toSet()

        groupsDbList.forEach { group ->
            group.isFavorite = group.id in favoriteIds
        }

        if(groupsDbList.isNotEmpty())
            emit(AppResult.Success(groupsDbList))

        if(networkStatusTracker.getCurrentNetworkStatus() is NetworkStatus.Unavailable){
            emit(AppResult.ApiError(LogicError.NoInternetConnection))
            return@flow
        }

        val result = repository.getGroupsList()

        if(result is AppResult.ApiError<NetError>) {
            emit(AppResult.ApiError(result.body.toLogicError()))
            return@flow
        }

        if(result !is AppResult.Success<List<Group>>) {
            emit(AppResult.ApiError(LogicError.FetchDataError("Не удалось получить список групп")))
            return@flow
        }

        groupsDAO.insertAll(result.data)

        val newGroupsDbList = groupsDAO.getAll()

        newGroupsDbList.forEach { group ->
            group.isFavorite = group.id in favoriteIds
        }

        emit(AppResult.Success(newGroupsDbList))

    }

}