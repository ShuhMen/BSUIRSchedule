package com.maximshuhman.bsuirschedule.presentation.views.settings

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maximshuhman.bsuirschedule.ApplicationThemes
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
fun ThemeItem(theme: ApplicationThemes, isChecked: Boolean, nameId: Int = theme.nameId, onCLick: () -> Unit = {}) {

    Column(
        modifier = Modifier.padding(horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box {
            Image(
                painterResource(theme.drawableId),
                contentDescription = stringResource(theme.nameId),
                modifier = Modifier
                    .size(76.dp)
                    .padding(5.dp)
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.onBackground,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        onCLick.invoke()
                    }
            )
            if (isChecked)
                Image(
                    painterResource(R.drawable.check_circle),
                    contentDescription = null
                )
        }
        Text(stringResource(nameId), textAlign = TextAlign.Center)
    }


}

@Preview(
    showBackground = true, name = "SettingsView Preview",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ThemeItemPreview() {
    BSUIRScheduleTheme {


        ThemeItem(ApplicationThemes.DarkTheme, true)

    }
}