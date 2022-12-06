package com.maximshuhman.bsuirschedule.DataBase

import android.provider.BaseColumns

object DBContract  {


    object Groups{
        const val TABLE_NAME = "groups"
        const val groupID = "groupID"
         //const val type: Int,
        const val name = "groupName"
         //const val facultyId : String?,
         const val facultyAbbrev  = "facultyAbbrev"
       //  const val specialityDepartmentEducationFormId : Int?,
        const val specialityName = "specialityName"
        const val specialityAbbrev = "specialityAbbrev"
        const val course = "course"
    }

    object CommonSchedule{
        const val TABLE_NAME = "commonSchedule"
        const val startExamsDate = "startExamsDate"
        const val endExamsDate = "endExamsDate"
        const val startDate = "startDate"
        const val endDate = "endDate"
        const val commonScheduleID = "commonScheduleID"
    }

    object Schedule: BaseColumns{
        const val TABLE_NAME = "schedule"
        const val scheduleID = BaseColumns._ID
        const val groupID =      "groupID"
        const val day_of_week = "day_of_week"
        const val auditories       = "auditories"
        const val endLessonTime    = "endLessonTime"
        const val lessonTypeAbbrev = "lessonTypeAbbrev"
        const val note             = "note"
        const val numSubgroup      = "numSubgroup"
        const val startLessonTime  = "startLessonTime"
        const val studentGroups    = "studentGroups"
        const val subject          = "subject"
        const val subjectFullName  = "subjectFullName"
        const val weekNumber       = "weekNumber"
        const val employeeID       = "employeeID"
        const val startLessonDate  = "startLessonDate"
        const val endLessonDate    = "endLessonDate"
    }

    object Employees{
        const val TABLE_NAME = "EMPLOYEES"
        const val employeeID   = "employeeID"
        const val firstName    = "firstName"
        const val middleName   = "middleName"
        const val lastName     = "lastName"
        const val photoLink    = "photoLink"
        const val degree       = "degree"
        const val degreeAbbrev = "degreeAbbrev"
        const val rank         = "rank"
        const val department   = "department"
        const val fio = "fio"
        const val photo = "photo"
    }

    object Favorites{
        const val TABLE_NAME = "favorites"
        const val groupID = "groupID"
    }


}