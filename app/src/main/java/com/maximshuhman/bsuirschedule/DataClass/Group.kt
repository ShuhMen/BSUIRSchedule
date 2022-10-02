package com.maximshuhman.bsuirschedule.DataClass

data class Group(
    val type: Int,
    val name : String?,
    val facultyId : String?,
    val facultyAbbrev : String?,
    val specialityDepartmentEducationFormId : Int?,
    val specialityName : String?,
    val specialityAbbrev: String?,
    val course : Int?,
    val id : Int?,
    val calendarId : String?,
    val educationDegree: Int?
)
