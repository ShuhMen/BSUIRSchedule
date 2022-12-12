package com.maximshuhman.bsuirschedule.Data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface IISApi {


    @GET("schedule?")
    suspend fun getgroupSchedule(
        @Query("studentGroup") groupId: String
    ): Response<ResponseBody>

    @GET("current-week")
    suspend fun getCurrrentWeek(): Response<ResponseBody>

    @GET("student-groups")
    suspend fun getGroupsList(): Response<ResponseBody>


    @GET("employees/all")
    suspend fun getEmployeesList(): Response<ResponseBody>

    @GET("photo/{id}")
    suspend fun getemployeePhoto(
        @Path("id") emplId: String
    ): Response<ResponseBody>

    //   fun groupList(@Path("id") groupId: Int, @Query("sort") sort: String?): Call<List<User?>?>?
    // @POST("/api")
    //  suspend fun postApi(@Body requestBody: RequestBody): Response<ResponseBody>


}