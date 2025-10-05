package com.example.gishack

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.auth.nav.authNavGrap
import com.example.map.nav.mapNavGraph
import com.example.map.screen.MapScreenDestination
import com.example.profile.nav.profileNavGraph
import com.example.profile.profile.ProfileScreenDestination

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object Map : Screen("map", "Карта", icon = R.drawable.map)
    object Profile : Screen("profile", "Профиль", icon = R.drawable.profile)
}

val items = listOf(
    Screen.Map,
    Screen.Profile
)
@Composable
fun DontPanicApplication(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    IconButton(
                        modifier = Modifier.weight(0.5f),
                        onClick = {
                            navController.navigate(if (screen.route == "map") MapScreenDestination else ProfileScreenDestination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier.weight(0.5f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(screen.icon),
                                contentDescription = null,
                                tint = androidx.compose.ui.graphics.Color.Unspecified
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = screen.title)
                        }
                    }
                }
            }


        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { pv ->
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
            profileNavGraph(
                modifier = maxSizeModifier,
                navController = navController
            )
        }
    }
}