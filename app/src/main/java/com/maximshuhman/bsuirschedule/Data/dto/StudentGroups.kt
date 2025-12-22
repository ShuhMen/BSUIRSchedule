package com.maximshuhman.bsuirschedule.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentGroups(
    val specialityName      : String?,
    val specialityCode      : String?,
    val numberOfStudents    : Int?,
    val name                : String?,
    val educationDegree     : Int?
)