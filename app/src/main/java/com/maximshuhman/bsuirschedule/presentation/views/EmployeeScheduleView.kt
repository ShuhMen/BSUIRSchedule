@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.maximshuhman.bsuirschedule.R
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

    LaunchedEffect(employeeId) {
        viewModel.loadSchedule(employeeId)
        viewModel.getFavorites()
    }

    if (examsDialogVisible) {
        ExamsView((uiState as EmployeeScheduleUiState.Success).schedule.exams!!) {
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
                    if(uiState is EmployeeScheduleUiState.Success) {
                        IconButton({
                            viewModel.clickFavorite()
                        }) {
                            if((uiState as EmployeeScheduleUiState.Success).isFavorite)
                                Icon(painterResource(R.drawable.ic_baseline_favorite_24), stringResource(R.string.favorite_click))
                            else
                                Icon(painterResource(R.drawable.ic_baseline_favorite_border_24), stringResource(R.string.favorite_click))
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()) { innerPadding ->

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
                    LinearProgressIndicator(Modifier
                        .fillMaxWidth()
                        .padding(innerPadding))
                }
            }

            is EmployeeScheduleUiState.Success -> {
                Box(modifier.fillMaxSize()) {
                    ScheduleList(
                        (uiState as EmployeeScheduleUiState.Success),
                        innerPadding
                    )

                    if ((uiState as EmployeeScheduleUiState.Success).schedule.exams != null) {

                        Button(
                            {
                                examsDialogVisible = true
                            },
                            modifier.align(Alignment.BottomEnd)
                                .padding(bottom = 30.dp, end = 20.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Экзамены", Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                        }
                    }
                }
            }
            is EmployeeScheduleUiState.NoConnection -> {
                NoConnectionView(Modifier.padding(innerPadding).padding(bottom = 70.dp).fillMaxSize())
            }
        }

    }
}

@Composable
fun ScheduleList(
    scheduleState: EmployeeScheduleUiState.Success,
    contentPaddingValues: PaddingValues
){

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

                    LessonCard(lesson) { }
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

/*

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun bottomPreview(){
    BSUIRScheduleTheme {
        Surface(Modifier) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.favorite_screen),
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 24.sp
                )

                IconButton ({
                },
                    modifier = Modifier
                        .size(20.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.edit_favorites),
                        stringResource(R.string.edit_favorites)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LessonCardSchedulePreview(){
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
                            listOf(Employee(
                                0,"Максим", "Шухман", "Юрьевич", "", "", "", "", ""
                            )),
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
                            null,
                            "ССПОиРС (ЛР) 230501-2 перенесено на 23.12.2025",
                            0,
                            "10:00",
                            arrayListOf(),
                            null,
                            null,
                            null,
                            listOf(Employee(
                                0,"Максим", "Шухман", "Юрьевич", "", "", "", "", ""
                            )),                            "10.09.2025",
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
*/