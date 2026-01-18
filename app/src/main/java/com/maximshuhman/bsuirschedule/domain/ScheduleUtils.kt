package com.maximshuhman.bsuirschedule.domain

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.maximshuhman.bsuirschedule.AppResult
import com.maximshuhman.bsuirschedule.data.ScheduleSource
import com.maximshuhman.bsuirschedule.data.dto.CommonSchedule
import com.maximshuhman.bsuirschedule.data.dto.Lesson
import com.maximshuhman.bsuirschedule.data.repositories.NetError
import com.maximshuhman.bsuirschedule.data.repositories.SettingsRepository
import com.maximshuhman.bsuirschedule.domain.models.LogicError
import com.maximshuhman.bsuirschedule.domain.models.ReadySchedule
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDay
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDayHeader
import com.maximshuhman.bsuirschedule.domain.models.toLogicError
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

abstract class GetScheduleUseCase(
    private val repository: ScheduleSource,
    private val networkStatusTracker: NetworkStatusTracker,
    private val settingsRepository: SettingsRepository
) {

    fun configureExams(schedule: CommonSchedule): AppResult<List<ScheduleDay>, LogicError> {

        if(schedule.exams == null)
            return AppResult.ApiError(LogicError.Empty)

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val prettyFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.getDefault())

        val listDays = mutableListOf<ScheduleDay>()

        val exams = schedule.exams
            .groupBy { LocalDate.parse(it.dateLesson, formatter) }
            .toSortedMap { o1, o2 ->
                 if (o1.isAfter(o2))
                     1
                 else
                     -1
            }

        exams.forEach { (date, exams) ->

            val lessons = mutableListOf<Lesson>()

            for (lesson in exams) {
                lessons.add(lesson)
            }

            listDays.add(
                ScheduleDay(
                    ScheduleDayHeader(
                        prettyFormatter.format(date)
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    ), exams))
        }

        return AppResult.Success(listDays)
    }

    suspend fun configureSchedule(schedule: CommonSchedule): AppResult<List<ScheduleDay>, LogicError> {

        if(schedule.schedules == null)
            return AppResult.ApiError(LogicError.Empty)

        var week: Int

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        var currentDate =
            if (schedule.startDate != null && LocalDate.now().isBefore(LocalDate.parse(schedule.startDate, formatter))) {
                LocalDate.parse(schedule.startDate, formatter)
            } else {
                LocalDate.now()
            }


        if (networkStatusTracker.getCurrentNetworkStatus() is NetworkStatus.Unavailable) {

            val settings = settingsRepository.settings.first()

            if(settings.week == null){
                return AppResult.ApiError(LogicError.NoInternetConnection)
            }

            val lastUpdateDate = LocalDate.parse(settings.lastWeekUpdate, formatter)

            var diff = ChronoUnit.WEEKS.between(lastUpdateDate, currentDate).toInt()

            if(currentDate.dayOfWeek.value < lastUpdateDate.dayOfWeek.value  )
                diff++

            week = settings.week + diff

            if(week > 4)
                week = week % 4 + 1

        } else {
            when (val weekResponse = repository.getCurrent()) {
                is AppResult.ApiError<NetError> -> {
                    Firebase.crashlytics.log(weekResponse.body::class.java.toString())

                    return AppResult.ApiError(weekResponse.body.toLogicError())
                }
                is AppResult.Success<Int> -> week = weekResponse.data
            }

            settingsRepository.setCurrentWeek(formatter.format(LocalDate.now()) ,week)
        }

        val listDays = mutableListOf<ScheduleDay>()

        val prettyFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.getDefault())

        val endDate = if (schedule.endDate != null) {
            LocalDate.parse(schedule.endDate, formatter)
        } else {
            currentDate.plusWeeks(4)
        }

        if(currentDate.isAfter(endDate))
            return AppResult.ApiError(LogicError.ConfigureError("Занятия закончились"))


        while (!endDate.isBefore(currentDate)) {

            fun makeSchedule(rawList: List<Lesson>) {

                if (rawList.any {
                        it.weekNumber?.contains(week)
                            ?: (currentDate == LocalDate.parse(it.dateLesson, formatter))
                    }) {

                    val lessons = mutableListOf<Lesson>()

                    for (lesson in rawList) {
                        if (
                            lesson.weekNumber?.contains(week) ?: (currentDate == LocalDate.parse(
                                lesson.dateLesson,
                                formatter
                            ))
                        ) {
                            lessons.add(lesson)
                        }
                    }

                    listDays.add(
                        ScheduleDay(
                            ScheduleDayHeader(
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


        return AppResult.Success(listDays)
    }

    suspend fun <Entity: Any> checkSchedule(entity: Entity, schedule: CommonSchedule ): AppResult<ReadySchedule<Entity>, LogicError>{

        if(schedule.schedules == null && schedule.exams == null)
            return AppResult.ApiError(LogicError.Empty)

        if(schedule.schedules == null){
            return when(val configureExams = configureExams(schedule)){
                is AppResult.ApiError -> {
                    configureExams
                }
                is AppResult.Success -> {
                    AppResult.Success(ReadySchedule.ExamsOnly(entity, configureExams.data))
                }
            }

        }

        if(schedule.exams == null){
            return when(val configureResult = configureSchedule(schedule)){
                is AppResult.ApiError -> {
                    configureResult
                }
                is AppResult.Success -> {
                    AppResult.Success(ReadySchedule.ScheduleOnly(entity, configureResult.data))
                }
            }
        }

        val configureResult = configureSchedule(schedule)
        val configureExams = configureExams(schedule)

        when (configureResult) {
            is AppResult.ApiError if configureExams is AppResult.ApiError -> {
                return configureResult
            }

            is AppResult.ApiError if configureExams is AppResult.Success -> {
                return AppResult.Success(ReadySchedule.ExamsOnly(entity, configureExams.data, configureResult.body))
            }

            is AppResult.Success if configureExams is AppResult.ApiError -> {
                return AppResult.Success(ReadySchedule.ScheduleOnly(entity, configureResult.data, configureExams.body))
            }

            else -> {
                return AppResult.Success(ReadySchedule.FullSchedule(entity, (configureResult as AppResult.Success).data, (configureExams as AppResult.Success).data))
            }
        }



    }

}