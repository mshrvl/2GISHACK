package com.example.map.nav

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.map.screen.MapScreen
import com.example.map.screen.MapScreenDestination

fun NavGraphBuilder.mapNavGraph(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    composable<MapScreenDestination> {
        MapScreen(modifier = modifier)
    }
}