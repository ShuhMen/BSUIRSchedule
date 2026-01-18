package com.maximshuhman.bsuirschedule.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.maximshuhman.bsuirschedule.data.dto.Group

@Entity(foreignKeys = [
    ForeignKey(
        entity = Group::class,
        parentColumns = ["groupID"],
        childColumns = ["id"],
        onDelete = CASCADE
    )
])
data class SubgroupEntity (
    @PrimaryKey
    val id: Int,
    val subgroup: Int
)