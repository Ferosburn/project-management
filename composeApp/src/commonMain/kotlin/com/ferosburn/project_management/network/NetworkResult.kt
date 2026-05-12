package com.ferosburn.project_management.network

sealed interface NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Error<T>(val message: String) : NetworkResult<T>
}