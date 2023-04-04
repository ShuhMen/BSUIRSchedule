package com.maximshuhman.bsuirschedule.DataClasses

data class EmployeeLesson(
    var inLessonID: Int,
    var day_of_week: Int,
    var auditories: String,
    var endLessonTime: String?,
    var lessonTypeAbbrev: String?,
    var note: String?,
    var numSubgroup: Int?,
    var startLessonTime: String?,
    var subject: String?,
    var subjectFullName: String?,
    var weekNumber: String,
    var groups: MutableList<Group>,
    var startLessonDate: String?,
    var endLessonDate: String?,
    var dateLesson: String?
)
