package com.maximshuhman.bsuirschedule.DataClasses

data class Group(
    var type: Int,
    var name: String?,
    val facultyAbbrev: String?,
    val specialityName: String?,
    val specialityAbbrev: String?,
    val course: Int?,
    val id: Int?
)

