package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Lesson
import com.maximshuhman.bsuirschedule.data.dto.LessonType
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme
import com.maximshuhman.bsuirschedule.ui.theme.Blue
import com.maximshuhman.bsuirschedule.ui.theme.Labaratory
import com.maximshuhman.bsuirschedule.ui.theme.Lecture
import com.maximshuhman.bsuirschedule.ui.theme.Practic


@Composable
inline fun LessonCard(lesson: Lesson, crossinline onClick: () -> Unit = { }) {

    val dividerColor = when(lesson.lessonTypeAbbrev){
        "Экзамен", "ЛР" -> Labaratory

        "Консультация", "ПЗ","УПз" -> Practic

        "УЛк", "ЛК" -> Lecture
        else -> if(lesson.announcement == true) Blue else Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if(lesson.announcement != true)
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
                .clip(CircleShape)
                .background(dividerColor)
                .width(10.dp)
                .height(60.dp)


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
            text = lesson.employees?.joinToString(", "){ it -> "${it.lastName} ${it.firstName.first()}. ${if (!it.middleName.isNullOrBlank()) it.middleName.first() + "." else ""}" } +  " : ${lesson.note}",
            fontSize = 16.sp
        )

        if (lesson.numSubgroup != 0) {

            Text(
                lesson.numSubgroup.toString(),
                modifier = Modifier.padding(start = 5.dp)
            )

            Icon(
                painterResource(R.drawable.subgroup),
                contentDescription = "Подгруппа",
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


        if (lesson.auditories != null)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = lesson.auditories.joinToString(", "),
                    fontSize = 16.sp
                )


                if(lesson.lessonType == LessonType.GROUP && !lesson.employees.isNullOrEmpty()) {
                    Text(
                        text = lesson.employees.joinToString(", "){ it -> "${it.lastName} ${it.firstName.first()}. ${if (!it.middleName.isNullOrBlank()) it.middleName.first() + "." else ""}" },
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }

                if(lesson.lessonType == LessonType.EMPLOYEE && !lesson.studentGroups.isNullOrEmpty()) {
                    Text(
                        text = lesson.studentGroups.joinToString(", "){ it -> it.name.toString() },
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }
            }

        if (!lesson.note.isNullOrBlank())
            Text(
                text = lesson.note,
                fontSize = 14.sp
            )
    }


}


@Preview(showBackground = true)
@Composable
fun LessonCardPreview(){

    BSUIRScheduleTheme {

        Box(
            Modifier.background( MaterialTheme.colorScheme.secondary)
        ) {
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
        }
    }
}


@Preview
@Composable
fun AnnouncementCardPreview(){

    BSUIRScheduleTheme {
        Box(
            Modifier.background(MaterialTheme.colorScheme.secondary)
        ) {
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