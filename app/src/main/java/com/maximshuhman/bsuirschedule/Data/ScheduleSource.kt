package com.maximshuhman.bsuirschedule.data

import CommonSchedule
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.dto.LastUpdateDate
import com.maximshuhman.bsuirschedule.data.repositories.NetError

interface ScheduleSource {

    suspend fun getGroupSchedule(grNum: String): AppResult<CommonSchedule, NetError>

    suspend fun getCurrent(): AppResult<Int, NetError>

    suspend fun getGroupsList(): AppResult<List<Group>, NetError>

    suspend fun getGroupScheduleLastUpdate(groupNumber: String): AppResult<LastUpdateDate, NetError>

    suspend fun getEmployeesList(): AppResult<List<Employee>, NetError>
}