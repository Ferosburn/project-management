package com.ferosburn.project_management.models

interface Validatable {
    fun validate(): List<String>
}