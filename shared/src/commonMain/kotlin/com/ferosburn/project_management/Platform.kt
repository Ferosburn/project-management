package com.ferosburn.project_management

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform