package com.ferosburn.project_management.screen.contributors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContributorsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ContributorsUiState>(ContributorsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getAllUsers(filter: UserFilter = UserFilter()) {
        viewModelScope.launch {
            _uiState.value = ContributorsUiState.Loading
            when (val response = UserApi.getAllUsers(filter)) {
                is NetworkResult.Error -> _uiState.value = ContributorsUiState.Failed(response.message)
                is NetworkResult.Success -> {
                    response.data.data.let {
                        _uiState.value = ContributorsUiState.Success(it ?: emptyList())
                    }
                }
            }
        }
    }
}