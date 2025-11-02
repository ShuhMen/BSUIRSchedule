@file:OptIn(ExperimentalMaterial3Api::class)

package com.maximshuhman.bsuirschedule.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupListViewModel
import com.maximshuhman.bsuirschedule.presentation.viewModels.GroupsListUiState
import com.maximshuhman.bsuirschedule.ui.theme.BSUIRScheduleTheme

@Composable
fun GroupsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: GroupListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()

    var expanded by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.loadList()
    }

    Scaffold(
        topBar = {
            SearchBar(
                searchBarState,inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = {
                        textFieldState.edit { replace(0, length, it) }
                        viewModel.search(textFieldState.text.toString())
                    },
                    onSearch = {
                        viewModel.search(textFieldState.text.toString())
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Введи номер группы") },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.search),
                            contentDescription = stringResource(R.string.search)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 5.dp)
            )
        }
    ) { innerPadding ->

        when (uiState) {
            is GroupsListUiState.Error -> {
                Box {
                    Text((uiState as GroupsListUiState.Error).message)
                }
            }
            GroupsListUiState.Loading -> {
                Box{
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
            is GroupsListUiState.Success -> GroupsList(navController,uiState as GroupsListUiState.Success, innerPadding)
        }
    }
}

@Composable
fun GroupsList(
    navController: NavController,
    state: GroupsListUiState.Success,
    paddingValues: PaddingValues
){
    LazyColumn(
        Modifier.fillMaxSize().padding(start = 5.dp, top = 5.dp, end = 5.dp),
        contentPadding = paddingValues
    ) {
        itemsIndexed(state.groupList) { _, group ->
            GroupCard(group) {
                navController.navigate("${NavRoutes.GroupSchedule.route}/${group.id}&${group.name}"){
                    navOptions {
                        restoreState = true
                    }

                    popUpTo(NavRoutes.GroupSchedule.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
}


@Composable
inline fun GroupCard(group: Group, crossinline onClick: () -> Unit = { },) {
    Card(
        onClick ={ onClick() },
        Modifier
            .padding(5.dp, 3.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {

        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically ) {

            Column(
                modifier = Modifier
            ) {
                Text(group.name, fontSize = 20.sp)
                Text(group.facultyAbbrev + ", " + group.specialityAbbrev, fontSize = 16.sp)
            }
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
        ){

        }
    }
}