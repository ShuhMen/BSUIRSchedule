package com.maximshuhman.bsuirschedule.domain.models

import com.maximshuhman.bsuirschedule.data.dto.Lesson

/*data class ReadySchedule<T>(
    val entity: T,
    val schedule: List<ScheduleDay>,
    val exams: List<ScheduleDay>
)*/

sealed class ReadySchedule<T>(val entity: T) {
    class ScheduleOnly<T>(entity: T, val schedule: List<ScheduleDay>, val examsError: LogicError? = null) : ReadySchedule<T>(entity)
    class ExamsOnly<T>(entity: T, val exams: List<ScheduleDay>, val scheduleError: LogicError? = null) : ReadySchedule<T>(entity)
    class FullSchedule<T>(entity: T, val schedule: List<ScheduleDay>, val exams: List<ScheduleDay>) : ReadySchedule<T>(entity)
    //class ErroredSchedule<T>(entity: T, val schedule: AppResult<List<ScheduleDay>, LogicError>, val exams: AppResult<List<ScheduleDay>, LogicError>) : ReadySchedule<T>(entity)
}

data class ScheduleDay(
    val header: ScheduleDayHeader,
    val list: List<Lesson>
)

@JvmInline
value class ScheduleDayHeader(
    val name: String
)