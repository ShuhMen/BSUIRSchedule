package com.maximshuhman.bsuirschedule.data.converters

import androidx.room.TypeConverter
import com.maximshuhman.bsuirschedule.data.dto.StudentGroupDto
import kotlinx.serialization.json.Json

object StudentGroupDtoConverter {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStudentGroupsArray(array: List<StudentGroupDto>?): String? {
        return array?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toStudentGroupsArray(value: String?): List<StudentGroupDto>? {
        return value?.let { json.decodeFromString<List<StudentGroupDto>>(it) }
    }

    @TypeConverter
    fun fromStudentGroups(value: StudentGroupDto?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toStudentGroups(value: String?): StudentGroupDto? {
        return value?.let { json.decodeFromString(it) }
    }
}