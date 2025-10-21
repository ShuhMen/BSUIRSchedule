package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.data.models.Group
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import javax.inject.Inject

class SetGroupListUseCase @Inject constructor(
    private val groupsDAO: GroupsDAO
) {
    suspend operator fun invoke(groups: List<Group>) {
        groupsDAO.insertAll(groups)
    }
}