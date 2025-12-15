package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
fun PickEntityView(parentNavController: NavController) {
    val navController = rememberNavController()
    val startDestination = PickEntityRoutes.GROUPS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets,
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 5.dp
            ) {
                PickEntityRoutes.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(destination.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                painterResource(destination.iconId),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { androidx.compose.material3.Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(PickEntityRoutes.GROUPS.route) {
                GroupsScreen(parentNavController)
            }
            composable(PickEntityRoutes.EMPLOYEES.route) {
                EmployeeScreen(parentNavController)
            }
        }
    }
}

enum class PickEntityRoutes(
    val route: String,
    val label: String,
    val iconId: Int,
    val contentDescription: String
) {
    GROUPS("groups", "Группы", R.drawable.schedule_outlined, "groups"),
    EMPLOYEES("employees", "Преподаватели", R.drawable.id_card_outlined, "employees"),
}

@Preview
@Composable
fun PreviewPickEntity(){
    val navController = rememberNavController()

    BSUIRScheduleTheme {

        PickEntityView(navController)


    }
}