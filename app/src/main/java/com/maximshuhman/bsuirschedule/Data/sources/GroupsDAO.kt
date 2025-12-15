package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.entities.FavoriteEntity

@Dao
interface GroupsDAO {
    @Query("SELECT * FROM `Group`")
    suspend fun getAll(): List<Group>

    @Query("SELECT * FROM `Group` WHERE groupID = :groupId")
    suspend fun getById(groupId: Int): Group?

    @Query("SELECT * FROM `Group` WHERE name = :name")
    suspend fun getByName(name: String): Group?

    @Query("SELECT id FROM favorites WHERE type = 0")
    suspend fun getFavoriteIds(): List<Int>

    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Upsert
    suspend fun insertAll(groups: List<Group>)
}