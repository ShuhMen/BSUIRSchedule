package com.maximshuhman.bsuirschedule.data.entities


import androidx.room.Embedded
import androidx.room.Entity
import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.dto.EmployeeDto
import com.maximshuhman.bsuirschedule.data.dto.StudentGroupDto

@Entity(
    tableName = "common_schedule",
    primaryKeys = ["groupId", "employeeId"]
)
data class CommonScheduleEntity(
    val groupId           : Int = -1,
    val employeeId        : Int = -1,
    val startDate         : String?,
    val endDate           : String?,
    val startExamsDate    : String?,
    val endExamsDate      : String?,
    @Embedded
    val employeeDto       : EmployeeDto?,
    @Embedded
    val studentGroupDto   : StudentGroupDto?,
    //val schedules         : Schedules,
    val currentTerm       : String?,
    //val exams             : List<Lesson>? = null,
    val currentPeriod     : String?,
)


fun CommonSchedule.toGroupEntity(): CommonScheduleEntity {

    return CommonScheduleEntity(
        studentGroupDto!!.id,
        -1,
        startDate,
        endDate,
        startExamsDate,
        endExamsDate,
        null,
        studentGroupDto,
        currentTerm,
        currentPeriod
    )


}

fun CommonSchedule.toEmployeeEntity(): CommonScheduleEntity {

    return CommonScheduleEntity(
        -1,
        employeeDto!!.id,
        startDate,
        endDate,
        startExamsDate,
        endExamsDate,
        null,
        studentGroupDto,
        currentTerm,
        currentPeriod
    )


}