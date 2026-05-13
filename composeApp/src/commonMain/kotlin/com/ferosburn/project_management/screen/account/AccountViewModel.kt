package com.ferosburn.project_management.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.network.AuthApi
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AccountUiState>(AccountUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AccountUiState.Loading
            when (val response = AuthApi.logout()) {
                is NetworkResult.Success -> {
                    LocalStorage.deleteTokens()
                    _uiState.value = AccountUiState.SuccessLogout
                }

                is NetworkResult.Error -> _uiState.value = AccountUiState.Failed(response.message)
            }
        }
    }

    fun checkToken() {
        viewModelScope.launch {
            val response = AuthApi.checkToken()
            when (response) {
                is NetworkResult.Success -> {
                    println("Check token success")
                }

                is NetworkResult.Error -> {
                    println("Check token failed")
                }
            }
        }
    }
}