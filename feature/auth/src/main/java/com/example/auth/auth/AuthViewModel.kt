package com.example.auth.auth

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.math.log

class AuthViewModel @Inject constructor(): ViewModel(), ContainerHost<AuthState, AuthAction> {

    override val container = container<AuthState, AuthAction>(AuthState())

    fun dispatch(action: AuthAction) {
        when(action) {
            is AuthAction.OnLoginChange -> TODO()
            is AuthAction.OnLoginNav -> TODO()
            is AuthAction.OnPasswordChange -> TODO()
        }
    }

    private fun onLoginChange(login: String) = intent {
        reduce { state.copy(login = login)}
            checkIsButtonEnabled()
    }

    private fun onPasswordChange(password: String) = intent {
        reduce { state.copy(password = password) }
        checkIsButtonEnabled()
    }

    private fun onAuthHandle() = intent {
    }

    private fun checkIsButtonEnabled() = intent {
        if (state.password.isEmpty() || state.login.isEmpty()) {
            reduce { state.copy(isAuthButtonEnabled = false) }
        } else {reduce { state.copy(isAuthButtonEnabled = true) }}
    }
}