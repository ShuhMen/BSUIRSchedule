package com.maximshuhman.bsuirschedule.domain

import com.maximshuhman.bsuirschedule.data.repositories.NetError

sealed class LogicError {
    object Empty:  LogicError()
    object ConfigureError: LogicError()
    data class FetchDataError(val message: String): LogicError()
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