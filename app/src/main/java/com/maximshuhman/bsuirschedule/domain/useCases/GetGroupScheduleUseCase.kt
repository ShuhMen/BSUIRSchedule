package com.maximshuhman.bsuirschedule.domain.useCases

import CommonSchedule
import Lesson
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.repositories.NetError
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.domain.models.GroupDay
import com.maximshuhman.bsuirschedule.domain.models.GroupDayHeader
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.models.ReadySchedule
import com.maximshuhman.bsuirschedule.domain.models.toLogicError
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class GetGroupScheduleUseCase @Inject constructor(
    private val repository: ScheduleSource,
    private val groupsSource: GroupsDAO,
    //private val scheduleDAO: ScheduleDAO,

) {
    suspend operator fun invoke(groupId: Int): AppResult<ReadySchedule, LogicError> {

        val group = groupsSource.getById(groupId)

        if (group == null)
            return AppResult.ApiError(LogicError.Empty)

        group.isFavorite = groupsSource.getFavoriteGroupIds().contains(group.id)

        val result = repository.getGroupSchedule(group.name)

        if (result is AppResult.ApiError<NetError>)
            return AppResult.ApiError(result.body.toLogicError())

        val schedule = (result as AppResult.Success<CommonSchedule>).data

        var week: Int
        val weekResponse = repository.getCurrent()

        when (weekResponse) {
            is AppResult.ApiError<NetError> -> return AppResult.ApiError(weekResponse.body.toLogicError())
            is AppResult.Success<Int> -> week = weekResponse.data
        }

        val listDays = mutableListOf<GroupDay>()

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val prettyFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.getDefault())

        val endDate = if(schedule.endDate != null){
            LocalDate.parse(schedule.endDate, formatter)
        }else{
            LocalDate.now().plusWeeks(4)
        }

        var currentDate = LocalDate.now()

        /*if(currentDate.dayOfWeek == DayOfWeek.SUNDAY)
            week = (week + 3) % 4*/

        while (currentDate.isBefore(endDate)) {

            fun makeSchedule(rawList: List<Lesson>) {

                if (rawList.any {
                    it.weekNumber?.contains(week) ?: (currentDate == LocalDate.parse(it.dateLesson, formatter))
                }) {

                    val lessons = mutableListOf<Lesson>()

                    for (lesson in rawList) {
                        if (
                            lesson.weekNumber?.contains(week) ?: (currentDate == LocalDate.parse(lesson.dateLesson, formatter))
                        ) {
                            lessons.add(lesson)
                        }
                    }

                    listDays.add(GroupDay(GroupDayHeader(
                        prettyFormatter.format(currentDate)
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    ), lessons))
                }
            }

            when (currentDate.dayOfWeek) {
                DayOfWeek.MONDAY -> {
                    makeSchedule(schedule.schedules.Monday)
                }

                DayOfWeek.TUESDAY -> {
                    makeSchedule(schedule.schedules.Tuesday)
                }

                DayOfWeek.WEDNESDAY -> {
                    makeSchedule(schedule.schedules.Wednesday)
                }

                DayOfWeek.THURSDAY -> {
                    makeSchedule(schedule.schedules.Thursday)
                }

                DayOfWeek.FRIDAY -> {
                    makeSchedule(schedule.schedules.Friday)
                }

                DayOfWeek.SATURDAY -> {
                    makeSchedule(schedule.schedules.Saturday)
                }

                DayOfWeek.SUNDAY -> {
                    week = (week % 4) + 1
                }
            }

            currentDate = currentDate.plusDays(1)
        }

        val readySchedule: ReadySchedule =
            ReadySchedule(group, listDays)

        return AppResult.Success(readySchedule)
    }
}