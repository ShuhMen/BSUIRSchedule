package com.maximshuhman.bsuirschedule.data.dto

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDto(
    @ColumnInfo("employeeDtoId")
    var id           : Int,
    var firstName    : String,
    var middleName   : String?,
    var lastName     : String,
    var photoLink    : String?,
    var degree       : String?,
    var degreeAbbrev : String?,
    var rank         : String?,
    var email        : String?,
    var urlId        : String,
    @ColumnInfo("employeeCalendarId")
    var calendarId   : String?,
    var jobPositions : String?,
    var chief        : Boolean?
)