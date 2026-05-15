package com.ferosburn.project_management.screen.contributor_manage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.user.UserCreateReq
import com.ferosburn.project_management.models.user.UserDivision
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.UserApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ContributorManageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ContributorManageUiState>(ContributorManageUiState.Initial)
    val uiState = _uiState.asStateFlow()
    var name by mutableStateOf("")
    var username by mutableStateOf("")
    var role by mutableStateOf<UserRole?>(null)
    var division by mutableStateOf<UserDivision?>(null)

    var nameError by mutableStateOf<String?>(null)
    var usernameError by mutableStateOf<String?>(null)
    var roleError by mutableStateOf<String?>(null)
    var divisionError by mutableStateOf<String?>(null)

    var roleDropdownExpanded by mutableStateOf(false)
    var divisionDropdownExpanded by mutableStateOf(false)

    private fun validateForm(): Boolean {
        nameError = when {
            name.isBlank() -> "name is required"
            else -> null
        }
        usernameError = when {
            username.isBlank() -> "username is required"
            else -> null
        }
        roleError = when {
            role == null -> "role is required"
            else -> null
        }
        divisionError = when {
            role == UserRole.DEVELOPER && division == null -> "division is required"
            else -> null
        }
        return listOf(nameError, usernameError, roleError, divisionError).all { it.isNullOrBlank() }
    }

    fun onNameChange(value: String) {
        name = value
        nameError = null
    }

    fun onUsernameChange(value: String) {
        username = value
        usernameError = null
    }

    fun onRoleChange(value: UserRole?) {
        roleError = null
        divisionError = null
        role = value
        division = null
        roleDropdownExpanded = false
    }

    fun onRoleExpandChange() {
        roleDropdownExpanded = !roleDropdownExpanded
    }

    fun onDivisionChange(value: UserDivision?) {
        divisionError = null
        division = value
        divisionDropdownExpanded = false
    }

    fun onDivisionExpandChange() {
        divisionDropdownExpanded = !divisionDropdownExpanded
    }

    fun createUser() {
        if (!validateForm()) return
        viewModelScope.launch {
            _uiState.value = ContributorManageUiState.Loading
            val response = UserApi.createUser(
                UserCreateReq(
                    name = name,
                    username = username,
                    role = role?.name,
                    division = division?.name,
                )
            )
            when (response) {
                is NetworkResult.Success -> _uiState.value = ContributorManageUiState.Success
                is NetworkResult.Error -> {
                    _uiState.value = ContributorManageUiState.Failed(response.message)
                    delay(3.seconds)
                    _uiState.value = ContributorManageUiState.Initial
                }
            }
        }
    }
}