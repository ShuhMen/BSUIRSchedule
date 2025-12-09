package com.maximshuhman.bsuirschedule.data.sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.FavoriteEntity

@Dao
interface EmployeeDAO {
    @Query("SELECT * FROM `Employee`")
    suspend fun getAll(): List<Employee>

    @Query("SELECT * FROM `Employee` WHERE employeeID = :employeeID")
    suspend fun getById(employeeID: Int): Employee?

    @Query("SELECT * FROM `Employee` WHERE urlId = :urlId")
    suspend fun getByName(urlId: String): Employee?

    @Query("SELECT id FROM favorites WHERE type = 1")
    suspend fun getFavoriteIds(): List<Int>

    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Upsert
    suspend fun insertAll(groups: List<Employee>)
}