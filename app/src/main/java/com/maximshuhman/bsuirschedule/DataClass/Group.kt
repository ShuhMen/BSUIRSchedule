package com.maximshuhman.bsuirschedule.DataClass

data class Group(
    val type: Int,
    val name : String?,
    val facultyId : String?,
    val facultyName : String?,
    val specialityDepartmentEducationFormId : Int?,
    val specialityName : String?,
    val course : Int?,
    val id : Int?,
    val calendarId : String?,
    val educationDegree: Int?
)

