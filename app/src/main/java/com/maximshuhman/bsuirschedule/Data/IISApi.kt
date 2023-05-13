package com.maximshuhman.bsuirschedule.Data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface IISApi {


    @GET("schedule?")
    suspend fun getGroupSchedule(
        @Query("studentGroup") groupId: String
    ): Response<ResponseBody>

    @GET("current-week")
    suspend fun getCurWeek(): Int?

    @GET("student-groups")
    suspend fun getGroupsList(): Response<ResponseBody>


    @GET("employees/all")
    suspend fun getEmployeesList(): Response<ResponseBody>

    @GET("photo/{id}")
    suspend fun getEmployeePhoto(
        @Path("id") emplId: String
    ): Response<ResponseBody>

    @GET("student-group?")
    suspend fun getGroupScheduleLastUpdate(
        @Query("groupNumber") groupNumber: String
    ): Response<ResponseBody>

    @GET("schedule/{id}")
    suspend fun getEmployeeSchedule(
        @Path("id") emplId: String
    ): Response<ResponseBody>

    @GET("employee?")
    suspend fun getEmployeeScheduleLastUpdate(
        @Query("id") employeeID: Int
    ): Response<ResponseBody>

    // https://iis.bsuir.by/api/v1/last-update-date/employee?id={empId}
}