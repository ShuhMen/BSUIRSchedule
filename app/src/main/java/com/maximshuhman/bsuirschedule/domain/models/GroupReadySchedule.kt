package com.maximshuhman.bsuirschedule.domain.models

import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.dto.Lesson

data class GroupReadySchedule (
    val group: Group,
    val schedule: List<ScheduleDay>,
    val exams: List<ScheduleDay>? = null
)

data class EmployeeReadySchedule (
    val employee: Employee,
    val schedule: List<ScheduleDay>,
    val exams: List<ScheduleDay>? = null
)

data class ScheduleDay(
    val header: ScheduleDayHeader,
    val list: List<Lesson>
)


@JvmInline
value class ScheduleDayHeader(
    val name: String
)