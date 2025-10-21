package com.maximshuhman.bsuirschedule.Data

import com.maximshuhman.bsuirschedule.data.IISApi
import okio.IOException
import retrofit2.Response

sealed class CommonResponse {
    data class ErrorResponse(val errorCode: ResponseCode, val errorMessage: String): CommonResponse()
    data class OkResponse<T>(val result: T): CommonResponse()
    data class IOError(val e: Throwable): CommonResponse()
}

enum class ResponseCode{
    OK,
    Error,
    Empty,
    NotFound
}

val BASE_URL = "https://iis.bsuir.by/api/v1/"

class Requests : ScheduleSource {

    val retrofit = Clientbuilder.getGroupScheduleClient(BASE_URL)

    val IISApiService = retrofit!!.create(IISApi::class.java)

    inline fun <T> makeRequest(request: () -> Response<T>) : CommonResponse {
        var response: Response<T>

        try {
            response = request()
        } catch (e: IOException) {
            return CommonResponse.IOError(e)
        }

        return if (response.isSuccessful)
            if(response.body() != null)
                CommonResponse.OkResponse(response.body())
            else
                CommonResponse.ErrorResponse(ResponseCode.Empty, "Данные отсутствуют")
        else
            CommonResponse.ErrorResponse(ResponseCode.Error, "Ошибка отправки запроса")
    }

    override suspend fun getGroupSchedule(grNum: String): CommonResponse {
        return makeRequest {
            IISApiService.getGroupSchedule(grNum)
        }
    }

    override suspend fun getCurrent(): CommonResponse {
        return makeRequest {
            IISApiService.getCurWeek()
        }
    }

    override suspend fun getGroupsList(): CommonResponse {
        return makeRequest {
            IISApiService.getGroupsList()
        }
    }

    override suspend fun getGroupScheduleLastUpdate(groupNumber: String): CommonResponse {
        return makeRequest {
            IISApiService.getGroupScheduleLastUpdate(groupNumber)
        }
    }

    override suspend fun getEmployeesList(baseUrl: String): CommonResponse {
        return makeRequest {
            IISApiService.getEmployeesList()
        }
    }

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
