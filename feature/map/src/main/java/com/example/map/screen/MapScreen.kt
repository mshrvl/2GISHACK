package com.example.map.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import ru.dgis.sdk.compose.map.MapComposable
import ru.dgis.sdk.compose.map.controls.MyLocationComposable
import ru.dgis.sdk.compose.map.controls.ZoomComposable

@Serializable
data object MapScreenDestination

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel()
) {
    val state by viewModel.collectAsState()
    val map by state.map.map.collectAsState()
    Scaffold(modifier = modifier) { pv ->
        MapComposable(
            modifier = Modifier.padding(pv),
            state = state.map
        )
        map?.let {
            MyLocationComposable(map = it)
            ZoomComposable(map = it)
        }
    }
}