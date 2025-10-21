package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.models.Group
import com.maximshuhman.bsuirschedule.data.repositories.NetError
import com.maximshuhman.bsuirschedule.domain.LogicError
import javax.inject.Inject

class GetGroupListUseCase @Inject constructor(
    private val repository: ScheduleSource
) {
    suspend operator fun invoke(): AppResult<List<Group>, LogicError> {

        val result = repository.getGroupsList()

        when(result) {
            is AppResult.ApiError<NetError> -> return AppResult.ApiError(result.body.toLogicError())
            is AppResult.Success<List<Group>> -> return result
        }
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