package com.maximshuhman.bsuirschedule.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maximshuhman.bsuirschedule.NavRoutes
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.domain.models.Favorites

@Composable
fun FavoritesBottomSheet(navController: NavController, favorites: Favorites, dismiss: () -> Unit){


    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
        , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.favorite_screen),
            modifier = Modifier.align(Alignment.CenterVertically),
            fontSize = 24.sp
        )

        IconButton ({
            dismiss()
            navController.navigate(NavRoutes.Groups.route)
        },
            modifier = Modifier
                .size(20.dp)
        ) {
            Icon(
                painterResource(R.drawable.edit_favorites),
                stringResource(R.string.edit_favorites)
            )
        }
    }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 5.dp)
    ) {

        if(favorites.groupsList.isNotEmpty() && favorites.employeeList.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.favorite_groups),
                    modifier = Modifier.padding(
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 10.dp
                    ),
                    fontSize = 20.sp
                )
            }
        }

        itemsIndexed(favorites.groupsList,
            key = { index, group ->
                Pair(group.id, 0)
            }
        ){ _,group ->

            GroupCard(group){
                dismiss()
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

        if(favorites.groupsList.isNotEmpty() && favorites.employeeList.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.favorite_employees),
                    modifier = Modifier.padding(
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 10.dp
                    ),
                    fontSize = 20.sp
                )
            }
        }

        itemsIndexed(favorites.employeeList,
            key = { index, employee ->
                Pair(employee.id, 1)
            }
        ){ _,employee ->

            Text(employee.lastName)

        }
    }
}