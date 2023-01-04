package com.maximshuhman.bsuirschedule.Data

import Employees
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.maximshuhman.bsuirschedule.DataClasses.Group
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


data class JSONResponse(var errorCode: Int, var errorMessage: String, var obj: JSONObject)
data class IntResponse(var errorCode: Int, var errorMessage: String, var res: Int)
data class StringResponse(var errorCode: Int, var errorMessage: String, var res: String)
data class JSONArrayResponse(var errorCode: Int, var errorMessage: String, var arr: JSONArray)

object Requests {

    fun getGroupSchedule(baseUrl: String, grNum: String): JSONResponse {
        var response: Response<ResponseBody>

        val retrofit = Clientbuilder.getGroupScheduleClient(baseUrl)

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getgroupSchedule(grNum)
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

    fun getCurrent(): IntResponse {
        var response: Response<ResponseBody>

        val retrofit = Clientbuilder.getGroupScheduleClient("https://iis.bsuir.by/api/v1/schedule/")

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getCurrrentWeek()
                }
            } catch (e: Exception) {
                return IntResponse(-1, "REQUEST ERROR", 0)
            }
        } catch (e: NullPointerException) {
            return IntResponse(-2, "WRONG ADDRESS TYPE", 0)
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
                IntResponse(0, "", jsonString.toInt())

            } catch (e: JSONException) {
                IntResponse(-1, "REQUEST ERROR", 0)
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

                        return IntResponse(-1, "REQUEST ERROR", 0)

                    } catch (e: Exception) {
                        return IntResponse(-9, "UNEXPECTED ERROR", 0)
                    }
                }
                "Unauthorized" -> IntResponse(-3, "WRONG_ANSWER", 0)
                else -> IntResponse(-1, "REQUEST ERROR", 0)
            }
        }
    }

    fun getGroupsList(baseUrl: String): JSONArrayResponse {
        var response: Response<ResponseBody>

        val retrofit = Clientbuilder.getGroupScheduleClient(baseUrl)

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getGroupsList()
                }
            } catch (e: Exception) {
                return JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Group>()))
            }
        } catch (e: NullPointerException) {
            return JSONArrayResponse(-2, "WRONG ADDRESS TYPE", JSONArray(emptyArray<Group>()))
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
                // jsonString
                JSONArrayResponse(0, "", JSONArray(jsonString))

            } catch (e: JSONException) {
                JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Group>()))
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

                        return JSONArrayResponse(
                            -1,
                            "REQUEST ERROR",
                            JSONArray(emptyArray<Group>())
                        )

                    } catch (e: Exception) {
                        return JSONArrayResponse(
                            -9,
                            "UNEXPECTED ERROR",
                            JSONArray(emptyArray<Group>())
                        )
                    }
                }
                "Unauthorized" -> JSONArrayResponse(
                    -3,
                    "WRONG_ANSWER",
                    JSONArray(emptyArray<Group>())
                )
                else -> JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Group>()))
            }
        }

    }

    fun getLastUpdate(groupNumber: String): StringResponse {
        var response: Response<ResponseBody>

        val retrofit =
            Clientbuilder.getGroupScheduleClient("https://iis.bsuir.by/api/v1/last-update-date/")

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getLastUpdate(groupNumber)
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
    }

    fun getEmployeesList(baseUrl: String): JSONArrayResponse {
        var response: Response<ResponseBody>

        val retrofit = Clientbuilder.getGroupScheduleClient(baseUrl)

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getEmployeesList()
                }
            } catch (e: Exception) {
                return JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Employees>()))
            }
        } catch (e: NullPointerException) {
            return JSONArrayResponse(-2, "WRONG ADDRESS TYPE", JSONArray(emptyArray<Employees>()))
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
                // jsonString
                JSONArrayResponse(0, "", JSONArray(jsonString))

            } catch (e: JSONException) {
                JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Employees>()))
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

                        return JSONArrayResponse(
                            -1,
                            "REQUEST ERROR",
                            JSONArray(emptyArray<Employees>())
                        )

                    } catch (e: Exception) {
                        return JSONArrayResponse(
                            -9,
                            "UNEXPECTED ERROR",
                            JSONArray(emptyArray<Employees>())
                        )
                    }
                }
                "Unauthorized" -> JSONArrayResponse(
                    -3,
                    "WRONG_ANSWER",
                    JSONArray(emptyArray<Employees>())
                )
                else -> JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Employees>()))
            }
        }

    }

    fun getEmployeePhoto(id: String): StringResponse {
        var response: Response<ResponseBody>

        val retrofit =
            Clientbuilder.getGroupScheduleClient("https://iis.bsuir.by/api/v1/employees/")

        try {
            val service = retrofit!!.create(IISApi::class.java)

            try {
                runBlocking {
                    response = service.getemployeePhoto(id)
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
                "Unauthorized" -> StringResponse(-3, "WRONG_ANSWER", "")
                else -> StringResponse(-1, "REQUEST ERROR", "")
            }
        }

    }

}
