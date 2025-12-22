package com.maximshuhman.bsuirschedule.domain.useCases

import com.maximshuhman.bsuirschedule.data.entities.FavoriteEntity
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import javax.inject.Inject

class SetFavoriteEntity @Inject constructor(
    private val groupsDAO: GroupsDAO,
)  {

    suspend operator fun invoke(entity: FavoriteEntity, isFavorite: Boolean){
        if(isFavorite)
            groupsDAO.upsertFavorite(entity)
        else
            groupsDAO.deleteFavorite(entity)
    }
}