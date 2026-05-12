package com.ferosburn.project_management.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.auth.AuthLoginReq
import com.ferosburn.project_management.network.AuthApi
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState = _uiState.asStateFlow()
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var showPassword by mutableStateOf(false)
    var usernameError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    fun onUsernameChange(value: String) {
        usernameError = null
        username = value
    }

    fun onPasswordChange(value: String) {
        passwordError = null
        password = value
    }

    fun togglePasswordVisibility() {
        showPassword = !showPassword
    }

    fun validateForm(): Boolean {
        usernameError = when {
            username.isBlank() -> "username is required"
            else -> null
        }
        passwordError = when {
            password.isBlank() -> "password is required"
            password.length < 8 -> "password is must be at least 8 characters long"
            else -> null
        }
        return listOf(usernameError, passwordError).all { it.isNullOrBlank() }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            when (val result = AuthApi.login(AuthLoginReq(username, password))) {
                is NetworkResult.Success -> {
                    result.data.data?.let {
                        LocalStorage.saveAccessToken(it.accessToken)
                        LocalStorage.saveRefreshToken(it.refreshToken)
                    }
                    _uiState.value = LoginUiState.Success
                }

                is NetworkResult.Error -> {
                    _uiState.value = LoginUiState.Error(result.message)
                    delay(3.seconds)
                    _uiState.value = LoginUiState.Initial
                }
            }
        }
    }
}