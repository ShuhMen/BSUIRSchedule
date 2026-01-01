package com.maximshuhman.bsuirschedule.presentation.views.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maximshuhman.bsuirschedule.data.dto.Lesson
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDay

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