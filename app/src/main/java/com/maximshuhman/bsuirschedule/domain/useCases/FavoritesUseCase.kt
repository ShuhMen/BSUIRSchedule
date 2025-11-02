package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.domain.models.Favorites
import jakarta.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val groupsDAO: GroupsDAO,
    //employeeDAO: EmployeeDAO
) {

    suspend operator fun invoke(): Favorites {

        val groupsDbList = groupsDAO.getAll()
        val favoriteIds = groupsDAO.getFavoriteGroupIds().toSet()

        val favoriteGroups : List<Group> = if (groupsDbList.isEmpty())
           listOf()
        else
            groupsDbList
                .asSequence()
                .filter { it.id in favoriteIds }
                .map {
                    it.isFavorite = true
                    it
                }
                .toList()

        val favoriteEmployee = listOf<Employee>()

        return Favorites(favoriteGroups, favoriteEmployee)

    }


}