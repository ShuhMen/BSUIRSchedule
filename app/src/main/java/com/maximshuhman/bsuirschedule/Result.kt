package com.maximshuhman.bsuirschedule

sealed class AppResult<out S, out E> {
    data class Success<out S>(val data: S) : AppResult<S,Nothing>()
    data class ApiError<out E>(val body:E) : AppResult<Nothing,E>()

}