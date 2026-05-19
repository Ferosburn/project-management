package com.ferosburn.project_management.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.auth.AuthRefreshTokenReq
import com.ferosburn.project_management.network.AuthApi
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun checkAuth() {
        viewModelScope.launch {
            val refreshToken = LocalStorage.readRefreshToken()
            if (refreshToken == null) {
                _uiState.value = SplashUiState.Failed("Not logged in")
                return@launch
            }
            when (val refresh = AuthApi.refreshToken(AuthRefreshTokenReq(refreshToken))) {
                is NetworkResult.Success -> {
                    refresh.data.data?.let {
                        LocalStorage.saveAccessToken(it.accessToken)
                        LocalStorage.saveRefreshToken(it.refreshToken)
                        LocalStorage.saveUserRole(it.role)
                        LocalStorage.saveUserId(it.userId)
                    }
                    _uiState.value = SplashUiState.Success
                }
                is NetworkResult.Error<*> -> {
                    LocalStorage.deleteTokenRes()
                    _uiState.value = SplashUiState.Failed(refresh.message)
                }
            }
        }
    }
}