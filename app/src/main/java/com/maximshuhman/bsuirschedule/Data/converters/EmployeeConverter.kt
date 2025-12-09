package com.maximshuhman.bsuirschedule.data.converters

import androidx.room.TypeConverter
import com.maximshuhman.bsuirschedule.data.dto.Employee
import kotlinx.serialization.json.Json

object EmployeeConverter {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStudentGroupsArray(array: List<Employee>?): String? {
        return array?.let { json.encodeToString(it.toList()) }
    }

    @TypeConverter
    fun toStudentGroupsArray(value: String?): List<Employee>? {
        return value?.let { json.decodeFromString<List<Employee>>(it) }
    }

    @TypeConverter
    fun fromStudentGroups(value: Employee?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toStudentGroups(value: String?): Employee? {
        return value?.let { json.decodeFromString(it) }
    }
}