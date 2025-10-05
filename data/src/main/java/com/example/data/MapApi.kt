package com.example.data

import retrofit2.http.GET

interface MapApi {

    @GET("stimuli/")
    suspend fun getStimuly(): IrritantsResponse

    //@POST("stimuli/{stimuli_id}")
}
