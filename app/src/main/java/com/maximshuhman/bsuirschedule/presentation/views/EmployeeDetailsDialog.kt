package com.maximshuhman.bsuirschedule.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
inline fun EmployeeDetailsDialog(
    employee: Employee,
    crossinline onDismiss: () -> Unit,
    crossinline onEnter: (Employee) -> Unit
) {

    Dialog(
        {
            onDismiss()

        },

    ) {
        Card(
            modifier = Modifier.padding(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {

            Column(
                modifier = Modifier.padding( vertical = 15.dp, horizontal = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        .padding(10.dp)
                        .size(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                )


                Text(employee.fio, modifier = Modifier.padding(vertical = 2.5.dp), fontSize = 16.sp)

                if (!employee.degree.isNullOrEmpty())
                    Text(employee.degree)

                Button(
                    {
                        onEnter(employee)
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text("Посмотреть расписание", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmployeeDetailsPreview() {

    val emp = Employee(
        0,
        "Максимааааааааа",
        "Шухман",
        "Юрьевич",
        "кандидат физико-математических наук",
        "",
        "",
        "АСОИ",

        "",
    )
    emp.isFavorite = true
    BSUIRScheduleTheme {

        Box(modifier = Modifier.size(400.dp)) {
            EmployeeDetailsDialog(
                emp, {}
            ) {

            }
        }

    }
}