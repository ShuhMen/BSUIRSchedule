package com.maximshuhman.bsuirschedule.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity (
    @PrimaryKey
    val id: Int,
    val type: Int
)