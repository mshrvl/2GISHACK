package com.example.GISHACK

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.auth.auth.AuthScreenDestination
import com.example.auth.nav.authNavGrap
import com.example.map.nav.mapNavGraph

@Composable
fun DontPanicApp() {
    val navController = rememberNavController()
    val maxSizeModifier = Modifier.fillMaxSize()
    NavHost(
        modifier = maxSizeModifier,
        navController = navController,
        startDestination = AuthScreenDestination,
    ) {
        authNavGrap(
            modifier = maxSizeModifier,
            navController = navController,
        )
        mapNavGraph(
            modifier = maxSizeModifier,
            navController = navController
        )
    }
}