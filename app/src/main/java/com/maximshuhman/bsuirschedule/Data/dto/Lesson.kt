package com.maximshuhman.bsuirschedule.data.dto

import androidx.compose.ui.graphics.Color
import com.maximshuhman.bsuirschedule.data.entities.LessonTable
import com.maximshuhman.bsuirschedule.ui.theme.Blue
import com.maximshuhman.bsuirschedule.ui.theme.Labaratory
import com.maximshuhman.bsuirschedule.ui.theme.Lecture
import com.maximshuhman.bsuirschedule.ui.theme.Practic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Lesson(
    val auditories       : List<String>?,
    val endLessonTime    : String,
    val lessonTypeAbbrev : String?,
    val note             : String?,
    val numSubgroup      : Int,
    val startLessonTime  : String,
    val studentGroups    : List<StudentGroups>?,
    val subject          : String?,
    val subjectFullName  : String?,
    val weekNumber       : List<Int>?,
    val employees        : List<Employee>?,
    val dateLesson       : String?,
    val startLessonDate  : String?,
    val endLessonDate    : String?,
    val announcement     : Boolean?,
    val split            : Boolean?,

    @Transient val groupId          : Int = -1,
    @Transient val employeeId       : Int = -1,
    /**Это занятие преподавателя или группы**/
    @Transient val lessonType       : LessonType = LessonType.GROUP,

    ){

    fun getLessonColor(): Color {
        return  when (this.lessonTypeAbbrev) {
            "Экзамен", "ЛР", "Зачет" -> Labaratory

            "Консультация", "ПЗ", "УПз" -> Practic

            "УЛк", "ЛК" -> Lecture
            else -> if (this.announcement == true) Blue else Color.White
        }
    }

}


enum class LessonType(){
    GROUP,
    EMPLOYEE
}


fun LessonTable.toGroupLesson(): Lesson {
    return this.toLesson(groupId =  groupId, lessonType = LessonType.GROUP)
}

fun LessonTable.toEmployeeLesson(): Lesson {
    return this.toLesson(employeeId = employeeId, lessonType = LessonType.EMPLOYEE)
}

private inline fun LessonTable.toLesson(groupId: Int = -1, employeeId: Int = -1, lessonType: LessonType): Lesson {
    return Lesson(
        auditories = this.auditories,
        endLessonTime = this.endLessonTime,
        lessonTypeAbbrev = this.lessonTypeAbbrev,
        note = this.note,
        numSubgroup = this.numSubgroup,
        startLessonTime = this.startLessonTime,
        studentGroups = this.studentGroups,
        subject = this.subject,
        subjectFullName = this.subjectFullName,
        weekNumber = this.weekNumber,
        employees = this.employees ?: emptyList(),
        dateLesson = this.dateLesson,
        startLessonDate = this.startLessonDate,
        endLessonDate = this.endLessonDate,
        announcement = this.announcement,
        split = this.split,
        groupId = groupId,
        employeeId = employeeId,
        lessonType = lessonType
    )
}