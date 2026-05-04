package com.ferosburn.project_management.utils

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordUtils {
    private const val COST: Int = 12

    fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray()).toString()
    }

    fun verify(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray()).verified
    }
}