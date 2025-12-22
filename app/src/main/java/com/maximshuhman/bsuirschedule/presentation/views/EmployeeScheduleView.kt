@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Lesson
import com.maximshuhman.bsuirschedule.presentation.viewModels.EmployeeScheduleUiState
import com.maximshuhman.bsuirschedule.presentation.viewModels.EmployeeScheduleViewModel

@SuppressLint("RestrictedApi")
@Composable
fun EmployeeScheduleView(
    navController: NavController,
    employeeId: Int,
    employeeFIO: String,
    modifier: Modifier = Modifier,
    viewModel: EmployeeScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var bottomSheetVisible by remember { mutableStateOf(false) }
    var examsDialogVisible by remember { mutableStateOf(false) }

    var detailsVisible by remember { mutableStateOf(false) }
    var lessonDetails by remember { mutableStateOf<Lesson?>(null) }

    var employeeDetailsVisible by remember { mutableStateOf(false) }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }

    LaunchedEffect(employeeId) {
        viewModel.loadSchedule(employeeId)
        viewModel.getFavorites()
    }

    if (examsDialogVisible) {
        ExamsView((uiState as EmployeeScheduleUiState.Success).schedule.exams,  {lesson ->
            lessonDetails = lesson
            detailsVisible = true
        }) {
            examsDialogVisible = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton({
                        bottomSheetVisible = true
                    }) {
                        Icon(
                            painterResource(R.drawable.burger_menu),
                            contentDescription = stringResource(R.string.menu_button),
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        when (uiState) {
                            is EmployeeScheduleUiState.Success -> (uiState as EmployeeScheduleUiState.Success).schedule.employee.fio
                            else -> employeeFIO
                        },
                        maxLines = 1
                    )
                },
                actions = {
                    if (uiState is EmployeeScheduleUiState.Success) {
                        if ((uiState as EmployeeScheduleUiState.Success).schedule.exams.isNotEmpty() && (uiState as EmployeeScheduleUiState.Success).schedule.schedule.isNotEmpty())
                            Surface(
                                onClick = {
                                    examsDialogVisible = true
                                },
                                shape = MaterialTheme.shapes.small,
                                color = Transparent
                            ) {
                                Text("ЭК", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                            }

                        IconButton({
                            viewModel.clickFavorite()
                        }) {
                            if ((uiState as EmployeeScheduleUiState.Success).isFavorite)
                                Icon(
                                    painterResource(R.drawable.ic_baseline_favorite_24),
                                    stringResource(R.string.favorite_click)
                                )
                            else
                                Icon(
                                    painterResource(R.drawable.ic_baseline_favorite_border_24),
                                    stringResource(R.string.favorite_click)
                                )
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        if (bottomSheetVisible)
            FavoritesBottomSheet(navController, favorites) {
                bottomSheetVisible = false
            }

        when (uiState) {
            is EmployeeScheduleUiState.Error -> {
                ViewError(innerPadding, (uiState as EmployeeScheduleUiState.Error).message)
            }

            EmployeeScheduleUiState.Loading -> {
                Box {
                    LinearProgressIndicator(
                        Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                    )
                }
            }

            is EmployeeScheduleUiState.Success -> {


                Box {
                    if ((uiState as EmployeeScheduleUiState.Success).schedule.schedule.isEmpty())
                        ExamsList(
                            (uiState as EmployeeScheduleUiState.Success).schedule.exams,
                            innerPadding
                        ) { lesson ->
                            lessonDetails = lesson
                            detailsVisible = true
                        }
                    else
                        ScheduleList(
                            (uiState as EmployeeScheduleUiState.Success).schedule.schedule,
                            0,
                            innerPadding,
                        ) { lesson ->
                            lessonDetails = lesson
                            detailsVisible = true
                        }

                    if (detailsVisible && lessonDetails != null) {
                        DetailsDialogView(
                            lesson = lessonDetails!!,
                            onDismissRequest = { detailsVisible = false },
                            onEmployeeClick = { emp ->
                                selectedEmployee = emp
                                employeeDetailsVisible = true
                                detailsVisible = false
                            }
                        )
                    }

                    if (employeeDetailsVisible && selectedEmployee != null) {
                        EmployeeDetailsDialog(
                            employee = selectedEmployee!!,
                            onDismiss = { employeeDetailsVisible = false },
                            onEnter = { employee ->

                                navController.navigate("${NavRoutes.EmployeeSchedule.route}/${employee.id}&${employee.fio}"){
                                    navOptions {
                                        restoreState = true
                                    }

                                    popUpTo(NavRoutes.EmployeeSchedule.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                }
            }

            is EmployeeScheduleUiState.NoConnection -> {
                NoConnectionView(
                    Modifier
                        .padding(innerPadding)
                        .padding(bottom = 70.dp)
                        .fillMaxSize()
                )
            }
        }

    }
}

/*
@Composable
fun ScheduleList(
    scheduleState: EmployeeScheduleUiState.Success,
    contentPaddingValues: PaddingValues,
    navController: NavController
) {

    var detailsVisible by remember { mutableStateOf(false) }
    var lessonDetails by remember { mutableStateOf<Lesson?>(null) }

    var employeeDetailsVisible by remember { mutableStateOf(false) }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }

    Box {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp),
            contentPadding = contentPaddingValues
        ) {
            items(
                scheduleState
                    .schedule.schedule
            ) { day ->
                ScheduleDayItem(day.header)

                Card(
                    Modifier
                        .padding(5.dp, 3.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {

                    val filteredLessons = day.list

                    filteredLessons.forEachIndexed { index, lesson ->

                        LessonCard(lesson) {
                            detailsVisible = true
                            lessonDetails = lesson
                        }
                        if (index < filteredLessons.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                thickness = 1.dp,
                            )
                        }
                    }
                }
            }
        }

        if (detailsVisible && lessonDetails != null) {
            DetailsDialogView(
                lesson = lessonDetails!!,
                onDismissRequest = { detailsVisible = false },
                onEmployeeClick = { emp ->
                    selectedEmployee = emp
                    employeeDetailsVisible = true
                    detailsVisible = false
                }
            )
        }

        if (employeeDetailsVisible && selectedEmployee != null) {
            EmployeeDetailsDialog(
                employee = selectedEmployee!!,
                onDismiss = { employeeDetailsVisible = false },
                onEnter = { employee ->

                    navController.navigate("${NavRoutes.EmployeeSchedule.route}/${employee.id}&${employee.fio}"){
                        navOptions {
                            restoreState = true
                        }

                        popUpTo(NavRoutes.EmployeeSchedule.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

    }
}*/
