@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import Lesson
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.maximshuhman.bsuirschedule.DataClasses.Employee
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.domain.models.GroupDayHeader
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupScheduleUiState
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupScheduleViewModel
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme
import com.maximshuhman.bsuirschedule.ui.theme.Labaratory
import com.maximshuhman.bsuirschedule.ui.theme.Lecture
import com.maximshuhman.bsuirschedule.ui.theme.Practic

@Composable
fun GroupScheduleView(
    navController: NavController,
    groupId: Int,
    groupName: String,
    modifier: Modifier = Modifier,
    viewModel: GroupScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadSchedule(groupId)
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
                        navController.popBackStack()
                    }) {
                        Icon(painterResource(R.drawable.nav_back), contentDescription = stringResource(R.string.go_back))
                    }
                },
                title = {
                    Text(
                        when (uiState) {
                            is GroupScheduleUiState.Success -> (uiState as GroupScheduleUiState.Success).schedule.groupDto.name
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
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()) { innerPadding ->

            when (uiState) {
                is GroupScheduleUiState.Error -> {
                    Column {
                        Text((uiState as GroupScheduleUiState.Error).message)
                    }
                }

                GroupScheduleUiState.Loading -> {
                    Column {
                        LinearProgressIndicator(Modifier.fillMaxWidth().padding(innerPadding))
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
        Modifier.fillMaxSize().padding(horizontal = 5.dp),
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

                day.list.asSequence().filter {
                    scheduleState.numSubgroup == 0 || it.numSubgroup == 0 || it.numSubgroup == scheduleState.numSubgroup
                }.forEachIndexed { it, lesson ->

                    LessonCard(lesson) { }
                    if (it < day.list.size - 1)
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            thickness = 1.dp,
                        )
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

@Composable
inline fun LessonCard(lesson: Lesson, crossinline onClick: () -> Unit) {

    val dividerColor = when(lesson.lessonTypeAbbrev){
        "ЛР" -> Labaratory
        "ПЗ" -> Practic
        "ЛК" -> Lecture
        else -> Color.Yellow
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
        .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 10.dp)
        ) {
            Text(
                text = lesson.startLessonTime,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            Text(
                text = lesson.endLessonTime,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clip(CircleShape)
                .background(dividerColor)
                .width(10.dp)

        )

        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
        ) {
            Row {
                Text(
                    text ="${lesson.subject} (${lesson.lessonTypeAbbrev})",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 16.sp
                )

                if(lesson.numSubgroup != 0) {


                    Text(
                        lesson.numSubgroup.toString(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    
                    Icon(
                        painterResource(R.drawable.subgroup),
                        contentDescription = "Преподаватель",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterVertically),

                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = lesson.auditories.joinToString(", "),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 16.sp
            )
        }

        if(lesson.employees.isNotEmpty()) {

            Text(
                text = lesson.employees.joinToString(", "){ it -> "${it.lastName} ${it.firstName.first()}. ${if (!it.middleName.isNullOrBlank()) it.middleName.first() + "." else ""}" },
                color = Color.LightGray,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Bottom)
                    .padding(end = 5.dp)
            )
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
                    ) {}
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
                    ) {}
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
                    ) {}

                }

            }
        }
    }
}
