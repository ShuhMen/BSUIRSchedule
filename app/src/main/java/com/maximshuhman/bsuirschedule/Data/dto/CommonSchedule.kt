package com.maximshuhman.bsuirschedule.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommonSchedule(
    val startDate         : String?,
    val endDate           : String?,
    val startExamsDate    : String,
    val endExamsDate      : String,
    val employeeDto       : EmployeeDto?,
    val studentGroupDto   : StudentGroupDto?,
    val schedules         : Schedules?,
    val currentTerm       : String?,
    val exams             : List<Lesson>? = null,
    val currentPeriod     : String?,
)