package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.maximshuhman.bsuirschedule.data.entities.SubgroupEntity

@Dao
interface SubgroupDAO {

    @Query("SELECT * FROM SubgroupEntity WHERE id = :id")
    suspend fun getSubgroup(id: Int): SubgroupEntity?

    @Delete
    suspend fun delete(subgroup: SubgroupEntity)

    @Upsert
    suspend fun insert(subgroup: SubgroupEntity)
}