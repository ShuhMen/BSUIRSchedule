package com.maximshuhman.bsuirschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maximshuhman.bsuirschedule.presentation.viewModels.MainActivityUiState
import com.maximshuhman.bsuirschedule.presentation.viewModels.MainViewModel
import com.maximshuhman.bsuirschedule.presentation.views.StartScreen
import com.maximshuhman.bsuirschedule.presentation.views.pickentity.PickEntityView
import com.maximshuhman.bsuirschedule.presentation.views.schedule.EmployeeScheduleView
import com.maximshuhman.bsuirschedule.presentation.views.schedule.GroupScheduleView
import com.maximshuhman.bsuirschedule.presentation.views.settings.SettingsView
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

        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()

            BSUIRScheduleTheme(settings.theme) {

                val modifier = if (settings.theme == ApplicationThemes.PancakesTheme) Modifier
                    .paint(
                        painter = painterResource(R.drawable.panckakes),
                        contentScale = ContentScale.FillHeight
                    )
                else
                    Modifier.background(MaterialTheme.colorScheme.background)

                Main(modifier, viewModel)
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier, viewModel: MainViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()



    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoutes.Start.route
    ) {
        composable(NavRoutes.Start.route) {
            StartScreen(uiState, navController)
        }

        composable(NavRoutes.Settings.route) {
            SettingsView(navController)
        }

        composable(NavRoutes.PickEntity.route) {
            PickEntityView(
                navController
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

        composable(
            "${NavRoutes.EmployeeSchedule.route}/{employeeId}&{employeeFIO}",
            arguments = listOf(
                navArgument("employeeId") { type = NavType.IntType },
                navArgument("employeeFIO") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getInt("employeeId")!!
            val employeeFIO = backStackEntry.arguments?.getString("employeeFIO")!!
            EmployeeScheduleView(navController, employeeId, employeeFIO)
        }
    }


}


sealed class NavRoutes(val route: String) {
    object Start : NavRoutes("start")
    object GroupSchedule : NavRoutes("group_schedule")
    object EmployeeSchedule : NavRoutes("employee_schedule")
    object PickEntity : NavRoutes("pick_entity")
    object Settings : NavRoutes("settings")
}
