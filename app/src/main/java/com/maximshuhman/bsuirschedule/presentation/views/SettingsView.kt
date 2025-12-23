@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

enum class LauncherIcons( val drawableId: Int, val nameId: Int, val backgroundColorId: Int){
    DefaultIcon(R.drawable.default_logo, R.string.default_logo, R.color.ic_launcher_background),
    ChristmasIcon(R.drawable.christmas_logo, R.string.christmas_logo, R.color.ic_launcher_christmas_background)
}

@Composable
fun SettingsView(
    navController: NavController,
    //SettingsViewModel: SettingsViewModel = hiltViewModel()
) {
    
    val scrollableState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Настройки")
            },
                colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
                navigationIcon = {
                    IconButton(
                        {
                            navController.popBackStack()
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

        Column(Modifier.fillMaxWidth()
            .padding(innerPadding).padding(vertical = 5.dp, horizontal = 10.dp)) {

            Text("Выберите иконку приложения", modifier = Modifier.padding(top = 10.dp, bottom = 5.dp) , fontSize = 18.sp)
            HorizontalDivider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .scrollable(scrollableState, orientation = Orientation.Horizontal)
                    .padding(vertical = 5.dp)
            ) {

                LauncherIcons.entries.forEach { launcherIcon ->

                    Column(modifier = Modifier.padding(horizontal = 5.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painterResource(launcherIcon.drawableId),
                            contentDescription = stringResource(launcherIcon.nameId),
                            modifier = Modifier
                                .padding(5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colorResource(launcherIcon.backgroundColorId))
                                .size(76.dp)
                                .padding(2.dp)
                                .clickable {

                                }
                        )
                        Text(stringResource(launcherIcon.nameId), textAlign = TextAlign.Center)
                    }

                }

            }
            HorizontalDivider()



        }


    }
}


@Preview
@Composable
fun SettingsPreview(){

    BSUIRScheduleTheme {

        val navController = rememberNavController()

        SettingsView(navController)


    }

}