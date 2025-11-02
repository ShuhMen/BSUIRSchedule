package com.maximshuhman.bsuirschedule.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximshuhman.bsuirschedule.data.dto.Group
import com.maximshuhman.bsuirschedule.data.sources.GroupsDAO
import com.maximshuhman.bsuirschedule.domain.useCases.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class MainViewModel @Inject constructor(
    private val groupsDAO: GroupsDAO,
    private val getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val uiState : StateFlow<MainActivityUiState> = _uiState.asStateFlow()


    fun getLastScreen(){

        viewModelScope.launch(Dispatchers.IO) {

            val favIds =  groupsDAO.getFavoriteGroupIds()

            if(favIds.isEmpty()){
                _uiState.emit(MainActivityUiState.Empty)
                return@launch
            }

            val group = groupsDAO.getById(favIds.first())

            if(group == null)
            {
                _uiState.emit(MainActivityUiState.Empty)
                return@launch
            }

            _uiState.emit(MainActivityUiState.Success(group))
        }

    }

}

sealed class MainActivityUiState {
    object Loading : MainActivityUiState()
    data class Success(val group: Group) : MainActivityUiState()
    object Empty : MainActivityUiState()
}