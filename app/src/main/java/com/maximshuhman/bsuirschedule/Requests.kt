package com.maximshuhman.bsuirschedule

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.maximshuhman.bsuirschedule.DataClass.Group
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Response


data class JSONResponse (var errorCode: Int, var errorMessage: String, var obj: JSONObject)
data class IntResponse (var errorCode: Int, var errorMessage: String, var res: Int)
data class JSONArrayResponse (var errorCode: Int, var errorMessage: String, var arr: JSONArray)

class Requests {

        fun getGroupSchedule(baseUrl: String, grNum: String) : JSONResponse{
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

                    if (json.getString("startDate") == "" )
                        JSONResponse(-1, "REQUEST ERROR", JSONObject())
                    else
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
                            return JSONResponse(-9, "UNEXPECTED ERROR",JSONObject())
                        }
                    }
                    "Unauthorized" -> JSONResponse(-3,"WRONG_ANSWER", JSONObject())
                    else -> JSONResponse(-1, "REQUEST ERROR", JSONObject())
                }
            }
        }

    fun getCurrent() : IntResponse{
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
                    IntResponse(0, "",jsonString.toInt())

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
                        return IntResponse(-9, "UNEXPECTED ERROR",0)
                    }
                }
                "Unauthorized" -> IntResponse(-3,"WRONG_ANSWER", 0)
                else -> IntResponse(-1, "REQUEST ERROR", 0)
            }
        }
    }

    fun getGroupsList(baseUrl: String):JSONArrayResponse{
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

                        return JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Group>()))

                    } catch (e: Exception) {
                        return JSONArrayResponse(-9, "UNEXPECTED ERROR",JSONArray(emptyArray<Group>()))
                    }
                }
                "Unauthorized" -> JSONArrayResponse(-3,"WRONG_ANSWER", JSONArray(emptyArray<Group>()))
                else -> JSONArrayResponse(-1, "REQUEST ERROR", JSONArray(emptyArray<Group>()))
            }
        }

    }

    fun getLastUpdate() : IntResponse{
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
                IntResponse(0, "",jsonString.toInt())

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
                        return IntResponse(-9, "UNEXPECTED ERROR",0)
                    }
                }
                "Unauthorized" -> IntResponse(-3,"WRONG_ANSWER", 0)
                else -> IntResponse(-1, "REQUEST ERROR", 0)
            }
        }
    }
}
