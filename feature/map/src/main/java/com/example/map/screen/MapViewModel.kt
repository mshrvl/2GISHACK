package com.example.map.screen

import androidx.lifecycle.ViewModel
import com.example.data.MapApi
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.dgis.sdk.compose.map.MapComposableState
import ru.dgis.sdk.coordinates.Bearing
import ru.dgis.sdk.coordinates.GeoPoint
import ru.dgis.sdk.map.CameraPosition
import ru.dgis.sdk.map.MapOptions
import ru.dgis.sdk.map.Zoom

class MapViewModel(private val api: MapApi) : ViewModel(), ContainerHost<MapScreenState, Nothing> {

    override val container = container<MapScreenState, Nothing>(MapScreenState())

    fun dispatch(action: MapScreenAction) {
        when (action) {
            is MapScreenAction.GetCurrentPosition -> updatePosition(action.lat, action.long)
        }
    }

    private fun updatePosition(latitude: Double, longitude: Double) = intent {
        reduce {
            state.copy(
                map = MapComposableState(
                    mapOptions = createMapOptions(
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            )
        }
    }

//    private fun getRoute() = intent  {
//        val points = api.createRoot(body = CoordBody.body)
//        reduce { state.copy(points = points.features.forEach { it.geometry.coordinates.forEach { } })}
//    }



    private fun createMapOptions(latitude: Double, longitude: Double): MapOptions {
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
}