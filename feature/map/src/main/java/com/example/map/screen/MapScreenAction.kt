package com.example.map.screen

sealed interface MapScreenAction {
    data class getCurrentPosition(val lat: Double, val long: Double) : MapScreenAction
}