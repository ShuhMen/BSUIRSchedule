package com.maximshuhman.bsuirschedule.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity
@Serializable
data class Group(
    @PrimaryKey
    @ColumnInfo("groupID")
    val id                                  : Int,
    val name                                : String,
    val facultyId                           : Int?,
    val facultyAbbrev                       : String?,
    val facultyName                         : String?,
    val specialityDepartmentEducationFormId : Int?,
    val specialityName                      : String?,
    val specialityAbbrev                    : String?,
    val course                              : Int?,
    val calendarId                          : String?,
    val educationDegree                     : Int?,
){
    @Transient
    @Ignore
    var isFavorite                          : Boolean = false
}