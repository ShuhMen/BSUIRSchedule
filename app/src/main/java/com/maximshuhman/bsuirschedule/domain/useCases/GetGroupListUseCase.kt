package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.repositories.NetError
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGroupListUseCase @Inject constructor(
    private val repository: ScheduleSource,
    private val groupsDAO: GroupsDAO
) {
    operator fun invoke(): Flow<AppResult<List<Group>, LogicError>> = flow {

        val groupsDbList = groupsDAO.getAll()
        val favoriteIds = groupsDAO.getFavoriteGroupIds().toSet()

        groupsDbList.forEach { group ->
            group.isFavorite = group.id in favoriteIds
        }

        if(groupsDbList.isNotEmpty())
            emit(AppResult.Success(groupsDbList))

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

    fun NetError.toLogicError(): LogicError {
        return when (this) {
            is NetError.ApiError -> LogicError.FetchDataError(this.message ?: "Ошибка запроса")
            NetError.EmptyError -> LogicError.Empty
            NetError.NetworkError -> LogicError.FetchDataError("Ошибка запроса")
            is NetError.UnknownError -> LogicError.FetchDataError(
                this.error.message ?: "Ошибка запроса"
            )
        }
    }
}