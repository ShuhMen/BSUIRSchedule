package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupScheduleUiState
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
fun ScheduleViewError(innerPadding: PaddingValues, state: GroupScheduleUiState.Error) {

    Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(bottom = 50.dp)) {
        Text( state.message,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun ScheduleViewErrorPreview(){
    BSUIRScheduleTheme {
        ScheduleViewError(PaddingValues(0.dp), GroupScheduleUiState.Error("Расписание отсутствует!"))
    }
}