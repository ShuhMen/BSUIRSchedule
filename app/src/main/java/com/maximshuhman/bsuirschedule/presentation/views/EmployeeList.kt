@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.presentation.viewModels.EmployeeListUiState
import com.maximshuhman.bsuirschedule.presentation.viewModels.EmployeeListViewModel

@Composable
fun EmployeeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: EmployeeListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val textFieldState = rememberTextFieldState()

    val listState = when (uiState) {
        is EmployeeListUiState.Loading -> ListScreenState.Loading
        is EmployeeListUiState.Error -> ListScreenState.Error((uiState as EmployeeListUiState.Error).message)
        is EmployeeListUiState.Success -> ListScreenState.Success((uiState as EmployeeListUiState.Success).groupList)
        EmployeeListUiState.NoConnection -> ListScreenState.NoConnection
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
        placeholder = "Введите преподавателя",
        state = listState,
        contentPadding = PaddingValues(
            horizontal = 5.dp,
            vertical = 5.dp
        )
    ) {
        if(uiState is EmployeeListUiState.Success)
            itemsIndexed((uiState as EmployeeListUiState.Success).groupList) { _, employee ->
                EmployeeCard(employee) {
                    navController.navigate("${NavRoutes.EmployeeSchedule.route}/${employee.id}&${employee.fio}"){
                        navOptions {
                            restoreState = true
                        }

                        popUpTo(NavRoutes.EmployeeSchedule.route) {
                            inclusive = true
                        }
                    }
                }
            }
    }
}

@Composable
fun EmployeeList(
    navController: NavController,
    state: EmployeeListUiState.Success,
    paddingValues: PaddingValues
){
    LazyColumn(
        Modifier.fillMaxSize().padding(start = 5.dp, top = 5.dp, end = 5.dp),
        contentPadding = paddingValues
    ) {
        itemsIndexed(state.groupList) { _, employee ->
            EmployeeCard(employee) {
                navController.navigate("${NavRoutes.EmployeeSchedule.route}/${employee.id}&${employee.fio}"){
                    navOptions {
                        restoreState = true
                    }

                    popUpTo(NavRoutes.EmployeeSchedule.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
}


