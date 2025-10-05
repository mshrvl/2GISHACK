package com.example.profile.nav

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.profile.profile.ProfileScreen
import com.example.profile.profile.ProfileScreenDestination

fun NavGraphBuilder.profileNavGraph(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    composable<ProfileScreenDestination> {
        ProfileScreen(modifier = modifier)
    }
}