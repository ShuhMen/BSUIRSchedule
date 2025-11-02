package com.maximshuhman.bsuirschedule.data.repositories

import CommonSchedule
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.dto.LastUpdateDate
import javax.inject.Inject


class ScheduleDataBaseSourceImpl @Inject constructor() : ScheduleSource{
    override suspend fun getGroupSchedule(grNum: String): AppResult<CommonSchedule, NetError> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrent(): AppResult<Int, NetError> {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupsList(): AppResult<List<Group>, NetError> {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupScheduleLastUpdate(groupNumber: String): AppResult<LastUpdateDate, NetError> {
        TODO("Not yet implemented")
    }

    override suspend fun getEmployeesList(): AppResult<List<Employee>, NetError> {
        TODO("Not yet implemented")
    }

}