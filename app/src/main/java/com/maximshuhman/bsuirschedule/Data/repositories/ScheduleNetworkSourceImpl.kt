package com.maximshuhman.bsuirschedule.data.repositories

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.sources.IISService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import javax.inject.Inject


sealed class NetError {
    data class ApiError(val message: String?, val code: ResponseCode) :NetError()
    object EmptyError: NetError()
    object NetworkError : NetError()
    data class UnknownError(val error: Throwable) : NetError()
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


    override suspend fun getGroupSchedule(grNum: String) = flow {

        val response = apiService.getGroupSchedule(grNum)

        if (response.isSuccessful) {

            if (response.body() != null)
                emit(AppResult.Success(response.body()!!))
            else
                emit(AppResult.ApiError(NetError.EmptyError))
        } else {
            if(response.code() == 404)
                emit(AppResult.ApiError(NetError.EmptyError))
            else
                emit(AppResult.ApiError(NetError.ApiError(response.message(), ResponseCode.Error)))
        }
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

    /* fun getEmployeePhoto(id: String): StringResponse {
        var response: CommonResponse<ResponseBody>

        val retrofit =
            Clientbuilder.getGroupScheduleClient("https://iis.bsuir.by/api/v1/employees/")

        try {
            val service = retrofit!!.create(IISApi::class.java)



            try {
                runBlocking {
                    response = service.getEmployeePhoto(id)
                }
            } catch (e: Exception) {
                return StringResponse(-1, "REQUEST ERROR", "")
            }
        } catch (e: NullPointerException) {
            return StringResponse(-2, "WRONG ADDRESS TYPE", "")
        }

        if (response.isSuccessful) {

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(
                JsonParser.parseString(
                    response.body()
                        ?.string()
                )
            )

            // val jsonString = response.body().toString()

            return try {
                jsonString
                StringResponse(0, "", jsonString)

            } catch (e: JSONException) {
                StringResponse(-1, "REQUEST ERROR", "")
            }
        } else {

            return when (response.message()) {
                "Bad Request" -> {
                    try {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val jsonString = gson.toJson(
                            JsonParser.parseString(
                                response.errorBody()
                                    ?.string()
                            )
                        )
                        val json = JSONObject(jsonString)

                        return StringResponse(-1, "REQUEST ERROR", "")

                    } catch (e: Exception) {
                        return StringResponse(-9, "UNEXPECTED ERROR", "")
                    }
                }

                else -> StringResponse(-1, "REQUEST ERROR", "")
            }
        }

    }*/

    /*    fun getEmployeeSchedule(empID: String): JSONResponse {
        var response: CommonResponse<ResponseBody>

        val retrofit =
            Clientbuilder.getGroupScheduleClient("https://iis.bsuir.by/api/v1/employees/")

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getEmployeeSchedule(empID)
                }
            } catch (e: Exception) {
                return JSONResponse(-1, "REQUEST ERROR", JSONObject())
            }
        } catch (e: NullPointerException) {
            return JSONResponse(-2, "WRONG ADDRESS TYPE", JSONObject())
        }

        if (response.isSuccessful) {

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(
                JsonParser.parseString(
                    response.body()
                        ?.string()
                )
            )

            // val jsonString = response.body().toString()

            return try {
                val json = JSONObject(jsonString)

                JSONResponse(0, "", json)

            } catch (e: JSONException) {
                JSONResponse(-1, "REQUEST ERROR", JSONObject())
            }
        } else {

            return when (response.message()) {
                "Bad Request" -> {
                    try {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val jsonString = gson.toJson(
                            JsonParser.parseString(
                                response.errorBody()
                                    ?.string()
                            )
                        )
                        val json = JSONObject(jsonString)

                        return JSONResponse(-1, "REQUEST ERROR", JSONObject())

                    } catch (e: Exception) {
                        return JSONResponse(-9, "UNEXPECTED ERROR", JSONObject())
                    }
                }

                "Unauthorized" -> JSONResponse(-3, "WRONG_ANSWER", JSONObject())
                else -> JSONResponse(-1, "REQUEST ERROR", JSONObject())
            }
        }
    }

    fun getEmployeeScheduleLastUpdate(employeeID: Int): StringResponse {
        var response: CommonResponse<ResponseBody>

        val retrofit =
            Clientbuilder.getGroupScheduleClient("https://iis.bsuir.by/api/v1/last-update-date/")

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getEmployeeScheduleLastUpdate(employeeID)
                }
            } catch (e: Exception) {
                return StringResponse(-1, "REQUEST ERROR", "")
            }
        } catch (e: NullPointerException) {
            return StringResponse(-2, "WRONG ADDRESS TYPE", "")
        }

        if (response.isSuccessful) {

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(
                JsonParser.parseString(
                    response.body()
                        ?.string()
                )
            )

            // val jsonString = response.body().toString()

            return try {
                val json = JSONObject(jsonString)
                StringResponse(0, "", json.getString("lastUpdateDate"))

            } catch (e: JSONException) {
                StringResponse(-1, "REQUEST ERROR", "")
            }
        } else {

            return when (response.message()) {
                "Bad Request" -> {
                    try {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val jsonString = gson.toJson(
                            JsonParser.parseString(
                                response.errorBody()
                                    ?.string()
                            )
                        )
                        val json = JSONObject(jsonString)

                        return StringResponse(-1, "REQUEST ERROR", "")

                    } catch (e: Exception) {
                        return StringResponse(-9, "UNEXPECTED ERROR", "")
                    }
                }

                "Unauthorized" -> StringResponse(-3, "WRONG_ANSWER", "")
                else -> StringResponse(-1, "REQUEST ERROR", "")
            }
        }
    }*/

}
