package com.maximshuhman.bsuirschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maximshuhman.bsuirschedule.presentation.viewModels.MainActivityUiState
import com.maximshuhman.bsuirschedule.presentation.viewModels.MainViewModel
import com.maximshuhman.bsuirschedule.presentation.views.GroupScheduleView
import com.maximshuhman.bsuirschedule.presentation.views.GroupsScreen
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val state = viewModel.uiState
        splashscreen.setKeepOnScreenCondition { state.value is MainActivityUiState.Loading }

        viewModel.getLastScreen()

        enableEdgeToEdge()
        setContent {
            BSUIRScheduleTheme {
                Main(viewModel)
            }
        }
    }
}

@Composable
fun Main(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is MainActivityUiState.Success) {
            val group = (uiState as MainActivityUiState.Success).group
            navController.navigate("${NavRoutes.GroupSchedule.route}/${group.id}&${group.name}") {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Groups.route
    ) {
        composable(NavRoutes.Groups.route) {
            GroupsScreen(
                navController = navController
            )
        }
        composable(
            "${NavRoutes.GroupSchedule.route}/{groupId}&{groupName}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType },
                navArgument("groupName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId")!!
            val groupName = backStackEntry.arguments?.getString("groupName")!!
            GroupScheduleView(navController, groupId, groupName)
        }
    }
}


sealed class NavRoutes(val route: String) {
    object Groups : NavRoutes("groups")
    object Employees : NavRoutes("employees")
    object GroupSchedule : NavRoutes("group_schedule")
}

