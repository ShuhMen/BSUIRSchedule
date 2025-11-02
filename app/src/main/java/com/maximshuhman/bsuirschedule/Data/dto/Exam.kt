package com.maximshuhman.bsuirschedule.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val auditories       : ArrayList<String>?,
    val endLessonTime: String?,
    val lessonTypeAbbrev: String?,
    val note: String?,
    val numSubgroup: Int?,
    val startLessonTime: String?,
    val subject: String?,
    val subjectFullName: String?,
    val weekNumber       : ArrayList<Int>?,
    val employee: Employee,
    val startLessonDate: String?,
    val endLessonDate: String?,
    val dateLesson: String?
)
