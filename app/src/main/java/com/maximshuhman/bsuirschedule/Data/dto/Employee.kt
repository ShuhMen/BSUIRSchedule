package com.maximshuhman.bsuirschedule.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity
@Serializable
data class Employee(
    @PrimaryKey
    @ColumnInfo("employeeID")
    var id                 : Int              ,
    val firstName          : String           ,
    val lastName           : String           ,
    val middleName         : String?          ,
    val degree             : String?           ,
    val rank               : String?          ,
    val photoLink          : String?           ,
    val calendarId         : String?           ,
    val urlId              : String           ,
){
    @Transient
    @Ignore
    var isFavorite: Boolean = false

    @Transient
    @Ignore
    val fio = "$lastName $firstName ${if (!middleName.isNullOrBlank()) middleName else ""}"
}
