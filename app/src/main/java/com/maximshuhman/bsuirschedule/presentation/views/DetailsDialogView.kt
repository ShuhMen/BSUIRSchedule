package com.maximshuhman.bsuirschedule.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.data.dto.Lesson
import com.maximshuhman.bsuirschedule.data.dto.LessonType
import com.maximshuhman.bsuirschedule.data.dto.StudentGroups
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DetailsDialogView(
    lesson: Lesson,
    onDismissRequest: () -> Unit,
    onEmployeeClick: (Employee) -> Unit
) {

    Dialog(
        onDismissRequest,
    ) {

        Card(
            Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(Modifier.padding(12.dp)) {

                lesson.apply {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.5.dp)
                    ) {
                        Text(
                            subjectFullName.toString(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(2f)
                        )

                        if (!auditories.isNullOrEmpty())
                            Column(Modifier.padding(start = 5.dp)) {
                                auditories.forEach {
                                    Text(
                                        it,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }

                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.5.dp)

                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Canvas(Modifier
                                .size(18.dp)
                                .padding(end = 5.dp)) {
                                drawCircle(lesson.getLessonColor())
                            }


                            Text(
                                when (lessonTypeAbbrev) {
                                    "ЛК", "УЛк" -> "Лекция"
                                    "ПЗ", "УПз" -> "Практическое занятие"
                                    "ЛР", "УЛр" -> "Лабораторная"
                                    null -> "Событие"
                                    else -> lessonTypeAbbrev
                                }
                            )
                        }

                        Text(
                            "c $startLessonTime до $endLessonTime",
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.5.dp)

                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(R.drawable.subgroup_all),
                                contentDescription = null,
                                Modifier
                                    .size(18.dp)
                                    .padding(end = 5.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                            )

                            Text(
                                when (numSubgroup) {
                                    0 -> "Для всей группы"
                                    else -> "Для ${numSubgroup} подгруппы"
                                }
                            )
                        }

                        if (!weekNumber.isNullOrEmpty())
                            Text(
                                "на ${weekNumber.joinToString(", ")} нед.",
                            )

                    }
                    if (startLessonDate != null && endLessonDate != null)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.5.dp, bottom = 5.dp)

                        ) {
                            val prettyFormatter =
                                DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
                            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

                            Image(
                                painterResource(R.drawable.calendar),
                                contentDescription = null,
                                Modifier
                                    .size(18.dp)
                                    .padding(end = 5.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                            )
                            Text(
                                "С ${
                                    prettyFormatter.format(
                                        LocalDate.parse(
                                            startLessonDate,
                                            formatter
                                        )
                                    )
                                } по ${
                                    prettyFormatter.format(
                                        LocalDate.parse(
                                            endLessonDate,
                                            formatter
                                        )
                                    )
                                } ",
                            )

                        }
                }

                if (!lesson.employees.isNullOrEmpty() && lesson.lessonType == LessonType.GROUP)
                    lesson.employees.forEach { employee ->
                        employee.apply {

                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.5.dp)
                                    .clickable {
                                        onEmployeeClick(employee)
                                        onDismissRequest()
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {

                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    AsyncImage(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(employee.photoLink)
                                            .listener(
                                                onError = { _, throwable ->
                                                    println("Image load error: ${throwable.throwable.message}")
                                                },
                                                onSuccess = { request, result ->

                                                    result.image.height

                                                }
                                            )
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(R.drawable.person_circle),
                                        error = painterResource(R.drawable.person_circle),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier.padding(
                                            horizontal = 5.dp
                                        )
                                    ) {
                                        Text(
                                            "$lastName $firstName ${if (!middleName.isNullOrBlank()) middleName else ""}",
                                            modifier = Modifier.padding(
                                                bottom = 2.dp,
                                                start = 5.dp,
                                            )
                                        )

                                        if (!degree.isNullOrEmpty())
                                            Text(
                                                degree,
                                                modifier = Modifier.padding(
                                                    start = 5.dp,
                                                    bottom = 2.dp
                                                )
                                            )


                                    }
                                }
                            }

                        }
                    }

                if (!lesson.studentGroups.isNullOrEmpty() && lesson.lessonType == LessonType.EMPLOYEE)
                    lesson.studentGroups.forEach { studentGroup ->
                        studentGroup.apply {

                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                /*.clickable {
                                    onEmployeeClick(studentGroup)
                                    onDismissRequest()
                                }*/,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 2.5.dp
                                    )
                                ) {


                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                bottom = 2.dp,
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Text(name.toString())

                                        if (numberOfStudents != null)
                                            Text("$numberOfStudents студентов")

                                    }
                                    if (!specialityName.isNullOrEmpty())
                                        Text(specialityName)
                                }

                            }

                        }
                    }

            }
        }

    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DetailsPreview() {

    BSUIRScheduleTheme {
        val lesson = Lesson(
            arrayListOf("202-4к.", "302-4к."),
            "11:00",
            "ПЗ",
            "ССПОиРС (ЛР) 230501-2 перенесено на 23.12.2025",
            0,
            "10:00",
            arrayListOf(
                StudentGroups(
                    "Автоматизированные системы обработки информации",
                    "01-02",
                    30,
                    "220601",
                    1
                )
            ),
            "АиПРП",
            "Администрирование и проектирования распределенных систем",
            null,
            listOf(
                Employee(
                    0,
                    "Максим",
                    "Шухман",
                    "Юрьевич",
                    "кандидат физико-математических наук",
                    "",
                    "https://iis.bsuir.by/api/v1/employees/photo/500245",
                    "",
                    ""
                )
            ), "10.09.2025",
            "10.09.2025",
            "10.09.2025",
            true,
            false,
            lessonType = LessonType.EMPLOYEE
        )

        DetailsDialogView(
            lesson,
            onDismissRequest = {

            }
        ) {}
    }
}
