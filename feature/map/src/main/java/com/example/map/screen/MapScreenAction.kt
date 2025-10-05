package com.example.map.screen

sealed interface MapScreenAction {
    data class GetCurrentPosition(val lat: Double, val long: Double) : MapScreenAction
}