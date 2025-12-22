package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maximshuhman.bsuirschedule.data.converters.EmployeeConverter
import com.maximshuhman.bsuirschedule.data.converters.StudentGroupsConverter
import com.maximshuhman.bsuirschedule.data.converters.TypesConverters
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.entities.CommonScheduleEntity
import com.maximshuhman.bsuirschedule.data.entities.FavoriteEntity
import com.maximshuhman.bsuirschedule.data.entities.LessonTable
import com.maximshuhman.bsuirschedule.data.entities.Settings
import com.maximshuhman.bsuirschedule.data.entities.SubgroupEntity

@Database(
    entities = [
        Group::class,
        FavoriteEntity::class,
        CommonScheduleEntity::class,
        LessonTable::class,
        Settings::class,
        Employee::class,
        SubgroupEntity::class
               ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
    ],
    version = 7
)
@TypeConverters(
    StudentGroupsConverter::class,
    TypesConverters::class,
    EmployeeConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupsDAO(): GroupsDAO
    abstract fun subgroupDAO(): SubgroupDAO
    abstract fun employeeDAO(): EmployeeDAO
    abstract fun scheduleDAO(): ScheduleDAO
    abstract fun settingsDAO(): SettingsDAO
}