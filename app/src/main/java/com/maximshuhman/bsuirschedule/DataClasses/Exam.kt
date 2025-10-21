package com.maximshuhman.bsuirschedule.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val auditories: String,
    val endLessonTime: String?,
    val lessonTypeAbbrev: String?,
    val note: String?,
    val numSubgroup: Int?,
    val startLessonTime: String?,
    val subject: String?,
    val subjectFullName: String?,
    val weekNumber: String,
    val employee: Employee,
    val startLessonDate: String?,
    val endLessonDate: String?,
    val dateLesson: String?
)
