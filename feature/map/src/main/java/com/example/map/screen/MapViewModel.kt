package com.example.map.screen

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class MapViewModel : ViewModel(), ContainerHost<MapScreenState, Nothing> {

    override val container = container<MapScreenState, Nothing>(MapScreenState())

    fun dispatch() {}

    private fun smth() = intent {
    }
}