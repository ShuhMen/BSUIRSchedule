@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import Lesson
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.domain.models.GroupDayHeader
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

    LaunchedEffect(groupId) {
        viewModel.loadSchedule(groupId)
        viewModel.getFavorites()
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
                    if(uiState is GroupScheduleUiState.Success) {
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
                            if((uiState as GroupScheduleUiState.Success).isFavorite)
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
            ModalBottomSheet({
                bottomSheetVisible = false
            }) {
                FavoritesBottomSheet(navController, favorites) {
                    bottomSheetVisible = false
                }
            }

        when (uiState) {
            is GroupScheduleUiState.Error -> {
                ScheduleViewError(innerPadding, uiState as GroupScheduleUiState.Error)
            }

            GroupScheduleUiState.Loading -> {
                Box {
                    LinearProgressIndicator(Modifier
                        .fillMaxWidth()
                        .padding(innerPadding))
                }
            }

            is GroupScheduleUiState.Success -> ScheduleList(
                (uiState as GroupScheduleUiState.Success),
                innerPadding
            )
        }

    }
}

@Composable
fun ScheduleList(
    scheduleState: GroupScheduleUiState.Success,
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
                .filter { lesson ->
                    lesson.list.any {
                        scheduleState.numSubgroup == 0 || it.numSubgroup == 0 || it.numSubgroup == scheduleState.numSubgroup
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
                        scheduleState.numSubgroup == 0 || it.numSubgroup == 0 || it.numSubgroup == scheduleState.numSubgroup
                    }
                    .toList()

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

@Composable
fun ScheduleDayItem(groupDay: GroupDayHeader) {

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
fun lessonCardPreview(){
    BSUIRScheduleTheme {
        Surface(Modifier) {

            Column {
                ScheduleDayItem(GroupDayHeader("Пятница, 4 октября"))

                Card(
                    Modifier
                        .padding(5.dp, 3.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {

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
                            arrayListOf<Employee>(),
                            "10.09.2025",
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
