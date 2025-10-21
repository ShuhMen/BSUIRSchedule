package com.maximshuhman.bsuirschedule.domain.models

import Lesson
import StudentGroupDto

data class ReadySchedule (
    val groupDto: StudentGroupDto,
    val schedule: List<GroupDay>,
    )

data class GroupDay(
    val header: GroupDayHeader,
    val list: List<Lesson>
)


data class GroupDayHeader(
    val name: String
)