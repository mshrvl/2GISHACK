package com.example.auth.auth

import androidx.compose.runtime.Immutable

@Immutable
data class AuthState(
    val login: String = "",
    val password: String = "",
    val isAuthButtonEnabled: Boolean = false
)