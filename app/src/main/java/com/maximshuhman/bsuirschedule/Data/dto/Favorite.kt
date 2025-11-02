package com.maximshuhman.bsuirschedule.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite (
    @PrimaryKey
    val id: Int,
    val type: Int
)