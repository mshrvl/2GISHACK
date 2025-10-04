package com.example.map.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel(), ContainerHost<MapScreenState, Nothing> {

    override val container = container<MapScreenState, Nothing>(MapScreenState())

    fun dispatch() {}

    private fun smth() = intent {
    }
}