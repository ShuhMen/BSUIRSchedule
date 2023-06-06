package com.maximshuhman.bsuirschedule.DataBase

import android.provider.BaseColumns

object DBContract {


    object Groups {
        const val TABLE_NAME = "groups"
        const val groupID = "groupID"

        //const val type: Int,
        const val name = "groupName"

        //const val facultyId : String?,
        const val facultyAbbrev = "facultyAbbrev"

        //  const val specialityDepartmentEducationFormId : Int?,
        const val specialityName = "specialityName"
        const val specialityAbbrev = "specialityAbbrev"
        const val course = "course"
    }

    object CommonSchedule {
        const val TABLE_NAME = "commonSchedule"
        const val startExamsDate = "startExamsDate"
        const val endExamsDate = "endExamsDate"
        const val startDate = "startDate"
        const val endDate = "endDate"
        const val commonScheduleID = "commonScheduleID"
        const val lastUpdate = "lastUpdate"
        const val lastBuild = "lastBuild"
    }

    object Schedule : BaseColumns {
        const val TABLE_NAME = "schedule"
        const val scheduleID = BaseColumns._ID
        const val inScheduleID = "inScheduleID"
        const val groupID = "groupID"
        const val day_of_week = "day_of_week"
        const val auditories = "auditories"
        const val endLessonTime = "endLessonTime"
        const val lessonTypeAbbrev = "lessonTypeAbbrev"
        const val note = "note"
        const val numSubgroup = "numSubgroup"
        const val startLessonTime = "startLessonTime"
        const val studentGroups = "studentGroups"
        const val subject = "subject"
        const val subjectFullName = "subjectFullName"
        const val weekNumber = "weekNumber"
        const val employeeID = "employeeID"
        const val startLessonDate = "startLessonDate"
        const val endLessonDate = "endLessonDate"
    }

    object finalSchedule : BaseColumns {
        const val TABLE_NAME = "finalSchedule"
        const val scheduleID = BaseColumns._ID
        const val inScheduleID = "inScheduleID"
        const val dayIndex = "dayIndex"
        const val groupID = "groupID"
        const val day_of_week = "day_of_week"
        const val auditories = "auditories"
        const val endLessonTime = "endLessonTime"
        const val lessonTypeAbbrev = "lessonTypeAbbrev"
        const val note = "note"
        const val numSubgroup = "numSubgroup"
        const val startLessonTime = "startLessonTime"
        const val studentGroups = "studentGroups"
        const val subject = "subject"
        const val subjectFullName = "subjectFullName"
        const val weekNumber = "weekNumber"
        const val startLessonDate = "startLessonDate"
        const val endLessonDate = "endLessonDate"
    }

    object Exams : BaseColumns {
        const val TABLE_NAME = "EXAMS"
        const val examID = BaseColumns._ID
        const val dateLesson = "dateLesson"
    }

    object Employees {
        const val TABLE_NAME = "EMPLOYEES"
        const val employeeID = "employeeID"
        const val firstName = "firstName"
        const val middleName = "middleName"
        const val lastName = "lastName"
        const val photoLink = "photoLink"
        const val degree = "degree"
        const val degreeAbbrev = "degreeAbbrev"
        const val rank = "rank"
        const val department = "department"
        const val fio = "fio"
        const val photo = "photo"
        const val urlId = "urlId"
    }

    object Favorites {
        const val TABLE_NAME = "favorites"
        const val groupID = "groupID"
        const val type = "type"
    }

    object PairToEmployers {
        const val TABLE_NAME = "PairToEmployers"
        const val employeeID = "employeeID"
        const val lessonID = "lessonID"
        const val groupID = "groupID"
    }

    object CommonEmployee {
        const val TABLE_NAME = "commonEmployee"
        const val startExamsDate = "startExamsDate"
        const val endExamsDate = "endExamsDate"
        const val startDate = "startDate"
        const val endDate = "endDate"
        const val commonEmployeeID = "commonEmployeeID"
        const val lastUpdate = "lastUpdate"
        const val lastBuild = "lastBuild"
    }

    object EmployeeSchedule : BaseColumns {
        const val TABLE_NAME = "EmployeeSchedule"
        const val scheduleID = BaseColumns._ID
        const val inScheduleID = "inScheduleID"
        const val employeeID = "employeeID"
        const val day_of_week = "day_of_week"
        const val auditories = "auditories"
        const val endLessonTime = "endLessonTime"
        const val lessonTypeAbbrev = "lessonTypeAbbrev"
        const val note = "note"
        const val numSubgroup = "numSubgroup"
        const val startLessonTime = "startLessonTime"
        const val studentGroups = "studentGroups"
        const val subject = "subject"
        const val subjectFullName = "subjectFullName"
        const val weekNumber = "weekNumber"
        const val startLessonDate = "startLessonDate"
        const val endLessonDate = "endLessonDate"
    }

    object EmployeeToPair {
        const val TABLE_NAME = "EmployeesToPair"
        const val employeeID = "employeeID"
        const val lessonID = "lessonID"
        const val groupName = "groupName"
    }

    object finalEmployeeSchedule : BaseColumns {
        const val TABLE_NAME = "finalEmployeeSchedule"
        const val scheduleID = BaseColumns._ID
        const val inScheduleID = "inScheduleID"
        const val employeeID = "employeeID"
        const val dayIndex = "dayIndex"
        const val day_of_week = "day_of_week"
        const val auditories = "auditories"
        const val endLessonTime = "endLessonTime"
        const val lessonTypeAbbrev = "lessonTypeAbbrev"
        const val note = "note"
        const val numSubgroup = "numSubgroup"
        const val startLessonTime = "startLessonTime"
        const val studentGroups = "studentGroups"
        const val subject = "subject"
        const val subjectFullName = "subjectFullName"
        const val weekNumber = "weekNumber"
        const val startLessonDate = "startLessonDate"
        const val endLessonDate = "endLessonDate"
    }

    object EmployeeExams : BaseColumns {
        const val TABLE_NAME = "EMPLOYEEEXAMS"
        const val examID = BaseColumns._ID
        const val dateLesson = "dateLesson"
    }

    object EmployeeToExam {
        const val TABLE_NAME = "EmployeeToExam"
        const val employeeID = "employeeID"
        const val lessonID = "lessonID"
        const val groupName = "groupName"
    }

    object SubgroupSettings {
        const val TABLE_NAME = "SubgroupSettings"
        const val groupID = "groupID"
        const val subGroup = "subGroup"
    }

    object Settings {
        const val TABLE_NAME = "Settings"
        const val openedID = "openedID"
        const val openedType = "openedType"
        const val lastWeekUpdate = "lastWeekUpdate"
        const val week = "week"
        const val widgetID = "widgetID"
        const val widgetOpened = "widgetOpened"
    }

}