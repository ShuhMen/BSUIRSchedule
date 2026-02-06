package com.maximshuhman.bsuirschedule.data.converters

import androidx.room.TypeConverter
import com.maximshuhman.bsuirschedule.data.dto.StudentGroups
import kotlinx.serialization.json.Json

object StudentGroupsConverter {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStudentGroupsArray(array: List<StudentGroups>?): String? {
        return array?.let { json.encodeToString(it.toList()) }
    }

    @TypeConverter
    fun toStudentGroupsArray(value: String?): List<StudentGroups>? {
        return value?.let { json.decodeFromString<List<StudentGroups>>(it) }
    }

    @TypeConverter
    fun fromStudentGroups(value: StudentGroups?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toStudentGroups(value: String?): StudentGroups? {
        return value?.let { json.decodeFromString(it) }
    }
}