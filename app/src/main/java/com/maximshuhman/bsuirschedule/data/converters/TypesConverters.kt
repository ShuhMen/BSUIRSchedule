package com.maximshuhman.bsuirschedule.data.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

object TypesConverters {

    private val json = Json { ignoreUnknownKeys = true }

    // List<String>
    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.let { json.encodeToString(it) }

    @TypeConverter
    fun toStringList(value: String?): List<String>? = value?.let { json.decodeFromString(it) }

    // List<Int> (weekNumber)
    @TypeConverter
    fun fromIntList(list: List<Int>?): String? = list?.let { json.encodeToString(it) }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? = value?.let { json.decodeFromString(it) }
}