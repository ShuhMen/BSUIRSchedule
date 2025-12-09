package com.maximshuhman.bsuirschedule.data.repositories

import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.SourceError
import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.dto.LastUpdateDate
import com.maximshuhman.bsuirschedule.data.dto.Schedules
import com.maximshuhman.bsuirschedule.data.dto.toEmployeeLesson
import com.maximshuhman.bsuirschedule.data.dto.toGroupLesson
import com.maximshuhman.bsuirschedule.data.entities.toEmployeeEntity
import com.maximshuhman.bsuirschedule.data.entities.toEmployeeLesson
import com.maximshuhman.bsuirschedule.data.entities.toGroupEntity
import com.maximshuhman.bsuirschedule.data.entities.toGroupLesson
import com.maximshuhman.bsuirschedule.data.sources.EmployeeDAO
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.data.sources.ScheduleDAO
import java.time.DayOfWeek
import javax.inject.Inject


sealed class DBError : SourceError() {
    object NoData: DBError()
}

class ScheduleDataBaseSourceImpl @Inject constructor(
    private val scheduleDAO: ScheduleDAO,
    private val groupsDAO: GroupsDAO,
    private val employeeDAO: EmployeeDAO,
) : ScheduleSource{


    suspend fun setGroupSchedule(schedule: CommonSchedule) {

        scheduleDAO.setCommonSchedule(schedule.toGroupEntity())

        scheduleDAO.deleteGroupLessons(schedule.studentGroupDto!!.id)

        scheduleDAO.setLessons(buildList {
            addAll(schedule.schedules!!.Monday.map { it.toGroupLesson(schedule.studentGroupDto.id, DayOfWeek.MONDAY) })
            addAll(schedule.schedules.Tuesday.map { it.toGroupLesson(schedule.studentGroupDto.id, DayOfWeek.TUESDAY) })
            addAll(schedule.schedules.Wednesday.map { it.toGroupLesson(schedule.studentGroupDto.id, DayOfWeek.WEDNESDAY) })
            addAll(schedule.schedules.Thursday.map { it.toGroupLesson(schedule.studentGroupDto.id, DayOfWeek.THURSDAY) })
            addAll(schedule.schedules.Friday.map { it.toGroupLesson(schedule.studentGroupDto.id, DayOfWeek.FRIDAY) })
            addAll(schedule.schedules.Saturday.map { it.toGroupLesson(schedule.studentGroupDto.id, DayOfWeek.SATURDAY) })
            addAll(schedule.exams?.map { it.toGroupLesson(schedule.studentGroupDto.id, DayOfWeek.MONDAY, true) }
                ?: listOf())
        }
        )
    }

    override suspend fun getGroupSchedule(grNum: String): AppResult<CommonSchedule, SourceError> {

        val group = groupsDAO.getByName(grNum)

        if(group == null){
            return AppResult.ApiError(SourceError.UnknownError(IllegalArgumentException("Группа отсутствует в БД")))
        }

        val commonSchedule = scheduleDAO.getGroupSchedule(group.id)

        if(commonSchedule == null)
            return AppResult.ApiError(DBError.NoData)

        val lessons = scheduleDAO.getGroupLessons(group.id)

        val days = lessons.asSequence()
            .filter{!it.isExam}
            .groupBy{ it.dayOfWeek }

        val exams = lessons.filter{it.isExam}


        return AppResult.Success(CommonSchedule(
            commonSchedule.startDate,
            commonSchedule.endDate,
            commonSchedule.startExamsDate,
            commonSchedule.endExamsDate,
            commonSchedule.employeeDto,
            commonSchedule.studentGroupDto,
            Schedules(
                days[DayOfWeek.MONDAY]?.map { it.toGroupLesson() } ?: listOf(),
                days[DayOfWeek.TUESDAY]?.map { it.toGroupLesson() } ?: listOf(),
                days[DayOfWeek.WEDNESDAY]?.map { it.toGroupLesson() } ?: listOf(),
                days[DayOfWeek.THURSDAY]?.map { it.toGroupLesson() } ?: listOf(),
                days[DayOfWeek.FRIDAY]?.map { it.toGroupLesson() } ?: listOf(),
                days[DayOfWeek.SATURDAY]?.map { it.toGroupLesson() } ?: listOf(),
            ),
            commonSchedule.currentTerm,
            if(exams.isEmpty()) null else exams.map { it.toGroupLesson() },
            commonSchedule.currentPeriod,
        )
        )


    }

    suspend fun setEmployeeSchedule(schedule: CommonSchedule) {

        scheduleDAO.setCommonSchedule(schedule.toEmployeeEntity())

        scheduleDAO.deleteEmployeeLessons(schedule.employeeDto!!.id)

        scheduleDAO.setLessons(buildList {
            addAll(schedule.schedules!!.Monday.map { it.toEmployeeLesson(schedule.employeeDto.id, DayOfWeek.MONDAY) })
            addAll(schedule.schedules.Tuesday.map { it.toEmployeeLesson(schedule.employeeDto.id, DayOfWeek.TUESDAY) })
            addAll(schedule.schedules.Wednesday.map { it.toEmployeeLesson(schedule.employeeDto.id, DayOfWeek.WEDNESDAY) })
            addAll(schedule.schedules.Thursday.map { it.toEmployeeLesson(schedule.employeeDto.id, DayOfWeek.THURSDAY) })
            addAll(schedule.schedules.Friday.map { it.toEmployeeLesson(schedule.employeeDto.id, DayOfWeek.FRIDAY) })
            addAll(schedule.schedules.Saturday.map { it.toEmployeeLesson(schedule.employeeDto.id, DayOfWeek.SATURDAY) })
            addAll(schedule.exams?.map { it.toEmployeeLesson(schedule.employeeDto.id, DayOfWeek.MONDAY, true) }
                ?: listOf())
        }
        )
    }


    override suspend fun getEmployeeSchedule(employeeUrlId: String): AppResult<CommonSchedule, SourceError> {
        val employee = employeeDAO.getByName(employeeUrlId)

        if(employee == null){
            return AppResult.ApiError(SourceError.UnknownError(IllegalArgumentException("Группа отсутствует в БД")))
        }

        val commonSchedule = scheduleDAO.getEmployeeSchedule(employee.id)

        if(commonSchedule == null)
            return AppResult.ApiError(DBError.NoData)

        val lessons = scheduleDAO.getEmployeeLessons(employee.id)

        val days = lessons.asSequence()
            .filter{!it.isExam}
            .groupBy{ it.dayOfWeek }

        val exams = lessons.filter{it.isExam}


        return AppResult.Success(CommonSchedule(
            commonSchedule.startDate,
            commonSchedule.endDate,
            commonSchedule.startExamsDate,
            commonSchedule.endExamsDate,
            commonSchedule.employeeDto,
            commonSchedule.studentGroupDto,
            Schedules(
                days[DayOfWeek.MONDAY]?.map { it.toEmployeeLesson() } ?: listOf(),
                days[DayOfWeek.TUESDAY]?.map { it.toEmployeeLesson() } ?: listOf(),
                days[DayOfWeek.WEDNESDAY]?.map { it.toEmployeeLesson() } ?: listOf(),
                days[DayOfWeek.THURSDAY]?.map { it.toEmployeeLesson() } ?: listOf(),
                days[DayOfWeek.FRIDAY]?.map { it.toEmployeeLesson() } ?: listOf(),
                days[DayOfWeek.SATURDAY]?.map { it.toEmployeeLesson() } ?: listOf(),
            ),
            commonSchedule.currentTerm,
            if(exams.isEmpty()) null else exams.map { it.toEmployeeLesson() },
            commonSchedule.currentPeriod,
        )
        )    }

    override suspend fun getCurrent(): AppResult<Int, NetError> {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupsList(): AppResult<List<Group>, NetError> {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupScheduleLastUpdate(groupNumber: String): AppResult<LastUpdateDate, NetError> {
        TODO("Not yet implemented")
    }

    override suspend fun getEmployeesList(): AppResult<List<Employee>, NetError> {
        TODO("Not yet implemented")
    }

}