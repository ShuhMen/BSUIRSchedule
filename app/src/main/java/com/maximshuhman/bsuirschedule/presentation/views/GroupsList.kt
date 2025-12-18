@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupListViewModel
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupsListUiState

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
            //viewModel.search(newText)
        },
        onSearch = {/* viewModel.search(it)*/ },
        placeholder = "Введите номер группы",
        state = listState,
        contentPadding = PaddingValues(
            horizontal = 5.dp,
            vertical = 5.dp
        )
    ) {
        if(uiState is GroupsListUiState.Success) {
            items((uiState as GroupsListUiState.Success).groupList.filter {
                it.name.contains(
                    textFieldState.text
                )
            }) { group ->
                GroupCard(group) {
                    parentNavController.navigate("${NavRoutes.GroupSchedule.route}/${group.id}&${group.name}") {
                        restoreState = true
                        popUpTo(NavRoutes.GroupSchedule.route) { inclusive = true }
                    }
                }
            }
        }
    }
}


