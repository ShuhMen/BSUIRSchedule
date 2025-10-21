package com.maximshuhman.bsuirschedule.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    var id                 : Int              ,
    val firstName          : String           ,
    val lastName           : String           ,
    val middleName         : String?          ,
    val degree             : String           ,
    val rank               : String?          ,
    val photoLink          : String           ,
    val calendarId         : String           ,
    val urlId              : String           ,
)