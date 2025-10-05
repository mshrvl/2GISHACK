package com.example.map.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import ru.dgis.sdk.compose.map.MapComposable
import ru.dgis.sdk.compose.map.controls.CompassComposable
import ru.dgis.sdk.compose.map.controls.IndoorComposable
import ru.dgis.sdk.compose.map.controls.MyLocationComposable
import ru.dgis.sdk.compose.map.controls.TrafficComposable
import ru.dgis.sdk.compose.map.controls.ZoomComposable
import ru.dgis.sdk.coordinates.GeoPoint
import ru.dgis.sdk.map.Map

@Serializable
data object MapScreenDestination

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel()
) {
    LaunchedEffect(Unit)
    { viewModel.dispatch(MapScreenAction.GetCurrentPosition(37.625853, 55.695196)) }

    val state by viewModel.collectAsState()
    val map by state.map.map.collectAsState()
        MapComposable(
            modifier = modifier,
            state = state.map
        )
        map?.let {
            MapControls(it)
        }
    val points = listOf(
        GeoPoint(latitude = 55.751244, longitude = 37.618423), // Москва, Кремль
        GeoPoint(latitude = 55.753544, longitude = 37.619423), // Пример следующей точки
        GeoPoint(latitude = 55.754544, longitude = 37.620423)  // Еще одна точка
    )


}

@Composable
fun MapControls(map: Map) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 30.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TrafficComposable(map)
        }
        var showImage by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        val context = LocalContext.current

        Column(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ZoomComposable(map)
            CompassComposable(map)
            MyLocationComposable(map)
            androidx.compose.material3.Button(onClick = {
                name = "our"
                showImage = true
            }) {
                Text(modifier = Modifier, text = "У")
            }
            androidx.compose.material3.Button(onClick = {
                name = "there"
                showImage = true
            }) {
                Text(modifier = Modifier, text = "Н")
            }
        }
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IndoorComposable(map)
        }
        val imageResId = remember {
            context.resources.getIdentifier("our", "drawable", context.packageName)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showImage) {
                if (imageResId != 0) {
                    AlertDialog(
                        onDismissRequest = { showImage = false },
                        text = {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                        confirmButton = {
                            Button(onClick = { showImage = false }) {
                                Text("Закрыть")
                            }
                        }
                    )
                } else {
                    Text("Изображение не найдено")
                }
            }
        }
    }
    }