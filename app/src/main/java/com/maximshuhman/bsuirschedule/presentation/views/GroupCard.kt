package com.maximshuhman.bsuirschedule.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
inline fun GroupCard(group: Group, crossinline onClick: () -> Unit = { }) {
    Card(
        onClick = { onClick() },
        Modifier
            .padding(5.dp, 3.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {

        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

            Column(
                modifier = Modifier
            ) {
                Text(group.name, fontSize = 20.sp)
                Text(group.facultyAbbrev + ", " + group.specialityAbbrev, fontSize = 16.sp)
            }

            if(group.isFavorite)
                Image(
                    painterResource(R.drawable.ic_baseline_favorite_24),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(end=5.dp)
                )
            /*else
                Image(
                    painterResource(R.drawable.ic_baseline_favorite_border_24),
                    contentDescription = null
                )*/

        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GroupCardPreview() {
    BSUIRScheduleTheme {
        GroupCard(
            Group(
                0,
                "220601",
                0,
                "ФИТУ",
                "",
                0,
                "",
                "АСОИ",

                0,
                "",
                0
            )
        ) {

        }
    }
}