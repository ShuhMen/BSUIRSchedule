package com.maximshuhman.bsuirschedule.presentation.views


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
inline fun TopAppBarState(
    title: @Composable () -> Unit,
    colors: TopAppBarColors,

){

    Row(modifier = Modifier.background(colors.containerColor)) {

        title()

    }
}