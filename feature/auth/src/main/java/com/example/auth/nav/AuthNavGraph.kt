package com.example.auth.nav

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.auth.auth.AuthScreen
import com.example.auth.auth.AuthScreenDestination
import androidx.navigation.compose.composable


fun NavGraphBuilder.authNavGrap(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    composable <AuthScreenDestination> {
        AuthScreen(modifier = modifier)
        }
    }