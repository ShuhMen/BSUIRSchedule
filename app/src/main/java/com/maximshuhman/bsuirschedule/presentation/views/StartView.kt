package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.presentation.viewModels.MainActivityUiState

@Composable
fun StartScreen(
    uiState: MainActivityUiState,
    navController: NavHostController
) {
    LaunchedEffect(uiState) {
        when (uiState) {
            MainActivityUiState.Loading -> {

            }
            is MainActivityUiState.GroupSuccess -> {
                val group = uiState.group
                navController.navigate("${NavRoutes.GroupSchedule.route}/${group.id}&${group.name}") {
                    popUpTo(NavRoutes.Start.route) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is MainActivityUiState.EmployeeSuccess -> {
                val employee = uiState.employee
                navController.navigate("${NavRoutes.EmployeeSchedule.route}/${employee.id}&${employee.fio}") {
                    popUpTo(NavRoutes.Start.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            MainActivityUiState.Empty -> {
                navController.navigate(NavRoutes.PickEntity.route) {
                    popUpTo(NavRoutes.Start.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    if (uiState is MainActivityUiState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}