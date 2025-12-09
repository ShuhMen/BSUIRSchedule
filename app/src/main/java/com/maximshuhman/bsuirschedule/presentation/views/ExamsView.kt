@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDay

@Composable
fun ExamsView(
    exams: List<ScheduleDay>,
    onDismissRequest: () -> Unit,
) {

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false,        decorFitsSystemWindows = false
        ),
        ) {

        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window

        if (dialogWindow != null) {
            SideEffect {
                dialogWindow.setDimAmount(0f)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    navigationIcon = {
                        IconButton(onDismissRequest) {
                            Icon(
                                painterResource(R.drawable.nav_back),
                                contentDescription = stringResource(R.string.menu_button),
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    title = {
                        Text(
                            "Экзамены",
                            maxLines = 1
                        )
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 5.dp),
                contentPadding = innerPadding
            ) {
                items(
                    exams
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
    }

}