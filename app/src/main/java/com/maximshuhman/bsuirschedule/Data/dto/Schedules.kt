package com.maximshuhman.bsuirschedule.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Schedules (

  @SerialName("Понедельник" ) var Monday      : List<Lesson>     = listOf(),
  @SerialName("Вторник"     ) var Tuesday     : List<Lesson>     = listOf(),
  @SerialName("Среда"       ) var Wednesday   : List<Lesson>     = listOf(),
  @SerialName("Четверг"     ) var Thursday    : List<Lesson>     = listOf(),
  @SerialName("Пятница"     ) var Friday      : List<Lesson>     = listOf(),
  @SerialName("Суббота"     ) var Saturday    : List<Lesson>     = listOf()

)