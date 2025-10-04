package com.example.auth.auth

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import javax.inject.Inject

class AuthViewModel @Inject constructor(): ViewModel(), ContainerHost<AuthState, > {
}