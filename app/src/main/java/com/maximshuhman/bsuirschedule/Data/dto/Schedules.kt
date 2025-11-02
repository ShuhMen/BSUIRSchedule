package com.maximshuhman.bsuirschedule.data.dto

import Lesson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Schedules (

  @SerialName("Понедельник" ) var Monday      : ArrayList<Lesson>    = arrayListOf(),
  @SerialName("Вторник"     ) var Tuesday     : ArrayList<Lesson>     = arrayListOf(),
  @SerialName("Среда"       ) var Wednesday   : ArrayList<Lesson>     = arrayListOf(),
  @SerialName("Четверг"     ) var Thursday    : ArrayList<Lesson>     = arrayListOf(),
  @SerialName("Пятница"     ) var Friday      : ArrayList<Lesson>     = arrayListOf(),
  @SerialName("Суббота"     ) var Saturday    : ArrayList<Lesson>     = arrayListOf()

)