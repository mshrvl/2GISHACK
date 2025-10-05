package com.example.data.repository

import com.example.data.IrritantsResponse
import com.example.data.MapApi

class MapRepository(private val mapApi: MapApi) {

    suspend fun getStimuli(): IrritantsResponse {
        return mapApi.getStimuly()
    }
}