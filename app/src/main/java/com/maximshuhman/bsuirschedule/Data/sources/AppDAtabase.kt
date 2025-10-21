package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maximshuhman.bsuirschedule.data.models.Group

@Database(entities = [Group::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun groupsDAO(): GroupsDAO
}