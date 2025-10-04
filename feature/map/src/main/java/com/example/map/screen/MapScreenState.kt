package com.example.map.screen

import ru.dgis.sdk.compose.map.MapComposableState
import ru.dgis.sdk.map.MapOptions

data class MapScreenState(
    val map: MapComposableState = MapComposableState(mapOptions = MapOptions())
)