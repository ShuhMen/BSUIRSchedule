package com.maximshuhman.bsuirschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maximshuhman.bsuirschedule.presentation.views.GroupScheduleView
import com.maximshuhman.bsuirschedule.presentation.views.GroupsScreen
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BSUIRScheduleTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Groups.route
    ){
        composable(NavRoutes.Groups.route) { GroupsScreen(navController) }
        composable(NavRoutes.GroupSchedule.route + "/{groupId}&{groupName}",
            arguments = listOf(navArgument("groupId") { type = NavType.IntType }, navArgument("groupName") { type = NavType.StringType })) {
                stackEntry ->
                val groupId = stackEntry.arguments?.getInt("groupId")!!
                val groupName = stackEntry.arguments?.getString("groupName")!!

            GroupScheduleView(navController, groupId, groupName)
        }
        //composable(NavRoutes.Contacts.route) { Contacts()  }
        //composable(NavRoutes.About.route) { About() }
    }
}

sealed class NavRoutes(val route: String) {
    object Groups : NavRoutes("groups")
    object Employees : NavRoutes("employees")
    object GroupSchedule : NavRoutes("group_schedule")
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BSUIRScheduleTheme {
        Main()
    }
}