package com.example.map.screen

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.dgis.sdk.compose.map.MapComposableState
import ru.dgis.sdk.coordinates.Bearing
import ru.dgis.sdk.coordinates.GeoPoint
import ru.dgis.sdk.map.CameraPosition
import ru.dgis.sdk.map.MapOptions
import ru.dgis.sdk.map.Zoom

class MapViewModel : ViewModel(), ContainerHost<MapScreenState, Nothing> {

    override val container = container<MapScreenState, Nothing>(MapScreenState())

    fun dispatch() {}

    init {
        updatePosition()
    }
    private fun smth() = intent {
    }

    private fun createMapOptions(): MapOptions {
        val cameraPosition = CameraPosition(
            point = GeoPoint(
                latitude = 55.760898,
                longitude = 37.620242
            ),
            bearing = Bearing(20.0),
            zoom = Zoom(17f)
        )

        return MapOptions().apply {
            position = cameraPosition
        }

    }

    private fun updatePosition() = intent {
        reduce { state.copy(map = MapComposableState(mapOptions = createMapOptions())) }
    }
}