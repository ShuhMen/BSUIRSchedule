package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.maximshuhman.bsuirschedule.data.dto.Favorite
import com.maximshuhman.bsuirschedule.data.dto.Group

@Dao
interface GroupsDAO {
    @Query("SELECT * FROM `Group`")
    suspend fun getAll(): List<Group>

    @Query("SELECT * FROM `Group` WHERE groupID = :groupId")
    suspend fun getById(groupId: Int): Group?

    @Query("SELECT id FROM favorites WHERE type = 0")
    suspend fun getFavoriteGroupIds(): List<Int>

    @Upsert
    suspend fun upsertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Upsert
    suspend fun insertAll(groups: List<Group>)
}