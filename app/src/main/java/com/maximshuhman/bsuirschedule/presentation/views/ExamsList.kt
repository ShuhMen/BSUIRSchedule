package com.maximshuhman.bsuirschedule.presentation.views

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
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDay

@Composable
inline fun ExamsList(
    exams: List<ScheduleDay>,
    innerPadding: PaddingValues
){
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