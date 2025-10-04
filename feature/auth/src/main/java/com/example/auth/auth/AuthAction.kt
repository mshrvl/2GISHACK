package com.example.auth.auth

sealed interface AuthAction {
    data class OnLoginChange (val login: String): AuthAction
    data class OnPasswordChange (val password: String): AuthAction
}