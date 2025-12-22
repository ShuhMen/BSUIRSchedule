@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Lesson
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDay
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDayHeader
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupScheduleUiState
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupScheduleViewModel
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@SuppressLint("RestrictedApi")
@Composable
fun GroupScheduleView(
    navController: NavController,
    groupId: Int,
    groupName: String,
    modifier: Modifier = Modifier,
    viewModel: GroupScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var bottomSheetVisible by remember { mutableStateOf(false) }
    var examsDialogVisible by remember { mutableStateOf(false) }


    var detailsVisible by remember { mutableStateOf(false) }
    var lessonDetails by remember { mutableStateOf<Lesson?>(null) }

    var employeeDetailsVisible by remember { mutableStateOf(false) }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }

    LaunchedEffect(groupId) {
        viewModel.loadSchedule(groupId)
        viewModel.getFavorites()
    }

    if (examsDialogVisible) {
        ExamsView((uiState as GroupScheduleUiState.Success).schedule.exams, { lesson ->
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
                            is GroupScheduleUiState.Success -> (uiState as GroupScheduleUiState.Success).schedule.group.name
                            else -> groupName
                        },
                        maxLines = 1
                    )
                },
                actions = {
                    if (uiState is GroupScheduleUiState.Success) {
                        if ((uiState as GroupScheduleUiState.Success).schedule.exams.isNotEmpty() && (uiState as GroupScheduleUiState.Success).schedule.schedule.isNotEmpty())
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
                            viewModel.clickSubgroup()
                        }) {
                            when ((uiState as GroupScheduleUiState.Success).numSubgroup) {
                                0 ->
                                    Icon(
                                        painterResource(R.drawable.subgroup_all),
                                        stringResource(R.string.subgroupAll),
                                        modifier = Modifier.height(36.dp)
                                    )

                                1 ->
                                    Icon(
                                        painterResource(R.drawable.subgroup1),
                                        stringResource(R.string.subgroup1)
                                    )

                                2 ->
                                    Icon(
                                        painterResource(R.drawable.subgroup2),
                                        stringResource(R.string.subgroup2)
                                    )
                            }
                        }

                        IconButton({
                            viewModel.clickFavorite()
                        }) {
                            if ((uiState as GroupScheduleUiState.Success).isFavorite)
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
            is GroupScheduleUiState.Error -> {
                ViewError(innerPadding, (uiState as GroupScheduleUiState.Error).message)
            }

            GroupScheduleUiState.Loading -> {
                Box {
                    LinearProgressIndicator(
                        Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                    )
                }
            }

            is GroupScheduleUiState.Success -> {

                Box {

                    if ((uiState as GroupScheduleUiState.Success).schedule.schedule.isEmpty())
                        ExamsList(
                            (uiState as GroupScheduleUiState.Success).schedule.exams,
                            innerPadding
                        ){ lesson ->
                            lessonDetails = lesson
                            detailsVisible = true
                        }
                    else
                        ScheduleList(
                            (uiState as GroupScheduleUiState.Success).schedule.schedule,
                            (uiState as GroupScheduleUiState.Success).numSubgroup,
                            innerPadding,
                        ){ lesson ->
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

            is GroupScheduleUiState.NoConnection -> {
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

@Composable
inline fun ScheduleList(
    scheduleList: List<ScheduleDay>,
    numSubgroup: Int,
    contentPaddingValues: PaddingValues,
    crossinline onItemClick: (Lesson) -> Unit
) {


    Box {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp),
            contentPadding = contentPaddingValues
        ) {
            items(
                scheduleList
                    .filter { lesson ->
                        lesson.list.any {
                            numSubgroup == 0 || it.numSubgroup == 0 || it.numSubgroup == numSubgroup
                        }
                    }
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

                    val filteredLessons = day.list.asSequence()
                        .filter {
                            numSubgroup == 0 || it.numSubgroup == 0 || it.numSubgroup == numSubgroup
                        }
                        .toList()

                    filteredLessons.forEachIndexed { index, lesson ->

                        LessonCard(lesson) {
                           onItemClick(lesson)
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


    }
}

@Composable
fun ScheduleDayItem(groupDay: ScheduleDayHeader) {
    Text(
        groupDay.name,
        fontSize = 20.sp,
        modifier = Modifier
            .padding(15.dp, 7.dp, bottom = 3.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LessonCardSchedulePreview() {
    BSUIRScheduleTheme {
        Surface(Modifier) {
            Column {

                ScheduleDayItem(ScheduleDayHeader("Пятница, 4 октября"))

                Card(
                    Modifier
                        .padding(5.dp, 3.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Column {

                        LessonCard(
                            Lesson(
                                arrayListOf("202-4к.", "302-4к."),
                                "11:00",
                                "ЛК",
                                "",
                                0,
                                "10:00",
                                arrayListOf(),
                                "ОБПСвИТ",
                                "10:00",
                                arrayListOf(1),
                                arrayListOf<Employee>(),
                                "10.09.2025",
                                "10.09.2025",
                                "10.09.2025",
                                false,
                                false

                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                        )
                        LessonCard(
                            Lesson(
                                arrayListOf("202-4к.", "302-4к."),
                                "11:00",
                                "ПЗ",
                                "",
                                1,
                                "10:00",
                                arrayListOf(),
                                "ОБПСвИТ",
                                "10:00",
                                arrayListOf(1),
                                arrayListOf<Employee>(),
                                "10.09.2025",
                                "10.09.2025",
                                "10.09.2025",
                                false,
                                false

                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            thickness = 1.dp,
                        )
                        LessonCard(
                            Lesson(
                                arrayListOf("202-4к.", "302-4к."),
                                "11:00",
                                "ЛР",
                                "",
                                2,
                                "10:00",
                                arrayListOf(),
                                "ОБПСвИТ",
                                "10:00",
                                arrayListOf(1),
                                listOf(
                                    Employee(
                                        0, "Максим", "Шухман", "Юрьевич", "", "", "", "", ""
                                    )
                                ),
                                "10.09.2025",
                                "10.09.2025",
                                "10.09.2025",
                                false,
                                false

                            )
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            thickness = 1.dp,
                        )

                        LessonCard(
                            Lesson(
                                null,
                                "11:00",
                                "ПЗ",
                                "ССПОиРС (ЛР) 230501-2 перенесено на 23.12.2025",
                                0,
                                "10:00",
                                arrayListOf(),
                                "АиПРП",
                                "Администрирование и проектирования распределенных систем",
                                null,
                                listOf(
                                    Employee(
                                        0, "Максим", "Шухман", "Юрьевич", "", "", "", "", ""
                                    )
                                ), "10.09.2025",
                                "10.09.2025",
                                "10.09.2025",
                                true,
                                false

                            )
                        )

                    }
                }
            }
        }
    }
}
