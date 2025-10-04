package com.example.map.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.Serializable
import org.orbitmvi.orbit.compose.collectAsState
import ru.dgis.sdk.compose.map.MapComposable
import ru.dgis.sdk.compose.map.MapComposableState
import ru.dgis.sdk.compose.map.controls.MyLocationComposable
import ru.dgis.sdk.map.MapOptions

@Serializable
data object MapScreenDestination

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val map by state.map.map.collectAsState()
    MapComposable(
        modifier = modifier,
        state = MapComposableState(mapOptions = MapOptions())
    )
    map?.let {
        MyLocationComposable(map = it)
    }
}