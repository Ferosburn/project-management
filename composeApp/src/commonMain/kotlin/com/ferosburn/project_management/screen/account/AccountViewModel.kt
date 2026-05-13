package com.ferosburn.project_management.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.network.AuthApi
import com.ferosburn.project_management.network.NetworkResult
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
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