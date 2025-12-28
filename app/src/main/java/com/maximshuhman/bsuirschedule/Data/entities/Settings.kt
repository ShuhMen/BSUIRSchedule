package com.maximshuhman.bsuirschedule.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maximshuhman.bsuirschedule.LauncherIcons


@Entity(tableName = "settings_table")
data class Settings(
    val lastOpenedID: Int? = null,
    val openedType: Int? = null,
    val lastWeekUpdate: String? = null,
    val week: Int? = null,
    val widgetID: Int? = null,
    val widgetOpened: Int? = null,
    @ColumnInfo(defaultValue = "DefaultIcon") val iconInstalled: LauncherIcons = LauncherIcons.DefaultIcon,
    @PrimaryKey
   val id: Int = 1
)