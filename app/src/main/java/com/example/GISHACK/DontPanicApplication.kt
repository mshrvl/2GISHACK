package com.example.GISHACK

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.auth.auth.AuthScreenDestination
import com.example.auth.nav.authNavGrap
import com.example.map.nav.mapNavGraph
import com.example.map.screen.MapScreenDestination

@Composable
fun DontPanicApp(
    modifier: Modifier = Modifier
){
    val navController = rememberNavController()
    val maxSizeModifier = Modifier.fillMaxSize()
    NavHost(
        modifier = maxSizeModifier,
        navController = navController,
        startDestination = MapScreenDestination,
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