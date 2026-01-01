package com.maximshuhman.bsuirschedule.presentation.views.schedule

import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.maximshuhman.bsuirschedule.R

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit){

    IconButton(onClick) {
        if (isFavorite)
            Image(
                painterResource(R.drawable.ic_baseline_favorite_24),
                stringResource(R.string.favorite_click),
            )
        else
            Image(
                painterResource(R.drawable.ic_baseline_favorite_border_24),
                stringResource(R.string.favorite_click)
            )
    }
}