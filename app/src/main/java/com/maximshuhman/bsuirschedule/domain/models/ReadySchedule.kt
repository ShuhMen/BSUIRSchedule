package com.maximshuhman.bsuirschedule.domain.models

import Lesson
import com.maximshuhman.bsuirschedule.data.dto.Group

data class ReadySchedule (
    val group: Group,
    val schedule: List<GroupDay>,
    )

data class GroupDay(
    val header: GroupDayHeader,
    val list: List<Lesson>
)


data class GroupDayHeader(
    val name: String
)