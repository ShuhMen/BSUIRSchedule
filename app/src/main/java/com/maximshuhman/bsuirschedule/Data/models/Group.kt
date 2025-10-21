package com.maximshuhman.bsuirschedule.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Group(
    @PrimaryKey
    @ColumnInfo("groupID")
    val id                                  : Int?,
    val name                                : String,
    val facultyId                           : Int?,
    val facultyAbbrev                       : String?,
    val facultyName                         : String?,
    val specialityDepartmentEducationFormId : Int?,
    val specialityName                      : String?,
    val specialityAbbrev                    : String?,
    val course                              : Int?,
    val calendarId                          : String?,
    val educationDegree                     : Int?
)