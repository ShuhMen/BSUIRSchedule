package com.maximshuhman.bsuirschedule.presentation.views

import Lesson
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.ui.theme.Labaratory
import com.maximshuhman.bsuirschedule.ui.theme.Lecture
import com.maximshuhman.bsuirschedule.ui.theme.Practic


@Composable
inline fun LessonCard(lesson: Lesson, crossinline onClick: () -> Unit = { }) {

    val dividerColor = when(lesson.lessonTypeAbbrev){
        "Экзамен",
        "ЛР" -> Labaratory

        "Консультация",
        "ПЗ" -> Practic

        "ЛК" -> Lecture
        else -> Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick()
            }
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

        if(lesson.announcement?: false){
            AnnouncementPart(lesson)
        }else{
            LessonPart(lesson)
        }

    }
}

@Composable
fun AnnouncementPart(lesson: Lesson){

    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = lesson.employees.joinToString(", "){ it -> "${it.lastName} ${it.firstName.first()}. ${if (!it.middleName.isNullOrBlank()) it.middleName.first() + "." else ""}" } +  ": ${lesson.note}",
            fontSize = 16.sp
        )

        if (lesson.numSubgroup != 0) {

            Text(
                lesson.numSubgroup.toString(),
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
}

@Composable
fun LessonPart(lesson: Lesson){
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Row {
            Text(
                text = "${lesson.subject} (${lesson.lessonTypeAbbrev})",
                fontSize = 16.sp
            )

            if (lesson.numSubgroup != 0) {


                Text(
                    lesson.numSubgroup.toString(),
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

        Row {

            if(lesson.note != null)
                Text(
                    text = lesson.note,
                    fontSize = 14.sp
                )

            if (lesson.auditories != null)
                Text(
                    text = lesson.auditories.joinToString(", "),
                    fontSize = 16.sp
                )
        }
    }

    if(lesson.employees.isNotEmpty()) {

        Text(
            text = lesson.employees.joinToString(", "){ it -> "${it.lastName} ${it.firstName.first()}. ${if (!it.middleName.isNullOrBlank()) it.middleName.first() + "." else ""}" },
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 5.dp)
        )
    }
}