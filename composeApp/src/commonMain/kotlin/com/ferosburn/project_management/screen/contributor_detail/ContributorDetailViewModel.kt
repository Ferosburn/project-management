package com.ferosburn.project_management.screen.contributor_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContributorDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ContributorDetailUiState>(ContributorDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getUserDetail(userId: String) {
        viewModelScope.launch {
            _uiState.value = ContributorDetailUiState.Loading
            when (val response = UserApi.getUserDetail(userId)) {
                is NetworkResult.Error -> _uiState.value = ContributorDetailUiState.Failed(response.message)
                is NetworkResult.Success -> {
                    response.data.data?.let {
                        _uiState.value = ContributorDetailUiState.Success(it)
                    }
                }
            }
        }
    }
}