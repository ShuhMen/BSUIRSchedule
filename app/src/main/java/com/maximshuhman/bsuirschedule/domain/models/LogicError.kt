package com.maximshuhman.bsuirschedule.domain.models

import com.maximshuhman.bsuirschedule.data.SourceError
import com.maximshuhman.bsuirschedule.data.repositories.DBError
import com.maximshuhman.bsuirschedule.data.repositories.NetError

sealed class LogicError {
    object NoInternetConnection:  LogicError()
    object Empty:  LogicError()
    object NoCriticalError: LogicError()
    data class ConfigureError(val message: String): LogicError()
    data class FetchDataError(val message: String): LogicError()
}

fun SourceError.toLogicError(): LogicError {

    return when(this){
        is NetError -> {
            when (this) {
                is NetError.ApiError -> LogicError.FetchDataError(this.message ?: "Ошибка запроса")
                NetError.EmptyError -> LogicError.Empty
                NetError.NetworkError -> LogicError.FetchDataError("Ошибка запроса")
            }

        }

        is DBError -> {
            when (this) {
                DBError.NoData -> {
                    LogicError.NoCriticalError
                }
            }
        }

        is SourceError.UnknownError ->{
            LogicError.FetchDataError(
                this.error.message ?: "Ошибка запроса"
            )
        }

        else -> {
            LogicError.FetchDataError(
                "Ошибка получения данных"
            )
        }
    }



}