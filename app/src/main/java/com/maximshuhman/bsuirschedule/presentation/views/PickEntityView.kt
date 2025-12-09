package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
                windowInsets = NavigationBarDefaults.windowInsets) {
                PickEntityRoutes.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                painterResource(destination.iconId),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { _ ->
        NavHost(
            navController,
            startDestination = startDestination.route
        ) {
            PickEntityRoutes.entries.forEach { destination ->
                composable(destination.route) {
                    when (destination) {
                        PickEntityRoutes.GROUPS -> GroupsScreen(parentNavController)
                        PickEntityRoutes.EMPLOYEES -> EmployeeScreen(parentNavController)
                    }
                }
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