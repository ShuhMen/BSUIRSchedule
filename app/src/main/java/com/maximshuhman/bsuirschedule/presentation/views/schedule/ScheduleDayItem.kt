package com.maximshuhman.bsuirschedule.presentation.views.schedule

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maximshuhman.bsuirschedule.domain.models.ScheduleDayHeader


@Composable
fun ScheduleDayItem(groupDay: ScheduleDayHeader) {
    Text(
        groupDay.name,
        fontSize = 20.sp,
        modifier = Modifier
            .padding(15.dp, 7.dp, bottom = 3.dp)
            .fillMaxWidth()
    )
}
