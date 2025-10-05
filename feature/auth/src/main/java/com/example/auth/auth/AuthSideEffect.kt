package com.example.auth.auth

sealed interface AuthSideEffect {
    data object OnNavSuccess: AuthSideEffect
    data object OnNavToLogin: AuthSideEffect
}