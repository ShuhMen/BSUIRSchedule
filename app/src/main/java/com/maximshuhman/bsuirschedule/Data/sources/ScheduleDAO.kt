package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.maximshuhman.bsuirschedule.data.entities.CommonScheduleEntity
import com.maximshuhman.bsuirschedule.data.entities.LessonTable


@Dao
abstract class ScheduleDAO {

    @Upsert
    abstract suspend fun setCommonSchedule(schedule: CommonScheduleEntity)

    @Query("SELECT * FROM `common_schedule` WHERE groupId = :groupId")
    abstract suspend fun getGroupSchedule(groupId: Int) : CommonScheduleEntity?

    @Query("SELECT * FROM `common_schedule` WHERE employeeId = :employeeId")
    abstract suspend fun getEmployeeSchedule(employeeId: Int) : CommonScheduleEntity?

    @Upsert
    abstract suspend fun setLessons( lessons: List<LessonTable>)

    @Query("DELETE FROM lessons WHERE groupId = :groupId")
    abstract suspend fun deleteGroupLessons(groupId: Int)

    @Query("DELETE FROM lessons WHERE employeeId = :employeeId")
    abstract suspend fun deleteEmployeeLessons(employeeId: Int)

    @Query("SELECT * FROM `lessons` WHERE groupId = :id")
    abstract fun getGroupLessons(id: Int): List<LessonTable>

    @Query("SELECT * FROM `lessons` WHERE employeeId = :id")
    abstract fun getEmployeeLessons(id: Int): List<LessonTable>



    /* @Transaction
     @Query("SELECT * FROM `LessonTable` WHERE groupId = :groupId")
     abstract suspend fun getLessons(groupId: Int): List<LessonEntity>

     @Query("SELECT * FROM `CommonScheduleEntity` WHERE groupId = :groupId")
     abstract suspend fun getGroupSchedule(groupId: Int): CommonScheduleEntity



     @Upsert
     abstract suspend fun setEmployee(lesson: Employee)

     @Upsert
     abstract suspend fun setLessonEmployeeRef(lesson: LessonEmployeeRef)
 */
}