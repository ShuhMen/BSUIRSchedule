@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.maximshuhman.bsuirschedule.ApplicationThemes
import com.maximshuhman.bsuirschedule.LauncherIcons
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.presentation.viewModels.SettingsViewModel
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme


@Composable
fun SettingsView(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val scrollableIconsState = rememberScrollState()
    val scrollableThemesState = rememberScrollState()
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Настройки")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(
                        {
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            } else {

                            }
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.nav_back),
                            contentDescription = stringResource(R.string.go_back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )

        }
    ) { innerPadding ->

        Column(
            Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {

            Text("Иконка приложения", modifier = Modifier.padding(top = 10.dp), fontSize = 18.sp)
            Text(
                "После изменения иконки приложение будет закрыто",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 14.sp
            )
            HorizontalDivider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .scrollable(scrollableIconsState, orientation = Orientation.Horizontal)
                    .padding(vertical = 5.dp)
            ) {

                LauncherIcons.entries.forEach { launcherIcon ->

                    LauncherItem(launcherIcon, settings.iconInstalled == launcherIcon) {
                        viewModel.setIcon(context.applicationContext, launcherIcon)
                    }

                }

            }
            HorizontalDivider()

            Spacer(Modifier.height(10.dp))

            Text(
                "Тема приложения",
                modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
                fontSize = 18.sp
            )
            //Text("При изменении иконки приложение будет закрыто", modifier = Modifier.padding(bottom = 5.dp) , fontSize = 14.sp)
            HorizontalDivider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .scrollable(scrollableThemesState, orientation = Orientation.Horizontal)
                    .padding(vertical = 5.dp)
            ) {

                ApplicationThemes.entries.forEach { theme ->

                    val shownTheme = if (theme == ApplicationThemes.SystemTheme)
                        if (isSystemInDarkTheme())
                            ApplicationThemes.DarkTheme
                        else
                            ApplicationThemes.LightTheme
                    else
                        theme

                    ThemeItem(shownTheme,settings.theme == theme, if(theme == ApplicationThemes.SystemTheme) ApplicationThemes.SystemTheme.nameId else theme.nameId) {
                        viewModel.setTheme(theme)
                    }
                }

            }
            HorizontalDivider()


        }


    }
}


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


@Composable
fun LauncherItem(launcherIcon: LauncherIcons, isChecked: Boolean, onCLick: () -> Unit = {}) {


    Column(
        modifier = Modifier.padding(horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box {
            Image(
                painterResource(launcherIcon.drawableId),
                contentDescription = stringResource(launcherIcon.nameId),
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(launcherIcon.backgroundColorId))
                    .size(76.dp)
                    .padding(5.dp)
                    .clickable {
                        onCLick()
                    }
            )
            if (isChecked)
                Image(
                    painterResource(R.drawable.check_circle),
                    contentDescription = null
                )
        }
        Text(stringResource(launcherIcon.nameId), textAlign = TextAlign.Center)
    }
}


@Preview(
    showBackground = true, name = "SettingsView Preview",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ThemeItemPreview() {
    BSUIRScheduleTheme {
        /*val navController = rememberNavController()

        val mockSettings = Settings(iconInstalled = LauncherIcons.DefaultIcon)

        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        every { mockViewModel.settings } returns MutableStateFlow(mockSettings).asStateFlow()

        CompositionLocalProvider(LocalContext provides LocalContext.current) {
            SettingsView(
                navController = navController,
                viewModel = mockViewModel
            )
        }*/

        ThemeItem(ApplicationThemes.DarkTheme, true)

    }
}


@Preview(
    showBackground = true, name = "SettingsView Preview",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SettingsViewPreview() {
    BSUIRScheduleTheme {

        LauncherItem(LauncherIcons.DefaultIcon, true)

    }
}