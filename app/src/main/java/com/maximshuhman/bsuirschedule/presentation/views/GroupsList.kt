@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupListViewModel
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupsListUiState
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
fun GroupsScreen(
    parentNavController: NavController,
    viewModel: GroupListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val textFieldState = rememberTextFieldState()

    val listState = when (uiState) {
        is GroupsListUiState.Loading -> ListScreenState.Loading
        is GroupsListUiState.Error -> ListScreenState.Error((uiState as GroupsListUiState.Error).message)
        is GroupsListUiState.Success -> ListScreenState.Success((uiState as GroupsListUiState.Success).groupList)
        GroupsListUiState.NoConnection -> ListScreenState.NoConnection
    }


    LaunchedEffect(Unit) {
        viewModel.loadList()
    }

    SearchableListScreen(
        query = textFieldState.text.toString(),
        onQueryChange = { newText ->
            textFieldState.edit { replace(0, length, newText) }
            viewModel.search(newText)
        },
        onSearch = { viewModel.search(it) },
        placeholder = "Введите номер группы",
        state = listState,
        contentPadding = PaddingValues(
            horizontal = 5.dp,
            vertical = 5.dp
        )
    ) {
        if(uiState is GroupsListUiState.Success)
        items((uiState as GroupsListUiState.Success).groupList) { group ->
            GroupCard(group) {
                parentNavController.navigate("${NavRoutes.GroupSchedule.route}/${group.id}&${group.name}") {
                    restoreState = true
                    popUpTo(NavRoutes.GroupSchedule.route) { inclusive = true }
                }
            }
        }
    }
}


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
                    contentDescription = null
                )
            else
                Image(
                    painterResource(R.drawable.ic_baseline_favorite_border_24),
                    contentDescription = null
                )

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