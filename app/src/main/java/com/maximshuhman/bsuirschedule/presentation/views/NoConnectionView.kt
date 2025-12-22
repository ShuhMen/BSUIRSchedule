package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
fun NoConnectionView(modifier: Modifier) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.no_network_icon),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = null,
        )

        Text("Подключение к интернету отсутствует",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 10.dp),
                fontWeight = FontWeight.Bold
            )

    }
}

@Composable
@Preview
fun NoConnectionPreview(){
    BSUIRScheduleTheme {
        NoConnectionView(Modifier.fillMaxSize())
    }
}