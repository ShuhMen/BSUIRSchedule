package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.maximshuhman.bsuirschedule.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <T> SearchableListScreen(
    query: String,
    crossinline onQueryChange: (String) -> Unit,
    crossinline onSearch: (String) -> Unit,
    placeholder: String,
    state: ListScreenState<T>,
    contentPadding: PaddingValues = PaddingValues(),
    crossinline listContent: LazyListScope.() -> Unit
) {
    val searchBarState = rememberSearchBarState()
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            searchBarState,
            inputField = {
                SearchBarDefaults.InputField(
                    query =query,
                    onQueryChange = {onQueryChange(it)},
                    onSearch = {onSearch(it)},
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(placeholder) },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.search),
                            contentDescription = stringResource(R.string.search)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )

        when (state) {
            ListScreenState.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            is ListScreenState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }
            }
            is ListScreenState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding
                ) {
                    listContent()
                }
            }
            ListScreenState.NoConnection -> {
                NoConnectionView(Modifier.fillMaxSize())
            }
        }
    }
}

sealed interface ListScreenState<out T> {
    data object Loading : ListScreenState<Nothing>
    data class Error(val message: String) : ListScreenState<Nothing>
    data class Success<T>(val data: List<T>) : ListScreenState<T>
    data object NoConnection : ListScreenState<Nothing>
}