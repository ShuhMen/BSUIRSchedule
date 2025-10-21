package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.maximshuhman.bsuirschedule.data.models.Group

@Dao
interface GroupsDAO {
    @Query("SELECT * FROM `Group`")
    fun getAll(): List<Group>

    @Query("SELECT * FROM `Group` WHERE groupID = :groupId")
    fun loadAllByIds(groupId: Int): List<Group>

    @Upsert
    fun insertAll(groups: List<Group>)

    @Delete
    fun delete(group: Group)
}