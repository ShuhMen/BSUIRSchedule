package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.maximshuhman.bsuirschedule.data.dto.FavoriteEntity
import com.maximshuhman.bsuirschedule.data.dto.Group

@Database(
    entities = [Group::class, FavoriteEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ], version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupsDAO(): GroupsDAO
}