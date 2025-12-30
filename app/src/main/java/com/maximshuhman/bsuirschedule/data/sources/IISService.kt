package com.maximshuhman.bsuirschedule.data.sources

import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.dto.LastUpdateDate
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IISService {


    @GET("schedule?")
    suspend fun getGroupSchedule(
        @Query("studentGroup") groupId: String
    ): Response<CommonSchedule>

    @GET("schedule/current-week")
    suspend fun getCurWeek(): Response<Int>

    @GET("student-groups")
    suspend fun getGroupsList(): Response<List<Group>>

    @GET("employees/all")
    suspend fun getEmployeesList(): Response<List<Employee>>

    @GET("photo/{id}")
    suspend fun getEmployeePhoto(
        @Path("id") emplId: String
    ): Response<ResponseBody>

    @GET("last-update-date/student-group?")
    suspend fun getGroupScheduleLastUpdate(
        @Query("groupNumber") groupNumber: String
    ): Response<LastUpdateDate>

    @GET("employees/schedule/{id}")
    suspend fun getEmployeeSchedule(
        @Path("id") employeeUrlID: String
    ): Response<CommonSchedule>

    @GET("last-update-date/employee?")
    suspend fun getEmployeeScheduleLastUpdate(
        @Query("id") employeeID: Int
    ): Response<LastUpdateDate>

    // https://iis.bsuir.by/api/v1/last-update-date/employee?id={empId}
}