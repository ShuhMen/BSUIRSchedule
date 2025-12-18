package com.maximshuhman.bsuirschedule.data.repositories

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.SourceError
import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.sources.IISService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import retrofit2.Response
import javax.inject.Inject


sealed class NetError : SourceError() {
    data class ApiError(val message: String?, val code: ResponseCode) :NetError()
    object EmptyError: NetError()
    object NetworkError : NetError()
}

enum class ResponseCode{
    Empty,
    Ok,
    Error
}


class ScheduleNetworkSourceImpl @Inject constructor(
    private val apiService: IISService
) : ScheduleSource {

    override suspend fun getGroupsList() = flow {

        try {
            val response = apiService.getGroupsList()

            if (response.isSuccessful) {

                if (response.body() != null)
                    emit(AppResult.Success(response.body()!!))
                else
                    emit(AppResult.ApiError(NetError.EmptyError))
            } else {
                emit(AppResult.ApiError(NetError.ApiError(response.message(), ResponseCode.Error)))
            }

        }catch (e: Exception){
            println(e.stackTrace)
            emit(AppResult.ApiError(NetError.ApiError(e.message, ResponseCode.Error)))
        }
    }.single()

    private inline fun getSchedule(response: Response<CommonSchedule>): AppResult<CommonSchedule, NetError>{

        return if (response.isSuccessful) {

            if (response.body() != null) {

                val schedule = response.body()!!

                if(schedule.schedules == null && schedule.exams == null  )
                    (AppResult.ApiError(NetError.EmptyError))
                else
                    (AppResult.Success(schedule))
            } else
                (AppResult.ApiError(NetError.EmptyError))
        } else {
            if(response.code() == 404)
                (AppResult.ApiError(NetError.EmptyError))
            else
                (AppResult.ApiError(NetError.ApiError(response.message(), ResponseCode.Error)))
        }
    }


    override suspend fun getGroupSchedule(grNum: String) = flow {

        emit(getSchedule(apiService.getGroupSchedule(grNum)))

    }.single()

    override suspend fun getEmployeeSchedule(employeeUrlId: String)= flow {

        emit(getSchedule(apiService.getEmployeeSchedule(employeeUrlId)))

    }.single()


    override suspend fun getCurrent() = flow {
        val response = apiService.getCurWeek()

        if (response.isSuccessful) {

            if (response.body() != null)
                emit(AppResult.Success(response.body()!!))
            else
                emit(AppResult.ApiError(NetError.EmptyError))
        } else {
            emit(AppResult.ApiError(NetError.ApiError(response.message(), ResponseCode.Error)))
        }
    }.single()

    override suspend fun getGroupScheduleLastUpdate(groupNumber: String)= flow {
        val response = apiService.getGroupScheduleLastUpdate(groupNumber)

        if (response.isSuccessful) {

            if (response.body() != null)
                emit(AppResult.Success(response.body()!!))
            else
                emit(AppResult.ApiError(NetError.EmptyError))
        } else {
            emit(AppResult.ApiError(NetError.ApiError(response.message(), ResponseCode.Error)))
        }
    }.single()

    override suspend fun getEmployeesList(): AppResult<List<Employee>, NetError> = flow {
        val response = apiService.getEmployeesList()

        if (response.isSuccessful) {

            if (response.body() != null)
                emit(AppResult.Success(response.body()!!))
            else
                emit(AppResult.ApiError(NetError.EmptyError))
        } else {
            emit(AppResult.ApiError(NetError.ApiError(response.message(), ResponseCode.Error)))
        }
    }.single()

}
