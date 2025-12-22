package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.sources.EmployeeDAO
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.domain.models.Favorites
import jakarta.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val groupsDAO: GroupsDAO,
    private val employeeDAO: EmployeeDAO
) {

    suspend operator fun invoke(): Favorites {

        val groupsDbList = groupsDAO.getAll()
        val employeesDbList = employeeDAO.getAll()


        val favoriteGroupsIds = groupsDAO.getFavoriteIds().toSet()
        val favoriteEmployeeIds = employeeDAO.getFavoriteIds().toSet()

        val favoriteGroups : List<Group> =
            groupsDbList
                .asSequence()
                .filter { it.id in favoriteGroupsIds }
                .map {
                    it.isFavorite = true
                    it
                }
                .toList()


        val favoriteEmployee : List<Employee> =
            employeesDbList
                .asSequence()
                .filter { it.id in favoriteEmployeeIds }
                .map {
                    it.isFavorite = true
                    it
                }
                .toList()

        return Favorites(favoriteGroups, favoriteEmployee)

    }


}