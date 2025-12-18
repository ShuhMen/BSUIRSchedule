package com.maximshuhman.bsuirschedule.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Employee
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
inline fun EmployeeCard(employee: Employee, crossinline onClick: () -> Unit = { },) {
    Card(
        onClick ={ onClick() },
        Modifier
            .padding(5.dp, 3.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = employee.fio,
                    fontSize = 20.sp,
                    softWrap = true,
                    maxLines = Int.MAX_VALUE, // или, например, 2–3, если нужно ограничение
                    overflow = TextOverflow.Clip // или Ellipsis, если ограничено maxLines
                )
            }

            if (employee.isFavorite) {
                Image(
                    painter = painterResource(R.drawable.ic_baseline_favorite_24),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(end=5.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmployeeCardPreview() {

    val  emp =  Employee(
        0,
        "Максимааааааааа",
        "Шухман",
        "Юрьевич",
        "",
        "",
        "",
        "АСОИ",

        "",
    )
    emp.isFavorite = true
    BSUIRScheduleTheme {
        EmployeeCard(
            emp
        ){

        }
    }
}