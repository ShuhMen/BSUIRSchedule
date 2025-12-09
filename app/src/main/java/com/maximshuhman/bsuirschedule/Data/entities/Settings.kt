package com.maximshuhman.bsuirschedule.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "settings_table")
data class Settings(
   val lastOpenedID: Int? = null,
   val openedType: Int? = null,
   val lastWeekUpdate: String? = null,
   val week: Int? = null,
   val widgetID: Int? = null,
   val widgetOpened: Int? = null,
   @PrimaryKey
   val id: Int = 1
)