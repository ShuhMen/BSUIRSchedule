package com.maximshuhman.bsuirschedule.domain.models

import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group

data class Favorites(
    val groupsList: List<Group> = listOf(),
    val employeeList: List<Employee> = listOf()
)
