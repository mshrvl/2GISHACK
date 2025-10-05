package com.example.data

import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MapApi {

    @GET("stimuli/")
    suspend fun getStimuly(): IrritantsResponse

    @PUT("stimuli/{id}")
    suspend fun setStatusStimuly(
        @Path("id") id: Int,
        @Query("is_active") isSelected: Boolean
    )
}
