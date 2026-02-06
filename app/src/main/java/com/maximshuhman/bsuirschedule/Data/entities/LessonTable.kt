package com.maximshuhman.bsuirschedule.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Lesson
import com.maximshuhman.bsuirschedule.data.dto.StudentGroups
import java.time.DayOfWeek

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            CommonScheduleEntity::class,
            ["groupId", "employeeId"],
            ["groupId", "employeeId"]
        )
    ],
    indices = [Index("groupId", "employeeId")]
)
data class LessonTable(
    val groupId          : Int,
    val employeeId       : Int,
    val dayOfWeek        : DayOfWeek,
    val isExam           : Boolean = false,

    val auditories       : List<String>?,
    val endLessonTime    : String,
    val lessonTypeAbbrev : String?,
    val note             : String?,
    val numSubgroup      : Int,
    val startLessonTime  : String,
    val studentGroups    : List<StudentGroups>?,
    val employees        : List<Employee>?,
    val subject          : String?,
    val subjectFullName  : String?,
    val weekNumber       : List<Int>?,
    val dateLesson       : String?,
    val startLessonDate  : String?,
    val endLessonDate    : String?,
    val announcement     : Boolean?,
    val split            : Boolean?,

    @PrimaryKey(autoGenerate = true)
    val id               : Int = 0,
)

fun Lesson.toGroupLesson(groupId: Int, dayOfWeek: DayOfWeek, isExam: Boolean = false) : LessonTable{
    return LessonTable(
        groupId,
        -1,
        dayOfWeek,
        isExam,
        auditories,
        endLessonTime,
        lessonTypeAbbrev,
        note,
        numSubgroup,
        startLessonTime,
        studentGroups,
        employees,
        subject,
        subjectFullName,
        weekNumber,
        dateLesson,
        startLessonDate,
        endLessonDate,
        announcement,
        split
    )
    
}

fun Lesson.toEmployeeLesson(employeeId: Int, dayOfWeek: DayOfWeek, isExam: Boolean = false) : LessonTable{
    return LessonTable(
        -1,
        employeeId,
        dayOfWeek,
        isExam,
        auditories,
        endLessonTime,
        lessonTypeAbbrev,
        note,
        numSubgroup,
        startLessonTime,
        studentGroups,
        employees,
        subject,
        subjectFullName,
        weekNumber,
        dateLesson,
        startLessonDate,
        endLessonDate,
        announcement,
        split
    )

}